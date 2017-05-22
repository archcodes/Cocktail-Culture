package cocktailculture.com.example.archus.cocktailculture;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {


    TextView button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);
        button = (TextView) findViewById(R.id.startButton);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(getBaseContext(), "Fill all the fields first", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(WelcomeActivity.this,MenuActivity.class);
        startActivity(intent);
    }
}
