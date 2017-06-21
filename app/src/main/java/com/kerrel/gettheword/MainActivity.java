package com.kerrel.gettheword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kerrel.getthewordlibrary.OcrCaptureActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
            startActivityForResult(intent, 1001);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            ArrayList<String> wordsList = data.getStringArrayListExtra("words");
            // 수집한 단어를 보여줘
            showWords(wordsList);
        }
    }

    /**
     * 수집한 단어를 보여줘
     */
    private void showWords(ArrayList<String> wordsList) {
        for (final String word : wordsList) {
            if (regularExpression(word)) {

                getWord(word)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                if (!s.isEmpty()) {
                                    Log.e("TAG", word);
                                    Log.d("TAG", s);
                                }
                            }
                        });

            }
        }
    }

    private Observable getWord(final String word) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                EnglishToKorean englishToKorean = new EnglishToKorean();
                try {
                    subscriber.onNext(englishToKorean.getWord(word));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean regularExpression(String text) {
        return Pattern.matches("^[a-zA-Z]*$", text);
    }
}
