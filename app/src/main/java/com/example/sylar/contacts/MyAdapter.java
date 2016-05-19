package com.example.sylar.contacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final String MY_LOG = "myLog";
    Context context;

    MyAdapter (Context context) {
        Log.d(MY_LOG,"Adapter.Constructor()");
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(MY_LOG,"onCreateViewHolder()");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {

        Log.d(MY_LOG,"onBindViewHolder()");
        if(!MainActivity.persons.get(position).photoID.equals("")) {
            loadBitmap(MainActivity.persons.get(position).photoID,holder.photoID);
        }

        else {
            holder.photoID.setImageResource(R.mipmap.ic_face_black_24dp);
        }

        holder.name.setText(MainActivity.persons.get(position).name);

        holder.number.setText(MainActivity.persons.get(position).number);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SecondActivity.class);
                if (MainActivity.persons.get(position).photoID.equals("")) {
                    intent.putExtra("photoId", "");
                } else
                    intent.putExtra("photoId", MainActivity.persons.get(position).photoID);

                intent.putExtra("number",MainActivity. persons.get(position).number);
                intent.putExtra("name", MainActivity.persons.get(position).name);
                v.getContext().startActivity(intent);
            }
        });

    }

    public void loadBitmap(String path, ImageView imageView) {

        final String imageKey = String.valueOf(path);

        final Bitmap bitmap = MainActivity.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView,dpToPx(68),dpToPx(68));

            task.execute(path);
        }

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    @Override
    public int getItemCount() {
        return MainActivity.persons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        CardView cardView;
        ImageView photoID;
        TextView number;
        TextView name;
        ViewHolder(final View itemView) {
            super(itemView);
            Log.d(MY_LOG,"ViewHolder.Constructor()");
            cardView = (CardView) itemView.findViewById(R.id.cv);
            cardView.setOnCreateContextMenuListener(this);
            photoID = (ImageView) itemView.findViewById(R.id.person_photo);
            number = (TextView) itemView.findViewById(R.id.person_number);
            name = (TextView) itemView.findViewById(R.id.person_name);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Выберите действие");
            contextMenu.add(Menu.NONE,MainActivity.MENU_DELETE,getAdapterPosition(),"Удалить");
            contextMenu.add(Menu.NONE,MainActivity.MENU_EDIT,getAdapterPosition(),"Изменить");
        }

    }
}
