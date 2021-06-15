package com.khairy.pt2sampleapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.khairy.pt2sampleapp.databinding.ActivityMainBinding
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startAlternativePaymentMethods
import com.payment.paymentsdk.PaymentSdkActivity.Companion.startCardPayment
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.*
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.paytabs.samsungpay.sample.SamsungPayActivity

class MainActivity : AppCompatActivity(), CallbackPaymentInterface {
    private var token: String? = null
    private var transRef: String? = null
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)
        b.version.text = "Version: " + BuildConfig.VERSION_NAME
        b.pay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails()
            startCardPayment(this, configData, this)
        }
        b.apmPay.setOnClickListener {
            val configData = generatePaytabsConfigurationDetails()
            startAlternativePaymentMethods(this, configData, this)
        }
        findViewById<View>(R.id.sam_pay).setOnClickListener { v: View? ->
            SamsungPayActivity.start(this, generatePaytabsConfigurationDetails())
        }

        b.amount.setText("20")
        b.currency.setSelection(7)
        b.language.setSelection(1)
        b.tokeniseType.setSelection(1)
        b.mid.setText("#####")
        b.serverKey.setText("#####")
        b.clientKey.setText("#####")
        b.street.setText("address street")
        b.city.setText("Dubai")
        b.state.setText("3510")
        b.country.setText("AE")
        b.postalCode.setText("54321")
        b.shippingEmail.setText("email1@domain.com")
        b.shippingName.setText("name1 last1")
        b.shippingPhone.setText("1234")

        b.streetBilling.setText("street2")
        b.cityBilling.setText("Dubai")
        b.stateBilling.setText("3510")
        b.countryBilling.setText("AE")
        b.postalCodeBilling.setText("12345")
        b.billingEmail.setText("email1@domain.com")
        b.billingName.setText("first last")
        b.billingPhone.setText("45")
        (findViewById<View>(R.id.screen_density) as TextView).text =
            "Screen Density " + resources.displayMetrics.density
    }


    private fun generatePaytabsConfigurationDetails(): PaymentSdkConfigurationDetails {
        val profileId = b.mid.text.toString()
        val serverKey = b.serverKey.text.toString()
        val clientKey = b.clientKey.text.toString()
        val locale = getLocale()
        val transactionTitle = b.transactionTitle.text.toString()
        val orderId = b.cartId.text.toString()
        val productName = b.productName.text.toString()
        val cartDesc = b.cartDesc.text.toString()
        val currency = b.currency.selectedItem.toString()
        val amount = b.amount.text?.toString()?.toDoubleOrNull() ?: 0.0
        val billingData = PaymentSdkBillingDetails(
            b.cityBilling.text.toString(), b.countryBilling.text.toString(),
            b.billingEmail.text.toString(), b.billingName.text.toString(),
            b.billingPhone.text.toString(), b.stateBilling.text.toString(),
            b.streetBilling.text.toString(), b.postalCodeBilling.text.toString()
        )
        val shippingData = PaymentSdkShippingDetails(
            b.city.text.toString(), b.country.text.toString(),
            b.shippingEmail.text.toString(), b.shippingName.text.toString(),
            b.shippingPhone.text.toString(), b.state.text.toString(),
            b.street.text.toString(), b.postalCode.text.toString()
        )
        val configData = PaymentSdkConfigBuilder(profileId, serverKey, clientKey, amount, currency)
            .setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setBillingData(billingData)
            .setMerchantCountryCode(b.merchantCountry.text.toString())
            .setShippingData(shippingData)
            .setTransactionType(if (b.transactionType.selectedItemPosition <2) PaymentSdkTransactionType.SALE else PaymentSdkTransactionType.AUTH)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM)
            .setCartId(orderId)
            .setAlternativePaymentMethods(getSelectedApms())
            .setTokenise(getTokeniseType())
            .setTokenisationData(token, transRef)
            .showBillingInfo(b.completeBillingInfo.isChecked)
            .showShippingInfo(b.completeShippingInfo.isChecked)
            .forceShippingInfo(b.forceShippingValidation.isChecked)
            .setScreenTitle(transactionTitle)
        if (b.showMerchantLogo.isChecked) {
            configData.setMerchantIcon(resources.getDrawable(R.drawable.payment_sdk_adcb_logo))
        }


        return configData.build()
    }


    private fun getSelectedApms(): List<PaymentSdkApms> {
        val apms = mutableListOf<PaymentSdkApms>()
        addApmToList(apms, PaymentSdkApms.STC_PAY, b.apmStcPay.isChecked)
        addApmToList(apms, PaymentSdkApms.UNION_PAY, b.apmUnionpay.isChecked)
        addApmToList(apms, PaymentSdkApms.VALU, b.apmValu.isChecked)
        addApmToList(apms, PaymentSdkApms.KNET_DEBIT, b.apmKnetDebit.isChecked)
        addApmToList(apms, PaymentSdkApms.KNET_CREDIT, b.apmKnet.isChecked)
        addApmToList(apms, PaymentSdkApms.FAWRY, b.apmFawry.isChecked)
        addApmToList(apms, PaymentSdkApms.OMAN_NET, b.apmOmannet.isChecked)
        addApmToList(apms, PaymentSdkApms.MEEZA_QR, b.apmMeezaQr.isChecked)
        return apms
    }

    private fun addApmToList(
        list: MutableList<PaymentSdkApms>,
        apm: PaymentSdkApms,
        checked: Boolean
    ) {
        if (checked)
            list.add(apm)
    }

    private fun getTokeniseType(): PaymentSdkTokenise {
        return when (b.tokeniseType.selectedItemPosition) {
            1 -> PaymentSdkTokenise.NONE
            2 -> PaymentSdkTokenise.MERCHANT_MANDATORY
            3 -> PaymentSdkTokenise.USER_MANDATORY
            4 -> PaymentSdkTokenise.USER_OPTIONAL
            else -> PaymentSdkTokenise.NONE
        }
    }

    private fun getLocale() =
        when (b.language.selectedItemPosition) {
            1 -> PaymentSdkLanguageCode.EN
            2 -> PaymentSdkLanguageCode.AR
            else -> PaymentSdkLanguageCode.DEFAULT
        }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentCancel() {
        Toast.makeText(this, "onPaymentCancel", Toast.LENGTH_SHORT).show()

    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        token = paymentSdkTransactionDetails.token
        transRef = paymentSdkTransactionDetails.transactionReference
        Toast.makeText(
            this,
            "${paymentSdkTransactionDetails.paymentResult?.responseMessage ?: "PaymentFinish"}",
            Toast.LENGTH_SHORT
        ).show()
    }
}
