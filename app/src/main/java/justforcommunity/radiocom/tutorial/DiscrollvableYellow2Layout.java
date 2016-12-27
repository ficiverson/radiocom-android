/*
 *
 *  * Copyright (C) 2016 @
 *  *
 *  * Developer Fernando Souto
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package justforcommunity.radiocom.tutorial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import justforcommunity.radiocom.R;
import com.flavienlaurent.discrollview.lib.Discrollvable;

/**
 *
 */
public class DiscrollvableYellow2Layout extends LinearLayout implements Discrollvable {

    private static final String TAG = "DiscrollvablePurpleLayout";

    private View mYellowView1;
    private View mYellowView2;

    private float mYellowView1TranslationX;
    private float mYellowView2TranslationX;

    public DiscrollvableYellow2Layout(Context context) {
        super(context);
    }

    public DiscrollvableYellow2Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscrollvableYellow2Layout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mYellowView1 = findViewById(R.id.purpleView1);
        mYellowView1TranslationX = mYellowView1.getTranslationX();
        mYellowView2 = findViewById(R.id.purpleView2);
        mYellowView2TranslationX = mYellowView2.getTranslationX();
    }

    @Override
    public void onResetDiscrollve() {
        mYellowView1.setAlpha(0);
        mYellowView2.setAlpha(0);
        mYellowView1.setTranslationX(mYellowView1TranslationX);
        mYellowView2.setTranslationX(mYellowView2TranslationX);
    }

    @Override
    public void onDiscrollve(float ratio) {
        if(ratio <= 0.5f) {
            mYellowView2.setTranslationX(0);
            mYellowView2.setAlpha(0.0f);
            float rratio = ratio / 0.5f;
            mYellowView1.setTranslationX(mYellowView1TranslationX * (1 - rratio));
            mYellowView1.setAlpha(rratio);
        } else {
            mYellowView1.setTranslationX(0);
            mYellowView1.setAlpha(1.0f);
            float rratio = (ratio - 0.5f) / 0.5f;
            mYellowView2.setTranslationX(mYellowView2TranslationX * (1 - rratio));
            mYellowView2.setAlpha(rratio);
        }
    }
}
