package com.example.sylar.contacts;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class NewPerson extends AppCompatActivity {

    //intent retutn id
    private static final int SELECT_PICTURE = 1;
    private static SQLiteDatabase dateDatabase;
    //contacts image
    private ImageView image;
    //contentvalue to add/edit
    private ContentValues cv;
    Intent intent;
    //width in dp
    int dpWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_person);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);

        intent = getIntent();
        cv = new ContentValues();
        image = (ImageView) findViewById(R.id.imageView2);

        if(intent.hasExtra("image")) {
            Log.d("myLog",intent.getStringExtra("image"));
            if(intent.getStringExtra("image").equals("")) {
                image.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.ic_face_black_24dp,null));
                cv.put(DBHelper.PHOTO_ID,"");
            }
            else {
                loadBitmap(intent.getStringExtra("image"), image);
                cv.put(DBHelper.PHOTO_ID,intent.getStringExtra("image"));
            }
        }
        else
            cv.put(DBHelper.PHOTO_ID,"");
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


                startActivityForResult(i, SELECT_PICTURE);

            }
        });

        final EditText name = (EditText) findViewById(R.id.editText);
        if(intent.hasExtra("name"))
            if (name != null) {
                name.setText(intent.getStringExtra("name"));
            }
        final EditText number = (EditText) findViewById(R.id.editText2);
        if(intent.hasExtra("number"))
            if (number != null) {
                number.setText(intent.getStringExtra("number"));
            }
        Button button = (Button) findViewById(R.id.button);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.hasExtra("dbId")) {
                    assert name != null;
                    cv.put(DBHelper.NAME, name.getText().toString());
                    assert number != null;
                    cv.put(DBHelper.NUMBER, number.getText().toString());
                    dateDatabase = new DBHelper(getApplicationContext()).getWritableDatabase();
                    if (dateDatabase.isOpen())
                        dateDatabase.update("mytable", cv, "id = ?" , new String[]{String.valueOf(intent.getIntExtra("dbId", 0))});
                    MainActivity.persons.set(intent.getIntExtra("id", 0), new Person(cv.get(DBHelper.PHOTO_ID).toString(), cv.get(DBHelper.NAME).toString(), cv.get(DBHelper.NUMBER).toString(),MainActivity.dbID++));

                    dateDatabase.close();
                    MainActivity.dataChanged = true;
                    finish();
                } else {
                    assert name != null;
                    cv.put(DBHelper.NAME, name.getText().toString());
                    System.out.print(cv.get(DBHelper.NAME));
                    assert number != null;
                    cv.put(DBHelper.NUMBER, number.getText().toString());
                    dateDatabase = new DBHelper(getApplicationContext()).getWritableDatabase();
                    if (dateDatabase.isOpen())
                        dateDatabase.insert("mytable", null, cv);
                    MainActivity.persons.add(new Person(cv.get(DBHelper.PHOTO_ID).toString(), cv.get(DBHelper.NAME).toString(), cv.get(DBHelper.NUMBER).toString(),MainActivity.dbID++));

                    dateDatabase.close();
                    MainActivity.dataChanged = true;
                    finish();
                }
            }
        });
    }

    private String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = getPath(selectedImageUri);
                cv.put(DBHelper.PHOTO_ID, selectedImagePath);
                loadBitmap(selectedImagePath,image);
            }
        }
    }
    public void loadBitmap(String path, ImageView imageView) {


        final String imageKey = String.valueOf(path);

        final Bitmap bitmap = MainActivity.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView,dpToPx(dpWidth),dpToPx(120));


            task.execute(path);
        }

    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
