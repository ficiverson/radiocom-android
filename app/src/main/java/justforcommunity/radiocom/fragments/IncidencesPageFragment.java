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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import justforcommunity.radiocom.activities.CreateIncidence;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.activities.Incidence;
import justforcommunity.radiocom.adapters.IncidenceListAdapter;
import justforcommunity.radiocom.model.IncidenceDTO;
import justforcommunity.radiocom.task.GetIncidences;
import justforcommunity.radiocom.utils.GlobalValues;

import static justforcommunity.radiocom.utils.GlobalValues.INCIDENCE_JSON;


public class IncidencesPageFragment extends Fragment {

    private Home mActivity;
    private Context mContext;
    private ListView incidenceList;
    private TextView noElements;
    private IncidenceListAdapter myAdapterIncidences;
    private AVLoadingIndicatorView avi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_incidences, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        incidenceList = (ListView) v.findViewById(R.id.incidenceList);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        // Get Incidences
        GetIncidences gp = new GetIncidences(mContext, this);
        gp.execute();

        // Float button to create new incidence
        FloatingActionButton button_create = (FloatingActionButton) v.findViewById(R.id.button_create);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch next activity
                Intent intent = new Intent(mActivity, CreateIncidence.class);
                startActivity(intent);
            }
        });

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.incidence_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

    public void resultKO() {
        avi.hide();
        noElements.setVisibility(View.VISIBLE);
        incidenceList.setVisibility(View.GONE);
        avi.setVisibility(View.GONE);
    }

    public void filterDataSearch(String query) {
        if (myAdapterIncidences != null) {
            myAdapterIncidences.getFilter().filter(query);
        }
    }

    public void setIncidenceList(final List<IncidenceDTO> incidences) {
        avi.hide();

        if (incidences == null || incidences.size() == 0) {
            noElements.setVisibility(View.VISIBLE);
            incidenceList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);
        } else {
            noElements.setVisibility(View.GONE);
            incidenceList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            myAdapterIncidences = new IncidenceListAdapter(mActivity, mContext, R.layout.listitem_new, incidences);
            incidenceList.setAdapter(myAdapterIncidences);

            incidenceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //serialize object station
                    Gson gson = new Gson();
                    String jsonInString = gson.toJson(myAdapterIncidences.getItem(position));

                    //save station object on prefs
                    SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("jsonIncidence", jsonInString);
                    edit.apply();

                    //launch next activity
                    Intent intent = new Intent(mActivity, Incidence.class);
                    intent.putExtra(INCIDENCE_JSON, jsonInString);
                    startActivity(intent);
                }
            });
        }
    }
}