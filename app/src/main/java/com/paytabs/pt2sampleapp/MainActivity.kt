package com.paytabs.pt2sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startAlternativePaymentMethods
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.QuerySdkActivity
import com.payment.paymentsdk.integrationmodels.*
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackQueryInterface
import com.payment.paymentsdk.sharedclasses.model.response.TransactionResponseBody
import com.paytabs.pt2sampleapp.databinding.ActivityMainBinding
import com.paytabs.pt2sampleapp.R
import com.paytabs.samsungpay.sample.SamsungPayActivity

/**
 * MainActivity handles various payment methods using PayTabs SDK.
 */
class MainActivity : AppCompatActivity(), CallbackPaymentInterface, CallbackQueryInterface {

    companion object {
        private const val TAG = "MainActivity"
        private val LANGUAGE_CODE = PaymentSdkLanguageCode.EN
    }

    private var token: String? = null
    private var transRef: String? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var merchantRegions: List<MerchantRegion>
    private lateinit var selectedMerchantRegion: MerchantRegion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupCurrencySpinner()
        setupMerchantCountrySpinner()
        setupSectionSwitches()
        setupClickListeners()
    }

    /**
     * Initializes view binding.
     */
    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        merchantRegions = buildMerchantRegions()
        if (merchantRegions.isEmpty()) {
            merchantRegions = listOf(MerchantRegion("AE - United Arab Emirates", "AE"))
        }
        selectedMerchantRegion = merchantRegions.first()
    }

    /**
     * Sets up the currency spinner dropdown.
     */
    private fun setupCurrencySpinner() {
        val currencies = resources.getStringArray(R.array.currencies)
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            currencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCurrency.adapter = adapter
        
        // Set default selection to AED if available
        val defaultCurrency = "AED"
        val defaultIndex = currencies.indexOf(defaultCurrency)
        if (defaultIndex >= 0) {
            binding.spCurrency.setSelection(defaultIndex)
        }
    }

    /**
     * Sets up the merchant country spinner and base URL display.
     */
    private fun setupMerchantCountrySpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            merchantRegions.map { it.displayName }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spMerchantCountry.adapter = adapter
        binding.spMerchantCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMerchantRegion = merchantRegions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        // Initialize display
        binding.spMerchantCountry.setSelection(0)
    }

    private fun buildMerchantRegions(): List<MerchantRegion> {
        val names = resources.getStringArray(R.array.merchant_country_names)
        val codes = resources.getStringArray(R.array.merchant_country_codes)
        return names.mapIndexed { index, name ->
            val code = codes.getOrElse(index) { "AE" }
            MerchantRegion(name, code)
        }
    }

    /**
     * Sets up switches to toggle section visibility.
     */
    private fun setupSectionSwitches() {
        // Transaction Details Switch
        binding.switchTransactionDetails.setOnCheckedChangeListener { _, isChecked ->
            binding.llTransactionDetails.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }

        // Billing Information Switch
        binding.switchBilling.setOnCheckedChangeListener { _, isChecked ->
            binding.llBillingDetails.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }

        // Shipping Information Switch
        binding.switchShipping.setOnCheckedChangeListener { _, isChecked ->
            binding.llShippingDetails.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    /**
     * Sets up click listeners for payment buttons.
     */
    private fun setupClickListeners() {
        binding.pay.setOnClickListener { initiateCardPayment() }
        binding.knetPay.setOnClickListener { initiateAlternativePayment(PaymentSdkApms.KNET_CREDIT) }
        binding.valuPay.setOnClickListener { initiateAlternativePayment(PaymentSdkApms.VALU) }
        binding.fawryPay.setOnClickListener { initiateAlternativePayment(PaymentSdkApms.FAWRY) }
        binding.samPay.setOnClickListener { initiateSamsungPay() }
        binding.queryFunction.setOnClickListener { queryTransaction() }
    }

    /**
     * Initiates card payment.
     */
    private fun initiateCardPayment() {
        val configData = generatePaymentConfiguration()
        configData?.let {
            startCardPayment(this, it, this)
        }
    }

    /**
     * Initiates alternative payment methods.
     * @param apm Selected alternative payment method.
     */
    private fun initiateAlternativePayment(apm: PaymentSdkApms) {
        val configData = generatePaymentConfiguration(apm)
        configData?.let {
            startAlternativePaymentMethods(this, it, this)
        }
    }

    /**
     * Initiates Samsung Pay.
     */
    private fun initiateSamsungPay() {
        val configData = generatePaymentConfiguration()
        configData?.let {
            SamsungPayActivity.start(this, it)
        }
    }

    /**
     * Queries a transaction.
     */
    private fun queryTransaction() {
        // For query, we'll use the same credentials from the input fields
        val serverKey = binding.etServerKey.text.toString().trim()
        val clientKey = binding.etClientKey.text.toString().trim()
        val profileId = binding.etProfileId.text.toString().trim()
        val merchantCountryCode = selectedMerchantRegion.countryCode
        
        if (serverKey.isEmpty() || clientKey.isEmpty() || profileId.isEmpty()) {
            showToast("Please enter credentials for query")
            return
        }
        
        // Note: Transaction reference would need to be entered separately for query
        // For now, using a placeholder - you may want to add an input field for this
        val queryConfig = PaymentSDKQueryConfiguration(
            serverKey = serverKey,
            clientKey = clientKey,
            merchantCountryCode = merchantCountryCode.ifEmpty { "AE" },
            profileID = profileId,
            transactionReference = "your_transaction_reference" // Add input field for this if needed
        )
        QuerySdkActivity.queryTransaction(this, queryConfig, this)
    }

    /**
     * Generates payment configuration details.
     * @param selectedApm Optional alternative payment method.
     * @return Configured PaymentSdkConfigurationDetails object.
     */
    private fun generatePaymentConfiguration(
        selectedApm: PaymentSdkApms? = null
    ): PaymentSdkConfigurationDetails? {
        // Validate required fields
        val profileId = binding.etProfileId.text.toString().trim()
        val serverKey = binding.etServerKey.text.toString().trim()
        val clientKey = binding.etClientKey.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val currency = binding.spCurrency.selectedItem?.toString() ?: ""
        
        if (profileId.isEmpty()) {
            showToast("Please enter Profile ID")
            return null
        }
        if (serverKey.isEmpty()) {
            showToast("Please enter Server Key")
            return null
        }
        if (clientKey.isEmpty()) {
            showToast("Please enter Client Key")
            return null
        }
        if (amountStr.isEmpty()) {
            showToast("Please enter Amount")
            return null
        }
        val amount = try {
            amountStr.toDouble()
        } catch (e: NumberFormatException) {
            showToast("Please enter a valid amount")
            return null
        }
        if (currency.isEmpty()) {
            showToast("Please select Currency")
            return null
        }
        
        val cartId = binding.etCartId.text.toString().trim()
        val cartDescription = binding.etCartDescription.text.toString().trim()
        val transactionTitle = binding.etTransactionTitle.text.toString().trim()
        val merchantCountryCode = selectedMerchantRegion.countryCode
        
        val configBuilder = PaymentSdkConfigBuilder(
            profileId = profileId,
            serverKey = serverKey,
            clientKey = clientKey,
            amount = amount,
            currencyCode = currency
        ).apply {
            if (cartDescription.isNotEmpty()) {
                setCartDescription(cartDescription)
            }
            setLanguageCode(LANGUAGE_CODE)
            // Optional: Set merchant icon if available
            // setMerchantIcon(ContextCompat.getDrawable(this@MainActivity, R.drawable.paytabs))
            setBillingData(getBillingDetails())
            setMerchantCountryCode(merchantCountryCode)
            setTransactionType(PaymentSdkTransactionType.SALE)
            setTransactionClass(PaymentSdkTransactionClass.ECOM)
            setShippingData(getShippingDetails())
            setTokenise(PaymentSdkTokenise.MERCHANT_MANDATORY)
            if (cartId.isNotEmpty()) {
                setCartId(cartId)
            }
            showBillingInfo(true)
            showShippingInfo(false)
            forceShippingInfo(false)
            if (transactionTitle.isNotEmpty()) {
                setScreenTitle(transactionTitle)
            }
            hideCardScanner(false)
            linkBillingNameWithCard(false)
            setCardDiscount(getCardDiscounts())
            selectedApm?.let { setAlternativePaymentMethods(listOf(it)) }
        }
        return configBuilder.build()
    }

    /**
     * Generates billing details from input fields.
     * @return Configured PaymentSdkBillingDetails object.
     */
    private fun getBillingDetails(): PaymentSdkBillingDetails {
        return PaymentSdkBillingDetails(
            city = binding.etBillingCity.text.toString().trim().ifEmpty { "Dubai" },
            countryCode = binding.etBillingCountry.text.toString().trim().ifEmpty { "AE" },
            email = binding.etBillingEmail.text.toString().trim().ifEmpty { "testuser@example.com" },
            name = binding.etBillingName.text.toString().trim().ifEmpty { "Ali Ahmed" },
            phone = binding.etBillingPhone.text.toString().trim().ifEmpty { "+971501234567" },
            state = binding.etBillingState.text.toString().trim().ifEmpty { "Dubai" },
            addressLine = binding.etBillingAddress.text.toString().trim().ifEmpty { "1234 Test Street" },
            zip = binding.etBillingZip.text.toString().trim().ifEmpty { "00000" }
        )
    }

    /**
     * Generates shipping details from input fields.
     * @return Configured PaymentSdkShippingDetails object.
     */
    private fun getShippingDetails(): PaymentSdkShippingDetails {
        return PaymentSdkShippingDetails(
            city = binding.etShippingCity.text.toString().trim().ifEmpty { "Abu Dhabi" },
            countryCode = binding.etShippingCountry.text.toString().trim().ifEmpty { "AE" },
            email = binding.etShippingEmail.text.toString().trim().ifEmpty { "testrecipient@example.com" },
            name = binding.etShippingName.text.toString().trim().ifEmpty { "Ali Ahmed" },
            phone = binding.etShippingPhone.text.toString().trim().ifEmpty { "+971501234568" },
            state = binding.etShippingState.text.toString().trim().ifEmpty { "Abu Dhabi" },
            addressLine = binding.etShippingAddress.text.toString().trim().ifEmpty { "5678 Sample Avenue" },
            zip = binding.etShippingZip.text.toString().trim().ifEmpty { "00000" }
        )
    }

    /**
     * Provides card discount details.
     * @return List of PaymentSdkCardDiscount.
     */
    private fun getCardDiscounts(): List<PaymentSdkCardDiscount> {
        return listOf(
            PaymentSdkCardDiscount(
                discountCards = listOf("40001"),
                discountValue = 10.0,
                discountTitle = "● 10% discount - 40001",
                isPercentage = true
            )
        )
    }


    /**
     * Handles cancellation of the payment process.
     */
    override fun onCancel() {
        showToast("Payment cancelled.")
    }

    /**
     * Handles errors during the payment process.
     * @param error The error information.
     */
    override fun onError(error: PaymentSdkError) {
        showToast("Error: message: ${error.msg}, code: ${error.code}, trace: ${error.trace}")
    }

    /**
     * Receives the result of the transaction.
     * @param transactionResponseBody The transaction response.
     */
    override fun onResult(transactionResponseBody: TransactionResponseBody) {
        showToast("Payment result received.")
    }

    /**
     * Handles payment cancellation by the user.
     */
    override fun onPaymentCancel() {
        showToast("Payment cancelled by user.")
    }

    /**
     * Finalizes the payment process.
     * @param paymentSdkTransactionDetails Details of the completed transaction.
     */
    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        Log.d(TAG, "Payment success: ${paymentSdkTransactionDetails.isSuccess}")
        token = paymentSdkTransactionDetails.token
        transRef = paymentSdkTransactionDetails.transactionReference
        val message =
            paymentSdkTransactionDetails.paymentResult?.responseMessage ?: "Payment completed."
        showToast(message)
    }

    /**
     * Displays a toast message.
     * @param message The message to be displayed.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

data class MerchantRegion(
    val displayName: String,
    val countryCode: String
)
