package cocktailculture.com.example.archus.cocktailculture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class InstructionsActivity extends Activity implements RecognitionListener {

    public static final String STR_INSTR = "strInstructions";
    public static final String DRINKS = "drinks";
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    TextView steps, drinkName;
    String strName;
    public static final String STR_ID = "idDrink";
    private HashMap<String, Integer> captions;
    private TextToSpeech text2speech;
    protected StringBuilder getJSONContents = new StringBuilder();
    String LOG_TAG = "Cocktail : ";

    Bundle bundle;
    int flag = 0;
    String[] verifySplit;
    TextToSpeech t1;
    TextView instructions;
    AudioManager amanager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        amanager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        steps = (TextView) findViewById(R.id.steps);
        drinkName = (TextView)findViewById(R.id.drinkName);
        bundle = new Bundle();

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        new InstructionsActivity.AsyncTaskClass().execute();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
       // super.onPause();
        /*if(t1 !=null){
            t1.stop();
            //     t1.shutdown();
        }*/
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
        super.onPause();

    }

    /*@Override
    public void onDestroy() {
        if (t1 != null) {
            t1.shutdown();
        }
        super.onDestroy();
    }*/

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        //   toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        //speech.stopListening();
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        /*returnedText.setText(errorMessage);*/
        speech.cancel();
       /* Intent data = new Intent();
        data.putExtra("returnData", errorMessage);
        setResult(RESULT_OK, data);
*/
        //mute audio
        amanager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        speech.startListening(recognizerIntent);
      /*  amanager.setStreamVolume(AudioManager.STREAM_MUSIC,3,AudioManager.FLAG_PLAY_SOUND);
        amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,3,AudioManager.FLAG_PLAY_SOUND);*/
      //  super.finish();
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {

        Log.i(LOG_TAG, "onReadyForSpeech");

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        speech.stopListening();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

 //       flag = bundle.getInt("flag");
       verifySplit = bundle.getStringArray("splitAtDot");


        /*for(int i=0;i<matches.size();i++) {*/

            Log.i("Cocktail", matches.toString());
            if (matches.toString().contains("next")) {
                Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                flag++;
                if (flag < verifySplit.length) {
                    Intent intent = new Intent(InstructionsActivity.this, InstructionsActivity.class);
//                  intent.putExtras(this.getIntent().getExtras());
                    bundle.putInt("flag", flag);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(InstructionsActivity.this, IngredientsActivity.class);
                    bundle.putInt("flag", flag); //For back from Ingredients activity
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            } else if (matches.toString().contains("previous")) {
                Toast.makeText(this,"Previous",Toast.LENGTH_SHORT).show();
                flag--;
                if (flag<0) {
                    Intent intent = new Intent(InstructionsActivity.this,ListenActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(InstructionsActivity.this,InstructionsActivity.class);
                    bundle.putInt("flag", flag);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            } else if (matches.toString().contains("speak")) {
                Toast.makeText(this,"Speak",Toast.LENGTH_SHORT).show();
                Log.i("Cocktail: TALK", "TALK");
                amanager.setStreamVolume(AudioManager.STREAM_MUSIC,7,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,7,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                /*t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.UK);
                        }
                    }
                });*/
              //  HashMap<String, String> map = new HashMap<String, String>();
              //  map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "NOW");
                instructions = (TextView) findViewById(R.id.instructions);
                String toSpeak = instructions.getText().toString();
                //Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
               // t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
                Bundle params = new Bundle();
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
                //Calling same page again
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(t1.isSpeaking()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                amanager.setStreamVolume(AudioManager.STREAM_MUSIC,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                amanager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                /*t1.stop();*/
                /*speech.stopListening();*/
                Intent intent = new Intent(InstructionsActivity.this,InstructionsActivity.class);
                /*bundle.putInt("flag", flag);*/
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            } else {
                //Dont do anything for now
                Intent intent = new Intent(InstructionsActivity.this,InstructionsActivity.class);
                /*bundle.putInt("flag", flag);*/
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            }
        /*}*/

        /*Intent data = new Intent();

        String returnString = matches.get(0);
        data.putExtra("returnData", returnString);

        setResult(RESULT_OK, data);
*/        super.finish();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    protected void cocktailDBAPI() throws IOException, JSONException {

        flag = this.getIntent().getExtras().getInt("flag");

        Log.i("Cocktail FLAG : ", flag+"");

        verifySplit = (String[]) this.getIntent().getExtras().getStringArray("splitAtDot");
        flag = this.getIntent().getExtras().getInt("flag");

        if(flag < verifySplit.length) {

            strName = this.getIntent().getExtras().getString("strName");

            Pattern pat = Pattern.compile("\\s\\s+");
            if (verifySplit[flag] != null &&
                    !verifySplit[flag].isEmpty() &&
                    !verifySplit[flag].matches(pat.pattern()) &&
                    !verifySplit[flag].contains("null"))
                getJSONContents.append("\u2022 ").append(verifySplit[flag]).append("\n\n");
//parse "."
//               flag++;
            bundle.putString(STR_ID, getIntent().getExtras().getString(STR_ID)); //ID
            bundle.putStringArray("splitAtDot", verifySplit);
//               bundle.putInt("flag", flag);
            bundle.putString("strName" , strName);

            Log.i("Cocktail_Instr_2 : ", getJSONContents.toString());

        }

        }


    private class AsyncTaskClass extends AsyncTask {

//        TextView instructions;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Log.i("Cocktail_Async : ", " Here");

                cocktailDBAPI();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            speech = SpeechRecognizer.createSpeechRecognizer(InstructionsActivity.this);
            speech.setRecognitionListener(InstructionsActivity.this);
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,InstructionsActivity.this.getPackageName());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            speech.startListening(recognizerIntent);
        }

        @Override
        protected void onPostExecute(Object o) {
            // super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);
            instructions = (TextView)findViewById(R.id.instructions);
            instructions.setText(getJSONContents);
            steps.setText("Steps");
            drinkName.setText(strName);
        }
    }

}
