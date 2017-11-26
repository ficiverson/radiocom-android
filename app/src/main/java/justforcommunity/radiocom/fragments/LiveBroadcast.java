/*
 *
 *  * Copyright © 2017 @ Pablo Grela
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.adapters.DayPage;
import justforcommunity.radiocom.adapters.PageAdapter;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.views.SlidingTabLayout;

import static justforcommunity.radiocom.utils.DateUtils.formatDate;
import static justforcommunity.radiocom.utils.DateUtils.formatDateWithLocale;

public class LiveBroadcast extends Fragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private List<DayPage> mTabs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale("es", "ES");
        Calendar dateSearch = Calendar.getInstance(locale);
        dateSearch.add(Calendar.DAY_OF_MONTH, -4);
        mTabs = new ArrayList<>();

        // Add pages from previous days
        for (int i = 1; i < 4; i++) {
            dateSearch.add(Calendar.DAY_OF_MONTH, 1);
            mTabs.add(new DayPage(formatDateWithLocale(dateSearch, DateUtils.FORMAT_DAY_WEEK, locale), ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getContext(), R.color.colorPrimary85), formatDate(dateSearch, DateUtils.FORMAT_DATE_GET)));
        }

        // Add page of actual day
        dateSearch.add(Calendar.DAY_OF_MONTH, 1);
        mTabs.add(new DayPage(getString(R.string.tab_today), ContextCompat.getColor(getContext(), R.color.colorAccent), ContextCompat.getColor(getContext(), R.color.colorPrimary85), formatDate(dateSearch, DateUtils.FORMAT_DATE_GET)));

        // Add pages from next days
        for (int i = 1; i < 4; i++) {
            dateSearch.add(Calendar.DAY_OF_MONTH, 1);
            mTabs.add(new DayPage(formatDateWithLocale(dateSearch, DateUtils.FORMAT_DAY_WEEK, locale), ContextCompat.getColor(getContext(), R.color.colorPrimaryDark), ContextCompat.getColor(getContext(), R.color.colorPrimary85), formatDate(dateSearch, DateUtils.FORMAT_DATE_GET)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_transmission, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        PageAdapter pageAdapter = new PageAdapter(getChildFragmentManager(), mTabs);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(3);
        mViewPager.setOffscreenPageLimit(pageAdapter.getCount());

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
}