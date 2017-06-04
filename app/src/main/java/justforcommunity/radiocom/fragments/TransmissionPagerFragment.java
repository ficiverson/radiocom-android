/*
 *
 *  * Copyright (C) 2016 @ Pablo Grela
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

package justforcommunity.radiocom.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.views.SlidingTabLayout;

import static justforcommunity.radiocom.utils.DateUtils.formatDate;


public class TransmissionPagerFragment extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private List<DayPage> mTabs = new ArrayList<>();

    static class DayPage {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;
        private final String mDate;

        DayPage(CharSequence title, int indicatorColor, int dividerColor, String date) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
            mDate = date;
        }

        Fragment createFragment(String date) {
            return TransmissionsPageFragment.newInstance(date);
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }

        String getDate() {
            return mDate;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar dateSearch = Calendar.getInstance();
        dateSearch.add(Calendar.DAY_OF_MONTH, -4);

        // Add pages from previous days
        for (int i = 1; i < 4; i++) {
            dateSearch.add(Calendar.DAY_OF_MONTH, 1);
            mTabs.add(new DayPage(formatDate(dateSearch, DateUtils.FORMAT_DAY_WEEK), Color.RED, Color.GRAY, formatDate(dateSearch, DateUtils.FORMAT_DATE_GET)));
        }

        // Add page of actual day
        dateSearch.add(Calendar.DAY_OF_MONTH, 1);
        mTabs.add(new DayPage(getString(R.string.tab_today), Color.BLUE, Color.GRAY, formatDate(dateSearch, DateUtils.FORMAT_DATE_GET)));

        // Add pages from next days
        for (int i = 1; i < 4; i++) {
            dateSearch.add(Calendar.DAY_OF_MONTH, 1);
            mTabs.add(new DayPage(formatDate(dateSearch, DateUtils.FORMAT_DAY_WEEK), Color.GREEN, Color.GRAY, formatDate(dateSearch, DateUtils.FORMAT_DATE_GET)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_transmission, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TransmissionPagerAdapter(getChildFragmentManager()));
        mViewPager.setCurrentItem(3);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }

        });
    }

    // Class to control number of page
    class TransmissionPagerAdapter extends FragmentPagerAdapter {

        TransmissionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment(mTabs.get(i).getDate());
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }

    }

}