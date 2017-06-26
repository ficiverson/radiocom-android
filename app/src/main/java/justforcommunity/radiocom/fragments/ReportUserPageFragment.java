/*
 *
 *  * Copyright (C) 2017 @ Pablo Grela
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
import justforcommunity.radiocom.activities.CreateReport;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.task.Report.GetReports;

import static justforcommunity.radiocom.utils.GlobalValues.REPORT_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.REST_URL;
import static justforcommunity.radiocom.utils.GlobalValues.programsUserURL;
import static justforcommunity.radiocom.utils.GlobalValues.reportsUserURL;


public class ReportUserPageFragment extends ReportPageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_reports, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        reportList = (ListView) v.findViewById(R.id.reportList);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        // Get Reports
        manage = false;
        new GetReports(mContext, this, reportsUserURL).execute();

        // Float button to create new report
        mActivity.fab_media_hide();
        FloatingActionButton button_create = (FloatingActionButton) v.findViewById(R.id.button_create);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CreateReport.class);
                intent.putExtra(REST_URL, programsUserURL);
                startActivityForResult(intent, REPORT_REQUEST);
            }
        });

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.report_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

}