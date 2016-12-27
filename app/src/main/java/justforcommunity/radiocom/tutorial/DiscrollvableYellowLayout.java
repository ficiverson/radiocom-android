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

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flavienlaurent.discrollview.lib.Discrollvable;

import justforcommunity.radiocom.R;


/**
 *
 */
public class DiscrollvableYellowLayout extends LinearLayout implements Discrollvable {

    private TextView mYellowView1;
    private float mYellowView1TranslationY;
    private int mYellowColor;
    private int mBlackColor = Color.BLACK;
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    public DiscrollvableYellowLayout(Context context) {
        super(context);
    }

    public DiscrollvableYellowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscrollvableYellowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mYellowView1 = (TextView) findViewById(R.id.greenView1);
        mYellowView1TranslationY = mYellowView1.getTranslationY();
        mYellowColor = getResources().getColor(R.color.colorThird);
    }

    @Override
    public void onResetDiscrollve() {
        mYellowView1.setTranslationY(mYellowView1TranslationY);
        mYellowView1.setTextColor(mYellowColor);
        setBackgroundColor(mBlackColor);
    }

    @Override
    public void onDiscrollve(float ratio) {
        mYellowView1.setTranslationY(mYellowView1TranslationY * (1 - ratio));
        if(ratio >= 0.8f) {
            ratio = (ratio - 0.5f) / 0.5f;
            mYellowView1.setTextColor((Integer) mArgbEvaluator.evaluate(ratio, mBlackColor, mYellowColor));
            setBackgroundColor((Integer) mArgbEvaluator.evaluate(ratio, mYellowColor, mBlackColor));
        }
    }
}
