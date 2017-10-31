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
import justforcommunity.radiocom.adapters.PodcastListAdapter;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.task.GetPrograms;
import justforcommunity.radiocom.utils.GlobalValues;

import static justforcommunity.radiocom.utils.GlobalValues.JSON_PODCAST;

public class PodcastPageFragment extends FilterFragment {

    private Home mActivity;
    private Context mContext;
    private ListView podcastList;
    private TextView noElements;
    private PodcastListAdapter myAdapterPodcast;
    private AVLoadingIndicatorView avi;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_podcast, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        podcastList = (ListView) v.findViewById(R.id.podcastlist);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        // Get Programs
        new GetPrograms(mContext, this).execute();

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.podcast_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

    public void resultKO() {
        avi.hide();
        noElements.setVisibility(View.VISIBLE);
        podcastList.setVisibility(View.GONE);
        avi.setVisibility(View.GONE);
    }

    @Override
    public void filterDataSearch(String query) {
        if (myAdapterPodcast != null) {
            myAdapterPodcast.getFilter().filter(query);
        }
    }

    public void listChannels(final List<ProgramDTO> programs) {
        avi.hide();

        if (programs == null || programs.isEmpty()) {
            noElements.setVisibility(View.VISIBLE);
            podcastList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);

        } else {
            noElements.setVisibility(View.GONE);
            podcastList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            myAdapterPodcast = new PodcastListAdapter(mContext, R.layout.listitem_new, programs);
            podcastList.setAdapter(myAdapterPodcast);

            podcastList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //serialize object
                    String jsonInString = new Gson().toJson(myAdapterPodcast.getItem(position));

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