package pl.polsl.pedometer;

import android.Manifest.permission;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    final static String allDataFilename = "allData";
    final static String lastDataFilename = "lastRecordedData";
    private static final int ACTIVITY_CODE = 77;
    private Integer lastSteps = 0;
    private Long lastTime = 0l;
    @SuppressWarnings("FieldCanBeLocal")
    private StepDetectorService stepService;
    boolean mStepServiceBound = false;

    private static final String settingsFile = "pedometerSettings.txt";

    private final ServiceConnection stepSensorConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            StepDetectorService.PedoBinder binder = (StepDetectorService.PedoBinder) service;
            stepService = binder.getStepService();
            mStepServiceBound = true;
            lastSteps = stepService.getCurrentSteps();
            lastTime = stepService.getCurrentTime();
        }

        public void onServiceDisconnected(ComponentName className) {
            mStepServiceBound = false;
        }
    };

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SummaryFragment summaryFragment = new SummaryFragment();
    AchievementsFragment achievementsFragment = new AchievementsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deleteRandomHistory();
        createRandomHistory();
        List<DateSteps> data = loadHistory();
        for(DateSteps ds : data)
            System.out.println(ds.getDate().toString() + " " + ds.getSteps());

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeButton);

        if (ContextCompat.checkSelfPermission(this, permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission.ACTIVITY_RECOGNITION}, ACTIVITY_CODE);
            //TODO: Reakcja na brak uprawnien?
            Toast.makeText(getApplicationContext(), "TODO: E KURWA", Toast.LENGTH_LONG).show();
        }
        if(SingletonServiceManager.isStepDetectorServiceRunning) {
            Intent intent = new Intent(this, StepDetectorService.class);
            bindService(intent, stepSensorConnection, 0);
        }
        else {
            lastActivityData last = getLastData();
            lastSteps = last.steps;
            lastTime = last.time;
        }
        initializeSettings();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.homeButton) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
            return true;
        }
        if (itemId == R.id.summaryButton) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, summaryFragment).commit();
            return true;
        }
//        if (itemId == R.id.achievementsButton) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, achievementsFragment).commit();
//            return true;
//        }
        if (itemId == R.id.settingsButton) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, settingsFragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTIVITY_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Activity Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Activity Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mStepServiceBound) {
            unbindService(stepSensorConnection);
        }
        mStepServiceBound = false;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (SingletonServiceManager.isStepDetectorServiceRunning) {
            Intent intent = new Intent(this, StepDetectorService.class);
            bindService(intent, stepSensorConnection, 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void startStepdetectorService() {
        Intent intent = new Intent(this, StepDetectorService.class);
        startService(intent);
    }

    public Integer getSteps() {
        if (mStepServiceBound && SingletonServiceManager.isStepDetectorServiceRunning) {
            return stepService.getCurrentSteps();
        }
        return lastSteps;
    }
    public Long getTime() {
        if (mStepServiceBound && SingletonServiceManager.isStepDetectorServiceRunning) {
            return stepService.getCurrentTime();
        }
        return lastTime;
    }

    public void startCounting() {
        if (!SingletonServiceManager.isStepDetectorServiceRunning) {
            startStepdetectorService();
        }
        Intent intent = new Intent(this, StepDetectorService.class);
        bindService(intent, stepSensorConnection, 0);
    }

    public void stopCounting() {
        if (mStepServiceBound && SingletonServiceManager.isStepDetectorServiceRunning) {
            lastSteps = stepService.getCurrentSteps();
            lastTime = stepService.getCurrentTime();
            stepService.stopStepService();
        }
    }

    public void saveSettings() {
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, settingsFile));
            writer.write(Settings.getAll().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSettings() {
        File path = getApplicationContext().getFilesDir();
        File fileToRead = new File(path, settingsFile);
        byte[] fileContent = new byte[(int) fileToRead.length()];
        try {
            FileInputStream reader = new FileInputStream(fileToRead);
            reader.read(fileContent);
            Settings.setAll(new String(fileContent));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeSettings() {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, settingsFile);
        if(file.exists()) {
            loadSettings();
        } else {
            Settings.loadDefault();
        }
    }

    public void deleteRandomHistory()
    {
        File path = getApplicationContext().getFilesDir();
        File fileToDelete = new File(path, allDataFilename);
        if(fileToDelete.exists())
            fileToDelete.delete();
    }
    public void createRandomHistory()
    {
        File path = getApplicationContext().getFilesDir();
        File fileToSave = new File(path, allDataFilename);
        try
        {
            FileOutputStream fos = new FileOutputStream(fileToSave,true);
            DataOutputStream dos = new DataOutputStream(fos);

            for(int i=0;i<10;i++) {
                dos.writeInt(2023);
                dos.writeInt(2);
                dos.writeInt(i+1);

                dos.writeInt(i*10+i%2);
            }
            dos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<DateSteps> loadHistory() {
        File path = getApplicationContext().getFilesDir();
        File fileToRead = new File(path, allDataFilename);
        if (fileToRead.exists()) {
            int numOfRecords = ((int) fileToRead.length()) / (4*4);
            List<DateSteps> data = new ArrayList<DateSteps>(numOfRecords);
            try {
                FileInputStream reader = new FileInputStream(fileToRead);
                DataInputStream dis = new DataInputStream(reader);
                for (int i = 0; i < numOfRecords; i++) {
                    data.add(new DateSteps(dis.readInt(), dis.readInt(), dis.readInt(), dis.readInt()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data;
        }
        return new ArrayList<DateSteps>();
    }
    private class lastActivityData
    {
        Integer steps;
        Long time;
    }

    private lastActivityData getLastData()
    {
        lastActivityData lastData = new lastActivityData();
        File file = getApplicationContext().getFileStreamPath(lastDataFilename);
        if(file.exists()) {
            try {
                FileInputStream fis = getApplicationContext().openFileInput(lastDataFilename);
                DataInputStream dis = new DataInputStream(fis);

                int year = dis.readInt();
                int month = dis.readInt();
                int day = dis.readInt();
                lastData.steps = dis.readInt();
                lastData.time = dis.readLong();
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
                        dos.writeInt(lastData.steps);
                        dos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    lastData.steps = 0;
                    lastData.time = 0L;
                    file.delete();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            lastData.steps = 0;
            lastData.time = 0L;
        }
        return lastData;
    }
}