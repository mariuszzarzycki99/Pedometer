package pl.polsl.pedometer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        setSpinnerValue(genderField);

        EditText heightField = (EditText) view.findViewById(R.id.height);
        heightField.setText(Settings.getHeight().toString());

        EditText weightField = (EditText) view.findViewById(R.id.weight);
        weightField.setText(Settings.getWeight().toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.setGender(genderField.getSelectedItem().toString());
                Settings.setHeight(Double.parseDouble(heightField.getText().toString()));
                Settings.setWeight(Double.parseDouble(weightField.getText().toString()));
                ((MainActivity) getActivity()).saveSettings();
            }
        });

        return view;
    }

    private void setSpinnerValue(Spinner genderField) {
        String compareValue = Settings.getGender();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderField.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            genderField.setSelection(spinnerPosition);
        }
    }
}