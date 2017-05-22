package cocktailculture.com.example.archus.cocktailculture;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    TextView next, item;
    TableLayout contentsTable;
    TableRow tr;
    protected StringBuilder getJSONContents = new StringBuilder();
    public static final String STR_ID = "idDrink";
    public static final String STR_NAME = "strDrink";
    public static final String STR_IMG = "strDrinkThumb";
    public static final String DRINKS = "drinks";
    private ProgressBar progressBar;
    Map<String,ArrayList<String>> mapOfcontents = new HashMap<String,ArrayList<String>>();


    List<String> menuItems = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        new AsyncTaskClass().execute();

        contentsTable = (TableLayout) findViewById(R.id.contentsTable);
        tr = (TableRow) findViewById(R.id.row);
        /*menuItems.add("Mai Tai");
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
        addItems(); */

    }

    private void addItems() {

        int counter = 0;

        for(Map.Entry<String,ArrayList<String>> entrySet : mapOfcontents.entrySet()) {
            tr = new TableRow(this);

            item = new TextView(this);

                item.setText(entrySet.getValue().get(0));
                counter+=2;
                //Log.i("Cocktail VALUES : ", entrySet.getValue().get(counter++));
                Log.i("Cocktail KEY : ", entrySet.getKey());

                item.setTextColor(Color.BLACK);
                item.setTextSize(18);
                tr.addView(item);
                tr.setPadding(0, 60, 0, 10);
                tr.setBackgroundResource(R.drawable.rowstyles);

                tr.setId(Integer.parseInt(entrySet.getKey()));

                tr.setOnClickListener(MenuActivity.this);
                contentsTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, 150));

                TableLayout.LayoutParams p = new TableLayout.LayoutParams();
                p.setMargins(0, 0, 0, 5);
                tr.setLayoutParams(p);
            }
        }

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        Intent intent = new Intent(MenuActivity.this,ListenActivity.class);
        bundle.putString(STR_ID, String.valueOf(v.getId()));
        intent.putExtras(bundle);
        Log.i("Cocktail V.GETID :", String.valueOf(v.getId()));
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "You clicked!!!"+v.getId(), Toast.LENGTH_SHORT).show();
    }

    protected void cocktailDBAPI() throws IOException, JSONException {

        int b;
        String searchById = "http://www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail";
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
        for(int j=0;j<jsonObject.getJSONArray(DRINKS).length();j++) {

            String drinkId = ((JSONArray) (jsonObject.get(DRINKS))).getJSONObject(j).get(STR_ID).toString();
            String drinkName = ((JSONArray) (jsonObject.get(DRINKS))).getJSONObject(j).get(STR_NAME).toString();
            String drinkImg = ((JSONArray) (jsonObject.get(DRINKS))).getJSONObject(j).get(STR_IMG).toString();

            Log.i("Cocktail : ",drinkId);
            Log.i("Cocktail : ",drinkImg);
            Log.i("Cocktail : ",drinkName);
            ArrayList<String> contents = new ArrayList<>();

            contents.add(drinkName);
            contents.add(drinkImg);

            mapOfcontents.put(drinkId,contents);
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

        }

        @Override
        protected void onPostExecute(Object o) {
            // super.onPostExecute(o);
            addItems();
            progressBar.setVisibility(View.GONE);

        }
    }

}