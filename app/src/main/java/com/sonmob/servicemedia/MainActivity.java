package com.sonmob.servicemedia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.sonmob.servicemedia.model.Song;
import com.sonmob.servicemedia.service.MyService;


public class MainActivity extends AppCompatActivity implements MyService.GetCurrentMediaPlayer {

    private Button mBtnPlay;
    private Button mBtnStop;
    private MyService myService;
    private SeekBar seekBar;
    private boolean isBind = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        Log.e("Tag", "onCreate main");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        mBtnPlay = findViewById(R.id.btn_play);
        mBtnStop = findViewById(R.id.btn_stop);
        seekBar = findViewById(R.id.sb_run);

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStartMusic();
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStopMusic();

            }
        });

    }

    private void clickStartMusic() {
        Song song = new Song("My Song", "Anny", R.drawable.callicon, R.raw.file_music);

        Intent intent = new Intent(this, MyService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("my_song", song);
        intent.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            Log.e("Tag", "startForegroundService");
        }
    }


    private void clickStopMusic() {
        Intent intent = new Intent(this, MyService.class);
        unbindService(connection);
        isBind = false;
        stopService(intent);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("Tag", "onServiceConnected");

            MyService.MySongBinder mySongBinder = (MyService.MySongBinder) service;
            myService = mySongBinder.getBoundService();
            myService.currentMediaPlayer = MainActivity.this;
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("Tag", "onServiceDisconnected");
            isBind = false;
        }
    };

    @Override
    public void updateSeekbar(int currentTime) {
        //Log.e("Tag", "updateSeekbar");
        seekBar.setProgress(currentTime);

    }

    @Override
    public void onPrepared(int duration) {
        Log.e("Tag", "onPrepared");
        seekBar.setMax(duration);
    }

    @Override
    protected void onDestroy() {
        Log.e("Tag", "onDestroy");

        super.onDestroy();
        unbindService(connection);
        isBind = false;
    }
}