package pl.polsl.pedometer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button button = (Button) view.findViewById(R.id.settingsButton);

        Spinner genderField = (Spinner) view.findViewById(R.id.gender);
        EditText heightField = (EditText) view.findViewById(R.id.height);
        EditText weightField = (EditText) view.findViewById(R.id.weight);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.setGender(genderField.getSelectedItem().toString());
                Settings.setHeight(Integer.parseInt(heightField.getText().toString()));
                Settings.setWeight(Double.parseDouble(weightField.getText().toString()));

                Toast.makeText(getView().getContext(),"gender " + Settings.getGender(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getView().getContext(),"height " + Settings.getHeight(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getView().getContext(),"weight " + Settings.getWeight(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}