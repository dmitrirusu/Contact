package com.example.sylar.contacts;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import static com.example.sylar.contacts.Utils.decodeSampledBitmapFromResource;

class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    public String data = "";
    int width,height;

    public BitmapWorkerTask(ImageView imageView, int width, int height) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
        this.width = width;
        this.height = height;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... strings) {
        data = strings[0];

        final Bitmap bitmap = decodeSampledBitmapFromResource(strings[0],width ,height);
        MainActivity.addBitmapToMemoryCache(String.valueOf(strings[0]), bitmap);
        return bitmap;
    }


    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}