package com.abjlab.subsub_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.JsonReader;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    private ArrayList<Post> posts;
    private RecyclerView postList;
    private Paint paint = new Paint();
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        loadList();
        postList = (RecyclerView) findViewById(R.id.postList);
        postList.setHasFixedSize(true);
        adapter = new PostAdapter(posts);
        postList.setAdapter(adapter);
        postList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        postList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        postList.setItemAnimator(new DefaultItemAnimator());
        new FetchPostTask(PostListActivity.this).execute();
        swipeHandler();

    }


    private void loadList(){
        if (posts == null) {
            posts = new ArrayList<Post>();
            posts.add(new Post("Titulo", "Desc", "Author", 1));
            posts.add(new Post("Titulo", "Desc", "Author", 2));
            posts.add(new Post("Titulo", "Desc", "Author", 3));
            posts.add(new Post("Titulo", "Desc", "Author", 4));

        }
    }
    private void swipeHandler() {
        ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                PostAdapter adapter = (PostAdapter) postList.getAdapter();

                if (direction == ItemTouchHelper.LEFT) {
                    Post post = posts.get(pos);
                    posts.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    posts.add(pos, post);
                    adapter.notifyItemInserted(pos);
                    //Toast.makeText(getApplicationContext(), "Almacenada en favoritos " + beer.getName() ,Toast.LENGTH_SHORT).show();
                    Snackbar.make(postList, "Almacenada en favoritos " + post.getTitle(), Snackbar.LENGTH_SHORT).show();
                } else {
                    Post removePost = posts.get(pos);
                    //posts.remove(pos);
                    adapter.removeItem(pos);

                    new RemovePostTask(PostListActivity.this, removePost).execute();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                float translationX = Math.max(dX, (-1) * viewHolder.itemView.getWidth() / 2);
                Bitmap icono;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    if (dX > 0) {
                        paint.setColor(Color.parseColor("#F44336"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icono = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_delete_forever_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icono, null,icon_dest,paint);
                    } else {
                        paint.setColor(Color.parseColor("#FF4081"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, paint);
                        icono = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_favorite_border_white_24dp);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icono, null,icon_dest,paint);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(touchCallback);
        helper.attachToRecyclerView(postList);
    }


    public class FetchPostTask extends AsyncTask<String, Void, String[]> {

        private Context c;
        private ArrayList<Post> dataList;


        public FetchPostTask(Context c) {
            this.c = c;
            dataList = new ArrayList<Post>();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            try{
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
                String token = sharedPrefs.getString("token", null);

                URL endpoint = new URL("http://10.0.2.2:8888/api/user/?token="+token);

                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection) endpoint.openConnection();
                myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
                if (myConnection.getResponseCode() == 200) {
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader =
                            new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    parseJson(jsonReader);
                }
            }catch(Exception e){
                e.printStackTrace();        }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            adapter.clear();
            int size = dataList.size();

            for (int i = 0; i<size; i++){

                adapter.add(dataList.get(i));

            }
            adapter.notifyDataSetChanged();

        }

        private void parseJson(JsonReader jsonReader){
            try{
                jsonReader.beginArray();
                while(jsonReader.hasNext()){
                    read(jsonReader);
                }
                jsonReader.endArray();
            }catch(Exception e){

            }

        }

        private void read(JsonReader reader){

            try {
                reader.beginObject();
                Post post = new Post();
                System.out.println(reader.toString());
                while(reader.hasNext()){

                    String key = reader.nextName();
                    System.out.println(key);

                    if(key.equals("title")) post.setTitle(reader.nextString());
                    else if(key.equals("description")) post.setDesc(reader.nextString());
                    else if(key.equals("id")) post.setId(reader.nextInt());
                    else reader.skipValue();
                }
                dataList.add(post);
                reader.endObject();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
