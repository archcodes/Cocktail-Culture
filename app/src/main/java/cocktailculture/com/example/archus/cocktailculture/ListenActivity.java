package cocktailculture.com.example.archus.cocktailculture;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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


public class ListenActivity extends Activity implements RecognitionListener {

    public static final String STR_INGREDIENT = "strIngredient";
    public static final String DRINKS = "drinks";
    //private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "MainActivity";

    /*Previously used*//*
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";

    private static final String KWS_SEARCH = "wakeup";
    private static final String NEXT_PHRASE = "next";
    private static final String PREVIOUS_PHRASE = "back";
    private static final String REPEAT_PHRASE = "repeat";*/

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private HashMap<String, Integer> captions;
    private TextToSpeech text2speech;
    protected StringBuilder getJSONContents = new StringBuilder();


    //   private SpeechRecognizer sphinxRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, String.format("Inside Google"));
        setContentView(R.layout.activity_listen);
        //returnedText = (TextView) findViewById(R.id.textView1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
      //  toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
     //   progressBar.setVisibility(View.INVISIBLE);

        new AsyncTaskClass().execute();

       /* int permissionCheck = ContextCompat.checkSelfPermission(ListenActivity.this,
                Manifest.permission.RECORD_AUDIO);
*/
        /*speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);*/


/*        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);

                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);

                }
            }
        });*/

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }

    }

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
        speech.stopListening();
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        /*returnedText.setText(errorMessage);*/

   //     toggleButton.setChecked(false);
        Intent data = new Intent();
        data.putExtra("returnData", errorMessage);
        setResult(RESULT_OK, data);
        super.finish();
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

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        speech.stopListening();
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for(int i=0;i<matches.size();i++) {
            Log.i("Cocktail", matches.get(i));
            if (matches.get(i).contains("next"))
            {
                Toast.makeText(this,"Next",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ListenActivity.this,StepsActivity.class);
                startActivity(intent);
            }
            if (matches.get(i).contains("previous"))
            {
                Toast.makeText(this,"Previous",Toast.LENGTH_LONG).show();
            }
            if (matches.get(i).contains("start"))
            {
                Toast.makeText(this,"Start",Toast.LENGTH_LONG).show();
            }
        }

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

        int b;
        String searchById = "http://www.thecocktaildb.com/api/json/v1/1/lookup.php?i=15113";
        StringBuilder getAPIContent = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(searchById);
        HttpResponse response = httpclient.execute(httpGet);
        InputStream stream = response.getEntity().getContent();

        while ((b = stream.read()) != -1) {
            getAPIContent.append((char) b);
        }

        Log.i("Cocktail : ",getAPIContent.toString());

        JSONObject jsonObject = new JSONObject(getAPIContent.toString());

        //15 because cocktailDB has max 15 ingredients ;)
        for(int j=1;j<15;j++) {
            String strIngredients = ((JSONArray) (jsonObject.get(DRINKS))).getJSONObject(0).get(STR_INGREDIENT + j).toString();

            if (strIngredients.length()>0)
                getJSONContents.append("\u2022 ").append(strIngredients).append("\n\n");
        }

        Log.i("Cocktail_Ingredients : ",getJSONContents.toString());

    }

    private class AsyncTaskClass extends AsyncTask {

        TextView ingredients;

        @Override
        protected Object doInBackground(Object[] params) {
            try {

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

            speech = SpeechRecognizer.createSpeechRecognizer(ListenActivity.this);
            speech.setRecognitionListener(ListenActivity.this);
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,ListenActivity.this.getPackageName());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            speech.startListening(recognizerIntent);
        }

        @Override
        protected void onPostExecute(Object o) {
           // super.onPostExecute(o);
            progressBar.setVisibility(View.GONE);
            ingredients = (TextView)findViewById(R.id.ingredients);
            ingredients.setText(getJSONContents);
        }
    }
}