package pl.polsl.pedometer;

import android.Manifest.permission;
import android.content.ComponentName;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

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
            //TODO: Pobierz kroki i czas z pliku i usun plik
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
        if (itemId == R.id.achievementsButton) {
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, achievementsFragment).commit();
            return true;
        }
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
            Toast.makeText(MainActivity.this, "Saved to file", Toast.LENGTH_SHORT).show();
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

}