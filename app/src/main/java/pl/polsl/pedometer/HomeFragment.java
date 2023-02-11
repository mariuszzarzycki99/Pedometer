package pl.polsl.pedometer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    //Handle selected state change
                    Toast.makeText(getView().getContext(),"Nalicza", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Handle de-select state change
                    Toast.makeText(getView().getContext(),"Nie nalicza", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public Integer getSteps() {
        return ((MainActivity) getActivity()).getSteps();
    }
}