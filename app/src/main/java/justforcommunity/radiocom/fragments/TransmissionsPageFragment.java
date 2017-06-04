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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.activities.Podcast;
import justforcommunity.radiocom.adapters.TransmissionListAdapter;
import justforcommunity.radiocom.model.TransmissionDTO;
import justforcommunity.radiocom.task.Transmissions.GetTransmissions;
import justforcommunity.radiocom.utils.GlobalValues;

import static justforcommunity.radiocom.utils.GlobalValues.JSON_PODCAST;

public class TransmissionsPageFragment extends FilterFragment {

    private Home mActivity;
    private Context mContext;
    private ListView transmissionList;
    private TextView noElements;
    private TransmissionListAdapter transmissionListAdapter;
    private AVLoadingIndicatorView avi;

    private static final String KEY_DAY = "day";

    public static TransmissionsPageFragment newInstance(String date) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DAY, date);

        TransmissionsPageFragment fragment = new TransmissionsPageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_transmissions, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        transmissionList = (ListView) v.findViewById(R.id.transmissionsList);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        // Get Transmissions
        new GetTransmissions(mContext, this, getArguments().getString(KEY_DAY)).execute();

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.transmisions_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

    public void resultKO() {
        avi.hide();
        noElements.setVisibility(View.VISIBLE);
        transmissionList.setVisibility(View.GONE);
        avi.setVisibility(View.GONE);
    }

    @Override
    public void filterDataSearch(String query) {
        if (transmissionListAdapter != null) {
            transmissionListAdapter.getFilter().filter(query);
        }
    }

    public void listChannels(final List<TransmissionDTO> transmissions) {
        avi.hide();

        if (transmissions == null || transmissions.isEmpty()) {
            noElements.setVisibility(View.VISIBLE);
            transmissionList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);

        } else {
            noElements.setVisibility(View.GONE);
            transmissionList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            transmissionListAdapter = new TransmissionListAdapter(mContext, R.layout.listitem_new, transmissions);
            transmissionList.setAdapter(transmissionListAdapter);

            transmissionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //serialize object
                    String jsonInString = new Gson().toJson(transmissionListAdapter.getItem(position));

                    //save json object on prefs
                    SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(JSON_PODCAST, jsonInString);
                    edit.commit();

                    //launch next activity
                    Intent intent = new Intent(mActivity, Podcast.class);
                    intent.putExtra(GlobalValues.EXTRA_PROGRAM, jsonInString);
                    startActivity(intent);
                }
            });
        }
    }
}