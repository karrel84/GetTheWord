package com.kerrel.gettheword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kerrel.getthewordlibrary.OcrCaptureActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.getWords).setOnClickListener(onGetWordsListener);
    }

    private View.OnClickListener onGetWordsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, OcrCaptureActivity.class);
            startActivity(intent);
        }
    };
}
