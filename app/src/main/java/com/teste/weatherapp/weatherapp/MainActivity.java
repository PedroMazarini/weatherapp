package com.teste.weatherapp.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView.Adapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imgTempo;
    TextView txtDia;
    TextView txtMinima;
    TextView txtMaxima;
    JSONObject tempoJson;
    JSONObject channelObject;
    JSONObject queryObject;
    JSONObject resultsObject;
    JSONObject itemObject;
    JSONArray forecastArray;
    Adapter mTempoAdapter;
    RecyclerView recyclerTempo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String link = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22S%C3%A3o%20Carlos%2C%20SP%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        try {
            MyTaskParams myTaskParams = new MyTaskParams(link);
            new downloadJSONTask().execute(myTaskParams);



        } catch (Exception e) {
            Toast.makeText(this,"Erro ao baixar JSON",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    private static class MyTaskParams {
        String link;

        public MyTaskParams(String link) {
            this.link = link;
        }
    }


        final String LOG_TAG = MainActivity.class.getSimpleName();
        class downloadJSONTask extends AsyncTask<MyTaskParams,String,String> {

            private Exception exception;

            protected String doInBackground(MyTaskParams... params) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String baseUrl;
                StringBuffer buffer=null;

                    try {
                        final MyTaskParams myTaskParams = params[0];


                        baseUrl = myTaskParams.link;

                        URL url = new URL(baseUrl);

                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET"); //<<<< Método do request
                        urlConnection.connect();

                        // Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        buffer = new StringBuffer();
                        if (inputStream == null) {
                            // Nothing to do.
                            return null;
                        }
                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Será recebido um JSON em linha reta então aki esta pulando linha
                            // Não faz diferença na hora do parse é apenas pra facilitar o debug
                            buffer.append(line + "\n");
                        }

                        if (buffer.length() == 0) {
                            // Stream was empty.  No point in parsing.
                            return null;
                        }
                    } catch (ConnectException e) {
                        Log.e(LOG_TAG, "Connection ERROR", e);
                        return null;

                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error ", e);
                        return null;


                    } catch (Exception e) {
                        this.exception = e;

                        return null;
                    }finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                                Log.e(LOG_TAG, "Error closing stream", e);
                            }
                        }
                    }

                    return buffer.toString();

            }

            @Override
            protected void onPostExecute(String json) {

                try {

                    JSONTokener jsonTokener = new JSONTokener(json);
                    tempoJson = new JSONObject(jsonTokener);
                    queryObject = tempoJson.getJSONObject("query");
                    resultsObject = queryObject.getJSONObject("results");
                    channelObject = resultsObject.getJSONObject("channel");
                    itemObject = channelObject.getJSONObject("item");

                    forecastArray = itemObject.getJSONArray("forecast");

                    recyclerTempo = findViewById(R.id.recycler_medicoes);
                    recyclerTempo.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<TempoModel>>(){}.getType();
                    List<TempoModel> tempoList = gson.fromJson(forecastArray.toString(), type);

                    TempoModel hojeModel = tempoList.get(0);
                    imgTempo = findViewById(R.id.img_tempo);
                    txtMinima = findViewById(R.id.txt_minima);
                    txtMaxima = findViewById(R.id.txt_maxima);
                    txtMinima.setText("Mínima "+hojeModel.getLow()+"°");
                    txtMaxima.setText("Máxima "+hojeModel.getHigh()+"°");
                    if(hojeModel.getText().equals("Partly Cloudy")){
                        imgTempo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.moon));
                    }else if(hojeModel.getText().equals("Cloudy")){
                        imgTempo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.moon));
                    }else if(hojeModel.getText().equals("Mostly Cloudy")){
                        imgTempo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.rain));
                    }else{
                        imgTempo.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sun));
                    }



                    mTempoAdapter = new TempoAdapter(tempoList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerTempo.setLayoutManager(mLayoutManager);
                    recyclerTempo.setItemAnimator(new DefaultItemAnimator());
                    recyclerTempo.setAdapter(mTempoAdapter);
                    mTempoAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("ERRO inflate",e.toString());
                    e.printStackTrace();
                }
            }
        }


}

