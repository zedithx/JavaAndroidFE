package com.example.javaandroidapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.javaandroidapp.R;

public class SearchFragment extends Fragment {
    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        searchEditText = rootView.findViewById(R.id.searchEditText);
//        searchButton = rootView.findViewById(R.id.searchButton);

//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String query = searchEditText.getText().toString();
//                // Perform search using the query
//                performSearch(query);
//            }
//        });

        return rootView;
    }

//    private void performSearch(String query) {
//        // Implement search logic here
//        Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
//    }
}