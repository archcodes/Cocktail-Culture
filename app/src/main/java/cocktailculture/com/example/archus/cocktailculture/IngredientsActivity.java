package cocktailculture.com.example.archus.cocktailculture;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity implements RecognitionListener {

    SpeechRecognizer speech;
    private Intent recognizerIntent;
    Intent intent;
    TextView enjoyString;
    public static final String STR_ID = "idDrink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        enjoyString = (TextView)findViewById(R.id.enjoy);
        enjoyString.setText("Done! Enjoy your Drink!");

        speech = SpeechRecognizer.createSpeechRecognizer(IngredientsActivity.this);
        speech.setRecognitionListener(IngredientsActivity.this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,IngredientsActivity.this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
        Bundle bundle;
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
            Log.i("", "destroy");
        }

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i("", "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i("", "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i("", "onEndOfSpeech");
        //   toggleButton.setChecked(false);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        speech.stopListening();
        Log.d("", "FAILED " + errorMessage);

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i("", "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i("", "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i("", "onReadyForSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
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


    @Override
    public void onResults(Bundle results) {
        Log.i("Cocktail ENJOYPAGE", "onResults");
        speech.stopListening();
        /*Toast.makeText(this, speech.toString(),Toast.LENGTH_LONG).show();*/
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for (int i = 0; i < matches.size(); i++) {
            Log.i("Cocktail", matches.get(i));
            if (matches.get(i).contains("next")) {
                Toast.makeText(this, "Next", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(IngredientsActivity.this, MenuActivity.class);
                startActivity(intent);
            }
            if (matches.get(i).contains("previous")) {
                Toast.makeText(this, "Previous", Toast.LENGTH_LONG).show();

                //initialize local variables for the bundle
                int localFlag = getIntent().getExtras().getInt("flag");
                localFlag--;

                Bundle localBundle = new Bundle();
                localBundle.putInt("flag", localFlag);
                localBundle.putStringArray("splitAtDot", getIntent().getExtras().getStringArray("splitAtDot"));
                localBundle.putString("strName", getIntent().getExtras().getString("strName"));
                localBundle.putString(STR_ID, getIntent().getExtras().getString(STR_ID)); //ID

                Intent localIntent = new Intent(IngredientsActivity.this,InstructionsActivity.class);
                localIntent.putExtras(localBundle);
                startActivity(localIntent);
            }
            if (matches.get(i).contains("start")) {
                Toast.makeText(this, "Start", Toast.LENGTH_LONG).show();
            }

        }
    }

}
