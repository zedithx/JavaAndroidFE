package com.example.javaandroidapp.utils;

//import com.algolia.search.DefaultSearchClient;
//import com.algolia.search.SearchClient;
//import com.algolia.search.SearchIndex;
//import com.algolia.search.models.indexing.Query;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.javaandroidapp.adapters.Callbacks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AlgoliaHelper {
    static ApplicationInfo applicationInfo;
    static String algAppId;
    static String algApiKey;
    public static void querySuggestion(Context context, String item, Callbacks callback) {
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null){
            algAppId = applicationInfo.metaData.getString("algoliaAppId");
            algApiKey = applicationInfo.metaData.getString("algoliaApiKey");
        }
        Client client = new Client(algAppId, algApiKey);
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

    public static void searchListingID(Context context, String item, Callbacks callback) {
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null){
            algAppId = applicationInfo.metaData.getString("algoliaAppId");
            algApiKey = applicationInfo.metaData.getString("algoliaApiKey");
        }
        Client client = new Client(algAppId, algApiKey);
        Index index = client.getIndex("Bulkify");
        index.searchAsync(new Query(item), new CompletionHandler() {
            @Override
            public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {
                try {
                    if (jsonObject != null) {
                        JSONArray hits = jsonObject.getJSONArray("hits");
                        List<String> items = new ArrayList<>();
                        if (hits.length() != 0) {
                            for (int i = 0; i < hits.length(); i ++) {
                                JSONObject hit = hits.getJSONObject(i);
                                String object = hit.getString("objectID");
                                items.add(object);
                            }
                            callback.getListOfString(items);
//                            Listings.searchListing(FirebaseFirestore.getInstance(), item, new CallbackAdapter() {
//                                @Override
//                                public void getList(List<Listing> listing) {
//                                    callback.getList(listing);
//                                }
//                            });
                        }
                    }
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

}
