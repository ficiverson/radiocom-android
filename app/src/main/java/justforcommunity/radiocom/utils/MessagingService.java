/*
 *
 *  * Copyright © 2017 @ Pablo Grela
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package justforcommunity.radiocom.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Book;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.activities.Report;

import static android.content.ContentValues.TAG;
import static justforcommunity.radiocom.utils.GlobalValues.BOOK;
import static justforcommunity.radiocom.utils.GlobalValues.JSON_BOOK;
import static justforcommunity.radiocom.utils.GlobalValues.JSON_REPORT;
import static justforcommunity.radiocom.utils.GlobalValues.MEMBERS;
import static justforcommunity.radiocom.utils.GlobalValues.NOTIFICATION_COUNT;
import static justforcommunity.radiocom.utils.GlobalValues.REPORT;
import static justforcommunity.radiocom.utils.GlobalValues.addToken;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Intent intent = null;
            String type = remoteMessage.getData().get("type");
            switch (type) {

                case BOOK:
                    // Open book info
                    intent = new Intent(getApplicationContext(), Book.class);
                    intent.putExtra(JSON_BOOK, remoteMessage.getData().get("value"));
                    //packageName = Book.class.getPackage().toString() + ".Book";
                    break;

                case REPORT:
                    // Open report info
                    intent = new Intent(getApplicationContext(), Report.class);
                    intent.putExtra(JSON_REPORT, remoteMessage.getData().get("value"));
                    //packageName = Report.class.getPackage().toString() + ".Report";
                    break;

                case MEMBERS:
                    // Redirect to members
                    intent = new Intent(getApplicationContext(), Home.class);
                    intent.putExtra("servicio", true);
                    intent.putExtra("text", "");
                    intent.putExtra("title", "");
                    intent.putExtra("notificationSkip", true);
                    intent.putExtra("stopService", true);
                    intent.putExtra(MEMBERS, GlobalValues.membersAPI + addToken);
                    break;

                default:
                    // Open home
                    intent = new Intent(getApplicationContext(), Home.class);
                    intent.putExtra("servicio", true);
                    intent.putExtra("text", "");
                    intent.putExtra("title", "");
                    intent.putExtra("notificationSkip", true);
                    intent.putExtra("stopService", true);
                    break;
            }

            // Detect is app in foreground
            // TODO Maybe is not necessary
            if (isAppOnForeground(getApplicationContext())) {
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("update", true);
            }

            // Notification count, get and set in SharedPreferences
            SharedPreferences prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            int notificationCount = prefs.getInt(NOTIFICATION_COUNT, 0);
            if (notificationCount > 100) {
                edit.putInt(NOTIFICATION_COUNT, 0);
            } else {
                notificationCount++;
                edit.putInt(NOTIFICATION_COUNT, notificationCount);
            }
            edit.apply();

            // Create Notification,
            PendingIntent localPendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(myIcon)
                    .setContentIntent(localPendingIntent)
                    // .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setContentText(remoteMessage.getData().get("body"))
                    .setCategory(type)
                    // .setStyle(new NotificationCompat.BigTextStyle().bigText(this.text))
                    .setAutoCancel(true);
            //.addAction(R.drawable.ic_launcher, null, localPendingIntent);
            NotificationManager mNM = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNM.notify(notificationCount, notification.build());
        }
    }

    // Detect is app in foreground
    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}