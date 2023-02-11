package pl.polsl.pedometer;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;


public class StepDetectorService extends Service implements SensorEventListener {

    private static long currentSteps;
    @SuppressWarnings("FieldCanBeLocal")
    private SensorManager sensorManager;
    @SuppressWarnings("FieldCanBeLocal")
    private Sensor sensor;
    private final IBinder binder = new PedoBinder();

    public class PedoBinder extends Binder {
        StepDetectorService getStepService() {
            return StepDetectorService.this;
        }
    }

    public StepDetectorService() {
    }

    @Override
    public void onCreate() {
        currentSteps = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new Notification.Builder(this, createNotificationChannel())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Pedometer")
                .setContentText("Counting steps")
                .setContentIntent(pendingIntent).build();

        startForeground(2137, notification);
        SingletonServiceManager.isStepDetectorServiceRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
        SingletonServiceManager.isStepDetectorServiceRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentSteps++;
        Toast.makeText(this, "Steps: " + currentSteps, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Toast.makeText(this, "Accuracy changed", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private String createNotificationChannel(){
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        String channelId = "PedoServiceID";
        String channelName = "PedoStepDetectorService";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    public long getCurrentSteps() {
        return currentSteps;
    }
}