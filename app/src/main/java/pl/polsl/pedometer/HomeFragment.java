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
        HomeFragment fragment = new HomeFragment();
        return fragment;
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


        Integer steps = ((MainActivity) getActivity()).getSteps();
        TextView textView = (TextView) view.findViewById(R.id.steps);
        textView.setText(steps.toString());

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
}