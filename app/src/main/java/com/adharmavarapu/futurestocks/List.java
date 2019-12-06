package com.adharmavarapu.futurestocks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class List extends Activity implements AdapterView.OnItemSelectedListener {
    int selection;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestions);
        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        Button submit = findViewById(R.id.submit);
        submit.setVisibility(View.INVISIBLE);
        selection = 0;
        ArrayList<String> s = getIntent().getStringArrayListExtra("Symbols");
        ArrayList<String> n = getIntent().getStringArrayListExtra("Names");
        String[] items = new String[s.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = s.get(i)+ "(" + n.get(i) + ")";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String i = String.valueOf(parent.getItemAtPosition(pos));
        String item = i.substring(0, i.indexOf("("));
        Log.d("BOB", item);
        if(selection > 0) {
            Button submit = findViewById(R.id.submit);
            submit.setVisibility(View.VISIBLE);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("Symbol", item);
                    startActivity(intent);
                }
            });
        }
        selection++;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
