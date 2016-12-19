package com.abjlab.subsub_demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RemovePostTask extends AsyncTask<String, Void, String[]> {

    Context context;
    Post post;
    SharedPreferences sharedPrefs;
    String token;

    public RemovePostTask(Context c, Post post) {
        this.context = c;
        this.post = post;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        token = sharedPrefs.getString("token", null);
    }

    @Override
    protected String[] doInBackground(String... strings) {

        try {
            String url = "http://10.0.2.2:8888/api/post/" + post.getId() + "?token=" + token;
            URL endpoint = new URL(url);

            // Create connection
            HttpURLConnection myConnection = (HttpURLConnection) endpoint.openConnection();
            myConnection.setDoOutput(true);
            myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
            myConnection.setRequestMethod("DELETE");

            if (myConnection.getResponseCode() == 200) {
                System.out.println("RES: " + myConnection.getResponseMessage());
                //Toast.makeText(context,"Post borrado!", Toast.LENGTH_LONG).show();
            } else {
                System.out.println(myConnection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[0];
    }
}
