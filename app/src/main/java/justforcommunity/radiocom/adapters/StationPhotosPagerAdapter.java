/*
 *
 *  * Copyright (C) 2016 @ Fernando Souto González
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

package justforcommunity.radiocom.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.activities.Gallery;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.fragments.StationPhotosPageFragment;

/**
 * Created by appeiros on 8/03/16.
 */
    public class StationPhotosPagerAdapter extends FragmentPagerAdapter {

        protected List<String> photos;
        private Context mContext;
        private int mCount;
        //we must store fragment to restore pan zoom in /out
        private ArrayList<StationPhotosPageFragment> fragments;
        private Gallery mActivity;

        public StationPhotosPagerAdapter(FragmentManager fm, Gallery activity,
                                         List<String> photos) {
            super(fm);
            this.photos = photos;
            mContext = activity;
            mActivity = activity;
            mCount = photos.size();

            fragments = new ArrayList<StationPhotosPageFragment>();

            for(int i = 0;i<photos.size();i++){
                StationPhotosPageFragment f =
                        StationPhotosPageFragment.newInstance(mContext,photos.get(i),mActivity);

                fragments.add(f);
            }
        }

        @Override
        public StationPhotosPageFragment getItem(int position) {

            return StationPhotosPageFragment.newInstance(mContext,photos.get(position),mActivity);

        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }

        public void setCount(int count) {

            mCount = count;

        }




    }