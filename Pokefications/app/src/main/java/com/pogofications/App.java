package com.pogofications;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;
import com.onesignal.OneSignal;
import org.json.JSONObject;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by rmammadov on 7/19/16.
 */
public class App extends Application{

    private String push_url = null;
    private String notification_text = null;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Install CustomActivityOnCrash
        CustomActivityOnCrash.install(this);

        //OneSignal Push
        OneSignal.startInit(this)
                .setAutoPromptLocation(true)
                .setNotificationOpenedHandler(new NotificationHandler())
                .init();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    // This fires when a notification is opened by tapping on it or one is received while the app is running.
    private class NotificationHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            notification_text = message;
            try {
                //If the app is not on foreground, clicking the notification will start the app, and push_url will be used.
                if (!isActive) {
                    if (additionalData != null && additionalData.has("nburl")) {
                        push_url = additionalData.getString("nburl");
                    }
                } else { //If the app is in foreground, don't interup the current activities, but open webview in a new window.
                    Log.v("INFO", "Received notification while app was on foreground");
                    Log.d("NotificationReceived", "Received notification while app was on foreground");
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public synchronized String getMessage() {
        String message = notification_text;
        notification_text = null;
        return message;
    }

    public synchronized String getPushUrl() {
        String url = push_url;
        push_url = null;
        return url;
    }

    public synchronized void setMessage(String message) {
        this.notification_text = message;
    }

    public synchronized void setPushUrl(String url) {
        this.push_url = url;
    }
}
