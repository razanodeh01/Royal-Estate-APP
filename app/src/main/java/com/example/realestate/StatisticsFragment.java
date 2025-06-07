/**
 * Description:
 * This fragment is used to display statistical insights
 * related to users and reservations within the real estate agency app, specifically for the admin dashboard.
 */

package com.example.realestate;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import android.graphics.Color;
import java.util.ArrayList;


public class StatisticsFragment extends Fragment {

    private TextView usersCountText, reservationsCountText;
    private DatabaseHelper dbHelper;
    private PieChart genderPieChart;
    private BarChart countryBarChart;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        usersCountText = view.findViewById(R.id.total_users);
        reservationsCountText = view.findViewById(R.id.total_reservations);
        genderPieChart = view.findViewById(R.id.gender_pie_chart);
        countryBarChart = view.findViewById(R.id.country_bar_chart);
        dbHelper = new DatabaseHelper(requireContext());

        loadStatistics();

        return view;
    }

    private void loadStatistics() {
        usersCountText.setText(String.format("Total Users: %d", dbHelper.getUserCount()));
        reservationsCountText.setText(String.format("Total Reservations: %d", dbHelper.getReservationCount()));


        Cursor genderCursor = dbHelper.getGenderDistribution();
        long total = dbHelper.getUserCount();

        float femalePercentage = 0, malePercentage = 0, otherPercentage = 0;

        while (genderCursor.moveToNext()) {
            String gender = genderCursor.getString(genderCursor.getColumnIndexOrThrow("gender"));
            int count = genderCursor.getInt(genderCursor.getColumnIndexOrThrow("count"));
            float percentage = (float) count / total * 100f;

            if (gender.equalsIgnoreCase("female")) {
                femalePercentage = percentage;
            } else if (gender.equalsIgnoreCase("male")) {
                malePercentage = percentage;
            } else {
                otherPercentage = percentage;
            }
        }
        genderCursor.close();

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(femalePercentage, "Female"));
        entries.add(new PieEntry(malePercentage, "Male"));
        entries.add(new PieEntry(otherPercentage, "Other"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF69B4"));
        colors.add(Color.parseColor("#2196F3"));
        colors.add(Color.parseColor("#FFD700"));
        dataSet.setColors(colors);
        dataSet.setValueTextSize(20f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(3f);
        dataSet.setValueTypeface(Typeface.create("times_new_roman", Typeface.NORMAL));

        PieData data = new PieData(dataSet);
        genderPieChart.setData(data);
        genderPieChart.setDrawHoleEnabled(true);
        genderPieChart.setHoleRadius(45f);
        genderPieChart.setTransparentCircleRadius(50f);
        genderPieChart.setCenterText("Gender Ratio");
        genderPieChart.setCenterTextSize(24f);
        genderPieChart.setCenterTextTypeface(Typeface.create("times_new_roman", Typeface.BOLD));
        genderPieChart.getDescription().setEnabled(false);
        genderPieChart.animateY(1000);
        genderPieChart.invalidate();

        Legend legend = genderPieChart.getLegend();
        legend.setTextSize(18f);
        legend.setTypeface(Typeface.create("times_new_roman", Typeface.NORMAL));
        genderPieChart.invalidate();



        Cursor countryCursor = dbHelper.getTopReservingCountries();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> countryLabels = new ArrayList<>();
        int index = 0;
        while (countryCursor.moveToNext()) {
            String country = countryCursor.getString(countryCursor.getColumnIndexOrThrow("country"));
            int count = countryCursor.getInt(countryCursor.getColumnIndexOrThrow("reservation_count"));
            barEntries.add(new BarEntry(index, count));
            countryLabels.add(country);
            index++;
        }
        countryCursor.close();

        BarDataSet barDataSet = new BarDataSet(barEntries, "Reservations per Country");
        barDataSet.setColors(Color.parseColor("#FFA726"));
        barDataSet.setValueTextSize(14f);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTypeface(Typeface.create("times_new_roman", Typeface.BOLD));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);

        countryBarChart.setData(barData);
        countryBarChart.setFitBars(true);
        countryBarChart.getDescription().setEnabled(false);
        countryBarChart.animateY(1000);


        XAxis xAxis = countryBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(countryLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(14f);
        xAxis.setTypeface(Typeface.create("times_new_roman", Typeface.BOLD));


        YAxis leftAxis = countryBarChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setTextSize(14f);
        leftAxis.setTypeface(Typeface.create("times_new_roman", Typeface.NORMAL));

        countryBarChart.getAxisRight().setEnabled(false);

        Legend legend2 = countryBarChart.getLegend();
        legend2.setTextSize(14f);
        legend2.setTypeface(Typeface.create("times_new_roman", Typeface.NORMAL));

        countryBarChart.invalidate();
    }

}