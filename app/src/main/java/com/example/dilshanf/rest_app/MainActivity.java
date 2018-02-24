package com.example.dilshanf.rest_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private TextView personData;
    private  final String TAG = "MainActivity";
    private Timer timer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnRefresh = findViewById(R.id.btnRefresh);
        personData = findViewById(R.id.txtPersonData);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute("http://10.0.2.2:8080/api/persons");
                Log.i(TAG, "Clicked on Refresh button");
                Toast.makeText(getApplicationContext(), "Force Refresh...", 3).show();
            }
        });

        //refresh after 60 seconds
        timer = new Timer();
        timer.schedule(new APICallerTask(),60000,60000);

    }

    public class APICallerTask  extends TimerTask {

        @Override
        public void run() {
            new JSONTask().execute("http://10.0.2.2:8080/api/persons");
        }
    }

    public class JSONTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader =new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (java.io.IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                    Log.i(TAG, "Disconnecting with DB.....");
                }
                 if (reader != null)
                     try {
                         reader.close();
                         Log.i(TAG, "Closing the reader.....");
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
            }
            return null;
        }

        protected void onPostExecute(String result) {
          super.onPostExecute(result);
            personData.setText(result);
            Log.i(TAG, "Attempting to fetch DATA......");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new JSONTask().execute("http://10.0.2.2:8080/api/persons");
        Log.i(TAG, "OnStart Activity");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
