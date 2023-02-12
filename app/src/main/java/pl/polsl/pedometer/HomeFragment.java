package pl.polsl.pedometer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private final int interval = 100; // 500ms
    private Handler refreshHandler;
    private Runnable refreshRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshHandler = new Handler();
        refreshRunnable = () -> {
            if(getView() != null){
                updateFragmentInfo();
                refreshHandler.postDelayed(refreshRunnable,interval);
            }
        };
        refreshHandler.postDelayed(refreshRunnable,interval);
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
        ImageButton button = (ImageButton) view.findViewById(R.id.button2);

        TextView stepsTextView = (TextView) view.findViewById(R.id.steps);
        TextView kmTextView = (TextView) view.findViewById(R.id.kilometers);
        TextView timeTextView = (TextView) view.findViewById(R.id.time);
        TextView kcalTextView = (TextView) view.findViewById(R.id.kcal);

        button.setSelected(SingletonServiceManager.isStepDetectorServiceRunning);

        Integer steps = getSteps();

        stepsTextView.setText(steps.toString());
        Double km = steps / 1312.33595801;
        kmTextView.setText(km.toString());

//        Average walk: 1 km = 1,408 steps
//        Brisk walk: 1 km = 1,209 steps
//        Jog: 1 km = 1,219 steps
//        Run: 1 km = 1,045 steps
//        Fast run: 1 km = 875 steps
//        Very fast run: 1 km = 675 steps

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    ((MainActivity) getActivity()).startCounting();
                    Toast.makeText(getView().getContext(),"Nalicza", Toast.LENGTH_SHORT).show();
                }
                else {
                    ((MainActivity) getActivity()).stopCounting();
                    Toast.makeText(getView().getContext(),"Nie nalicza", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public Integer getSteps() {
        return ((MainActivity) getActivity()).getSteps();
    }
    public Long getTime() {
        return ((MainActivity) getActivity()).getTime();
    }
    private void updateFragmentInfo()
    {
        View view = getView();
        TextView stepsTextView = (TextView) view.findViewById(R.id.steps);
        TextView kmTextView = (TextView) view.findViewById(R.id.kilometers);
        TextView timeTextView = (TextView) view.findViewById(R.id.time);
        TextView kcalTextView = (TextView) view.findViewById(R.id.kcal);

        Integer steps = getSteps();
        Long time = getTime();

        //TODO: km dodatkowe? Nwm Ty cos mowiles jak dla mnie wyjebane xD
        //TODO: kalorie
        //TODO: czasu dalej nie zwracam z serwisu xD
        stepsTextView.setText(steps.toString());
        Double km = steps / 1312.33595801;
        kmTextView.setText(km.toString());
        timeTextView.setText(time.toString());
    }
}