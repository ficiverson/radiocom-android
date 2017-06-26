/*
 *
 *  * Copyright (C) 2017 @ Pablo Grela
 *  *
 *  * Developer Pablo Grela
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

package justforcommunity.radiocom.adapters;

import android.support.v4.app.Fragment;

import justforcommunity.radiocom.fragments.LiveBroadcastPageFragment;

public class DayPage {
    private final CharSequence mTitle;
    private final int mIndicatorColor;
    private final int mDividerColor;
    private final String mDate;

    public DayPage(CharSequence title, int indicatorColor, int dividerColor, String date) {
        mTitle = title;
        mIndicatorColor = indicatorColor;
        mDividerColor = dividerColor;
        mDate = date;
    }

    Fragment createFragment(String date) {
        return LiveBroadcastPageFragment.newInstance(date);
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public String getDate() {
        return mDate;
    }

}