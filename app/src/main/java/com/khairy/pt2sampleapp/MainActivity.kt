package com.khairy.pt2sampleapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.khairy.pt2sampleapp.databinding.ActivityMainBinding
import com.paytabs.pt2sdk.PaytabsActivity.Companion.startCardPayment
import com.paytabs.pt2sdk.PtConfigBuilder
import com.paytabs.pt2sdk.integrationmodels.*
import com.paytabs.pt2sdk.sharedclasses.interfaces.PaytabsPaymentInterface
import com.paytabs.samsungpay.sample.SamsungPayActivity
import fr.ganfra.materialspinner.MaterialSpinner

class MainActivity : AppCompatActivity(), PaytabsPaymentInterface {
    private var token: String? = null
    private var transRef: String? = null
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)
        b.version.text = "Version: " + BuildConfig.VERSION_NAME
        b.pay.setOnClickListener { _: View? ->
            startPaymentProcess()
        }
        findViewById<View>(R.id.sam_pay).setOnClickListener { v: View? ->
            SamsungPayActivity.start(this, generatePaytabsConfigurationDetails())
        }

        b.mid.setText("PROFILE_ID")
        b.serverKey.setText("SERVER_KEY")
        b.amount.setText("20")
        b.currency.setSelection(6)
        b.language.setSelection(1)
        b.tokeniseType.setSelection(1)

        b.street.setText("address street")
        b.city.setText("Dubai")
        b.state.setText("3510")
        b.country.setText("AE")
        b.postalCode.setText("54321")
        b.shippingEmail.setText("email1@domain.com")
        b.shippingName.setText("name1 last1")
        b.shippingPhone.setText("971555555555")

        b.streetBilling.setText("street")
        b.cityBilling.setText("Dubai")
        b.stateBilling.setText("3510")
        b.countryBilling.setText("AE")
        b.postalCodeBilling.setText("12345")
        b.billingEmail.setText("email1@domain.com")
        b.billingName.setText("first last")
        b.billingPhone.setText("0522222222")
        (findViewById<View>(R.id.screen_density) as TextView).text =
            "Screen Density " + resources.displayMetrics.density
    }


    private fun startPaymentProcess() {
        val configData = generatePaytabsConfigurationDetails()
        startCardPayment(this, configData, this)
    }

    private fun generatePaytabsConfigurationDetails(): PaytabsConfigurationDetails {
        val profileId = b.mid.text.toString()
        val serverKey = b.serverKey.text.toString()
        val locale = getLocale()
        val transactionTitle = b.transactionTitle.text.toString()
        val orderId = b.cartId.text.toString()
        val productName = b.productName.text.toString()
        val cartDesc = b.cartDesc.text.toString()
        val currency = b.currency.selectedItem.toString()
        val amount = b.amount.text?.toString()?.toDoubleOrNull() ?: 0.0
        val billingData = PaytabsBillingDetails(
            b.cityBilling.text.toString(), b.countryBilling.text.toString(),
            b.billingEmail.text.toString(), b.billingName.text.toString(),
            b.billingPhone.text.toString(), b.stateBilling.text.toString(),
            b.streetBilling.text.toString(), b.postalCodeBilling.text.toString()
        )
        val shippingData = PaytabsShippingDetails(
            b.city.text.toString(), b.country.text.toString(),
            b.shippingEmail.text.toString(), b.shippingName.text.toString(),
            b.shippingPhone.text.toString(), b.state.text.toString(),
            b.street.text.toString(), b.postalCode.text.toString()
        )
        val configData = PtConfigBuilder(profileId, serverKey,b.clientKey.text.toString() ,amount, currency)
            .setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setBillingData(billingData)
            .setMerchantCountryCode(b.merchantCountry.text.toString())
            .setShippingData(shippingData)
            .setCartId(orderId)
            .setTokenise(getTokeniseType())
            .setTokenisationData(token, transRef)
            .showBillingInfo(b.completeBillingInfo.isChecked)
            .showShippingInfo(b.completeShippingInfo.isChecked)
            .forceShippingInfo(b.forceShippingValidation.isChecked)
            .setScreenTitle(transactionTitle)

        return if (b.showMerchantLogo.isChecked) {
            configData.setMerchantIcon(resources.getDrawable(R.drawable.bt_ic_unionpay)).build()
        } else
            configData.build()
    }

    private fun getTokeniseType(): PaytabsTokenise {
       return when(b.tokeniseType.selectedItemPosition){
            1 -> PaytabsTokenise.NONE
            2 -> PaytabsTokenise.MERCHANT_MANDATORY
            3 -> PaytabsTokenise.USER_MANDATORY
            4 -> PaytabsTokenise.USER_OPTIONAL
            else->PaytabsTokenise.NONE
        }
    }

    private fun getLocale() =
        when (b.language.selectedItemPosition) {
            1 -> PaytabsLanguageCode.EN
            2 -> PaytabsLanguageCode.AR
            else -> PaytabsLanguageCode.DEFAULT
        }

    override fun onError(error: PaytabsError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentCancel() {
        Toast.makeText(this, "onPaymentCancel", Toast.LENGTH_SHORT).show()

    }

    override fun onPaymentFinish(payTabsTransactionDetails: PayTabsTransactionDetails) {
        token = payTabsTransactionDetails.token
        transRef = payTabsTransactionDetails.transactionReference
        Toast.makeText(
            this,
            "${payTabsTransactionDetails.paymentResult?.responseMessage ?: "PaymentFinish"}",
            Toast.LENGTH_SHORT
        ).show()
    }
}