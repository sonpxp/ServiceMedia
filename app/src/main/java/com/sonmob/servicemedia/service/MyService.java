 package com.sonmob.servicemedia.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.sonmob.servicemedia.MainActivity;
import com.sonmob.servicemedia.R;
import com.sonmob.servicemedia.model.Song;

import static com.sonmob.servicemedia.notification.MyApplication.CHANNEL_ID;

public class MyService extends Service {

    private MediaPlayer mMediaPlayer;
    public MySongBinder songBinder = new MySongBinder();
    private final Handler handler = new Handler();

    public GetCurrentMediaPlayer currentMediaPlayer;

    public interface GetCurrentMediaPlayer {
        void updateSeekbar(int currentTime);

        void onPrepared(int duration);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return songBinder;
    }

    public class MySongBinder extends Binder {
        public MyService getBoundService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Tag", "onCreate myService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Tag", "onStartCommand");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get("my_song");
            if (song != null) {
                startMusic(song);
                sendNotification(song);
            }
        }
        return START_NOT_STICKY;
    }

    private void startMusic(Song song) {
        Log.e("Tag", "startMusic");

        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (currentMediaPlayer != null) {
                    currentMediaPlayer.onPrepared(mp.getDuration());
                }
            }
        });
        mMediaPlayer.start();
        handler.postDelayed(mRunnable, 1000);
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            currentMediaPlayer.updateSeekbar(mMediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 1000);
        }
    };

    private void sendNotification(Song song) {
        //navigate to the activity
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //converter image large bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImage());

        //custom notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.item_song);
        remoteViews.setTextViewText(R.id.tv_title_song, song.getTitle());
        remoteViews.setTextViewText(R.id.tv_single_song, song.getSingle());
        remoteViews.setImageViewBitmap(R.id.img_song, bitmap);
        remoteViews.setImageViewResource(R.id.img_start_pause, R.drawable.ic_pause_24);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_important)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
