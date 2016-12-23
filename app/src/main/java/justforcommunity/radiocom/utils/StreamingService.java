/*
 *
 *  * Copyright (C) 2016 @ Fernando Souto González
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;


import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.io.IOException;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Home;

public class StreamingService extends Service {
    private int NOTIFICATION = 1034;
    private String audio;
    private String title, text;
    private NotificationManager mNM;
    private int total;
    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private MediaCodecAudioTrackRenderer audioRenderer;
    private ExoPlayer exoPlayer;

    private void showNotification(String paramString1, String paramString2) {
        String str = paramString2;
        Notification localNotification = new Notification(R.drawable.logo_nav_header, str, System.currentTimeMillis());

        Intent localIntent = new Intent(getApplicationContext(), Home.class);
        localIntent.putExtra("servicio", true);
        localIntent.putExtra("text", this.text);
        localIntent.putExtra("title", this.title);
        localIntent.putExtra("notificationSkip", true);
        localIntent.putExtra("stopService", true);

        PendingIntent localPendingIntent = PendingIntent.getActivity(this, 1, localIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        localNotification.flags = 32;


        //stop intent
        Intent stopIntent = new Intent(getApplicationContext(), Home.class);
        stopIntent.putExtra("stopService", false);
        PendingIntent stopIntentPending = PendingIntent.getActivity(this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //stop intent
        Intent pauseIntent = new Intent(getApplicationContext(), Home.class);
        pauseIntent.putExtra("stopService", true);
        PendingIntent pauseIntentPending = PendingIntent.getActivity(this, 3, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        int smallIconId = 0;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smallIconId = R.drawable.notification_transparent;
        } else {
            smallIconId = R.drawable.notification;
        }

        NotificationCompat.Builder noti = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(smallIconId)
                .setLargeIcon(myIcon)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(localPendingIntent)
                .setContentTitle(this.title)
                .setContentText(this.text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(this.text))
                .setAutoCancel(false)
                .addAction(R.drawable.stoppodcast, getResources().getString(R.string.drawer_item_streaming_stop), pauseIntentPending)
                .addAction(R.drawable.exitpodcast, getResources().getString(R.string.drawer_item_streaming_close), stopIntentPending);

        this.mNM.notify(this.NOTIFICATION, noti.build());


    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onCreate() {
        exoPlayer = ExoPlayer.Factory.newInstance(1);

        prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();
    }

    @Override
    public void onDestroy() {
        destroyMediaPlayerAndNotification();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        destroyMediaPlayerAndNotification();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onStart(Intent paramIntent, int paramInt) {

        //stop the streaming
        Intent i = new Intent(StreamingService.this, PodcastingService.class);
        stopService(i);

        if(paramIntent!=null) {
            this.audio = paramIntent.getStringExtra("audio");
            this.title = paramIntent.getStringExtra("title");
            this.text = paramIntent.getStringExtra("text");

            this.mNM = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            try {
                // String with the url of the radio you want to play
                Uri radioUri = Uri.parse(audio);
                // Settings for exoPlayer
                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                String userAgent = Util.getUserAgent(this, "ComRadioPlayer");
                DataSource dataSource = new DefaultUriDataSource(this, null, userAgent);
                ExtractorSampleSource sampleSource = new ExtractorSampleSource(radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
                audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
                // Prepare ExoPlayer
                exoPlayer.prepare(audioRenderer);
                exoPlayer.setPlayWhenReady(true);

                showNotification(text, title);
            }
            catch (Exception e){

            }
        }

    }


    private void destroyMediaPlayerAndNotification() {
        if(exoPlayer!=null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer=null;
        }
        if(mNM!=null) {
            this.mNM.cancel(1034);
        }

        edit.putBoolean("isMediaPlaying", false);
        edit.commit();
    }

}