package android.sipgate.lunch.data;

import android.net.Uri;
import android.util.Log;

import android.sipgate.lunch.CustomApplication;
import android.sipgate.lunch.CustomPreferenceActivity;
import android.sipgate.lunch.ui.MealView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * request meal photos via google image search.
 *
 * @author schafm
 */
public class GoogleImageSearch {
    public static final String TAG = GoogleImageSearch.class.getSimpleName();

    public GoogleImageSearch() {

    }

    public void requestImageSearch(final MealView mealView, final ImageSearchResponse searchResponse) {
        String customKeywords = CustomApplication.getPrefs().getString("googleImageSearchKeywords",
                CustomPreferenceActivity.DEFAULT_GOOGLE_IMAGE_SEARCH_KEYWORDS);
        String queryUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8" +
                "&q=" + Uri.encode(mealView.getMealTitle() + " " + customKeywords) +
                "&imgtype=photo" +
                "&imgsz=large";
        Log.d(TAG, "request google image search with url=" + queryUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(queryUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                searchResponse.onSearchResponse(mealView, "http://");
                Log.w(TAG, "google image search failed", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    searchResponse.onSearchResponse(mealView, mealView.getUrl());
                    Log.w(TAG, "google image search wasn't successful");
                    return;
                }

                try {
                    String jsonData = response.body().string();
                    JSONArray resultArr = new JSONObject(jsonData)
                            .getJSONObject("responseData").getJSONArray("results");
                    if (resultArr.length() > 0) {
                        String url = resultArr.getJSONObject(0).getString("url");
                        Log.w(TAG, "google image search found something! url=" + url);
                        searchResponse.onSearchResponse(mealView, url);
                    }
                } catch (JSONException e) {
                    Log.w(TAG, "google image search failed while trying to parse json data", e);
                    searchResponse.onSearchResponse(mealView, "");
                }
            }
        });
    }

    /**
     * Response interface for the google image view.
     * @author schafm
     */
    public interface ImageSearchResponse {
        public void onSearchResponse(MealView mealView, String url);
    }
}
