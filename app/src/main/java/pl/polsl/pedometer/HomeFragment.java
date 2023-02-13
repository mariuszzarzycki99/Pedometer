package pl.polsl.pedometer;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private final int interval = 100; // 500ms
    private Handler refreshHandler;
    private Runnable refreshRunnable;

    private TextView stepsTextView;
    private TextView kmTextView;
    private TextView timeTextView;
    private TextView kcalTextView;

    private Integer steps;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshHandler = new Handler();
        refreshRunnable = () -> {
            if (getView() != null) {
                updateFragmentInfo();
                refreshHandler.postDelayed(refreshRunnable, interval);
            }
        };
        refreshHandler.postDelayed(refreshRunnable, interval);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton button = view.findViewById(R.id.button2);

        stepsTextView = view.findViewById(R.id.steps);
        kmTextView = view.findViewById(R.id.kilometers);
        timeTextView = view.findViewById(R.id.time);
        kcalTextView = view.findViewById(R.id.kcal);

        button.setSelected(SingletonServiceManager.isStepDetectorServiceRunning);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!((MainActivity)getActivity()).isPermissionsGranted())
                {
                    Toast.makeText(getActivity(), "You can't use pedometer without permissions",Toast.LENGTH_LONG).show();
                    ((MainActivity)getActivity()).askForPermissions();
                    return;
                }
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    ((MainActivity) getActivity()).startCounting();
                } else {
                    ((MainActivity) getActivity()).stopCounting();
                }
            }
        });

        return view;
    }

    public Integer getSteps() {
        return ((MainActivity) getActivity()).getSteps();
    }

    public String getTime() {
        Long time = ((MainActivity) getActivity()).getTime();
        long s = time % 60;
        long m = (time / 60) % 60;
        long h = (time / (60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", h,m,s);
    }

    private void updateFragmentInfo() {
        this.steps = getSteps();
        stepsTextView.setText(steps.toString());
        kmTextView.setText(calculateKilometers().toString());
        timeTextView.setText(getTime());
        kcalTextView.setText(calculateCalories().toString());
    }

    private Double calculateKilometers() {
        if(Settings.getGender().equals("Male")) {
            return steps * (Settings.getHeight() * 0.017 + 4.307) / 10000;
        } else if (Settings.getGender().equals("Female")) {
            return steps * (Settings.getHeight() * 0.0165 + 4.287) / 10000;
        }
        return steps / 1312.33595801;
    }

    private Integer calculateCalories() {
        return (int)(steps * 0.04);
    }
}