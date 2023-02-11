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


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final int ACTIVITY_CODE = 77;
    @SuppressWarnings("FieldCanBeLocal")
    private StepDetectorService stepService;
    boolean mStepServiceBound = false;
    private final ServiceConnection stepSensorConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            StepDetectorService.PedoBinder binder = (StepDetectorService.PedoBinder) service;
            stepService = binder.getStepService();
            mStepServiceBound = true;
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
        } else {
            if (!SingletonServiceManager.isStepDetectorServiceRunning) {
                startStepdetectorService();
            }
            Intent intent = new Intent(this, StepDetectorService.class);
            bindService(intent, stepSensorConnection, 0);
        }
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
        if (mStepServiceBound) {
            return stepService.getCurrentSteps();
        }
        return 0;
    }

}