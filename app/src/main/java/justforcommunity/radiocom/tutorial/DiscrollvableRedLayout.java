/*
 *
 *  * Copyright (C) 2016 @ Fernando Souto González
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
import android.widget.FrameLayout;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.views.discrollview.Discrollvable;

public class DiscrollvableRedLayout extends FrameLayout implements Discrollvable {

    private static final String TAG = "DiscrollvableRedLayout";

    private View mRedView1;
    private View mRedView2;

    private float mRedView1TranslationY;

    public DiscrollvableRedLayout(Context context) {
        super(context);
    }

    public DiscrollvableRedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscrollvableRedLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mRedView1 = findViewById(R.id.redView1);
        mRedView1TranslationY = mRedView1.getTranslationY();
        mRedView2 = findViewById(R.id.redView2);
    }

    @Override
    public void onResetDiscrollve() {

    }

    @Override
    public void onDiscrollve(float ratio) {
        if (ratio <= 0.65f) {
            mRedView1.setTranslationY(-1 * (mRedView1.getHeight() / 1.5f) * (ratio / 0.65f));
        } else {
            float rratio = (ratio - 0.65f) / 0.35f;
            rratio = Math.min(rratio, 1.0f);
            mRedView1.setTranslationY(-1 * (mRedView1.getHeight() / 1.5f));
            mRedView2.setAlpha(1 * rratio);
            mRedView2.setScaleX(1.0f * rratio);
            mRedView2.setScaleY(1.0f * rratio);
        }
    }
}
