package pl.polsl.pedometer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    BarChart barChart;
    private static final String historyFile = "pedometerHistory.txt";

    public SummaryFragment() {
        // Required empty public constructor
    }

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        barChart = view.findViewById(R.id.barChart);

        BarDataSet barDataSet = createGraphData();
        barChart.setData(new BarData(barDataSet));
        barChart.animateY(1000);
        barChart.getDescription().setText("Weekly steps");

        return view;
    }

    @NonNull
    private BarDataSet createGraphData() {
        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            barEntries.add(new BarEntry(i, i));
        }
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
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }
}