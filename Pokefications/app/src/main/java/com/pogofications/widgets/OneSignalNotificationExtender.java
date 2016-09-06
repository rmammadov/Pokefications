package com.pogofications.widgets;

import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationPayload;
import com.pogofications.constants.Config;

import java.math.BigInteger;

/**
 * Created by rmammadov on 7/26/16.
 */
public class OneSignalNotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationPayload notification) {
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Green on Android 5.0+ devices.
                return builder.setColor(new BigInteger("E12727", 16).intValue());
            }
        };

        OSNotificationDisplayedResult result = displayNotification(overrideSettings);

        //Sharing notification data intent
        String title = notification.title;
        String message = notification.message;

        Intent intent = new Intent(Config.NOTIFICATION_INTENT_FILTER_TAG);
        // add data
        intent.putExtra(Config.NOTIFICATION_TITLE_TAG, title);
        intent.putExtra(Config.NOTIFICATION_MESSAGE_TAG, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        return true;
    }
}
