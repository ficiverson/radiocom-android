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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.CreateReserve;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.activities.Reserve;
import justforcommunity.radiocom.adapters.ReserveListAdapter;
import justforcommunity.radiocom.model.ReserveDTO;
import justforcommunity.radiocom.task.Reserve.Reserve.GetReserves;
import justforcommunity.radiocom.utils.FileUtils;
import justforcommunity.radiocom.utils.GlobalValues;

import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;
import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_ANSWER_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.REST_URL;
import static justforcommunity.radiocom.utils.GlobalValues.elementsURL;
import static justforcommunity.radiocom.utils.GlobalValues.reservesURL;


public class ReservePageFragment extends FilterFragment {

    protected Home mActivity;
    protected Context mContext;
    protected ListView reserveList;
    protected TextView noElements;
    protected ReserveListAdapter myAdapterReserves;
    protected AVLoadingIndicatorView avi;
    protected List<ReserveDTO> reserves;
    protected boolean manage;

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
        this.manage = true;
        GetReserves gp = new GetReserves(mContext, this, reservesURL);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == RESERVE_REQUEST || requestCode == RESERVE_ANSWER_REQUEST)) {
            ReserveDTO reserve = new Gson().fromJson((String) data.getExtras().get(RESERVE_JSON), ReserveDTO.class);
            if (requestCode == RESERVE_ANSWER_REQUEST) {
                for (ReserveDTO aux : new ArrayList<>(reserves)) {
                    if (aux.getId().equals(reserve.getId())) {
                        reserves.remove(aux);
                    }
                }
            }
            if (!manage || reserve.getState().equals(FileUtils.states.MANAGEMENT.toString())) {
                reserves.add(0, reserve);
            }
            setReserveList(reserves);
        }
    }

    @Override
    public void filterDataSearch(String query) {
        if (myAdapterReserves != null) {
            myAdapterReserves.getFilter().filter(query);
        }
    }

    public void resultKO() {
        avi.hide();
        noElements.setVisibility(View.VISIBLE);
        reserveList.setVisibility(View.GONE);
        avi.setVisibility(View.GONE);
    }

    public void setReserveList(final List<ReserveDTO> reserves) {
        avi.hide();
        this.reserves = reserves;

        if (reserves == null || reserves.size() == 0) {
            noElements.setVisibility(View.VISIBLE);
            reserveList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);
        } else {
            noElements.setVisibility(View.GONE);
            reserveList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            myAdapterReserves = new ReserveListAdapter(mActivity, mContext, R.layout.listitem_new, reserves, manage);
            reserveList.setAdapter(myAdapterReserves);

            reserveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //serialize object reserve
                    Gson gson = new Gson();
                    String jsonInString = gson.toJson(myAdapterReserves.getItem(position));

                    //save reserve object on prefs
                    SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(RESERVE_JSON, jsonInString);
                    edit.apply();

                    //launch next activity
                    Intent intent = new Intent(mActivity, Reserve.class);
                    intent.putExtra(RESERVE_JSON, jsonInString);
                    intent.putExtra(MANAGE, manage);
                    startActivityForResult(intent, RESERVE_ANSWER_REQUEST);
                }
            });
        }
    }
}