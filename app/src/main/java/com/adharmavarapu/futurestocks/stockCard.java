package com.adharmavarapu.futurestocks;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.jar.Attributes;

public class stockCard extends RelativeLayout {
    public TextView company1;
    public TextView price1;
    public TextView rate1;
    public stockCard(Context context){
        super(context);
        initializeViews(context);
    }
    public stockCard(Context context, AttributeSet a){
        super(context, a);
        initializeViews(context);
    }
    public stockCard(Context context, AttributeSet attrs, int dS) {
        super(context, attrs, dS);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.stock, this);
        company1 = this.findViewById(R.id.company);
        price1 = this.findViewById(R.id.price);
        rate1 = this.findViewById(R.id.rate);
    }
    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        company1.setText("Company Name");
        price1.setText("USD");
        rate1.setText("%");
    }
    public void moveDown() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) company1.getLayoutParams();
        params.setMargins(0, params.topMargin + 470, 0, 0); //substitute parameters for left, top, right, bottom
        company1.setLayoutParams(params);
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) price1.getLayoutParams();
        params1.setMargins(0, params1.topMargin + 470, 0, 0);
        price1.setLayoutParams(params1);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) rate1.getLayoutParams();
        params2.setMargins(0, params2.topMargin + 470, 0, 0);
        rate1.setLayoutParams(params2);
        Log.d("AWESdsFJK", "MOVE DOWN");
    }
    public void setText(String c, String p, String r){
        company1.setText(c);
        price1.setText(p);
        rate1.setText(r);
    }
}
