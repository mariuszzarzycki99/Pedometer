package pl.polsl.pedometer;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, SensorEventListener {

    private static final int ACTIVITY_CODE = 77;
    private static int steps = 0;
    @SuppressWarnings("FieldCanBeLocal")
    private SensorManager sensorManager;
    @SuppressWarnings("FieldCanBeLocal")
    private Sensor sensor;


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
            Toast.makeText(getApplicationContext(), "E KURWA", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "GIT", Toast.LENGTH_LONG).show();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        steps++;
        Toast.makeText(MainActivity.this, "Steps: " + steps, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Toast.makeText(MainActivity.this, "Zmienilo sie na: " + steps, Toast.LENGTH_SHORT).show();
    }

}