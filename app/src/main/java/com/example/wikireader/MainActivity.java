package com.example.wikireader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button go, stop, play;
    TextToSpeech textToSpeech;
    String pythonResult;
    EditText input;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        go = findViewById(R.id.button);
        play = findViewById(R.id.btnPlay);
        stop = findViewById(R.id.btnStop);
        result = findViewById(R.id.textResult);
        input = findViewById(R.id.textInput);


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.GERMANY);
                }
            }
        });

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Python py = Python.getInstance();
                PyObject pyobj = py.getModule("main");
                PyObject obj = null;

                obj = pyobj.callAttr("wiki", input.getText().toString());

                pythonResult = obj.toString();

                result.setText(pythonResult);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speech = textToSpeech.speak(pythonResult, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
            }
        });



    }
}