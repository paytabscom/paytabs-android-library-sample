package com.paytabs.samsungpay.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface

import com.samsung.android.sdk.samsungpay.v2.PartnerInfo
import com.samsung.android.sdk.samsungpay.v2.SamsungPay
import com.samsung.android.sdk.samsungpay.v2.SpaySdk
import com.samsung.android.sdk.samsungpay.v2.payment.CardInfo
import com.samsung.android.sdk.samsungpay.v2.payment.CustomSheetPaymentInfo
import com.samsung.android.sdk.samsungpay.v2.payment.PaymentManager
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountBoxControl
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.AmountConstants
import com.samsung.android.sdk.samsungpay.v2.payment.sheet.CustomSheet
import java.util.*

class SamsungPayActivity : Activity(), PaymentManager.CustomSheetTransactionInfoListener,
    CallbackPaymentInterface {
    private val TAG = "SampleMerchantActivity"
    private val AMOUNT_CONTROL_ID = "amount_control_id"
    private var paymentManager: PaymentManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_samsung_pay)
        val brandList = ArrayList<SpaySdk.Brand>()
        brandList.add(SpaySdk.Brand.VISA)
        brandList.add(SpaySdk.Brand.MASTERCARD)
        val customSheet = CustomSheet()
        customSheet.addControl(makeAmountControl())
        val customSheetPaymentInfo = CustomSheetPaymentInfo.Builder()
            .setMerchantId("123456")
            .setMerchantName("Sample Merchant")
            .setOrderNumber("12345566")
            .setAllowedCardBrands(brandList)
            .setCardHolderNameEnabled(true)
            .setRecurringEnabled(false)
            .setCustomSheet(customSheet)
            .build()

        startSamsungPayment(customSheetPaymentInfo)

    }

    private fun startSamsungPayment(customSheetPaymentInfo: CustomSheetPaymentInfo?) {
        try {
            val bundle = Bundle()
            bundle.putString(
                SamsungPay.PARTNER_SERVICE_TYPE,
                SpaySdk.ServiceType.INAPP_PAYMENT.toString()
            )
            val partnerInfo = PartnerInfo(getString(R.string.gradle_product_id), bundle)
            paymentManager = PaymentManager(this, partnerInfo)
            paymentManager?.startInAppPayWithCustomSheet(customSheetPaymentInfo, this)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Toast.makeText(this@SamsungPayActivity, "Fail ${e.localizedMessage}", Toast.LENGTH_LONG)
                .show()
            finish()
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            e.printStackTrace()
            Toast.makeText(this@SamsungPayActivity, "Fail ${e.localizedMessage}", Toast.LENGTH_LONG)
                .show()
            Log.e(TAG, e.toString())
            finish()

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Toast.makeText(
                this@SamsungPayActivity,
                "Fail  ${e.localizedMessage}",
                Toast.LENGTH_LONG
            )
                .show()
            Log.e(TAG, e.toString())
            finish()

        }
    }

    override fun onCardInfoUpdated(cardInfo: CardInfo?, customSheet: CustomSheet) {
        val amountBoxControl = customSheet.getSheetControl(AMOUNT_CONTROL_ID) as AmountBoxControl
        amountBoxControl.setAmountTotal(1.0, AmountConstants.FORMAT_TOTAL_PRICE_ONLY) // grand total
        customSheet.updateControl(amountBoxControl)
        // Call updateSheet() with AmountBoxControl; mandatory.
        try {
            paymentManager!!.updateSheet(customSheet)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(
        customSheetPaymentInfo: CustomSheetPaymentInfo?,
        s: String?,
        bundle: Bundle?
    ) {
        Toast.makeText(this@SamsungPayActivity, "Success", Toast.LENGTH_LONG).show()
        Toast.makeText(this@SamsungPayActivity, s, Toast.LENGTH_LONG).show()
        bundle?.keySet()?.forEach {
            Log.d(TAG, it + " : " + bundle[it])
            Toast.makeText(this@SamsungPayActivity, it + " : " + bundle[it], Toast.LENGTH_LONG)
                .show()
        }
        Log.d(TAG, s!!)
        startPaymentProcess(s, customSheetPaymentInfo?.merchantName ?: "mo khairy")
    }

    override fun onFailure(i: Int, bundle: Bundle) {
        Toast.makeText(this@SamsungPayActivity, "Fail $i", Toast.LENGTH_LONG).show()
        Log.e(TAG, "$i ")
        for (key in bundle.keySet()) {
            Log.d(TAG, key + " : " + bundle[key])
            Toast.makeText(this@SamsungPayActivity, key + " : " + bundle[key], Toast.LENGTH_LONG)
                .show()
        }
        finish()

    }

    private fun makeAmountControl(): AmountBoxControl? {
        val amountBoxControl = AmountBoxControl(AMOUNT_CONTROL_ID, "AED")
        amountBoxControl.setAmountTotal(1.0, AmountConstants.FORMAT_TOTAL_PRICE_ONLY)
        return amountBoxControl
    }

    private fun startPaymentProcess(samPaytoken: String, customerName: String) {
        val ptConfig = intent.getParcelableExtra<PaymentSdkConfigurationDetails>("ptConfig")
        PaymentSdkActivity.startSamsungPayment(this, ptConfig!!, samPaytoken, this)
    }

    companion object {
        fun start(activity: Activity, configurationDetails: PaymentSdkConfigurationDetails) {
            val intent = Intent(activity, SamsungPayActivity::class.java).apply {
                putExtra("ptConfig", configurationDetails)
            }
            activity.startActivity(intent)
        }
    }

    override fun onError(error: PaymentSdkError) {
        Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onPaymentCancel() {
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show()
         finish()
    }

    override fun onPaymentFinish(payTabsTransactionDetails: PaymentSdkTransactionDetails) {
        Toast.makeText(
            this,
            "${payTabsTransactionDetails.paymentResult?.responseMessage}",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}