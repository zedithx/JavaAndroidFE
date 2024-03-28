package com.example.javaandroidapp.utils;

//import com.algolia.search.DefaultSearchClient;
//import com.algolia.search.SearchClient;
//import com.algolia.search.SearchIndex;
//import com.algolia.search.models.indexing.Query;

import androidx.annotation.Nullable;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.javaandroidapp.adapters.CallbackAdapter;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.Listing;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlgoliaHelper {
    public static void querySuggestion(String item, Callbacks callback) {
        Client client = new Client("2E5RB96ERX", "d488826decadd6593821cefdabd53fa2");
        Index index = client.getIndex("Bulkify_query_suggestions");
        index.searchAsync(new Query(item), new CompletionHandler() {
            @Override
            public void requestCompleted(@androidx.annotation.Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    if (jsonObject != null) {
                        JSONArray hits = jsonObject.getJSONArray("hits");

                        List<String> item = new ArrayList<String>();
                        int limit = Math.min(4, hits.length());
                        if (hits.length() != 0) {
                            for (int i = 0; i < limit; i++) {
                                JSONObject hit = hits.getJSONObject(i);
                                item.add(hit.getString("query"));
                            }
                            callback.getListOfString(item);
                        } else {
                            callback.getListOfString(item);
                        }
                    }
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static void searchListingID(String item, Callbacks callback) {
        Client client = new Client("2E5RB96ERX", "d488826decadd6593821cefdabd53fa2");
        Index index = client.getIndex("Bulkify");
        index.searchAsync(new Query(item), new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    if (jsonObject != null) {
                        JSONArray hits = jsonObject.getJSONArray("hits");
                        List<String> item = new ArrayList<>();
                        if (hits.length() != 0) {
                            for (int i = 0; i < hits.length(); i ++) {
                                JSONObject hit = hits.getJSONObject(i);
                                String object = hit.getString("objectID");
                                item.add(object);
                            }
                            Listings.searchListing(FirebaseFirestore.getInstance(), item, new CallbackAdapter() {
                                @Override
                                public void getList(List<Listing> listing) {
                                    callback.getList(listing);
                                }
                            });
                        }
                    }
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

}
