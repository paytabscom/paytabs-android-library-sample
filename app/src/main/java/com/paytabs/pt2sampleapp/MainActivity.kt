package com.paytabs.pt2sampleapp

import android.os.Bundle
import android.util.Log
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
import com.paytabs.samsungpay.sample.SamsungPayActivity

/**
 * MainActivity handles various payment methods using PayTabs SDK.
 */
class MainActivity : AppCompatActivity(), CallbackPaymentInterface, CallbackQueryInterface {

    companion object {
        private const val TAG = "MainActivity"

        // Payment Configuration Constants
        private const val PROFILE_ID = "your_profile_id"
        private const val SERVER_KEY = "your_server_key"
        private const val CLIENT_KEY = "your_client_key"
        private const val MERCHANT_COUNTRY_CODE = "AE" // ISO2 country code for UAE
        private const val CURRENCY = "AED"
        private const val AMOUNT = 20.0
        private const val TRANSACTION_TITLE = "SDK sample"
        private const val CART_ID = "123456"
        private const val CART_DESCRIPTION = "Cart description"
        private val LANGUAGE_CODE = PaymentSdkLanguageCode.EN

        // Billing Details Constants
        private const val BILLING_CITY = "Dubai"
        private const val BILLING_COUNTRY_CODE = "AE" // ISO2 country code for UAE
        private const val BILLING_EMAIL = "testuser@example.com"
        private const val BILLING_NAME = "Ali Ahmed"
        private const val BILLING_PHONE = "+971501234567"
        private const val BILLING_STATE = "Dubai"
        private const val BILLING_ADDRESS = "1234 Test Street"
        private const val BILLING_ZIP = "00000"

        // Shipping Details Constants
        private const val SHIPPING_CITY = "Abu Dhabi"
        private const val SHIPPING_COUNTRY_CODE = "AE" // ISO2 country code for UAE
        private const val SHIPPING_EMAIL = "testrecipient@example.com"
        private const val SHIPPING_NAME = "Ali Ahmed"
        private const val SHIPPING_PHONE = "+971501234568"
        private const val SHIPPING_STATE = "Abu Dhabi"
        private const val SHIPPING_ADDRESS = "5678 Sample Avenue"
        private const val SHIPPING_ZIP = "00000"

        // Query Configuration Constants
        private const val QUERY_SERVER_KEY = "your_query_server_key"
        private const val QUERY_CLIENT_KEY = "your_query_client_key"
        private const val QUERY_MERCHANT_COUNTRY_CODE = "AE"
        private const val QUERY_PROFILE_ID = "your_query_profile_id"
        private const val TRANSACTION_REFERENCE = "your_transaction_reference"
    }

    private var token: String? = null
    private var transRef: String? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupClickListeners()
    }

    /**
     * Initializes view binding.
     */
    private fun initializeBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        startCardPayment(this, configData, this)
    }

    /**
     * Initiates alternative payment methods.
     * @param apm Selected alternative payment method.
     */
    private fun initiateAlternativePayment(apm: PaymentSdkApms) {
        val configData = generatePaymentConfiguration(apm)
        startAlternativePaymentMethods(this, configData, this)
    }

    /**
     * Initiates Samsung Pay.
     */
    private fun initiateSamsungPay() {
        val configData = generatePaymentConfiguration()
        SamsungPayActivity.start(this, configData)
    }

    /**
     * Queries a transaction.
     */
    private fun queryTransaction() {
        val queryConfig = generateQueryConfiguration()
        QuerySdkActivity.queryTransaction(this, queryConfig, this)
    }

    /**
     * Generates payment configuration details.
     * @param selectedApm Optional alternative payment method.
     * @return Configured PaymentSdkConfigurationDetails object.
     */
    private fun generatePaymentConfiguration(
        selectedApm: PaymentSdkApms? = null
    ): PaymentSdkConfigurationDetails {
        val configBuilder = PaymentSdkConfigBuilder(
            profileId = PROFILE_ID,
            serverKey = SERVER_KEY,
            clientKey = CLIENT_KEY,
            amount = AMOUNT,
            currencyCode = CURRENCY
        ).apply {
            setCartDescription(CART_DESCRIPTION)
            setLanguageCode(LANGUAGE_CODE)
            setMerchantIcon(
                ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.payment_sdk_adcb_logo
                )
            )
            setBillingData(getBillingDetails())
            setMerchantCountryCode(MERCHANT_COUNTRY_CODE)
            setTransactionType(PaymentSdkTransactionType.SALE)
            setTransactionClass(PaymentSdkTransactionClass.ECOM)
            setShippingData(getShippingDetails())
            setTokenise(PaymentSdkTokenise.MERCHANT_MANDATORY)
            setCartId(CART_ID)
            showBillingInfo(true)
            showShippingInfo(false)
            forceShippingInfo(false)
            setScreenTitle(TRANSACTION_TITLE)
            hideCardScanner(false)
            linkBillingNameWithCard(false)
            setCardDiscount(getCardDiscounts())
            selectedApm?.let { setAlternativePaymentMethods(listOf(it)) }
        }
        return configBuilder.build()
    }

    /**
     * Generates billing details.
     * @return Configured PaymentSdkBillingDetails object.
     */
    private fun getBillingDetails(): PaymentSdkBillingDetails {
        return PaymentSdkBillingDetails(
            city = BILLING_CITY,
            countryCode = BILLING_COUNTRY_CODE,
            email = BILLING_EMAIL,
            name = BILLING_NAME,
            phone = BILLING_PHONE,
            state = BILLING_STATE,
            addressLine = BILLING_ADDRESS,
            zip = BILLING_ZIP
        )
    }

    /**
     * Generates shipping details.
     * @return Configured PaymentSdkShippingDetails object.
     */
    private fun getShippingDetails(): PaymentSdkShippingDetails {
        return PaymentSdkShippingDetails(
            city = SHIPPING_CITY,
            countryCode = SHIPPING_COUNTRY_CODE,
            email = SHIPPING_EMAIL,
            name = SHIPPING_NAME,
            phone = SHIPPING_PHONE,
            state = SHIPPING_STATE,
            addressLine = SHIPPING_ADDRESS,
            zip = SHIPPING_ZIP
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
     * Generates query configuration for transaction querying.
     * @return Configured PaymentSDKQueryConfiguration object.
     */
    private fun generateQueryConfiguration(): PaymentSDKQueryConfiguration {
        return PaymentSDKQueryConfiguration(
            serverKey = QUERY_SERVER_KEY,
            clientKey = QUERY_CLIENT_KEY,
            merchantCountryCode = QUERY_MERCHANT_COUNTRY_CODE,
            profileID = QUERY_PROFILE_ID,
            transactionReference = TRANSACTION_REFERENCE
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
