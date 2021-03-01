Paytabs android library sample( PT2 Version)
========

Install
--------
## Requirements

Library requires at minimum Java 7 or Android 5.1.

You have to include the following dependencies:
```groovy
allprojects {
	repositories {
	    ...
     maven { url "http://pay.cards/maven" }
     maven {
       url  "https://paytabs.bintray.com/paytabs_pt2sdk"
     }
	}
}


```
```groovy

    implementation 'com.paytabs:pt2sdk:6.0.1-beta04'
   

```
Proguard
--------
If you are using ProGuard you might need to exclude the library classes.
```java
-keep class com.paytabs.pt2sdk.** { *; }
```

Pay now
--------
```kotlin
val profileId = "PROFILE_ID"
val serverKey = "SERVER_KEY"
val clientLey = "CLIENT_KEY"
val locale = PaytabsLanguageCode.EN or PaytabsLanguageCode.AR
val screenTitle = "Test SDK"
val cartId = "123456"
val cartDesc = "cart description"
val currency = "AED"
val amount = 20.0

val tokeniseType = PaytabsTokenise.NONE // tokenise is off
                   or PaytabsTokenise.USER_OPTIONAL // tokenise if optional as per user approval
                   or PaytabsTokenise.USER_MANDATORY // tokenise is forced as per user approval
                   or PaytabsTokenise.MERCHANT_MANDATORY // tokenise is forced without user approval

val tokenFormat =  PaytabsTokenFormat.Hex32Format() 
                   or PaytabsTokenFormat.NoneFormat() 
                   or PaytabsTokenFormat.AlphaNum20Format() 
                   or PaytabsTokenFormat.Digit22Format()
                   or PaytabsTokenFormat.Digit16Format()
                   or PaytabsTokenFormat.AlphaNum32Format()

val billingData = PaytabsBillingDetails(
 "City",
 "Country",
 "email1@domain.com",
 "name ",
 "phone", "state",
 "address street", "zip"
)

val shippingData = PaytabsShippingDetails(
 "City",
 "Country",
 "email1@domain.com",
 "name ",
 "phone", "state",
 "address street", "zip"
)
val configData = PtConfigBuilder(profileId, serverKey, clientKey, amount ?: 0.0, currency)
 .setCartDescription(cartDesc)
 .setLanguageCode(locale)
 .setMerchantIcon(resources.getDrawable(R.drawable.bt_ic_amex))
 .setBillingData(billingData)
 .setMerchantCountryCode("AE") // ISO alpha 2
 .setShippingData(shippingData)
 .setCartId(orderId)
 .showBillingInfo(false)
 .showShippingInfo(true)
 .forceShippingInfo(true)
 .setScreenTitle(screenTitle)
 .build()
startCardPayment(this, configData, callback=this)
or
startSamsungPayment(this, configData, "samsungpay token",callback=this)

override fun onError(error: PaytabsError) {
 Log.d(TAG_PAYTABS, "onError: $error")
 Toast.makeText(this, "${error.msg}", Toast.LENGTH_SHORT).show()
}

override fun onPaymentFinish(payTabsTransactionDetails: PayTabsTransactionDetails) {
 Toast.makeText(this, "${payTabsTransactionDetails.paymentResult?.responseMessage}", Toast.LENGTH_SHORT).show()
 Log.d(TAG_PAYTABS, "onPaymentFinish: $payTabsTransactionDetails")

}

override fun onPaymentCancel() {
 Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
 Log.d(TAG_PAYTABS, "onPaymentCancel:")

}

```
## Tokenisation
To enable tokenisation please follow the below instructions
```kotlin
 // to request token and transaction reference
.setTokenise(PaytabsTokenise.NONE,PaytabsTokenFormat.Hex32Format()) 
 // to pass the token and transaction reference returned from sdk
.setTokenisationData(token = "", transactionReference = "") 
```

## SamsungPay 
To enable pay with samsungpay you need to use this method
```kotlin
startSamsungPayment(this, configData, "samsungpay token",callback=this)
```
## Overriding Resources:
 
 to override fonts 
 Please add your custom fonts files with these names
 
 paytabs_primary_font.tff && paytabs_secondary_font.tff
 
 to override strings, colors or dimens 
 add the resource you need to override from below resources with the value you want

## Theme
Use the following guide to customize the colors, font, and logo by configuring the theme and pass it to the payment configuration.

![UI guide](https://github.com/paytabscom/paytabs-android-library-sample/tree/PT2/res/UIguide.jpg)

## Override strings
To override string you can find the keys with the default values here
![english]( https://github.com/paytabscom/paytabs-android-library-sample/blob/PT2/res/strings.xml)
![arabic](https://github.com/paytabscom/paytabs-android-library-sample/blob/PT2/res/strings-ar.xml)

````xml
<resourse>
  // to override colors
     <color name="paytabs_primary_color">#5C13DF</color>
     <color name="paytabs_secondary_color">#FFC107</color>
     <color name="paytabs_primary_font_color">#111112</color>
     <color name="paytabs_secondary_font_color">#6D6C70</color>
     <color name="paytabs_separators_color">#FFC107</color>
     <color name="paytabs_stroke_color">#673AB7</color>
     <color name="paytabs_button_text_color">#FFF</color>
     <color name="paytabs_title_text_color">#FFF</color>
     <color name="paytabs_button_background_color">#3F51B5</color>
     <color name="paytabs_background_color">#F9FAFD</color>
     <color name="paytabs_card_background_color">#F9FAFD</color> 
   
  // to override dimens
     <dimen name="paytabs_primary_font_size">17sp</dimen>
     <dimen name="paytabs_secondary_font_size">15sp</dimen>
     <dimen name="paytabs_separator_thickness">1dp</dimen>
     <dimen name="paytabs_stroke_thickness">.5dp</dimen>
     <dimen name="paytabs_input_corner_radius">8dp</dimen>
     <dimen name="paytabs_button_corner_radius">8dp</dimen>
     
</resourse>
````


Paytabs
--------
[Support][2] | [Terms of Use][3] | [Privacy Policy][4]




 [1]: https://dev.paytabs.com/docs/android/
 [2]: https://www.paytabs.com/en/support/
 [3]: https://www.paytabs.com/en/terms-of-use/
 [4]: https://www.paytabs.com/en/privacy-policy/
