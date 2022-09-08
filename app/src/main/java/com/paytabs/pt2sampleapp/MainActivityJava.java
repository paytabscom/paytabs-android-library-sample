package com.paytabs.pt2sampleapp;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.paymentsdk.PaymentSdkActivity;
import com.payment.paymentsdk.PaymentSdkConfigBuilder;
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails;
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails;
import com.payment.paymentsdk.integrationmodels.PaymentSdkError;
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode;
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails;
import com.payment.paymentsdk.integrationmodels.PaymentSdkTokenFormat;
import com.payment.paymentsdk.integrationmodels.PaymentSdkTokenise;
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails;
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface;

import org.jetbrains.annotations.NotNull;

public class MainActivityJava extends AppCompatActivity implements CallbackPaymentInterface {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main);
        startPayment();
    }

    private void startPayment() {
        String profileId = "PROFILE_ID";
        String serverKey = "SERVER_KEY";
        String clientKey = "CLIENT_KEY";
        PaymentSdkLanguageCode locale = PaymentSdkLanguageCode.EN;
        String screenTitle = "Test SDK";
        String cartId = "123456";
        String cartDesc = "cart description";
        String currency = "AED";
        String merchantCountryCode = "Country code ISO2";
        double amount = 20.0;

        PaymentSdkTokenise tokeniseType = PaymentSdkTokenise.NONE;

        PaymentSdkTokenFormat tokenFormat = new PaymentSdkTokenFormat.Hex32Format();

        PaymentSdkBillingDetails billingData = new PaymentSdkBillingDetails(
                "City",
                "Country",
                "email1@domain.com",
                "name ",
                "phone", "state",
                "address street", "zip"
        );

        PaymentSdkShippingDetails shippingData = new PaymentSdkShippingDetails(
                "City",
                "Country",
                "email1@domain.com",
                "name ",
                "phone", "state",
                "address street", "zip"
        );
        PaymentSdkConfigurationDetails configData = new PaymentSdkConfigBuilder(profileId, serverKey, clientKey, amount, currency)
                .setCartDescription(cartDesc)
                .setLanguageCode(locale)
                .setBillingData(billingData)
                .setMerchantCountryCode(merchantCountryCode) // ISO alpha 2
                .setShippingData(shippingData)
                .setCartId(cartId)
                .showBillingInfo(false)
                .showShippingInfo(true)
                .forceShippingInfo(true)
                .setScreenTitle(screenTitle)
                .build();
        PaymentSdkActivity.startCardPayment(
                this,
                configData,
                this);
        /*
        **The rest of payment methods
        PaymentSdkActivity.startTokenizedCardPayment(
                this,
                configData,
                "Token",
                "TransactionRef",
                this);
        PaymentSdkActivity.start3DSecureTokenizedCardPayment(
                this,
                configData,
                new PaymentSDKSavedCardInfo("Masked card", "Visa or MC or card type"),
                "Token",
                this);
        PaymentSdkActivity.startPaymentWithSavedCards(
                this,
                configData,
                true,
                this);

         */
    }

    @Override
    public void onError(@NotNull PaymentSdkError paymentSdkError) {

    }

    @Override
    public void onPaymentCancel() {

    }

    @Override
    public void onPaymentFinish(@NotNull PaymentSdkTransactionDetails paymentSdkTransactionDetails) {

    }
}
