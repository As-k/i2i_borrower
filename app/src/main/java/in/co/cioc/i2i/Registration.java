package in.co.cioc.i2i;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class Registration {


    RequestParams requestParams = new RequestParams();;
    private static AsyncHttpClient client = new AsyncHttpClient();

    String jsonResponse;

    // basic
    String fName;
    String mName;
    String lName;
    String email;
    String mobile;
    String pan;

    String typ;


    Registration(String t){
        typ = t;


    }


    public Boolean checkPan(String pan ){


        return false;
    }

}
