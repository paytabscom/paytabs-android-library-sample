Paytabs android library sample( PT2 Version)
========
![Paytabs-android-library-v4.0.1](https://img.shields.io/badge/Paytabs/android/library-v4.0.1-green.svg)

For more information please see [the website][1].


Download
--------

Download [the latest AAR](sdk/paytabs_sdk-v5.0.3.aar):

Read the documentation to know how to integrate your application with the library
[documentation](https://dev.paytabs.com/docs/android/)

```groovy
implementation project(':paytabs_sdk-v5.0.3')
```

Library requires at minimum Java 7 or Android 4.0.

You have to include the following dependencies:
```groovy
allprojects {
	repositories {
	    ...
	    maven { url 'https://jitpack.io' }
	}
}
```
```groovy
implementation 'com.android.support:design:28.0.0'
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.squareup.retrofit2:retrofit:2.4.0'
implementation 'com.google.code.gson:gson:2.8.5'
implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
implementation 'com.github.dbachelder:CreditCardEntry:1.4.9'
```
Proguard
--------
If you are using ProGuard you might need to exclude library classes till we update with R8 proguard rules
```java
-keep class com.paytabs.paytabs_sdk.** { *; }
```

Pay now
--------
```java
Intent in = new Intent(getApplicationContext(), PayTabActivity.class);
in.putExtra(PaymentParams.MERCHANT_EMAIL, "merchant-email@example.com"); //this a demo account for testing the sdk
in.putExtra(PaymentParams.SECRET_KEY,"secret key");//Add your Secret Key Here
in.putExtra(PaymentParams.LANGUAGE,PaymentParams.ENGLISH);
in.putExtra(PaymentParams.TRANSACTION_TITLE, "Test Paytabs android library");
in.putExtra(PaymentParams.AMOUNT, 5.0);

in.putExtra(PaymentParams.CURRENCY_CODE, "BHD");
in.putExtra(PaymentParams.CUSTOMER_PHONE_NUMBER, "009733");
in.putExtra(PaymentParams.CUSTOMER_EMAIL, "customer-email@example.com");
in.putExtra(PaymentParams.ORDER_ID, "123456");
in.putExtra(PaymentParams.PRODUCT_NAME, "Product 1, Product 2");

//Billing Address
in.putExtra(PaymentParams.ADDRESS_BILLING, "Flat 1,Building 123, Road 2345");
in.putExtra(PaymentParams.CITY_BILLING, "Manama");
in.putExtra(PaymentParams.STATE_BILLING, "Manama");
in.putExtra(PaymentParams.COUNTRY_BILLING, "BHR");
in.putExtra(PaymentParams.POSTAL_CODE_BILLING, "00973"); //Put Country Phone code if Postal code not available '00973'

//Shipping Address
in.putExtra(PaymentParams.ADDRESS_SHIPPING, "Flat 1,Building 123, Road 2345");
in.putExtra(PaymentParams.CITY_SHIPPING, "Manama");
in.putExtra(PaymentParams.STATE_SHIPPING, "Manama");
in.putExtra(PaymentParams.COUNTRY_SHIPPING, "BHR");
in.putExtra(PaymentParams.POSTAL_CODE_SHIPPING, "00973"); //Put Country Phone code if Postal code not available '00973'

//Payment Page Style
in.putExtra(PaymentParams.PAY_BUTTON_COLOR, "#2474bc");

//Tokenization
in.putExtra(PaymentParams.IS_TOKENIZATION, false);
//PreAuth
in.putExtra(PaymentParams.IS_PREAUTH, false);

//SamsungPay Feature ->
// merchant must integrate with samsung pay and pass the returned success token and merchant name to paytabs sdk.
 in.putExtra(PaymentParams.SAMSUNG_PAY_JSON, samPaytoken)
 in.putExtra(PaymentParams.CUSTOMER_NAME, merchantName)

//Region Based url->
//merchant should pass the region using one of those values
 PaymentParams.EGYPT_REGION
 PaymentParams.OMAN_REGION
 PaymentParams.UAE_REGION
 PaymentParams.SAUDI_REGION
 PaymentParams.JORDAN_REGION
 PaymentParams.DEMO
 PaymentParams.GLOBAL_REGION
// example:
 in.putExtra(PaymentParams.REGION_ENDPOINT,PaymentParams.UAE_REGION);
 
startActivityForResult(in, PaymentParams.PAYMENT_REQUEST_CODE);
```

Paytabs
--------
[Support][2] | [Terms of Use][3] | [Privacy Policy][4]




 [1]: https://dev.paytabs.com/docs/android/
 [2]: https://www.paytabs.com/en/support/
 [3]: https://www.paytabs.com/en/terms-of-use/
 [4]: https://www.paytabs.com/en/privacy-policy/
