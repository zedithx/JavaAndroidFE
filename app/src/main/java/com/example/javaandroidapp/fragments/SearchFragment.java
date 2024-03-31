package com.example.javaandroidapp.fragments;

import static com.example.javaandroidapp.utils.AlgoliaHelper.querySuggestion;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.javaandroidapp.R;
import com.example.javaandroidapp.activities.LandingActivity;
import com.example.javaandroidapp.activities.SearchActivity;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.objects.Listing;
import com.example.javaandroidapp.objects.Order;
import com.example.javaandroidapp.utils.AlgoliaHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        // input box
        searchEditText = rootView.findViewById(R.id.searchEditText);
        ImageView backButton = rootView.findViewById(R.id.back_search);
        // search icon
        ImageView fragmentSearch = rootView.findViewById(R.id.fragment_search);
        TextView suggestionText1 = rootView.findViewById(R.id.suggestion1);
        TextView suggestionText2 = rootView.findViewById(R.id.suggestion2);
        TextView suggestionText3 = rootView.findViewById(R.id.suggestion3);
        TextView suggestionText4 = rootView.findViewById(R.id.suggestion4);
        ArrayList<TextView> queryBox = new ArrayList<TextView>();
        queryBox.add(suggestionText1);
        queryBox.add(suggestionText2);
        queryBox.add(suggestionText3);
        queryBox.add(suggestionText4);
        for (TextView suggestionTextquery: queryBox){
            suggestionTextquery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Clicking should return the view of all products
                    AlgoliaHelper.searchListingID(suggestionTextquery.getText().toString(), new CallbackAdapter(){
                        @Override
                        public void getListOfString(List<String> listings_query) {
                            Intent Main = new Intent(getActivity(), SearchActivity.class);
                            System.out.println(listings_query);
                            Main.putStringArrayListExtra("listings", (ArrayList<String>) listings_query);
                            startActivity(Main);
                        }
                    });
                }
            });
        }
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

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this example
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // As the user types, fetch and display query suggestions
                String query = s.toString();
                //Update my fragment by passing the query into the algolia helper
                querySuggestion(query, new CallbackAdapter() {
                    @Override
                    public void getListOfString(List<String> strings) {
                        for (int i=0; i<strings.size(); i++){
                            queryBox.get(i).setText(strings.get(i));
                        }
                    }
                });
            }
            @Override
            public void afterTextChanged(Editable s) {

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