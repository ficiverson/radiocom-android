/*
 *
 *  * Copyright (C) 2016 @ Appeiros Mobile Development S.L.
 *  *
 *  * Developer Fernando Souto
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


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Podcast;

public class PodcastingService extends Service {
    private int NOTIFICATION = 1035;
    private String audio;
    private String title, text;
    private NotificationManager mNM;
    private MediaPlayer mp;
    private int total;
    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;

    private void showNotification(String paramString1, String paramString2) {
        String str = paramString2;
        Notification localNotification = new Notification(R.drawable.logo_nav_header, str, System.currentTimeMillis());

        Intent localIntent = new Intent(getApplicationContext(), Podcast.class);
        localIntent.putExtra("servicio", true);
        localIntent.putExtra("text", this.text);
        localIntent.putExtra("title", this.title);
        localIntent.putExtra("notificationSkip", true);
        localIntent.putExtra("stopService", true);

        PendingIntent localPendingIntent = PendingIntent.getActivity(this, 1, localIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        localNotification.flags = 32;


        //stop intent
        Intent pauseIntent = new Intent(getApplicationContext(), Podcast.class);
        pauseIntent.putExtra("stopService", true);
        PendingIntent pauseIntentPending = PendingIntent.getActivity(this, 3, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder noti = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.logo_nav_header)
                .setContentIntent(localPendingIntent)
                .setContentTitle(this.title)
                .setContentText(this.text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(this.text))
                .setAutoCancel(false)
                .addAction(R.drawable.stoppodcast, getResources().getString(R.string.drawer_item_podcasting_stop), pauseIntentPending);

        this.mNM.notify(this.NOTIFICATION, noti.build());


    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.mp = new MediaPlayer();
        this.mp.setLooping(false);

        prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();
    }

    @Override
    public void onDestroy() {
        if(mp!=null) {
            this.mp.stop();
            this.mp.reset();
            this.mp.release();
            this.mp = null;
        }
        if(mNM!=null) {
            this.mNM.cancelAll();
        }

        //reset preference value
        edit.putString("currentTitlePlaying","");
        edit.commit();
    }

    @Override
    public void onStart(Intent paramIntent, int paramInt) {

        if(paramIntent!=null) {
            this.audio = paramIntent.getStringExtra("audio");
            this.title = paramIntent.getStringExtra("title");
            this.text = paramIntent.getStringExtra("text");


            //set title
            edit.putString("currentTitlePlaying", title);
            edit.commit();

            this.mNM = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));


            new Thread(new Runnable() {
                public void run() {
                    //set up MediaPlayer
                    try {
                        mp.reset();
                        mp.setDataSource(audio);
                        mp.prepare();
                        mp.start();

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            mp.setOnPreparedListener(new OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    total = mp.getDuration();
                }
            });


            showNotification(text, title);
        }

    }

}