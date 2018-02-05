package in.co.cioc.i2i;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Extras.PayUSdkDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;


public class PaymentActivity extends AppCompatActivity {

    public Button payBtn;
    TextView discountTxt , discountAmt , gstTxt , totalTxt, amountTxt;
    Integer amount = 500 , finalPay;
    EditText couponTxt;
    Boolean couponValid;

    LinearLayout errorCoupon, successCoupon;
    SharedPreferences sharedPreferences;

    private static AsyncHttpClient client = new AsyncHttpClient();
    Backend backend = new Backend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Payu.setInstance(this);

        errorCoupon = findViewById(R.id.errorCoupon);
        successCoupon = findViewById(R.id.successCoupon);

        errorCoupon.setVisibility(LinearLayout.GONE);
        successCoupon.setVisibility(LinearLayout.GONE);

        discountAmt = findViewById(R.id.discountAmt);
        discountTxt = findViewById(R.id.discountTxt);
        gstTxt = findViewById(R.id.gst);
        totalTxt = findViewById(R.id.total);
        amountTxt = findViewById(R.id.amount);

        payBtn = findViewById(R.id.button_pay_now);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalTxt.getText().toString().equals("0.0") && couponValid){
                    Intent i = new Intent(getApplicationContext(), UserDetails.class);
                    startActivity(i);
                }else {
                    navigateToBaseActivity(view);
                }
            }
        });

        couponValid = false;
        Button applyCoupnBtn = findViewById(R.id.couponApply);

        couponTxt = findViewById(R.id.couponCode);

        applyCoupnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                '/api/v1/checkCoupon/' , {coupon : this.couponForm.code}

                String coupon = couponTxt.getText().toString();

                RequestParams params = new RequestParams();
                params.put("coupon", coupon);

                client.post(backend.BASE_URL + "/api/v1/checkCoupon/", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);

                        couponValid = true;

                        try {
                            setDiscount(c.getInt("discount"));
                            successCoupon.setVisibility(LinearLayout.VISIBLE);
                            errorCoupon.setVisibility(LinearLayout.GONE);
                        }catch(JSONException e){

                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(PaymentActivity.this, "Invalid Coupon code", Toast.LENGTH_SHORT).show();
                        errorCoupon.setVisibility(LinearLayout.VISIBLE);
                        successCoupon.setVisibility(LinearLayout.GONE);
                    }

                });

            }
        });


        client.get(backend.BASE_URL + "/api/v1/getPaymentMatrix/", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                super.onSuccess(statusCode, headers, c);

                try {
                    amount = c.getInt("borrower");
                    amountTxt.setText(amount.toString());

                }catch (JSONException e) {

                }
            }

        });
        hideDiscount();


    }

    public void hideDiscount(){
        discountAmt.setVisibility(TextView.GONE);
        discountTxt.setVisibility(TextView.GONE);
        Double gst = amount*0.18;
        gstTxt.setText(gst.toString());
        Double total = gst + amount;
        totalTxt.setText(total.toString());
    }

    public void setDiscount(Integer percent){
        if (percent == null){
            return;
        }
        discountAmt.setVisibility(TextView.VISIBLE);
        discountTxt.setVisibility(TextView.VISIBLE);
        Double discnt = percent*amount*0.01;
        discountAmt.setText(discnt.toString());
        Double gst = (amount - discnt)*0.18;
        gstTxt.setText(gst.toString());

        Double total = gst + amount - discnt;
        totalTxt.setText(total.toString());
    }


    private String merchantKey = "r9pHRt";
    private String userCredentials;

    // These will hold all the payment parameters
    private PaymentParams mPaymentParams;

    // This sets the configuration
    private PayuConfig payuConfig;


    // Used when generating hash from SDK
    private PayUChecksum checksum;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();


                String response = data.getStringExtra("payu_response");
                sharedPreferences = getSharedPreferences("core", MODE_PRIVATE);

                String session_id = sharedPreferences.getString("session_id" , null);
                String csrf_token = sharedPreferences.getString("csrf_token" , null);
                RequestParams params = new RequestParams();

                try{
                    JSONObject responseJson = new JSONObject(response);
                    params.add("mihpayid" , Integer.toString(responseJson.getInt("id")));
                    params.add("mode" , responseJson.getString("mode"));
                    params.add("status" , responseJson.getString("status"));
                    params.add("unmappedstatus" , responseJson.getString("unmappedstatus"));
                    params.add("key" , responseJson.getString("key"));
                    params.add("txnid" ,responseJson.getString("txnid"));
                    params.add("amount" , responseJson.getString("amount"));
                    params.add("cardCategory" , responseJson.getString("cardCategory"));
                    params.add("discount" , responseJson.getString("discount"));
                    params.add("net_amount_debit" , responseJson.getString("amount"));
                    params.add("addedon" , responseJson.getString("addedon"));
                    params.add("productinfo" ,responseJson.getString("productinfo"));
                    params.add("firstname" , responseJson.getString("firstname"));
                    params.add("lastname" , "");
                    params.add("address1" , "");
                    params.add("address2" , "");
                    params.add("city" , "");
                    params.add("state" , "");
                    params.add("country" , "");
                    params.add("zipcode" , "");
                    params.add("email" , responseJson.getString("email"));
                    params.add("phone" , responseJson.getString("phone"));
                    params.add("udf1" , "");
                    params.add("udf2" , "");
                    params.add("udf3" , "");
                    params.add("udf4" , "");
                    params.add("udf5" , "");
                    params.add("udf6" , "");
                    params.add("udf7" , "");
                    params.add("udf8" , "");
                    params.add("udf9" , "");
                    params.add("udf10" , "");
                    params.add("hash" , responseJson.getString("hash"));
                    params.add("field1" , "");
                    params.add("field2" , "");
                    params.add("field3" , "");
                    params.add("field4" , "");
                    params.add("field5" , "");
                    params.add("field6" , "");
                    params.add("field7" , "");
                    params.add("field8" , "");
                    params.add("field9" , responseJson.getString("field9"));
                    params.add("payment_source" , responseJson.getString("payment_source"));
                    params.add("PG_TYPE" , responseJson.getString("PG_TYPE"));
                    params.add("bank_ref_num" , responseJson.getString("bank_ref_no"));
                    params.add("bankcode" , "");
                    params.add("error" , responseJson.getString("error_code"));
                    params.add("error_Message" , responseJson.getString("Error_Message"));
                    params.add("name_on_card" , responseJson.getString("name_on_card"));
                    params.add("cardnum" , responseJson.getString("card_no"));
                    params.add("cardhash" , "");

                }catch (JSONException e){

                }

                client.post(backend.BASE_URL + "/api/v1/makePayment/?csrf_token=" + csrf_token + "&session_id=" + session_id, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject c) {
                        super.onSuccess(statusCode, headers, c);
                        Intent i = new Intent(getApplicationContext(), UserDetails.class);
                        startActivity(i);

                        Toast.makeText(PaymentActivity.this, "in success", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Intent i = new Intent(getApplicationContext(), UserDetails.class);
                        startActivity(i);
                        Toast.makeText(PaymentActivity.this, "in failure", Toast.LENGTH_SHORT).show();

                    }

                });



    //                params.put("coupon", coupon);

    //                addedon	2018-02-04+16:10:31
    //                address1
    //                        address2
    //                amount	1.18
    //                bank_ref_num	803540020439
    //                bankcode	CC
    //                cardCategory	domestic
    //                cardhash	This+field+is+no+longer+supported+in+postback+params.
    //                        cardnum	489377XXXXXX5141
    //                        city
    //                country
    //                discount	0.00
    //                email	das@aaa.com
    //                error	E000
    //                error_Message	No+Error
    //                field1
    //                field2	071218
    //                field3
    //                        field4
    //                field5	131803579533952
    //                field6	140121803520434313
    //                field7
    //                        field8
    //                field9	Transaction+Completed+Successfully
    //                firstname	Fsdf
    //                hash	4dfafa1a2dbd5fdf761ae858990858cfa7eb1f43b2847578f28640084c7b182df8a0cfb19c0c9c8917e14703704c51687fcc1049bbea346343399173f3829911
    //                key	r9pHRt
    //                lastname
    //                mihpayid	6729537215
    //                mode	CC
    //                name_on_card	pradeep+yadav
    //                net_amount_debit	1.18
    //                payment_source	payu
    //                PG_TYPE	UBIFSSPG
    //                phone	3546543332
    //                productinfo	Registration+fees
    //                state
    //                status	success
    //                txnid	GOUKzDyQJ2ZbV8i0UVXV
    //                udf1
    //                        udf10
    //                udf2
    //                        udf3
    //                udf4
    //                        udf5
    //                udf6
    //                        udf7
    //                udf8
    //                        udf9
    //                unmappedstatus	captured
    //                zipcode

                //mihpayid=6729537215&mode=CC&status=success&unmappedstatus=captured&key=r9pHRt&txnid=GOUKzDyQJ2ZbV8i0UVXV&amount=1.18&cardCategory=domestic&discount=0.00&net_amount_debit=1.18&addedon=2018-02-04+16%3A10%3A31&productinfo=Registration+fees&firstname=Fsdf&lastname=&address1=&address2=&city=&state=&country=&zipcode=&email=das%40aaa.com&phone=3546543332&udf1=&udf2=&udf3=&udf4=&udf5=&udf6=&udf7=&udf8=&udf9=&udf10=&hash=4dfafa1a2dbd5fdf761ae858990858cfa7eb1f43b2847578f28640084c7b182df8a0cfb19c0c9c8917e14703704c51687fcc1049bbea346343399173f3829911&field1=&field2=071218&field3=&field4=&field5=131803579533952&field6=140121803520434313&field7=&field8=&field9=Transaction+Completed+Successfully&payment_source=payu&PG_TYPE=UBIFSSPG&bank_ref_num=803540020439&bankcode=CC&error=E000&error_Message=No+Error&name_on_card=pradeep+yadav&cardnum=489377XXXXXX5141&cardhash=This+field+is+no+longer+supported+in+postback+params.




            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * This method prepares all the payments params to be sent to PayuBaseActivity.java
     */
    public void navigateToBaseActivity(View view) {
        pay();
    }

    public void pay(){
        // merchantKey="";
        String amount = totalTxt.getText().toString();
        String email = "pkyisky@gmail.com";

        amount = "0.5";
        userCredentials = PayuConstants.DEFAULT;

        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        /**
         * For Test Environment, merchantKey = please contact mobile.integration@payu.in with your app name and registered email id

         */
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo("Borrower Registration Fees");
        mPaymentParams.setFirstName("firstname");
        mPaymentParams.setEmail("test@gmail.com");
        mPaymentParams.setPhone("9876543210");


        /*
        * Transaction Id should be kept unique for each transaction.
        * */
        mPaymentParams.setTxnId("" + System.currentTimeMillis());

        /**
         * Surl --> Success url is where the transaction response is posted by PayU on successful transaction
         * Furl --> Failre url is where the transaction response is posted by PayU on failed transaction
         */
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        mPaymentParams.setNotifyURL(mPaymentParams.getSurl());  //for lazy pay

        /*
         * udf1 to udf5 are options params where you can pass additional information related to transaction.
         * If you don't want to use it, then send them as empty string like, udf1=""
         * */
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");

        /**
         * These are used for store card feature. If you are not using it then user_credentials = "default"
         * user_credentials takes of the form like user_credentials = "merchant_key : user_id"
         * here merchant_key = your merchant key,
         * user_id = unique id related to user like, email, phone number, etc.
         * */
        mPaymentParams.setUserCredentials(userCredentials);

        //TODO Pass this param only if using offer key
        //mPaymentParams.setOfferKey("cardnumber@8370");

        //TODO Sets the payment environment in PayuConfig object
        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
        //TODO It is recommended to generate hash from server only. Keep your key and salt in server side hash generation code.
        /**
         * Below approach for generating hash is not recommended. However, this approach can be used to test in PRODUCTION_ENV
         * if your server side hash generation code is not completely setup. While going live this approach for hash generation
         * should not be used.
         * */
        String salt = "YVj4MmV1";
        generateHashFromSDK(mPaymentParams, salt);
    }
    /******************************
     * Client hash generation
     ***********************************/
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }


    /**
     * This method adds the Payuhashes and other required params to intent and launches the PayuBaseActivity.java
     *
     * @param payuHashes it contains all the hashes generated from merchant server
     */
    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH,PayuConstants.STORE_ONE_CLICK_HASH_NONE);

        //Lets fetch all the one click card tokens first
        startActivityForResult(intent , PayuConstants.PAYU_REQUEST_CODE);

    }





}
