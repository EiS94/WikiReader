package com.schaubeck.wikireader;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.schaubeck.wikireader.wikipedia.Article;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button stop, play;
    public TextToSpeech textToSpeech;
    // String pythonResult;
    TextView result;

    Article article;

    //SearchView
    SearchView searchView;
    ListView listView;
    ArrayList<String> suggestions;
    ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification",
                    "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        play = findViewById(R.id.btnPlay);
        stop = findViewById(R.id.btnStop);
        result = findViewById(R.id.textResult);
        //SearchView
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.resultView);

        result.setMovementMethod(ScrollingMovementMethod.getInstance());


        //SearchView
        suggestions = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions);
        listView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Python script
                listView.setVisibility(View.INVISIBLE);
                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("main");
                PyObject obj;
                if (suggestions.size() > 0) {
                    obj = pyobj.callAttr("getBestResult", query, "de");

                    if (obj != null && !obj.toString().equals("")) {
                        // String value = obj.toString();
                        article = Article.parseFromPythonResult(query, obj.toString());
                        article.removeRegex();

                        result.setText(article.getRepresentation());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Kein Ergebnis gefunden", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                result.setText("");
                if (listView.getVisibility() == View.INVISIBLE)
                    listView.setVisibility(View.VISIBLE);
                suggestions.clear();

                //Python script
                if (s.length() > 2) {

                    Python py = Python.getInstance();
                    PyObject pyobj = py.getModule("main");
                    PyObject pyResult = pyobj.callAttr("requestSuggestions", s);
                    if (pyResult != null && !pyResult.toString().equals("")) {
                        String[] split = pyResult.toString().split(";");
                        suggestions.addAll(Arrays.asList(split));
                        listView.invalidateViews();
                    }
                }
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String value = (String) parent.getItemAtPosition(position);
            searchView.setQuery(value, true);
        });


        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.GERMANY);
            }
        });

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        play.setOnClickListener(v -> {

            if (!textToSpeech.isSpeaking()) {

                int next = 200;
                int pos = 0;
                String text = article.getRepresentation();
                while (true) {
                    new StringBuilder();
                    StringBuilder temp;
                    Log.e("In loop", "" + pos);
                    try {
                        temp = new StringBuilder(text.substring(pos, next));
                        int counter = 0;
                        pos += 200;
                        next = next + 200;

                        while (text.charAt(pos + counter - 1) != '.') {
                            //while (!Character.isWhitespace(text.charAt(pos + counter))) {
                            temp.append(text.charAt(pos + counter++));
                        }

                        pos += counter;
                        next += counter;

                        HashMap<String, String> params = new HashMap<>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, temp.toString());
                        int res = textToSpeech.synthesizeToFile(temp.toString(), params,
                                Environment.getDataDirectory().getAbsolutePath() + "/bla.mp3");
                        if (res == TextToSpeech.SUCCESS) {
                            System.out.println("Erfolg!");
                        } else {
                            System.out.println("Kein Erfolg");
                        }
                        textToSpeech.speak(temp.toString(), TextToSpeech.QUEUE_ADD, params);

                    } catch (Exception e) {
                        temp = new StringBuilder(text.substring(pos));
                        textToSpeech.speak(temp.toString(), TextToSpeech.QUEUE_ADD, null);
                        break;
                    }

                }
            }

            //Intent intent = new Intent(MainActivity.this, MainActivity.class);
            //PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

            //create notification while playing
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,
                    "My Notification")
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("WikiReader")
                    .setContentText("Audio wird abgespielt")
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSubText(article.getTitle());


            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            managerCompat.notify(1, builder.build());

        });

        stop.setOnClickListener(v -> {
            if (textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(1);
            }
        });


    }
}