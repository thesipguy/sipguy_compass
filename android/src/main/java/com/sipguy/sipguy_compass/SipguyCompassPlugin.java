package com.sipguy.sipguy_compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterJNI;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public final class SipguyCompassPlugin implements StreamHandler, FlutterPlugin {
    // A static variable which will retain the value across Isolates.
    private static Double currentAzimuth;

    private double newAzimuth;
    private double filter;
    private int lastAccuracy;
    private SensorEventListener sensorEventListener;
    private Context appContext;
    private SensorManager sensorManager;
    private Sensor sensor;
    private float[] orientation;
    private float[] rMat;
    private EventChannel channel;
    private BinaryMessenger binaryMessenger;

    // public static void registerWith(Registrar registrar) {
    // EventChannel channel = new EventChannel(registrar.messenger(),
    // "sipguy_compass/compass");
    // channel.setStreamHandler(new SipguyCompassPlugin());
    // }

    public void onListen(Object arguments, EventSink events) {
        System.out.println("onListen activated");
        // Added check for the sensor, if null then the device does not have the
        // TYPE_ROTATION_VECTOR or TYPE_GEOMAGNETIC_ROTATION_VECTOR sensor
        if (sensor != null) {
            System.out.println("Sensor was not null");
            sensorEventListener = createSensorEventListener(events);
            sensorManager.registerListener(sensorEventListener, this.sensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            // Send null to Flutter side
            events.success(null);
            // events.error("Sensor Null", "No sensor was found", "The device does not have
            // any sensor");
        }
    }

    public void onCancel(Object arguments) {
        this.sensorManager.unregisterListener(this.sensorEventListener);
    }

    private SensorEventListener createSensorEventListener(final EventSink events) {
        return new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                lastAccuracy = accuracy;
            }

            public void onSensorChanged(SensorEvent event) {
                SensorManager.getRotationMatrixFromVector(rMat, event.values);
                newAzimuth = (Math.toDegrees((double) SensorManager.getOrientation(rMat, orientation)[0])
                        + (double) 360) % (double) 360;
                if (currentAzimuth == null || Math.abs(currentAzimuth - newAzimuth) >= filter) {
                    currentAzimuth = newAzimuth;

                    // Compute the orientation relative to the Z axis (out the back of the device).
                    float[] zAxisRmat = new float[9];
                    SensorManager.remapCoordinateSystem(rMat, SensorManager.AXIS_X, SensorManager.AXIS_Z, zAxisRmat);
                    float[] dv = new float[3];
                    SensorManager.getOrientation(zAxisRmat, dv);
                    double azimuthForCameraMode = (Math.toDegrees((double) dv[0]) + (double) 360) % (double) 360;

                    double[] v = new double[3];
                    v[0] = newAzimuth;
                    v[1] = azimuthForCameraMode;
                    // Include reasonable compass accuracy numbers. These are not representative
                    // of the real error.
                    if (lastAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
                        v[2] = 15;
                    } else if (lastAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) {
                        v[2] = 30;
                    } else if (lastAccuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
                        v[2] = 45;
                    } else {
                        v[2] = -1; // unknown
                    }
                    events.success(v);
                }
            }
        };
    }

    // private SipguyCompassPlugin(Context context, int sensorType, int
    // fallbackSensorType) {
    // filter = 0.1F;
    // lastAccuracy = 1; // SENSOR_STATUS_ACCURACY_LOW
    //
    // sensorManager = (SensorManager)
    // context.getSystemService(Context.SENSOR_SERVICE);
    // orientation = new float[3];
    // rMat = new float[9];
    // Sensor defaultSensor = this.sensorManager.getDefaultSensor(sensorType);
    // if (defaultSensor != null) {
    // sensor = defaultSensor;
    // } else {
    // sensor = this.sensorManager.getDefaultSensor(fallbackSensorType);
    // }
    // }

    @Override
    public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding binding) {

        appContext = binding.getApplicationContext();
        filter = 0.1F;
        lastAccuracy = 1; // SENSOR_STATUS_ACCURACY_LOW

        sensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
        orientation = new float[3];
        rMat = new float[9];
        // int sensorType=Sensor.TYPE_ACCELEROMETER;
        // int fallbackSensorType=Sensor.TYPE_MAGNETIC_FIELD;
        int fallbackSensorType = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR;
        int sensorType = Sensor.TYPE_ROTATION_VECTOR;
        Sensor defaultSensor = this.sensorManager.getDefaultSensor(sensorType);
        if (defaultSensor != null) {
            sensor = defaultSensor;
        } else {
            sensor = this.sensorManager.getDefaultSensor(fallbackSensorType);
        }
         binaryMessenger = binding.getBinaryMessenger();
        channel = new EventChannel(binaryMessenger, "sipguy_compass/compass");
        channel.setStreamHandler(this);
        System.out.println("Engine attached");

    }

    @Override
    public void onDetachedFromEngine(FlutterPlugin.FlutterPluginBinding binding) {
        this.sensorManager.unregisterListener(this.sensorEventListener);
        binaryMessenger=null;
        channel.setStreamHandler(null);

        //channel.
        channel=null;
     //   FlutterJNI flutterJNI = new FlutterJNI();
        sensorManager=null;
        sensorEventListener=null;

       // flutterJNI.detachFromNativeAndReleaseResources();
        System.out.println("I QUIT&&&***************");
    }


}
