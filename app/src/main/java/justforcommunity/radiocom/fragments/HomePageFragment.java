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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.utils.GlobalValues;


public class HomePageFragment extends Fragment {

    private StationDTO station;
    private Home mActivity;
    private Context mContext;

    public void setStation(StationDTO station){
        this.station=station;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mActivity = (Home)getActivity();
        mContext = getContext();


        try {
            station.getHistory().replace("<body", "<body style=\'text-align:justify;color:gray;background-color:black;\'");
            station.getHistory().replaceAll("style=\".+?\"", "");

        }catch (Exception e) {

        }

        WebView wb = (WebView) v.findViewById(R.id.history_frame);
        wb.setBackgroundColor(Color.WHITE);

//        String fontscript = "<style>@font-face {font-family : 'HelveticaNeue-Light';src:url('file:///android_asset/fonts/"+VariablesGlobalesActivity.GENERICFONT+"');}</style>";
        String fontscript="";
        String script = "<style type='text/css' >p{width:100%;}img{width:100%;height:auto;-webkit-transform: translate3d(0px,0px,0px);}a,h1,h2,h3,h4,h5,h6{color:"+ GlobalValues.colorHTML+";}div,p,span,a {max-width: 100%;}iframe{width:100%;height:auto;}</style>";

        wb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        wb.loadDataWithBaseURL("","<html><head>"+fontscript+script+"</head><body style=\"font-family:HelveticaNeue-Light; \">"+station.getHistory()+"</body></html>", "text/html", "utf-8","");

        wb.getSettings().setDomStorageEnabled(true);
        wb.getSettings().setJavaScriptEnabled(true);

        App appliaction = (App) getActivity().getApplication();
        Tracker mTracker = appliaction.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.home_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        return v;
    }

}