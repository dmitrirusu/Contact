package com.example.sylar.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.LruCache;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //list with contacts
    static List<Person> persons = new ArrayList<>();
    //if true need to redraw recycleview
    public static boolean dataChanged = false;
    MyAdapter adapter;
    //contacts image cache
    private static LruCache<String, Bitmap> mMemoryCache;
    //id for context menu
    static final int MENU_DELETE = 1;
    static final int MENU_EDIT = 2;
    //counter in the db
    public static int dbID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting contacts from db to arraylist persons
        getData();
        //setting cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        //setting recycleview's adapter and layoutmanager
        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        if (myRecyclerView != null) {
            myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        adapter = new MyAdapter(this);

        if (myRecyclerView != null) {
            myRecyclerView.setAdapter(adapter);
        }

        //context menu for recycleview  (delete,edit)
        registerForContextMenu(myRecyclerView);

        //setting floating button listener
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.fab);
        if (button != null) {
            button.setImageResource(R.mipmap.ic_launcher);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),NewPerson.class));

                }
            });
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        CharSequence message;
        switch (item.getItemId())
        {
            case MENU_DELETE:
                message = "Выбран пункт Удалить " + item.getOrder();
                SQLiteDatabase dateDatabase = new DBHelper(this).getWritableDatabase();
                dateDatabase.delete("mytable","id = " +(persons.get(item.getOrder()).dbId),null);
                persons.remove(item.getOrder());
                dateDatabase.close();
                adapter.notifyDataSetChanged();
                break;
            case MENU_EDIT:
                message = "Выбран пункт Редактировать " + + item.getOrder();
                Intent intent = new Intent(this,NewPerson.class);
                intent.putExtra("image",persons.get(item.getOrder()).photoID);
                intent.putExtra("name",persons.get(item.getOrder()).name);
                intent.putExtra("number",persons.get(item.getOrder()).number);
                intent.putExtra("id",item.getOrder());
                intent.putExtra("dbId",persons.get(item.getOrder()).dbId);
                startActivity(intent);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return true;
    }

    void getData(){
        SQLiteDatabase dateDatabase = new DBHelper(this).getWritableDatabase();
        Cursor c = dateDatabase.query("mytable", null, null, null, null, null, null);
        persons.clear();
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex(DBHelper.PHOTO_ID);
            int dbIdColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex(DBHelper.NAME);
            int numberColIndex = c.getColumnIndex(DBHelper.NUMBER);

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                persons.add(new Person(c.getString(idColIndex), c.getString(nameColIndex), c.getString(numberColIndex),Integer.parseInt(c.getString(dbIdColIndex))));
            }
            while (c.moveToNext());
        }
        c.close();
        dateDatabase.close();
    }

    //method to add image to cache
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    //method for getting image from cache
    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    //if dataChanged true redraw recycleview items
    @Override
    protected void onResume() {
        super.onResume();
        if(dataChanged) {
            adapter.notifyDataSetChanged();
            dataChanged = false;
        }
    }
}
