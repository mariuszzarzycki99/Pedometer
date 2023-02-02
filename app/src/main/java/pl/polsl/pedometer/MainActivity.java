package pl.polsl.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    SummaryFragment summaryFragment = new SummaryFragment();
    AchievementsFragment achievementsFragment = new AchievementsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    //asd

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeButton);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,homeFragment).commit();
                return true;
            case R.id.summaryButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,summaryFragment).commit();
                return true;
            case R.id.achievementsButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,achievementsFragment).commit();
                return true;
            case R.id.settingsButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,settingsFragment).commit();
                return true;
        }
        return false;
    }
}