package com.hairstyle.simu.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Simon on 2017/7/7.
 */

public class Accelerometer {
    /**
     *
     * @author MatrixCV
     *
     * CLOCKWISE_ANGLE为手机旋转角度
     * 其Deg0定义如下图所示
     *  ___________________
     * | +--------------+  |
     * | |              |  |
     * | |              |  |
     * | |              | O|
     * | |              |  |
     * | |______________|  |
     * ---------------------
     * 顺时针旋转后得到Deg90，即手机竖屏向上，如下图所示
     *  ___________
     * |           |
     * |+---------+|
     * ||         ||
     * ||         ||
     * ||         ||
     * ||         ||
     * ||         ||
     * |+---------+|
     * |_____O_____|
     */
    public enum CLOCKWISE_ANGLE {
        Deg0(0), Deg90(1), Deg180(2), Deg270(3);
        private int value;
        private CLOCKWISE_ANGLE(int value){
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    private SensorManager sensorManager = null;

    private boolean hasStarted = false;

    private static CLOCKWISE_ANGLE rotation;

    public Accelerometer(Context ctx) {
        sensorManager = (SensorManager) ctx
                .getSystemService(Context.SENSOR_SERVICE);
        rotation = CLOCKWISE_ANGLE.Deg0;
    }

    public void start() {
        if (hasStarted) return;
        hasStarted = true;
        rotation = CLOCKWISE_ANGLE.Deg0;
        sensorManager.registerListener(accListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        if (!hasStarted) return;
        hasStarted = false;
        sensorManager.unregisterListener(accListener);
    }

    static public int getDirection() {
        return rotation.getValue();
    }

    private SensorEventListener accListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = arg0.values[0];
                float y = arg0.values[1];
                float z = arg0.values[2];
                if (Math.abs(x)>3 || Math.abs(y)>3) {
                    if (Math.abs(x)>Math.abs(y)) {
                        if (x > 0) {
                            rotation = CLOCKWISE_ANGLE.Deg0;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg0");
                        } else {
                            rotation = CLOCKWISE_ANGLE.Deg180;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg180");
                        }
                    } else {
                        if (y > 0) {
                            rotation = CLOCKWISE_ANGLE.Deg90;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg90");
                        } else {
                            rotation = CLOCKWISE_ANGLE.Deg270;
                            //Log.d("ROTATION","CLOCKWISE_ANGLE: Deg270");
                        }
                    }
                }
            }
        }
    };
}
