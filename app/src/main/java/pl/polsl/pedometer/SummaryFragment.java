package pl.polsl.pedometer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    BarChart barChart;
    private static final String historyFile = "pedometerHistory.txt";
    private List<DateSteps> history;

    private TextView infoTextView;

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHistory();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        barChart = view.findViewById(R.id.barChart);
        infoTextView = view.findViewById(R.id.info);

        BarDataSet barDataSet = createGraphData();
        barChart.setData(new BarData(barDataSet));
        barChart.animateY(500);
        barChart.getDescription().setText("Last 7 days");
        barChart.setExtraTopOffset(10.0f);
        barChart.setExtraBottomOffset(10.0f);
        barChart.setExtraLeftOffset(10.0f);
        barChart.setExtraRightOffset(10.0f);

        return view;
    }


    private void getHistory() {
        history = ((MainActivity) getActivity()).loadHistory();
    }
    @NonNull
    private BarDataSet createGraphData() {
        List<BarEntry> barEntries = new ArrayList<>();

        int noOfDaysToShow = 7;

        List<DateSteps> lastSevenDays = history.stream()
                .filter(x -> x.getDate().isAfter(LocalDate.now().minusDays(noOfDaysToShow)))
                .collect(Collectors.toList());

        for(int i = 0; i < noOfDaysToShow; i++) {
            LocalDate date = LocalDate.now().minusDays(noOfDaysToShow - i - 1);
            for(DateSteps day : lastSevenDays) {
                if(date.equals(day.getDate())) {
                    barEntries.add(new BarEntry(date.getDayOfMonth(), day.getSteps()));
                    break;
                } else {
                    barEntries.add(new BarEntry(date.getDayOfMonth(), 0));
                }
            }
            barEntries.add(new BarEntry(LocalDate.now().getDayOfMonth(), getSteps()));
        }
        String infoText = "From " + LocalDate.now().minusDays(noOfDaysToShow - 1).toString()
                + " to " + LocalDate.now().toString();
        infoTextView.setText(infoText);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Steps");
        final int[] GRAPH_COLORS = {rgb("#000000")};
        barDataSet.setColors(GRAPH_COLORS);
        barDataSet.setDrawValues(false);
        return barDataSet;
    }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return Color.rgb(r, g, b);
    }

    public Integer getSteps() {
        return ((MainActivity) getActivity()).getSteps();
    }
}