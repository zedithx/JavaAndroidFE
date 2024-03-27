package com.example.javaandroidapp.utils;

//import com.algolia.search.DefaultSearchClient;
//import com.algolia.search.SearchClient;
//import com.algolia.search.SearchIndex;
//import com.algolia.search.models.indexing.Query;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.javaandroidapp.adapters.Callbacks;
import com.example.javaandroidapp.objects.Listing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AlgoliaHelper {
    public static void searchListings(String item, Callbacks callback) {
        Client client = new Client("2E5RB96ERX", "d488826decadd6593821cefdabd53fa2");
        Index index = client.getIndex("Bulkify");
        index.searchAsync(new Query(item), new CompletionHandler() {
            @Override
            public void requestCompleted(@androidx.annotation.Nullable JSONObject jsonObject, @androidx.annotation.Nullable AlgoliaException e) {
                try {
                    if (jsonObject != null) {
                        JSONArray hits = jsonObject.getJSONArray("hits");
                        List<Listing> item = new ArrayList<Listing>();
                        if (hits.length() != 0) {
                            for (int i = 0; i < hits.length(); i++) {
                                JSONObject hit = hits.getJSONObject(i);
                                Listing new_item = new Listing();
                                new_item.setExpiry(Date.from(Instant.ofEpochSecond(hit.getLong("expiry"))));
                                new_item.setName(hit.getString("name"));
                                new_item.setPrice(hit.getDouble("price"));
                                ArrayList<String> images = new ArrayList<String>();
                                JSONArray images_json = hit.getJSONArray("imageList");
                                for (int j = 0; j < images_json.length(); j++) {
                                    images.add(images_json.getString(j));
                                }
                                new_item.setImageList(images);
                                item.add(new_item);
                            }
                            callback.getList(item);

                        } else {
                            callback.getList(item);
                        }
                    }
                    } catch (JSONException ex) {
                        throw new RuntimeException(ex);
                    }

            }
        });
    }
}
