package com.example.mylocation.caloriecounter;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StepCounterService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    float d2x;
    float d2y;
    float d2z;
    double time=((double)System.currentTimeMillis())/1000.0;
    double prevTime=0;
    double short_average=9.81;
    double long_average=9.81;
    double diff=0;
    double step_min_acc=2.0;
    double step_min_time=0.5;
    double buildup=0.0;
    double prev_step_time=0;
    int steps=0;
    double hgt=174;
    double wgt=73;
    double age=29;
    int man=1;
    double step_coeff=0.0011*wgt;
    double baseline_cal=(655.1-588.6*man)+(9.563+4.187*man)*wgt+(1.850+3.153*man)*hgt-(4.676+2.074*man)*age;
    int cals=(int)baseline_cal;


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
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);

        System.out.println(baseline_cal);


    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            d2x = event.values[0];
            d2y = event.values[1];
            d2z = event.values[2];
            prevTime = time;
            time = ((double) (System.currentTimeMillis() ))/ 1000.0;
            short_average = Math.exp(-20 * (time - prevTime)) * short_average + (1 - Math.exp(-20 * (time - prevTime))) * Math.sqrt(d2x * d2x + d2y * d2y + d2z * d2z);
            long_average = Math.exp(-0.001 * (time - prevTime)) * long_average + (1 - Math.exp(-0.001 * (time - prevTime))) * Math.sqrt(d2x * d2x + d2y * d2y + d2z * d2z);
            diff = short_average - long_average;
            diff=Math.abs(diff);
            if (diff > step_min_acc && time - prev_step_time > step_min_time) {
                buildup+=time-prevTime;
                if(buildup>=0.25){
                    prev_step_time = ((double) (System.currentTimeMillis()) / 1000.0);
                    diff = 1;
                    buildup=0;
                }
                else diff=0;
            } else diff = 0;
        }

        steps+=diff;
        cals=(int)(0.5+baseline_cal+steps*step_coeff);
        System.out.println(step_coeff*steps);

        Intent local = new Intent();
        local.setAction("service.to.activity.transfer");
        local.putExtra("cals", Double.toString(cals));
        local.setAction("service.to.activity.transfer");
        local.putExtra("steps", Double.toString(steps));
        this.sendBroadcast(local);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);


        return START_STICKY;

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}