package com.esmcegypt.ahmedapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ScreenResult extends AppCompatActivity {

    TextView resultType, percentageOne, percentageTwo, percentageThree, percentageFour;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_result);

        resultType = findViewById(R.id.ResultType);
        percentageOne = findViewById(R.id.PercentageOne);
        percentageTwo = findViewById(R.id.PercentageTwo);
        percentageThree = findViewById(R.id.PercentageThree);
        percentageFour = findViewById(R.id.PercentageFour);
        imageView = findViewById(R.id.imageView);

        resultType.setText(getIntent().getStringExtra("RESULT"));
        percentageOne.setText(getIntent().getStringExtra("PERCENTAGEONE"));
        percentageTwo.setText(getIntent().getStringExtra("PERCENTAGETWO"));
        percentageThree.setText(getIntent().getStringExtra("PERCENTAGETHREE"));
        percentageFour.setText(getIntent().getStringExtra("PERCENTAGEFOUR"));

        Intent iin= getIntent();

        Bundle b = iin.getExtras();

        if(b!=null)
        {
            Bitmap image = (Bitmap) b.get("IMAGE");
            imageView.setImageBitmap(image);
        }

        int x = 0;
        x= 19;
    }
}