package android.example.com.booklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteTonight on 05.06.2017.
 */

public class Utils {
    public static final String LOG_TAG = Utils.class.getName();
    public static final String ARG_BOOK = "arg_book";

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            int resposeCode = urlConnection.getResponseCode();
            switch (resposeCode) {
                case 200:
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                    break;
                default:
                    Log.e(LOG_TAG, "Server responded with :" + resposeCode);
                    //do something
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error trying to fetch JSON data", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public static ArrayList<Book> getBooksFromJSON(Context context, String response) {
        // If the JSON string is empty or null, then return early.
        if (response.isEmpty()) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Book> books = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(response);
            if (root.has("items")) {
                JSONArray bookArray = root.getJSONArray("items"); //JSON Node named items
                for (int i = 0; i < bookArray.length(); i++) {

                    JSONObject currentBook = bookArray.getJSONObject(i);

                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");


                    List<String> authors = new ArrayList<String>();

                    if (volumeInfo.has("authors")) {
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authorsArray.length(); j++) {
                            authors.add(authorsArray.getString(j));
                        }
                    }

                    String description = "";

                    if (volumeInfo.has("description")) {
                        description = volumeInfo.getString("description");
                    }

                    String thumbnailLink = "";
                    if (volumeInfo.has("imageLinks")) {
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        thumbnailLink = imageLinks.getString("thumbnail");
                    }
                    Book book = new Book(context, title, authors, description, thumbnailLink);

                    books.add(book);
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing JSON results", e);
        }
        return books;
    }

    public static boolean isWifi(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return isWiFi;
    }

    public static boolean hasConnection(Context context)
    {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
