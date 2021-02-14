package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText searchBar;
    TextView heading, description;
    ConstraintLayout main;

    public class downloadUnsplashImage extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                Log.i("data","connection made");
                return BitmapFactory.decodeStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Drawable getRandomBitmap() throws ExecutionException, InterruptedException {
        Bitmap bm = new downloadUnsplashImage().execute("https://source.unsplash.com/random").get();
        return new BitmapDrawable(getResources(), bm);
    }

    public Drawable getImage(String searchParams) throws ExecutionException, InterruptedException {
        Bitmap bm = new downloadUnsplashImage().execute("https://source.unsplash.com/featured/?" + searchParams).get();
        return new BitmapDrawable(getResources(), bm);
    }

    public class downloadJsonData extends AsyncTask<String,Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... city) {
            try {
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q="
                        + city[0] +"&APPID=348bd0a63fa1f61a44d57c1a8dcb2fb2&units=metric");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                String str = "";
                int in = reader.read();
                while(in != -1){
                    char c = (char) in;
                    str += c;
                    in = reader.read();
                }
                return new JSONObject(str);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void getWeather(View view) throws ExecutionException, InterruptedException, JSONException {
        String cityName = searchBar.getText().toString();
        JSONObject jsonData = new downloadJsonData().execute(cityName).get();
        JSONArray weather = jsonData.getJSONArray("weather");
        String headingText = weather.getJSONObject(0).getString("main");
        String descriptionText = weather.getJSONObject(0).getString("description");
        heading.setText(headingText);
        description.setText(descriptionText);
        main.setBackground(getImage("weather," + headingText));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = findViewById(R.id.mainLayout);
        description = findViewById(R.id.desctiptionText);
        heading = findViewById(R.id.headingText);
        searchBar = findViewById(R.id.searchBar);

        try {
            main.setBackground(getRandomBitmap());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
