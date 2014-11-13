package com.marctan.gdgpenangwear;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView text = (TextView) findViewById(R.id.text);
        ImageView img = (ImageView) findViewById(R.id.img);

        if(getIntent().hasExtra("result")){
            String result = getIntent().getStringExtra("result");
            if(result.indexOf("|") != -1){
                String[] words = result.split("\\|");
                for(String word : words){
                    text.setText(text.getText() + word.trim() + "\n");
                }
            }else{
                text.setText(result);
            }

        }else if(getIntent().hasExtra("image")){
            Bitmap image = getIntent().getParcelableExtra("image");
            img.setImageBitmap(image);
        }
    }
}


