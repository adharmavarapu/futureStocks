package com.adharmavarapu.futurestocks;

import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity {
    Button add;
    SearchView search;
    TextView company;
    TextView price;
    TextView rate;
    stockCard card;
    stockCard card1;
    ArrayList<stockCard> cards;
    int i;
    String resultName;
    String symbol;
    int click;
    Relevance r;
    ArrayList<Double> relValues = new ArrayList<>();
    stockAnalysis sA;
    descriptionTask dT;
    ArrayList<String> Descriptions;
    double Cprice;
    double prevPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cards = new ArrayList<>();
        r = new Relevance();
        card = findViewById(R.id.stock);
        sA = new stockAnalysis();
        dT = new descriptionTask();
        i = 0;
        click = 1;
        add = findViewById(R.id.addButton);
        add.setText("+");
        Descriptions = new ArrayList<String>();
        search = findViewById(R.id.search);
        search.setVisibility(View.INVISIBLE);
        company = card.findViewById(R.id.company);
        rate = card.findViewById(R.id.rate);
        price = card.findViewById(R.id.price);
    }
    protected void onStart() {
        super.onStart();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click >= 1) {
                    if(i > 0){
                        createCard();
                    }
                    company.setText("");
                    price.setText("");
                    rate.setText("");
                    search.setVisibility(View.VISIBLE);
                    search.setQueryHint("Company Name");
                    search.setIconified(false);
                    add.setText("-");
                    click = 0;
                } else if (click == 0) {
                    search.setVisibility(View.INVISIBLE);
                    add.setText("+");
                    click++;
                }
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                resultName = newText;
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                resultName = query;
                r.main(resultName);
                return true;
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setSymbol();
    }
    protected void createSymbolList(String result) throws ParseException {
        ArrayList<String> symbols = new ArrayList<>();
        symbols.add("Symbol");
        Log.d("IndexOf",  Integer.toString(result.indexOf("[")));
        Object obj = new JSONParser().parse(result);
        JSONObject jo = (JSONObject) obj;
        JSONArray ja = (JSONArray) jo.get("bestMatches");
        Iterator itr2 = ja.iterator();

        while (itr2.hasNext())
        {
            Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
            while (itr1.hasNext()) {
                Map.Entry pair = itr1.next();
                if(((String) pair.getKey()).equals("1. symbol")) {
                    symbols.add((String) pair.getValue());
                }
            }
        }
        ArrayList<String> name = new ArrayList<>();
        name.add("Company");
        Iterator itr4 = ja.iterator();
        while (itr4.hasNext())
        {
            Iterator<Map.Entry> itr3 = ((Map) itr4.next()).entrySet().iterator();
            while (itr3.hasNext()) {
                Map.Entry pair = itr3.next();
                if(((String) pair.getKey()).equals("2. name")) {
                    name.add((String) pair.getValue());
                }
            }
        }
        Intent intent = new Intent(getBaseContext(), List.class);
        intent.putExtra("Symbols", symbols);
        intent.putExtra("Names", name);
        startActivity(intent);
    }
    public void setSymbol(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("WORKS", "WORKS");
            symbol = getIntent().getStringExtra("Symbol");
            company.setText(symbol);
            stockAnalysis sa = new stockAnalysis();
            sa.execute("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + BuildConfig.AVKey);
        }
    }
    public void resetButton(){
        search.setVisibility(View.INVISIBLE);
        add.setText("+");
        click++;
    }
    public void createCard(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        card1 = new stockCard(this);
        View originalCard = card;
        ViewGroup insertPoint = (ViewGroup) originalCard;
        insertPoint.addView(card1);
        card = card1;
        card.setText("", "", "");
        Log.d("AWESdsFJK", "MOVE DOWN");
        cards.add(card1);
        card = cards.get(cards.size()-1);
        company = card1.findViewById(R.id.company);
        price = card1.findViewById(R.id.price);
        rate = card1.findViewById(R.id.rate);
    }
    private class descriptionTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String wordToFind = "description";
            for (int i = 0; i < result.length() - wordToFind.length(); i++) {
                if (result.substring(i, i + wordToFind.length()).equals("description")) {
                    String n = "url";
                    int k = i;
                    String temp = "";
                    while (!(result.substring(k, k + n.length()).equals(n))) {
                        temp += result.substring(k, k + 1);
                        k++;
                    }
                    Descriptions.add(temp);
                    i++;
                }
                if(Descriptions.size() >= 10){
                    break;
                }
            }
            try {
                r.relevanceRecording();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void getDesc(ArrayList<String> desc1){
            for(String t: Descriptions){
                desc1.add(t);
            }
        }
    }
    private class Relevance {
        String strDate;
        String prevDate;
        String companyName;
        descriptionTask dT;

        public void main(String s) {
            companyName = s;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat mdformat1 = new SimpleDateFormat("dd");
            String strDay = mdformat1.format(calendar.getTime());
            String strDay1 = strDay;
            String strDate = mdformat.format(calendar.getTime()) + "-" + strDay;
            strDate = mdformat.format(calendar.getTime()) + "-" + strDay1;
            Log.d("i", strDate);
            int prevDay = (Integer.parseInt((String) DateFormat.format("dd", calendar.getTime())) - 30);
            String pDay = Integer.toString(prevDay);
            String pMonth = (String) DateFormat.format("MM", calendar.getTime());
            if (prevDay < 0) {
                int prevMonth = Integer.parseInt((String) DateFormat.format("MM", calendar.getTime())) - 1;
                if (prevMonth == 9 || prevMonth == 4 || prevMonth == 6 || prevMonth == 11) {
                    prevDay = 30 + prevDay;
                } else {
                    prevDay = 31 + prevDay;
                }
                if (prevDay < 10) {
                    pDay = "0" + prevDay;
                } else {
                    pDay = Integer.toString(prevDay);
                }
                if (prevMonth < 10) {
                    pMonth = "0" + prevMonth;
                } else {
                    pMonth = Integer.toString(prevMonth);
                }
            }
            prevDate = DateFormat.format("yyyy", calendar.getTime()) + "-" + pMonth + "-" + pDay;
            Log.d("DATE", prevDate);
            sA = new stockAnalysis();
            sA.execute("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + companyName + "&apikey=" + BuildConfig.AVKey);
        }
        public void relevanceRecording(String s) {
            dT = new descriptionTask();
            dT.execute("https://newsapi.org/v2/everything?q=" + companyName + " news" + "&from=" + s + "&to=" + s + "&sortBy=popularity&apiKey="+BuildConfig.NewsKey);
        }
        public void relevanceRecording() {
            int art = Descriptions.size();
            Log.d("PREV", "PREVDATE" + companyName);
            double rel = 0.0;
            double sentiment = 0.0;
            for (int i = 0; i < art; i++) {
                try {
                    sentiment s = new sentiment(Descriptions.get(i));
                    s.analyze();
                    sentiment = s.getSentiment();
                }
                catch(Exception e){
                    sentiment = sentiment;
                }
                rel+=sentiment;
            }
            relValues.add(rel/art);
            Log.d("desfskdfhkjs", Integer.toString(relValues.size()));
            Descriptions = new ArrayList<>();
            relevanceCalculation(Cprice, prevPrice);
        }
        public void relevanceCalculation(double cPrice, double pPrice) {
            //CALCULATION
            double r1 = relValues.get(0);
            double u1 = cPrice;
            double u2 = pPrice;
            double Uslope = (u1-u2)/30;
            double Rslope = r1;
            Log.d("RSLOPE", Rslope + "%");
            Log.d("Val", u1-u2 + "$");
            double rate1 = ((Uslope*60) + (u2 + (u2*Rslope*(Math.abs(Rslope)/Math.abs(u1-u2)))))/u2;
            DecimalFormat df = new DecimalFormat("0.00");
            if(rate1 >= 1){
                rate.setText(df.format((rate1-1)*100.0) + "% Increase");
            }
            else {
                rate.setText(df.format((1-rate1)*100.0) + "% Decrease");
            }
            relValues = new ArrayList<>();
            Descriptions = new ArrayList<>();
            dT.cancel(true);
            sA.cancel(true);
            resetButton();
        }
    }

        public class stockAnalysis extends AsyncTask<String, String, String> {
        String prevDate;
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... strings) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(strings[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();


                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                        Log.d("Response: ", "> " + line);

                    }

                    return buffer.toString();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("d", Integer.toString(company.getText().length()));
                if (company.getText().length() > 0) {
                    try {
                        findPrice(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        createSymbolList(result);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            protected void findPrice(String result) {
                int k = 0;
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM");
                SimpleDateFormat mdformat1 = new SimpleDateFormat("dd");
                String strDay = mdformat1.format(calendar.getTime());
                String strDay1 = strDay;
                String strDate = mdformat.format(calendar.getTime()) + "-" + strDay;
                while(!(result.contains(strDate))){
                    k++;
                    strDay1 = Integer.toString(Integer.parseInt(strDay) - k);
                    if(Integer.parseInt(strDay1) < 10){
                        strDay1 = "0" + strDay1;
                    }
                    strDate = mdformat.format(calendar.getTime()) + "-" + strDay1;
                }
                strDate = mdformat.format(calendar.getTime()) + "-" + strDay1;
                Log.d("i", strDate);
                int prevDay = (Integer.parseInt((String) DateFormat.format("dd", calendar.getTime())) - 30);
                String pDay = Integer.toString(prevDay);
                String pMonth = (String) DateFormat.format("MM", calendar.getTime());
                if (prevDay < 0) {
                    int prevMonth = Integer.parseInt((String) DateFormat.format("MM", calendar.getTime())) - 1;
                    if (prevMonth == 9 || prevMonth == 4 || prevMonth == 6 || prevMonth == 11) {
                        prevDay = 30 + prevDay;
                    } else {
                        prevDay = 31 + prevDay;
                    }
                    if (prevDay < 10) {
                        pDay = "0" + prevDay;
                    } else {
                        pDay = Integer.toString(prevDay);
                    }
                    if (prevMonth < 10) {
                        pMonth = "0" + prevMonth;
                    } else {
                        pMonth = Integer.toString(prevMonth);
                    }
                }
                prevDate = DateFormat.format("yyyy", calendar.getTime()) + "-" + pMonth + "-" + pDay;
                String pDay1 = Integer.toString(Integer.parseInt(pDay));
                k = 0;
                while(!(result.contains(prevDate))) {
                    k++;
                    pDay1 = Integer.toString(Integer.parseInt(strDay) - k);
                    if (Integer.parseInt(pDay1) < 10) {
                        pDay1 = "0" + pDay1;
                    }
                    prevDate = DateFormat.format("yyyy", calendar.getTime()) + "-" + pMonth + "-" + pDay1;
                }
                Log.d("PrevDate", prevDate);
                String prevStock = result.substring(result.indexOf(prevDate));
                prevPrice = Double.parseDouble(prevStock.substring(prevStock.indexOf("high") + 8, prevStock.indexOf("high") + 15));
                String stock = result.substring(result.indexOf(strDate));
                Cprice = Double.parseDouble(stock.substring(stock.indexOf("high") + 8, stock.indexOf("high") + 15));
                price.setText("$" + Cprice + " USD");
                rate.setText("Calculating...");
                r.relevanceRecording(strDate);
                Log.d("VALUEI", Integer.toString(i));
                if(i > 0){
                    for(int j = 1; j <= i; j++) {
                        cards.get(cards.size() - 1).moveDown();
                    }
                }
                i++;
            }
        }
}

