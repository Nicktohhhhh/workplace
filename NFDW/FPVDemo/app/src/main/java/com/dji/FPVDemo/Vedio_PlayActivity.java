package com.dji.FPVDemo;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.dji.FPVDemo.utils.ChangeOrientationHandler;
import com.dji.FPVDemo.utils.OrientationSensorListener;

public class Vedio_PlayActivity extends AppCompatActivity {

    VideoView vide_play;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler;
    private OrientationSensorListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.layout);

        vide_play=(VideoView)findViewById(R.id.vedio_play);
        String uri="android.resource://"+getPackageName()+"/"+R.raw.video_play;
        vide_play.setVideoURI(Uri.parse(uri));
        vide_play.start();

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        handler = new ChangeOrientationHandler(this);
        listener = new OrientationSensorListener(handler);
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent myIntent = new Intent();
            myIntent = new Intent(this, ConnectionActivity.class);
            startActivity(myIntent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onPause(){
//        sensorManager.unregisterListener(sensorEventListener);
        sensorManager.unregisterListener(listener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        sensorManager.registerListener(listener,sensor,sensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }
    
}
