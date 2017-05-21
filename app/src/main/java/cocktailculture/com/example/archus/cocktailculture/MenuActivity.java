package cocktailculture.com.example.archus.cocktailculture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView next, item;
    TableLayout contentsTable;
    TableRow tr;

    List<String> menuItems = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*next = (TextView) findViewById(R.id.nextButton);
        next.setOnClickListener(this);*/

        contentsTable = (TableLayout) findViewById(R.id.contentsTable);
        tr = (TableRow) findViewById(R.id.row);
        menuItems.add("Mai Tai");
        menuItems.add("Margarita");
        menuItems.add("Sangria");
        menuItems.add("Mimosa");
        menuItems.add("Mojito");
        menuItems.add("Manhattan");
        menuItems.add("Highball");
        menuItems.add("Daiquiri");
        menuItems.add("Cosmopolitan");
        menuItems.add("Pina Colada");
        menuItems.add("Long Island Ice Tea");
        menuItems.add("Screwdriver");
        menuItems.add("White Russian");

        addItems(menuItems);
    }

    private void addItems(List<String> menuItems) {
        for(int i=0; i<menuItems.size(); i++) {
            tr = new TableRow(this);
            //tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
             //       TableRow.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
            item = new TextView(this);
            item.setText(menuItems.get(i));
            item.setTextColor(Color.BLACK);
            item.setTextSize(18);
            tr.addView(item);
            tr.setPadding(0,60,0,10);
            tr.setBackgroundResource(R.drawable.rowstyles);

            tr.setId(i);

            tr.setOnClickListener(MenuActivity.this);
            contentsTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, 150));

           /* TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            params.gravity= Gravity.CENTER;
            contentsTable.addView(tr);
            contentsTable.setLayoutParams(params);*/

           TableLayout.LayoutParams p = new TableLayout.LayoutParams();
            p.setMargins(0,0,0,5);
            tr.setLayoutParams(p);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MenuActivity.this,ListenActivity.class);
        startActivity(intent);
        //Toast.makeText(getApplicationContext(), "You clicked!!!"+menuItems.get(v.getId()), Toast.LENGTH_SHORT).show();
    }
}