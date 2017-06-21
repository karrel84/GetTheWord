package com.kerrel.gettheword;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 이주영 on 2017-06-21.
 */

public class EnglishToKorean {
    // 품사들
    private List<String> partList;
    private final String dicUrl = "http://endic.naver.com/search.nhn?sLn=kr&searchOption=all&query=%s";

    public EnglishToKorean() {
        partList = new ArrayList<>();
        partList.add("[명사]");
        partList.add("[동사]");
        partList.add("[대명사]");
        partList.add("[자동사]");
        partList.add("[타동사]");
        partList.add("[조동사]");
        partList.add("[부사]");
        partList.add("[전치사]");
        partList.add("[접속사]");
        partList.add("[감탄사]");
        partList.add("[형용사]");
    }

    public String getWord(String word) throws IOException {
        String url = String.format(dicUrl, word);
        String body = httpRun(url);

        // HTML 구문을 없앤다.
        String textWithoutTag = body.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
        // 라인피드별로 나눈다
        String splitTextWithoutTag[] = textWithoutTag.split("\\n");
        // 뜻이 제대로 있는 단어를 가져온다.
        StringBuilder builder = filterWord(splitTextWithoutTag);
        // 가져온 단어가 없으면 다른 방식으로 단어를 가져와본다.
        if (builder.toString().trim().length() == 0) {
            builder = filterWord2(splitTextWithoutTag);
        }

        return builder.toString();
    }

    /**
     * 품사를 갖고있는 문장을 가져온다.
     */
    private StringBuilder filterWord(String[] splitTextWithoutTag) {
        StringBuilder builder = new StringBuilder();
        String nextWord;
        for (String str : splitTextWithoutTag) {
            nextWord = str.trim();
            // 품사를 돌린다.
            for (String part : partList) {
                // 원하는 품사를 가진 문장이 있으면 추가한다.
                if (partOfSpeech(nextWord, part)) {
                    builder.append(str.trim());
                    builder.append("\n");
                    break;
                }
            }
        }
        return builder;
    }

    /**
     * 1번 필터로 문장을 찾을 수 없으면
     * 2번 필터로 뜻을 찾는다.
     */
    private StringBuilder filterWord2(String[] splitTextWithoutTag) {
        StringBuilder builder = new StringBuilder();
        String nextWord;
        String englishWord = null;
        String tmpPart = null;
        for (String str : splitTextWithoutTag) {
            nextWord = str.trim();
            if (nextWord.length() == 0) continue;
            if (tmpPart != null) {
                // 임시 품사의 값이 있으면
                // 다음문장들을 추가한다.
                builder.append(nextWord);
                builder.append("\n");
                // 한글을 추가한 후 루프를 멈춘다.
                if (nextWord.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                    tmpPart = null;
                }
            } else {
                if (nextWord.matches(".*[a-zA-Z]+.*")) {
                    // 추가된 단어가 없으면 추가하지않는다.
                    if (builder.length() != 0) englishWord = nextWord;
                }

                for (String part : partList) {
                    // 해당 단어에 품사가 포함되어있으면
                    if (nextWord.contains(part)) {
                        // 해당 품사를 추가하고 브레이크한다.
                        tmpPart = part;
                        // 예문이 나온후에 품사가 나오는 경우가있어서
                        // 영어단어를 갖고있다가 품사가 나오면 예문을 저장한다.
                        if (englishWord != null) {
                            builder.append(englishWord);
                            builder.append("\n");
                            englishWord = null;
                        }

                        builder.append(part + " ");
                        break;
                    }
                }
            }
        }
        return builder;
    }

    private boolean partOfSpeech(String word, String part) {
        return word.contains(part) && !word.equals(part);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String httpRun(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
