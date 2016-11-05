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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.ContentDetail;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.activities.Podcast;
import justforcommunity.radiocom.adapters.NewsListAdapter;
import justforcommunity.radiocom.adapters.PodcastListAdapter;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.task.GetPrograms;
import justforcommunity.radiocom.utils.GlobalValues;


public class PodcastPageFragment extends Fragment {

    private StationDTO station;
    private Home mActivity;
    private Context mContext;
    private ListView podcastlist;
    private TextView noElements;
    private PodcastListAdapter myAdapterPodcast;
    private AVLoadingIndicatorView avi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_podcast, container, false);

        mActivity = (Home)getActivity();
        mContext = getContext();

        podcastlist = (ListView)v.findViewById(R.id.podcastlist);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView)v.findViewById(R.id.avi);
        avi.show();


        GetPrograms gp =  new GetPrograms(mContext,this);
        gp.execute();


        App appliaction = (App) getActivity().getApplication();
        Tracker mTracker = appliaction.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.podcast_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

    public void resultKO(){
        avi.hide();
        noElements.setVisibility(View.VISIBLE);
        podcastlist.setVisibility(View.GONE);
        avi.setVisibility(View.GONE);
    }


    public void listChannels(final List<ProgramDTO> programas) {
        avi.hide();
        if(programas==null || programas.size()==0){
            noElements.setVisibility(View.VISIBLE);
            podcastlist.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);
        }
        else {
            noElements.setVisibility(View.GONE);
            podcastlist.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);


            myAdapterPodcast = new PodcastListAdapter(mActivity, mContext, R.layout.listitem_new, programas);
            podcastlist.setAdapter(myAdapterPodcast);


            podcastlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //serialize objecy station
                    Gson gson = new Gson();
                    String jsonInString = gson.toJson(programas.get(position));


                    //save station object on prefs
                    SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("jsonPodcast",jsonInString);
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