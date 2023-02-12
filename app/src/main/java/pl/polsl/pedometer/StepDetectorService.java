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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


public class StepDetectorService extends Service implements SensorEventListener {

    private static Integer currentSteps;
    private static Long currentTime;
    @SuppressWarnings("FieldCanBeLocal")
    private SensorManager sensorManager;
    @SuppressWarnings("FieldCanBeLocal")
    private Sensor sensor;
    private final IBinder binder = new PedoBinder();
    final static String lastDataFilename = "lastRecordedData";

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

                int day = dis.readInt();
                int month = dis.readInt();
                int year = dis.readInt();

                if((year != Calendar.getInstance().get(Calendar.YEAR)) ||
                        ((Calendar.getInstance().get(Calendar.MONTH)+1)!= month) ||
                        ((Calendar.getInstance().get(Calendar.DAY_OF_MONTH) != day)))
                {
                    //TODO: save time and steps
                    currentSteps = 0;
                    currentTime = 0l;
                }
                currentSteps = dis.readInt();
                currentTime = dis.readLong();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            currentSteps = 0;
            currentTime = 0l;
        }
        Toast.makeText(this, "Service starting", Toast.LENGTH_SHORT).show();
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
            dos.writeLong(currentTime);

            dos.close();
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
//ZW
    public Integer getCurrentSteps() {
        return currentSteps;
    }
    public Long getCurrentTime() {
        return currentTime;
    }
    public void stopStepService()
    {
        stopSelf();
    }
}