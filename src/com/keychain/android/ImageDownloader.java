package com.keychain.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        // TODO Auto-generated method stub

        Bitmap bitmap = null;
        try {
            URL aURL = new URL(params[0]);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();

            // Buffered is always good for a performance plus.
            BufferedInputStream bis = new BufferedInputStream(is);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            // Decode url-data to a bitmap.
            bitmap = BitmapFactory.decodeStream(bis, null, options);

            bis.close();
            is.close();

            return bitmap;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }
}