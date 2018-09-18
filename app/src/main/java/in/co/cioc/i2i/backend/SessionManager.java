package in.co.cioc.i2i.backend;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ashish on 17/9/18.
 */

class SessionManager {
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private String csrfId = "csrftoken";
    private String sessionId = "sessionid";
    private String userName = "username";

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("registered_status", Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public String getCsrfId() {
        return sp.getString(csrfId, "");
    }

    public void setCsrfId(String csrf) {
        spe.putString(csrfId, csrf);
        spe.apply();
    }

    public String getSessionId() {
        return sp.getString(sessionId, "");
    }

    public void setSessionId(String session) {
        spe.putString(sessionId, session);
        spe.apply();
    }

    public String getUsername() {
        return sp.getString(userName, "");
    }

    public void setUsername(String username) {
        spe.putString(userName, username);
        spe.apply();
    }

    public void clearAll(){
        spe = sp.edit();
        spe.clear();
        spe.apply();
    }
}
