package com.paytabs.pt2sampleapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startAlternativePaymentMethods
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.*
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.paytabs.pt2sampleapp.databinding.ActivityMainBinding
import com.paytabs.samsungpay.sample.SamsungPayActivity

class MainActivity : AppCompatActivity(), CallbackPaymentInterface {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var token: String? = null
    private var transRef: String? = null
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)
        b.pay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails()
            startCardPayment(this, configData, this)
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
        b.samPay.setOnClickListener {
            SamsungPayActivity.start(this, generatePaytabsConfigurationDetails())
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
            "+966568595106", "121321",
            "address street", ""
        )
        val shippingData = PaymentSdkShippingDetails(
            "City",
            "Country Code ISO2",
            "test@test.com",
            "name1 last1",
            "+966568595106", "3510",
            "street2", ""
        )
        val configData = PaymentSdkConfigBuilder(
            profileId,
            serverKey,
            clientKey, amount, currency
        )
            .setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setMerchantIcon(ContextCompat.getDrawable(this, R.drawable.payment_sdk_adcb_logo))
            .setBillingData(billingData)
            .setMerchantCountryCode(merchantCountryCode)
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM)
            .setShippingData(shippingData)
            .setTokenise(PaymentSdkTokenise.USER_MANDATORY) //Check other tokenizing types in PaymentSdkTokenise
            .setCartId(orderId)
            .showBillingInfo(true)
            .showShippingInfo(false)
            .forceShippingInfo(false)
            .setScreenTitle(transactionTitle)
            .linkBillingNameWithCard(false)

        if (selectedApm != null)
            configData.setAlternativePaymentMethods(listOf(selectedApm))
        /*Check PaymentSdkApms for more payment options*/

        return configData.build()
    }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
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
