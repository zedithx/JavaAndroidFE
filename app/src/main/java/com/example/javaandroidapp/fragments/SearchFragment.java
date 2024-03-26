package com.example.javaandroidapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.javaandroidapp.R;

public class SearchFragment extends Fragment {
    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);

        searchEditText = rootView.findViewById(R.id.searchEditText);
        ImageView backButton = rootView.findViewById(R.id.back_search);
//        searchButton = rootView.findViewById(R.id.searchButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Check if there are any fragments in the back stack
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    // Pop the top fragment from the back stack
                    fragmentManager.popBackStack();
                }
            }
        });
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