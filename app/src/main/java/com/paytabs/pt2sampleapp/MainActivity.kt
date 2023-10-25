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
import com.payment.paymentsdk.integrationmodels.PaymentSDKQueryConfiguration
import com.payment.paymentsdk.integrationmodels.PaymentSdkApms
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTokenise
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionClass
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackQueryInterface
import com.payment.paymentsdk.sharedclasses.model.response.TransactionResponseBody
import com.paytabs.pt2sampleapp.databinding.ActivityMainBinding
import com.paytabs.samsungpay.sample.SamsungPayActivity

class MainActivity : AppCompatActivity(), CallbackPaymentInterface, CallbackQueryInterface {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var token: String? = null
    private var transRef: String? = null
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails()
            startCardPayment(
                this, configData, this
            )
        }
        binding.knetPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.KNET_CREDIT)
            startAlternativePaymentMethods(this, configData, this)
        }
        binding.valuPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.VALU)
            startAlternativePaymentMethods(this, configData, this)
        }
        binding.fawryPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails(PaymentSdkApms.FAWRY)
            startAlternativePaymentMethods(this, configData, this)
        }
        binding.samPay.setOnClickListener {
            SamsungPayActivity.start(this, generatePaytabsConfigurationDetails())
        }
        binding.queryFunction.setOnClickListener {
            QuerySdkActivity.queryTransaction(
                this, PaymentSDKQueryConfiguration(
                    serverKey = "ServerKey",
                    clientKey = "ClientKey",
                    merchantCountryCode = "Country Iso 2",
                    profileID = "Profile Id",
                    transactionReference = "Transaction Reference"
                ), this
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
            //Check other tokenizing types in PaymentSdkTokenize
            .setTokenise(PaymentSdkTokenise.MERCHANT_MANDATORY).setCartId(orderId)
            .showBillingInfo(true).showShippingInfo(false).forceShippingInfo(false)
            .setScreenTitle(transactionTitle).hideCardScanner(false).linkBillingNameWithCard(false)
        if (selectedApm != null) configData.setAlternativePaymentMethods(listOf(selectedApm))
        return configData.build()
    }

    override fun onCancel() {
        Toast.makeText(this, "onCancel", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onResult(transactionResponseBody: TransactionResponseBody) {
        Toast.makeText(this, "onResult", Toast.LENGTH_SHORT).show()
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
}
