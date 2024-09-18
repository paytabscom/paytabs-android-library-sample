package com.paytabs.pt2sampleapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.payment.paymentsdk.PaymentSdkActivity;
import com.payment.paymentsdk.PaymentSdkConfigBuilder;
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails;
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails;
import com.payment.paymentsdk.integrationmodels.PaymentSdkError;
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode;
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails;
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails;
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface;

import org.jetbrains.annotations.NotNull;

/**
 * MainActivityJava handles payment processing using the PayTabs SDK.
 */
public class MainActivityJava extends AppCompatActivity implements CallbackPaymentInterface {

    // Constants for Payment Configuration
    private static final String PROFILE_ID = "your_profile_id";
    private static final String SERVER_KEY = "your_server_key";
    private static final String CLIENT_KEY = "your_client_key";
    private static final String MERCHANT_COUNTRY_CODE = "AE"; // ISO2 country code for UAE
    private static final String CURRENCY = "AED";
    private static final double AMOUNT = 20.0;
    private static final PaymentSdkLanguageCode LANGUAGE_CODE = PaymentSdkLanguageCode.EN;
    private static final String SCREEN_TITLE = "Test SDK";
    private static final String CART_ID = "123456";
    private static final String CART_DESCRIPTION = "Cart description";

    // Constants for Billing Details
    private static final String BILLING_CITY = "Dubai";
    private static final String BILLING_COUNTRY_CODE = "AE";
    private static final String BILLING_EMAIL = "testuser@example.com";
    private static final String BILLING_NAME = "Ali Ahmed";
    private static final String BILLING_PHONE = "+971501234567";
    private static final String BILLING_STATE = "Dubai";
    private static final String BILLING_ADDRESS = "1234 Test Street";
    private static final String BILLING_ZIP = "00000";

    // Constants for Shipping Details
    private static final String SHIPPING_CITY = "Abu Dhabi";
    private static final String SHIPPING_COUNTRY_CODE = "AE";
    private static final String SHIPPING_EMAIL = "testrecipient@example.com";
    private static final String SHIPPING_NAME = "Ahmed Khan";
    private static final String SHIPPING_PHONE = "Ali Ahmed";
    private static final String SHIPPING_STATE = "Abu Dhabi";
    private static final String SHIPPING_ADDRESS = "5678 Sample Avenue";
    private static final String SHIPPING_ZIP = "00000";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the UI
        setContentView(R.layout.activity_main);
        // Start the payment process
        startPayment();
    }

    /**
     * Initiates the payment process by configuring payment details and starting the payment activity.
     */
    private void startPayment() {
        PaymentSdkBillingDetails billingData = getBillingDetails();
        PaymentSdkShippingDetails shippingData = getShippingDetails();

        PaymentSdkConfigurationDetails configData = new PaymentSdkConfigBuilder(PROFILE_ID, SERVER_KEY, CLIENT_KEY, AMOUNT, CURRENCY).setCartDescription(CART_DESCRIPTION).setLanguageCode(LANGUAGE_CODE).setBillingData(billingData).setMerchantCountryCode(MERCHANT_COUNTRY_CODE).setShippingData(shippingData).setCartId(CART_ID).showBillingInfo(true).showShippingInfo(true).forceShippingInfo(true).setScreenTitle(SCREEN_TITLE).build();

        PaymentSdkActivity.startCardPayment(this, configData, this);
    }

    /**
     * Constructs the billing details for the payment.
     *
     * @return A configured PaymentSdkBillingDetails object.
     */
    private PaymentSdkBillingDetails getBillingDetails() {
        return new PaymentSdkBillingDetails(BILLING_CITY, BILLING_COUNTRY_CODE, BILLING_EMAIL, BILLING_NAME, BILLING_PHONE, BILLING_STATE, BILLING_ADDRESS, BILLING_ZIP);
    }

    /**
     * Constructs the shipping details for the payment.
     *
     * @return A configured PaymentSdkShippingDetails object.
     */
    private PaymentSdkShippingDetails getShippingDetails() {
        return new PaymentSdkShippingDetails(SHIPPING_CITY, SHIPPING_COUNTRY_CODE, SHIPPING_EMAIL, SHIPPING_NAME, SHIPPING_PHONE, SHIPPING_STATE, SHIPPING_ADDRESS, SHIPPING_ZIP);
    }

    /**
     * Callback method invoked when an error occurs during the payment process.
     *
     * @param paymentSdkError The error details.
     */
    @Override
    public void onError(@NotNull PaymentSdkError paymentSdkError) {
        // Handle the error
        // You can display a message or log the error
        showToast("Error: " + paymentSdkError.getMsg());
    }

    /**
     * Callback method invoked when the payment is canceled by the user.
     */
    @Override
    public void onPaymentCancel() {
        // Handle payment cancellation
        showToast("Payment canceled by user.");
    }

    /**
     * Callback method invoked when the payment is completed successfully.
     *
     * @param paymentSdkTransactionDetails The transaction details.
     */
    @Override
    public void onPaymentFinish(@NotNull PaymentSdkTransactionDetails paymentSdkTransactionDetails) {
        // Handle the payment result
        String message = paymentSdkTransactionDetails.getPaymentResult() != null ? paymentSdkTransactionDetails.getPaymentResult().getResponseMessage() : "Payment completed.";
        showToast(message);
    }

    /**
     * Utility method to display a toast message.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
