/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kerrel.getthewordlibrary;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.kerrel.getthewordlibrary.camera.GraphicOverlay;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 * TODO: Make this implement Detector.Processor<TextBlock> and add text to the GraphicOverlay
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    interface OnTextDetectedListener {
        void onTextDetected(String text);
    }

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    private OnTextDetectedListener mOnTextDetectedListener;

    public void setOnTextDetectedListener(OnTextDetectedListener onTextDetectedListener) {
        mOnTextDetectedListener = onTextDetectedListener;
    }

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
                onTextDetected(item);
            }
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }

    /**
     * 리스너를 통해 발견한 텍스트를 전달한다.
     */
    private void onTextDetected(TextBlock item) {
        if (mOnTextDetectedListener != null)
            mOnTextDetectedListener.onTextDetected(item.getValue());
    }

    // TODO:  Once this implements Detector.Processor<TextBlock>, implement the abstract methods.
}
