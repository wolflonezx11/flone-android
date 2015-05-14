package com.fewlaps.flone.util;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.fewlaps.flone.communication.bean.PhoneSensorInformation;

import de.greenrobot.event.EventBus;


/**
 * Created by Kaz on 26/07/2014.
 *
 * @see https://github.com/kazouh/Rotaduino
 */
public class OrientationSensorsListener implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetic;

    private float[] mValuesMagnet = new float[3];
    private float[] mValuesAccel = new float[3];
    private float[] mValuesOrientation = new float[3];
    private float[] mRotationMatrix = new float[9];

    PhoneSensorInformation sensorInformation = new PhoneSensorInformation();

    public OrientationSensorsListener(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Handle the events for which we registered
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(sensorEvent.values, 0, mValuesAccel, 0, 3);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(sensorEvent.values, 0, mValuesMagnet, 0, 3);
                break;
        }
        SensorManager.getRotationMatrix(mRotationMatrix, null, mValuesAccel, mValuesMagnet);
        SensorManager.getOrientation(mRotationMatrix, mValuesOrientation);

        sensorInformation.setHeading(restrictAngle((int) Math.toDegrees((double) mValuesOrientation[0])));
        sensorInformation.setPitch(restrictAngle((int) Math.toDegrees((double) mValuesOrientation[1])));
        sensorInformation.setRoll(restrictAngle((int) Math.toDegrees((double) mValuesOrientation[2])));

        EventBus.getDefault().post(sensorInformation);
    }


    private int restrictAngle(int tmpAngle) {
        while (tmpAngle >= 180) tmpAngle -= 360;
        while (tmpAngle < -180) tmpAngle += 360;
        return tmpAngle;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
