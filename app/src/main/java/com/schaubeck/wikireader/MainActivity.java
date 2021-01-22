package com.schaubeck.wikireader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.schaubeck.wikireader.wikipedia.Article;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button go, stop, play;
    TextToSpeech textToSpeech;
    String pythonResult;
    EditText input;
    TextView result;

    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        go = findViewById(R.id.button);
        play = findViewById(R.id.btnPlay);
        stop = findViewById(R.id.btnStop);
        result = findViewById(R.id.textResult);
        input = findViewById(R.id.textInput);

        result.setMovementMethod(new ScrollingMovementMethod());


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

                //obj = pyobj.callAttr("getArticleBySections", "Star Wars", "de");
                obj = pyobj.callAttr("getArticleBySections", input.getText().toString(), "de");

                article = Article.parseFromPythonResult(input.getText().toString(), obj.toString());

                pythonResult = obj.toString();

                result.setText(article.getRepresentation());
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(article.getSections().get(0).getRepresentation(), TextToSpeech.QUEUE_FLUSH, null);
                textToSpeech.speak(article.getSections().get(1).getRepresentation(), TextToSpeech.QUEUE_FLUSH, null);
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