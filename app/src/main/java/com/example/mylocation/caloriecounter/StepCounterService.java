package com.example.mylocation.caloriecounter;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;

public class StepCounterService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private double prevTime = 0;
    private double Time = System.currentTimeMillis();
    private double prevStep = 0;
    private double Step = System.currentTimeMillis();
    double Magnitude=System.currentTimeMillis();
    private double prevMagnitude=0;
    double stepCount=0;

    public StepCounterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        // for the system's orientation sensor registered listeners

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x_acceleration = event.values[0];
        float y_acceleration = event.values[1];
        float z_acceleration = event.values[2];
        prevMagnitude=Magnitude;
        Magnitude = Math.sqrt(x_acceleration*x_acceleration + y_acceleration*y_acceleration + z_acceleration*z_acceleration);
        if((1000*(Magnitude-prevMagnitude)/(Time-prevTime)>1)&(Math.abs(Magnitude-9.81)>2)){
            if(System.currentTimeMillis()-Step>500) {
                Step = System.currentTimeMillis();
                stepCount+=1;
            }
        }
        System.out.println(stepCount);
        prevTime=Time;
        Time=System.currentTimeMillis();



        Intent local = new Intent();
        local.setAction("service.to.activity.transfer");
        local.putExtra("steps", Double.toString(stepCount));
        this.sendBroadcast(local);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);

        return START_STICKY;

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}