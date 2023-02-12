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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class StepDetectorService extends Service implements SensorEventListener {

    private static Integer currentSteps;
    private static Long currentTime;
    private static Long lastStartTime;
    @SuppressWarnings("FieldCanBeLocal")
    private SensorManager sensorManager;
    @SuppressWarnings("FieldCanBeLocal")
    private Sensor sensor;
    private final IBinder binder = new PedoBinder();
    final static String lastDataFilename = "lastRecordedData";
    final static String allDataFilename = "allData";

    public class PedoBinder extends Binder {
        StepDetectorService getStepService() {
            return StepDetectorService.this;
        }
    }

    public StepDetectorService() {
    }

    @Override
    public void onCreate() {
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SingletonServiceManager.isStepDetectorServiceRunning = true;
        File file = getApplicationContext().getFileStreamPath(lastDataFilename);
        if(file.exists()) {
            try {
                FileInputStream fis = getApplicationContext().openFileInput(lastDataFilename);
                DataInputStream dis = new DataInputStream(fis);

                int year = dis.readInt();
                int month = dis.readInt();
                int day = dis.readInt();
                currentSteps = dis.readInt();
                currentTime = dis.readLong();
                dis.close();

                if((year != Calendar.getInstance().get(Calendar.YEAR)) ||
                        ((Calendar.getInstance().get(Calendar.MONTH)+1)!= month) ||
                        ((Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != day)))
                {
                    File path = getApplicationContext().getFilesDir();
                    try {
                        FileOutputStream writer = new FileOutputStream(new File(path, allDataFilename),true);
                        DataOutputStream dos = new DataOutputStream(writer);
                        dos.writeInt(year);
                        dos.writeInt(month);
                        dos.writeInt(day);
                        dos.writeInt(currentSteps);
                        dos.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    currentSteps = 0;
                    currentTime = 0L;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            currentSteps = 0;
            currentTime = 0L;
        }
        lastStartTime = System.currentTimeMillis();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try
        {
            FileOutputStream fos = getApplicationContext().openFileOutput(lastDataFilename, Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(Calendar.getInstance().get(Calendar.YEAR));
            dos.writeInt(Calendar.getInstance().get(Calendar.MONTH)+1);
            dos.writeInt(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            dos.writeInt(currentSteps);
            dos.writeLong(currentTime-lastStartTime+System.currentTimeMillis());

            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SingletonServiceManager.isStepDetectorServiceRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        currentSteps++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
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

    public Integer getCurrentSteps() {
        return currentSteps;
    }
    public Long getCurrentTime() {
        return (currentTime-lastStartTime+System.currentTimeMillis())/1000;
    }
    public void stopStepService()
    {
        stopSelf();
    }
}