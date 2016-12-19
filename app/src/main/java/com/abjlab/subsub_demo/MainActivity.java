package com.abjlab.subsub_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.R.attr.value;

public class MainActivity extends AppCompatActivity {

    EditText txtUser;
    EditText txtPass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUser = (EditText) findViewById(R.id.loginName);
        txtPass = (EditText) findViewById(R.id.loginPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginUser().execute(txtUser.getText().toString(), txtPass.getText().toString());
            }
        });

    }


    //Configuración login usuario
    class LoginUser extends AsyncTask<String, Void, String[]>{
        String token;
        @Override
        protected String[] doInBackground(String... strings) {

            try{
                URL endpoint = new URL("http://10.0.2.2:8888/api/user/auth");
                String credentials = strings[0] + ":" + strings[1];
                System.out.println(credentials);
                // Create connection
                HttpURLConnection myConnection = (HttpURLConnection) endpoint.openConnection();
                myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
                myConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.encode(credentials.getBytes(),Base64.NO_WRAP )));
                if (myConnection.getResponseCode() == 200) {
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    responseHandler(jsonReader);
                }else{
                    System.out.println("error: " + myConnection.getResponseCode());
                    InputStream responseBody = myConnection.getInputStream();
                    System.out.println(responseBody.toString());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return new String[0];
        }

        private void responseHandler(JsonReader jsonReader){
            System.out.println("aaaaa");
            try{
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    System.out.println("cccc");

                    if (key.equals("token")) { // Check if desired key
                        String value = jsonReader.nextString();
                        System.out.println("Token: " + value);
                        token = value;
                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }
                jsonReader.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", token);
            editor.commit();
            if(token != null){
                Intent i = new Intent(MainActivity.this, PostListActivity.class);
                startActivity(i);
            }

        }
    }
}
