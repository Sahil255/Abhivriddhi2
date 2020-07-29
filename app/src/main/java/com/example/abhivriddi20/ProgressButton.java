package com.example.abhivriddi20;


import android.content.Context;

import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton {
    private CardView cardView;
    private ProgressBar progressBar;
    ConstraintLayout layout;
    TextView textView;

    Animation fade_in;
    ProgressButton(Context ct, View view){
        cardView=view.findViewById(R.id.cardView);
        progressBar=view.findViewById(R.id.progressBar);
        textView=view.findViewById(R.id.textView);
        layout=view.findViewById(R.id.constraint_layout);
    }
    public void progressButton_Activated(Context ctx,View view)
    {
        progressBar.setVisibility(View.VISIBLE);
        textView.setCompoundDrawables(null,null,null,null);
        textView.setText("Please Wait..");
    }
    public void progressButton_deactivate(Context ctx,View view)
    {
        progressBar.setVisibility(View.INVISIBLE);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_upload_black_24dp, 0, 0, 0);
        //textView.setCompoundDrawables(null,null,null,null);
        textView.setText(" file complaint");
    }
    public void progressVerify(Context ctx,View v)
    {
        progressBar.setVisibility(View.INVISIBLE);
        textView.setText("Verify");
    }
    public void buttonFinished(Context ctx,View view)
    {
        //layout.setBackgroundColor(cardView.getResources().getColor(R.color.colorAccent));
        progressBar.setVisibility(View.GONE);
        textView.setText("Get Code");
    }
}
