package com.keychain.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

    EditText etResponse;
    TextView tvIsConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get reference to the views
        etResponse = (EditText) findViewById(R.id.etResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

        // check if you are connected or not
        if (isConnected()) {
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are connected");
        } else {
            tvIsConnected.setText("You are NOT connected");
        }

        new HttpAsyncTask().execute("http://sandbox-api.keychain.cc/users/1/apps");
    }

    public static String GET(String url) {
        Log.d("Keychain", "Downloading " + url);
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            Log.i("Keychain", "About to make HTTP connection.");
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            Log.i("Keychain", "Received HTTP response.");
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                Log.i("Keychain", result);
            } else {
                result = "Did not work!";
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Keychain", "Inside onPostExecute with " + result);
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            etResponse.setText(result.substring(0, 20));

            ImageAdapter images = new ImageAdapter(getBaseContext());

            try {
                JSONArray jsonRecords = new JSONArray(result);
                for (Integer i = 0; i < jsonRecords.length(); ++i) {
                    String imageURL = "http:" + jsonRecords.getJSONObject(i).getString("image_url");
                    Log.i("Keychain", "Downloading image: " + imageURL);
                    try {
                        ImageDownloader d = new ImageDownloader();
                        Bitmap b = d.execute(imageURL).get();
                        images.addBitmap(b);
                    } catch (InterruptedException e) {
                    } catch (ExecutionException e) {
                    }
                }
            } catch (JSONException e) {

            }

            GridView grid = (GridView) findViewById(R.id.gridView);
            grid.setAdapter(images);
        }
    }
}