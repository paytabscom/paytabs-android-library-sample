Paytabs android library sample
========

Install
--------
## Requirements

Library requires at minimum Java 7 or Android 5.1, targetSdk and compileSdk to be 31+.

You have to include the following dependencies:

```groovy

    implementation 'com.paytabs:payment-sdk:6.3.2'

```
## Known Coroutine issue
Please in case you faced "Duplicated class" dependency conflict with the coroutine api
add the following in your app gradle file.
  ```groovy
configurations.all {
        resolutionStrategy {
            exclude group: "org.jetbrains.kotlinx", module: "kotlinx-coroutines-debug"
        }
    }
```

Proguard
--------
If you are using ProGuard you might need to exclude the library classes.
```java
-keep public class com.payment.paymentsdk.**{*}
```

Pay now (in Kotlin)
--------

```kotlin
val profileId = "PROFILE_ID"
val serverKey = "SERVER_KEY"
val clientLey = "CLIENT_KEY"
val locale = PaymentSdkLanguageCode.EN or PaymentSdkLanguageCode.AR
val screenTitle = "Test SDK"
val cartId = "123456"
val cartDesc = "cart description"
val currency = "AED"
val amount = 20.0

val tokeniseType = PaymentSdkTokenise.NONE // tokenise is off
or PaymentSdkTokenise . USER_OPTIONAL // tokenise if optional as per user approval
        or PaymentSdkTokenise . USER_MANDATORY // tokenise is forced as per user approval
        or PaymentSdkTokenise . MERCHANT_MANDATORY // tokenise is forced without user approval

val transType = PaymentSdkTransactionType.SALE
or PaymentSdkTransactionType . AUTH


val tokenFormat = PaymentSdkTokenFormat.Hex32Format()
or PaymentSdkTokenFormat . NoneFormat ()
or PaymentSdkTokenFormat . AlphaNum20Format ()
or PaymentSdkTokenFormat . Digit22Format ()
or PaymentSdkTokenFormat . Digit16Format ()
or PaymentSdkTokenFormat . AlphaNum32Format ()

val billingData = PaymentSdkBillingDetails(
    "City",
    "2 digit iso Country code",
    "email1@domain.com",
    "name ",
    "phone", "state",
    "address street", "zip"
)

val shippingData = PaymentSdkShippingDetails(
    "City",
    "2 digit iso Country code",
    "email1@domain.com",
    "name ",
    "phone", "state",
    "address street", "zip"
)
val configData = PaymentSdkConfigBuilder(profileId, serverKey, clientKey, amount ?: 0.0, currency)
    .setCartDescription(cartDesc)
    .setLanguageCode(locale)
    .setMerchantIcon(resources.getDrawable(R.drawable.bt_ic_amex))
    .setBillingData(billingData)
    .setMerchantCountryCode("AE") // ISO alpha 2
    .setShippingData(shippingData)
    .setCartId(orderId)
    .setTransactionType(transType)
    .showBillingInfo(false)
    .showShippingInfo(true)
    .forceShippingInfo(true)
    .setScreenTitle(screenTitle)
    .build()
startCardPayment(this, configData, callback = this)
or
startSamsungPayment(this, configData, "samsungpay token", callback = this)

override fun onError(error: PaymentSdkError) {
    Log.d(TAG_PAYTABS, "onError: $error")
    Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
}

override fun onPaymentFinish(PaymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
    Toast.makeText(
        this,
        "${paymentSdkTransactionDetails.paymentResult?.responseMessage}",
        Toast.LENGTH_SHORT
    ).show()
    Log.d(TAG_PAYTABS, "onPaymentFinish: $paymentSdkTransactionDetails")

}

override fun onPaymentCancel() {
    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
    Log.d(TAG_PAYTABS, "onPaymentCancel:")

}


3.You are now ready to start payment
* For normal card payment use:

```

startCardPayment(context = this, ptConfigData = configData, callback = this)

```

* For recurring payment use:

```Kotlin
startTokenizedCardPayment(context = this,
ptConfigData = configData,
token= yourToken,
transactionRef = yourTransactionReference,
callback = this)
```

* For recurring payment with 3DS feature enabled (request CVV) use:

```Kotlin
start3DSecureTokenizedCardPayment(context = this,
                ptConfigData = configData,
                savedCardInfo = PaymentSDKSavedCardInfo("Masked card", "Visa or MC or card type"),
                token = token!!,
                callback = this)
```

* For recurring payment with the ability to let SDK save Cards on your behalf and show sheet of
  saved cards for user to choose from. use:

```Kotlin
startPaymentWithSavedCards(context = this, 
                ptConfigData = configData,
                support3DS = true,
                callback = this)
```

Pay now (in Java)
--------

```java
String profileId = "PROFILE_ID";
String serverKey = "SERVER_KEY";
String clientKey = "CLIENT_KEY";
PaymentSdkLanguageCode locale = PaymentSdkLanguageCode.EN;
String screenTitle = "Test SDK";
String cartId = "123456";
String cartDesc = "cart description";
String currency = "AED";
double amount = 20.0;
 
PaymentSdkTokenise tokeniseType = PaymentSdkTokenise.NONE; // tokenise is off
                               or PaymentSdkTokenise.USER_OPTIONAL // tokenise if optional as per user approval
                               or PaymentSdkTokenise.USER_MANDATORY // tokenise is forced as per user approval
                               or PaymentSdkTokenise.MERCHANT_MANDATORY // tokenise is forced without user approval

 PaymentSdkTransactionType transType = PaymentSdkTransactionType.SALE; 
                               or PaymentSdkTransactionType.AUTH 
                             
 
PaymentSdkTokenFormat tokenFormat = new PaymentSdkTokenFormat.Hex32Format();
                                   or new PaymentSdkTokenFormat.NoneFormat() 
                                   or new PaymentSdkTokenFormat.AlphaNum20Format() 
                                   or new PaymentSdkTokenFormat.Digit22Format()
                                   or new PaymentSdkTokenFormat.Digit16Format()
                                   or new PaymentSdkTokenFormat.AlphaNum32Format()

PaymentSdkBillingDetails billingData = new PaymentSdkBillingDetails(
        "City",
        "2 digit iso Country code",
         "email1@domain.com",
         "name ",
         "phone", "state",
         "address street", "zip"
         );
 
PaymentSdkShippingDetails shippingData = new PaymentSdkShippingDetails(
          "City",
          "2 digit iso Country code",
          "email1@domain.com",
          "name ",
          "phone", "state",
          "address street", "zip"
         );
PaymentSdkConfigurationDetails configData = new PaymentSdkConfigBuilder(profileId, serverKey, clientKey, amount, currency)
          .setCartDescription(cartDesc)
          .setLanguageCode(locale)
          .setBillingData(billingData)
          .setMerchantCountryCode("AE") // ISO alpha 2
          .setShippingData(shippingData)
          .setCartId(cartId)
          .setTransactionType(transType)
          .showBillingInfo(false)
          .showShippingInfo(true)
          .forceShippingInfo(true)
          .setScreenTitle(screenTitle)
          .build();
PaymentSdkActivity.startCardPayment(this, configData, this);

  @Override
   public void onError(@NotNull PaymentSdkError paymentSdkError) {
        
    }

  @Override
  public void onPaymentCancel() {

    }

  @Override
  public void onPaymentFinish(@NotNull PaymentSdkTransactionDetails paymentSdkTransactionDetails) {

    }
```

You are now ready to start payment

* For normal card payment use:

```Java
PaymentSdkActivity.startCardPayment(
        this,
        configData,
        this);
```

* For recurring payment use:

```Java
PaymentSdkActivity.startTokenizedCardPayment(
    this,
    configData,
    "Token",
    "TransactionRef",
    this);
```

* For recurring payment with 3DS feature enabled (request CVV) use:

```Java
PaymentSdkActivity.start3DSecureTokenizedCardPayment(
    this,
    configData,
    new PaymentSDKSavedCardInfo("Masked card", "Visa or MC or card type"),
    "Token",
    this);
```

* For recurring payment with the ability to let SDK save Cards on your behalf and show sheet of
  saved cards for user to choose from. use:

```Java
PaymentSdkActivity.startPaymentWithSavedCards(
    this,
    configData,
    true,
    this);
```

## Handling Transaction response

you can use paymentSdkTransactionDetails?.isSuccess to ensure a successful transaction. If the
transaction is not successful you should check for the corresponding failure code you will receive
the code in paymentSdkTransactionDetails?.paymentResult?.responseCode All codes can be found
in [Payment Response Codes](https://site.paytabs.com/en/pt2-documentation/testing/payment-response-codes/)

## Tokenisation

To enable tokenisation please follow the below instructions

```kotlin
 // to request token and transaction reference pass tokeniseType and Format
.setTokenise(PaymentSdkTokenise.MERCHANT_MANDATORY,PaymentSdkTokenFormat.Hex32Format()) 
 // you will receive token and reference after the first transaction       
 // to pass the token and transaction reference returned from sdk 
.setTokenisationData(token = "", transactionReference = "") 
```

## SamsungPay 
1. To enable pay with samsungpay you need first to integrate with SamsungPay api.
  Here how you can integrate with SamsungPay Api.
  [SamsungPay Integration Guide](https://github.com/paytabscom/paytabs-android-library-sample/blob/master/samsung_pay.md).

2. Pass the returned json token from samsung pay to the following  method.

```kotlin
startSamsungPayment(this, configData, "samsungpay token",callback=this)
```
## Overriding Resources:
 
 to override fonts 
 Please add your custom fonts files with these names
 
 payment_sdk_primary_font.tff && payment_sdk_secondary_font.tff
 
 to override strings, colors or dimens 
 add the resource you need to override from below resources with the value you want

## Theme
Use the following guide to customize the colors, font, and logo by configuring the theme and pass it to the payment configuration.

![UI guide](https://user-images.githubusercontent.com/13621658/109432213-d7981380-7a12-11eb-9224-c8fc12b0024d.jpg)

## Override strings
To override string you can find the keys with the default values here
[English](https://github.com/paytabscom/paytabs-android-library-sample/blob/master/res/strings.xml),
[Arabic](https://github.com/paytabscom/paytabs-android-library-sample/blob/master/res/strings-ar.xml)

````xml
<resourse>
  // to override colors
     <color name="payment_sdk_primary_color">#5C13DF</color>
     <color name="payment_sdk_secondary_color">#FFC107</color>
     <color name="payment_sdk_primary_font_color">#111112</color>
     <color name="payment_sdk_secondary_font_color">#6D6C70</color>
     <color name="payment_sdk_separators_color">#FFC107</color>
     <color name="payment_sdk_stroke_color">#673AB7</color>
     <color name="payment_sdk_button_text_color">#FFF</color>
     <color name="payment_sdk_title_text_color">#FFF</color>
     <color name="payment_sdk_button_background_color">#3F51B5</color>
     <color name="payment_sdk_background_color">#F9FAFD</color>
     <color name="payment_sdk_card_background_color">#F9FAFD</color>
     <color name="payment_sdk_status_bar_color">#FFC107</color>

    // to override dimens
     <dimen name="payment_sdk_primary_font_size">17sp</dimen>
     <dimen name="payment_sdk_secondary_font_size">15sp</dimen>
     <dimen name="payment_sdk_separator_thickness">1dp</dimen>
     <dimen name="payment_sdk_stroke_thickness">.5dp</dimen>
     <dimen name="payment_sdk_input_corner_radius">8dp</dimen>
     <dimen name="payment_sdk_button_corner_radius">8dp</dimen>
     
</resourse>
````
## Override drawables
 To override the back button icon you will need to add your own drawable file with the name below:
  ```xml
      payment_sdk_back_arrow.xml
  ```

## See the common issues from here
 [Common issues](https://github.com/paytabscom/paytabs-android-library-sample/blob/master/common_issues.md)


## License
See [License](https://github.com/paytabscom/paytabs-android-library-sample/blob/master/LICENSE)

## PayTabs

[Support][2] | [Terms of Use][3] | [Privacy Policy][4]


 [1]: https://dev.paytabs.com/docs/android/
 [2]: https://support.paytabs.com
 [3]: https://www.paytabs.com/en/terms-of-use/
 [4]: https://www.paytabs.com/en/privacy-policy/
