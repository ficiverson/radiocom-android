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
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.ContentDetail;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.adapters.NewsListAdapter;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.utils.GlobalValues;


public class NewsPageFragment extends Fragment {

    private StationDTO station;
    private Home mActivity;
    private Context mContext;
    private ListView newsList;
    private TextView noElements;
    private NewsListAdapter myAdapterNews;
    private AVLoadingIndicatorView avi;

    public void setStation(StationDTO station) {
        this.station = station;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_news, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        newsList = (ListView) v.findViewById(R.id.newslist);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        Callback callback = new Callback() {

            @Override
            public void onPreload() {
            }

            @Override
            public void onLoaded(List<Article> newArticles) {
                listChannels(newArticles);
            }

            @Override
            public void onLoadFailed() {
                listChannels(null);
            }
        };

        if (!station.getNews_rss().isEmpty()) {
            PkRSS.with(mContext).load(station.getNews_rss()).callback(callback).async();
        } else {
            listChannels(null);
        }

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.news_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

    public void listChannels(final List<Article> news) {
        avi.hide();

        if (news == null || news.isEmpty()) {
            noElements.setVisibility(View.VISIBLE);
            newsList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);

        } else {
            noElements.setVisibility(View.GONE);
            newsList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            myAdapterNews = new NewsListAdapter(mActivity, mContext, R.layout.listitem_new, news);
            newsList.setAdapter(myAdapterNews);

            newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String content = news.get(position).getContent();
                    if (news.get(position).getContent() == null) {
                        if (news.get(position).getDescription() != null) {
                            content = news.get(position).getDescription();
                        } else if (news.get(position).getContent() != null) {
                            content = news.get(position).getContent();
                        } else {
                            content = getString(R.string.no_content);
                        }
                    }
                    Intent intent = new Intent(mActivity, ContentDetail.class);
                    intent.putExtra(GlobalValues.EXTRA_CONTENT, content);
                    intent.putExtra(GlobalValues.EXTRA_TITLE, news.get(position).getTitle());
                    startActivity(intent);
                }
            });
        }
    }
}