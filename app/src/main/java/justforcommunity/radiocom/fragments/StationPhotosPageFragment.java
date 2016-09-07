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

package justforcommunity.radiocom.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Gallery;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.views.CircleTransform;
import justforcommunity.radiocom.views.TouchImageView;

/**
 * Created by appeiros on 8/03/16.
 */
public class StationPhotosPageFragment extends Fragment {

    private Context mContext;
    private Gallery mActivity;
    private String photo;
    private TouchImageView photo_fragment_image;
    private static StationPhotosPageFragment fragment;



    public static StationPhotosPageFragment newInstance(Context mContext,
                                                     String photo, Gallery photAct) {

        fragment = new StationPhotosPageFragment();
        fragment.photo = photo;
        fragment.mActivity=photAct;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_station_photos,container,false);

        mContext = container.getContext();
        mActivity = (Gallery)getActivity();

        photo_fragment_image = (TouchImageView) v.findViewById(R.id.photo_fragment_image);

        Picasso.with(mContext).load(photo).into(photo_fragment_image);

        return v;
    }



}