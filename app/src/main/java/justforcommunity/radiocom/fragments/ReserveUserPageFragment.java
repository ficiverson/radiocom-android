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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wang.avi.AVLoadingIndicatorView;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.CreateReserve;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.task.Reserve.Reserve.GetReserves;

import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.REST_URL;
import static justforcommunity.radiocom.utils.GlobalValues.elementsURL;
import static justforcommunity.radiocom.utils.GlobalValues.reservesUserURL;


public class ReserveUserPageFragment extends ReservePageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reserves, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        reserveList = (ListView) v.findViewById(R.id.reserveList);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        // Get Reserves
        this.manage = false;
        GetReserves gp = new GetReserves(mContext, this, reservesUserURL);
        gp.execute();

        // Float button to create new reserve
        mActivity.fab_media_hide();
        FloatingActionButton button_create = (FloatingActionButton) v.findViewById(R.id.button_create);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CreateReserve.class);
                intent.putExtra(REST_URL, elementsURL);
                startActivityForResult(intent, RESERVE_REQUEST);
            }
        });

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.reserve_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }
}