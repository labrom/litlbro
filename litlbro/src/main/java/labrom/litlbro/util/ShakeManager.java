package labrom.litlbro.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Taken from http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
 * 
 * @author Romain Laboisse labrom@gmail.com
 *
 */
public class ShakeManager {
    
    public interface ShakeListener {
        void onShake(float acceleration);
    }
    
    
    private final SensorManager mgr;
    private float accel; // acceleration apart from gravity
    private float accelCurrent; // current acceleration including gravity
    private float accelLast; // last acceleration including gravity
    final ShakeListener listener;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

      public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        accelLast = accelCurrent;
        accelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = accelCurrent - accelLast;
        accel = accel * 0.9f + delta; // perform low-cut filter
        
        if(listener != null && accel > 1)
            listener.onShake(accel);
      }

      public void onAccuracyChanged(Sensor sensor, int accuracy) {
      }
    };
    
    public ShakeManager(Context ctx, ShakeListener listener) {
        mgr = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        this.listener = listener;
        accel = 0.00f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
    }

    public void register() {
        mgr.registerListener(mSensorListener, mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        mgr.unregisterListener(mSensorListener);
    }

}
