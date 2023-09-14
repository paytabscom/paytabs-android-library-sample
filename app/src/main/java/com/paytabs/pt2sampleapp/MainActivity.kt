package com.paytabs.pt2sampleapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startAlternativePaymentMethods
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.QuerySdkActivity
import com.payment.paymentsdk.integrationmodels.*
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackQueryInterface
import com.payment.paymentsdk.sharedclasses.model.response.TransactionResponseBody
import com.paytabs.pt2sampleapp.databinding.ActivityMainBinding
import com.samsung.android.sdk.samsungpay.v2.PartnerInfo
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.StatusListener
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountBoxControl
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountConstants
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet

class MainActivity : AppCompatActivity(), CallbackPaymentInterface, CallbackQueryInterface {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var token: String? = null
    private var transRef: String? = null
    private lateinit var b: ActivityMainBinding

    //TODO Add samServiceId
    private val samServiceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)
        b.pay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails()
            startCardPayment(
                this,
                configData,
                this
            )
//            Paymestart3DSecureTokenizedCardPayment(
//                this,
//                configData,
//                PaymentSDKSavedCardInfo("4111 11## #### 1111", "visa"),
//                "2C4652BF67A3EA33C6B590FE658078BD",
//                this
//            )

            /*
            **The rest of payment methods
            startTokenizedCardPayment(
                this,
                configData,
                "Token",
                "TransactionRef",
                this
            )
            *
            * */
//            startPaymentWithSavedCards(
//                this, configData, false, this
//            )

        }
        b.knetPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.KNET_CREDIT)
            startAlternativePaymentMethods(this, configData, this)
        }
        b.valuPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.VALU)
            startAlternativePaymentMethods(this, configData, this)
        }
        b.fawryPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.FAWRY)
            startAlternativePaymentMethods(this, configData, this)
        }
        b.checkSamPay.setOnClickListener { handleSam() }

        b.samPay.setOnClickListener {
            startInAppPayWithCustomSheet()
        }
//        b.samPay.setOnClickListener {
//            SamsungPayActivity.start(this, generatePaytabsConfigurationDetails())
//        }
        b.queryFunction.setOnClickListener {
            QuerySdkActivity.queryTransaction(
                this,
                PaymentSDKQueryConfiguration(
                    "ServerKey",
                    "ClientKey",
                    "Country Iso 2",
                    "Profile Id",
                    "Transaction Reference"
                ),
                this
            )

        }

    }


    private fun generatePaytabsConfigurationDetails(selectedApm: PaymentSdkApms? = null): PaymentSdkConfigurationDetails {
        val profileId = "*profile ID*"
        val serverKey = "*server key*"
        val clientKey = "*client key*"
        val locale = PaymentSdkLanguageCode.EN /*Or PaymentSdkLanguageCode.AR*/
        val transactionTitle = "Test SDK"
        val orderId = "123456"
        val cartDesc = "Cart description"
        val currency = "Currency"
        val amount = 20.0
        val merchantCountryCode = "Country Code ISO2"
        val billingData = PaymentSdkBillingDetails(
            "City",
            "Country Code ISO2",
            "email1@domain.com",
            "name name",
            "+966568595106",
            "121321",
            "address street",
            ""
        )
        val shippingData = PaymentSdkShippingDetails(
            "City",
            "Country Code ISO2",
            "test@test.com",
            "name1 last1",
            "+966568595106",
            "3510",
            "street2",
            ""
        )
        val configData = PaymentSdkConfigBuilder(
            profileId, serverKey, clientKey, amount, currency
        ).setCartDescription(cartDesc).setLanguageCode(locale)
            .setMerchantIcon(ContextCompat.getDrawable(this, R.drawable.payment_sdk_adcb_logo))
            .setBillingData(billingData).setMerchantCountryCode(merchantCountryCode)
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM).setShippingData(shippingData)
            .setTokenise(PaymentSdkTokenise.MERCHANT_MANDATORY) //Check other tokenizing types in PaymentSdkTokenise
            .setCartId(orderId).showBillingInfo(true).showShippingInfo(false)
            .forceShippingInfo(false).setScreenTitle(transactionTitle).hideCardScanner(false)
            .linkBillingNameWithCard(false)

        if (selectedApm != null) configData.setAlternativePaymentMethods(listOf(selectedApm))

        return configData.build()
    }

    override fun onCancel() {
        TODO("Not yet implemented")
    }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onResult(transactionResponseBody: TransactionResponseBody) {
        TODO("Not yet implemented")
    }

    override fun onPaymentCancel() {
        Toast.makeText(this, "onPaymentCancel", Toast.LENGTH_SHORT).show()

    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        Log.d(TAG, "Did payment success?: ${paymentSdkTransactionDetails.isSuccess}")
        token = paymentSdkTransactionDetails.token
        transRef = paymentSdkTransactionDetails.transactionReference
        Toast.makeText(
            this,
            paymentSdkTransactionDetails.paymentResult?.responseMessage ?: "PaymentFinish",
            Toast.LENGTH_SHORT
        ).show()
    }


    // SamsungPay ////////////////////////////////////


    private lateinit var partnerInfo: PartnerInfo
    private val transactionInfoListener: PaymentManager.CustomSheetTransactionInfoListener =
        object : PaymentManager.CustomSheetTransactionInfoListener {
            // This callback is received when the user changes card on the custom payment sheet in Samsung Pay.
            override fun onCardInfoUpdated(selectedCardInfo: CardInfo, customSheet: CustomSheet) {/*
                 * Called when the user changes card in Samsung Pay.
                 * Newly selected cardInfo is passed and partner app can update transaction amount based on new card (if needed).
                 * Call updateSheet() method. This is mandatory.
                 */
                paymentManager.updateSheet(customSheet)
            }

            override fun onSuccess(
                response: CustomSheetPaymentInfo, paymentCredential: String, extraPaymentData: Bundle
            ) {/*
                 * You will receive the payloads shown below in paymentCredential parameter
                 * The output paymentCredential structure varies depending on the PG you're using and the integration model (direct, indirect) with Samsung.
                 */
                PaymentSdkActivity.startSamsungPayment(
                    this@MainActivity, generatePaytabsConfigurationDetails(), paymentCredential, this@MainActivity
                )
            }

            // This callback is received when the online payment transaction has failed.
            override fun onFailure(errorCode: Int, errorData: Bundle?) {
                Toast.makeText(applicationContext, "onFailure() ", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startInAppPayWithCustomSheet() {
        if (samServiceId.isBlank()) {
            return
        }
        if (!::partnerInfo.isInitialized) {
            val bundle = Bundle()
            bundle.putString(SpaySdk.PARTNER_SERVICE_TYPE, SpaySdk.ServiceType.INAPP_PAYMENT.toString())
            partnerInfo = PartnerInfo(samServiceId, bundle)
            updateSamsungPayButton()
            return
        }
        paymentManager = PaymentManager(applicationContext, partnerInfo)
        paymentManager.startInAppPayWithCustomSheet(
            makeTransactionDetailsWithSheet(), transactionInfoListener
        )
    }

    private fun handleSam() {
        if (samServiceId.isBlank()) {
            Toast.makeText(this, "Please enter Samsung service id", Toast.LENGTH_SHORT).show()
            return
        }
        val bundle = Bundle()
        bundle.putString(SpaySdk.PARTNER_SERVICE_TYPE, SpaySdk.ServiceType.INAPP_PAYMENT.toString())
        partnerInfo = PartnerInfo(samServiceId, bundle)
        updateSamsungPayButton()
    }

    private fun doActivateSamsungPay(serviceType: String) {
        if (samServiceId.isBlank() == true) {
            Toast.makeText(this, "Please enter Samsung service id", Toast.LENGTH_SHORT).show()
            return
        }
        val bundle = Bundle()
        bundle.putString(SamsungPay.PARTNER_SERVICE_TYPE, serviceType)
        val partnerInfo = PartnerInfo(samServiceId, bundle)
        val samsungPay = SamsungPay(this, partnerInfo)
        samsungPay.activateSamsungPay()
    }

    private fun updateSamsungPayButton() {
        val samsungPay = SamsungPay(this, partnerInfo)
        samsungPay.getSamsungPayStatus(object : StatusListener {
            override fun onSuccess(status: Int, bundle: Bundle) {
                when (status) {
                    SpaySdk.SPAY_READY -> {
                        b.samPay.visibility = View.VISIBLE
                        b.samPay.isEnabled = true
                        Toast.makeText(this@MainActivity, "Samsung Pay supported", Toast.LENGTH_SHORT).show()
                        // Perform your operation.
                    }

                    SpaySdk.SPAY_NOT_READY -> {
                        // Samsung Pay is supported but not fully ready.

                        // If EXTRA_ERROR_REASON is ERROR_SPAY_APP_NEED_TO_UPDATE,
                        // Call goToUpdatePage().

                        // If EXTRA_ERROR_REASON is ERROR_SPAY_SETUP_NOT_COMPLETED,
                        // Call activateSamsungPay().
                        // If EXTRA_ERROR_REASON is ERROR_SPAY_SETUP_NOT_COMPLETED,
                        // Call activateSamsungPay().
                        val extraError = bundle.getInt(SamsungPay.EXTRA_ERROR_REASON)
                        if (extraError == SamsungPay.ERROR_SPAY_SETUP_NOT_COMPLETED) {
                            doActivateSamsungPay(SpaySdk.ServiceType.INAPP_PAYMENT.toString())
                        }

                        b.samPay.isEnabled = false
                        Toast.makeText(this@MainActivity, "Samsung Pay not supported", Toast.LENGTH_SHORT).show()
                    }

                    SpaySdk.SPAY_NOT_ALLOWED_TEMPORALLY -> {
                        // If EXTRA_ERROR_REASON is ERROR_SPAY_CONNECTED_WITH_EXTERNAL_DISPLAY,
                        // guide user to disconnect it.
                        b.samPay.isEnabled = false
                        Toast.makeText(this@MainActivity, "Samsung Pay not supported", Toast.LENGTH_SHORT).show()
                    }

                    SpaySdk.SPAY_NOT_SUPPORTED -> {
                        b.samPay.isEnabled = false
                        Toast.makeText(this@MainActivity, "Samsung Pay not supported", Toast.LENGTH_SHORT).show()
                    }

                    else -> b.samPay.visibility = View.INVISIBLE
                }
            }

            override fun onFail(errorCode: Int, bundle: Bundle) {
                b.samPay.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, "getSamsungPayStatus fail", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makeTransactionDetailsWithSheet(): CustomSheetPaymentInfo? {
        val brandList = brandList

        val extraPaymentInfo = Bundle()
        val customSheet = CustomSheet()

        customSheet.addControl(makeAmountControl())
        return CustomSheetPaymentInfo.Builder().setMerchantId("123456").setMerchantName("Sample Merchant")
            .setOrderNumber("AMZ007MAR")
            // If you want to enter address, please refer to the javaDoc :
            // reference/com/samsung/android/sdk/samsungpay/v2/payment/sheet/AddressControl.html
            .setAddressInPaymentSheet(CustomSheetPaymentInfo.AddressInPaymentSheet.DO_NOT_SHOW).setAllowedCardBrands(brandList)
            .setCardHolderNameEnabled(true).setRecurringEnabled(false).setCustomSheet(customSheet)
            .setExtraPaymentInfo(extraPaymentInfo).build()
    }

    private fun makeAmountControl(): AmountBoxControl {
        // Todo(Change Currency)
        val amountBoxControl = AmountBoxControl("100", "USD")
        //amountBoxControl.addItem(binding.productItemId.text.toString(), "Item", 990.99, "")
        //amountBoxControl.addItem(binding.productTaxId.text.toString(), "Tax", 5.0, "")
        amountBoxControl.setAmountTotal(
            "100".toDouble() ?: 0.0, AmountConstants.FORMAT_TOTAL_PRICE_ONLY
        )
        return amountBoxControl
    }
    private val brandList: ArrayList<SpaySdk.Brand>
        get() {
            val brandList = ArrayList<SpaySdk.Brand>()
            brandList.add(SpaySdk.Brand.VISA)
            brandList.add(SpaySdk.Brand.MASTERCARD)
            brandList.add(SpaySdk.Brand.AMERICANEXPRESS)
            brandList.add(SpaySdk.Brand.DISCOVER)

            return brandList
        }
    private lateinit var paymentManager: PaymentManager
}
