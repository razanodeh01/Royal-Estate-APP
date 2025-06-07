/**
 * Description:
 * `FilterFragment` is a modal dialog that allows users to refine their property search using custom filters.
 * It enables filtering based on minimum and maximum price, property location, and type (e.g., Apartment, Villa, Land).
 */

package com.example.realestate;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class FilterFragment extends DialogFragment {

    private EditText minPriceInput, maxPriceInput;
    private Spinner locationSpinner, typeSpinner;
    private Button applyButton;

    private OnFilterAppliedListener filterAppliedListener;


    public interface OnFilterAppliedListener {
        void onFilterApplied(String minPrice, String maxPrice, String location, String type);
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.filterAppliedListener = listener;
    }

    private final String[] locations = {
            "Any", "Ramallah, Palestine", "Nablus, Palestine", "Hebron, Palestine",
            "Amman, Jordan", "Zarqa, Jordan", "Irbid, Jordan",
            "Giza, Egypt", "Alexandria, Egypt", "Cairo, Egypt"
    };

    private final String[] types = { "Any", "Apartment", "Villa", "Land" };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        minPriceInput = view.findViewById(R.id.min_price_filter);
        maxPriceInput = view.findViewById(R.id.max_price_filter);
        locationSpinner = view.findViewById(R.id.location_spinner);
        typeSpinner = view.findViewById(R.id.type_spinner);
        applyButton = view.findViewById(R.id.apply_filter_button);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                locations
        );
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                types
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);


        applyButton.setOnClickListener(v -> {
            String minPrice = minPriceInput.getText().toString().trim();
            String maxPrice = maxPriceInput.getText().toString().trim();
            String selectedLocation = locationSpinner.getSelectedItem().toString();
            String selectedType = typeSpinner.getSelectedItem().toString();

            if (filterAppliedListener != null) {
                filterAppliedListener.onFilterApplied(
                        minPrice,
                        maxPrice,
                        selectedLocation.equals("Any") ? null : selectedLocation,
                        selectedType.equals("Any") ? null : selectedType
                );
            }

            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9), // 90% of screen width
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

}
