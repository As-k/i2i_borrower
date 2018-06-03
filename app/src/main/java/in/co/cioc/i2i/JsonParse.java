package in.co.cioc.i2i;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class JsonParse {
    Backend backend = new Backend();
    String type;
    public JsonParse(String typ){
        type = typ;
    }
    public List<SuggestGetSet> getParseJsonWCF(String sName) {
        final List<SuggestGetSet> ListData = new ArrayList<SuggestGetSet>();
        try {
            final String temp = sName.replace(" ", "%20");


            SyncHttpClient syncHttpClient = new SyncHttpClient(true, 80, 443);

            syncHttpClient.get(backend.BASE_URL + "/api/v1/" + type + "/" + temp, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
//                            URL js = new URL(backend.BASE_URL + "/api/v1/"+ type +"/"+temp);
//                            URLConnection jc = js.openConnection();
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
//                            String line = reader.readLine();
//                            JSONArray jsonArray = new JSONArray(line);
                            for (int i = 0; i < response.length(); i++) {
                                String r = null;
                                try {
                                    r = response.getString(i);
                                    ListData.add(new SuggestGetSet(Integer.toString(i), r));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }


                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }
                    }
            );

//            URL js = new URL(backend.BASE_URL + "/api/v1/"+ type +"/"+temp);
//                            URLConnection jc = js.openConnection();
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
//                            String line = reader.readLine();
//                            JSONArray jsonArray = new JSONArray(line);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String r = jsonArray.getString(i);
//                ListData.add(new SuggestGetSet(Integer.toString(i), r));
//            }

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return ListData;
    }

}
