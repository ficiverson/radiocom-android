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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.ContentDetail;
import justforcommunity.radiocom.activities.Gallery;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.adapters.NewsListAdapter;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.utils.GlobalValues;


public class NoticiasPageFragment extends Fragment {

    private StationDTO station;
    private Home mActivity;
    private Context mContext;
    private ListView newslist;
    private TextView noElements;
    private NewsListAdapter myAdapterNews;
    private AVLoadingIndicatorView avi;

    public void setStation(StationDTO station){
        this.station=station;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_noticias, container, false);

        mActivity = (Home)getActivity();
        mContext = getContext();

        newslist = (ListView)v.findViewById(R.id.newslist);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView)v.findViewById(R.id.avi);
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

        PkRSS.with(mContext).load(station.getNews_rss()).callback(callback).async();
        //habria paginacion con .nextPage()


        return v;
    }



    public void listChannels(final List<Article> noticias) {
        avi.hide();
        if(noticias==null || noticias.size()==0){
            noElements.setVisibility(View.VISIBLE);
            newslist.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);
        }
        else {
            noElements.setVisibility(View.GONE);
            newslist.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);


            myAdapterNews = new NewsListAdapter(mActivity, mContext, R.layout.listitem_new, noticias);
            newslist.setAdapter(myAdapterNews);


            newslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String content = noticias.get(position).getContent();
                    if(noticias.get(position).getContent()==null){
                        if(noticias.get(position).getDescription()!=null) {
                            content = noticias.get(position).getDescription();
                        }
                        else{
                            content = getString(R.string.no_content);
                        }
                    }
                    Intent intent = new Intent(mActivity, ContentDetail.class);
                    intent.putExtra(GlobalValues.EXTRA_CONTENT ,content);
                    intent.putExtra(GlobalValues.EXTRA_TITLE,noticias.get(position).getTitle());
                    startActivity(intent);
                }
            });
        }
    }
}