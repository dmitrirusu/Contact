package com.example.sylar.contacts;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;


public class SecondActivity extends AppCompatActivity {
    int dpWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        String id = getIntent().getStringExtra("photoId");
        String name1 = getIntent().getStringExtra("name");
        String number1 = getIntent().getStringExtra("number");

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);

        //ImageView tmp = (ImageView) findViewById(id);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        if(id.equals("")) {
            assert imageView != null;
            imageView.setImageResource(R.mipmap.ic_face_black_24dp);
        }
        else {
            File f = new File(id);
            assert imageView != null;
            loadBitmap(f.getAbsolutePath(),imageView);
            //  imageView.setImageBitmap(Utils.decodeSampledBitmapFromResource(f.getAbsolutePath(),imageView.getWidth(),imageView.getHeight()));
        }

        TextView name = (TextView) findViewById(R.id.textView);
        if (name != null) {
            name.setText(name1);
        }

        TextView number = (TextView) findViewById(R.id.textView2);
        if (number != null) {
            number.setText(number1);
        }

    }

    public void loadBitmap(String path, ImageView imageView) {


        final String imageKey = String.valueOf(path);

        final Bitmap bitmap = MainActivity.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView,dpToPx(dpWidth),dpToPx(150));


            task.execute(path);
        }

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
