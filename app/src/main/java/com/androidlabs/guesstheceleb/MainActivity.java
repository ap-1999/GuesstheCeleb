package com.androidlabs.guesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celeburl = new ArrayList<String>();
    ArrayList<String> celebname = new ArrayList<String>();
    int chosenCeleb = 0;
    int locationofcorrectanswer;
    String[] answers = new String[4];
    ImageView imageView;
    Button bt, bt1, bt2, bt3;

    public class ImageDownload extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputstream = connection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(inputstream);
                return mybitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result = result + current;
                    data = reader.read();


                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        bt = (Button) findViewById(R.id.button);
        bt1 = (Button) findViewById(R.id.button2);
        bt2 = (Button) findViewById(R.id.button3);
        bt3 = (Button) findViewById(R.id.button4);
        DownloadTask task = new DownloadTask();
        String result = null;
        try {

            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitresult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitresult[0]);
            int i = 0;
            while (m.find()) {
                celeburl.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebname.add(m.group(1));
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createNewQuestion();
    }

    public void celebChosen(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationofcorrectanswer))) {
            Toast toast = Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Wrong!! It is "+celebname.get(chosenCeleb), Toast.LENGTH_SHORT);
            toast.show();
        }
        createNewQuestion();
    }
    public void createNewQuestion(){
        try {
            Random rand = new Random();
            chosenCeleb = rand.nextInt(celeburl.size());
            ImageDownload imageDownload = new ImageDownload();
            Bitmap celebImage;
            celebImage = imageDownload.execute(celeburl.get(chosenCeleb)).get();
            System.out.println(celebname.get(chosenCeleb));
            System.out.println(celeburl.get(chosenCeleb));
            imageView.setImageBitmap(celebImage);
            locationofcorrectanswer = rand.nextInt(4);
            int locationofincorrectanser;
            int i;
            for (i = 0; i < 4; i++) {
                if (i == locationofcorrectanswer) {
                    answers[i] = celebname.get(chosenCeleb);

                } else {
                    locationofincorrectanser = rand.nextInt(celeburl.size());
                    while (locationofincorrectanser == chosenCeleb) {
                        locationofincorrectanser = rand.nextInt(celeburl.size());
                    }
                    answers[i] = celebname.get(locationofincorrectanser);

                }
            }
            bt.setText(answers[0]);
            bt1.setText(answers[1]);
            bt2.setText(answers[2]);
            bt3.setText(answers[3]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}