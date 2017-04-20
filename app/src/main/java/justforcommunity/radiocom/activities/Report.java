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

package justforcommunity.radiocom.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.task.FirebaseUtils;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.utils.FileUtils;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;

import static justforcommunity.radiocom.utils.GlobalValues.REPORT_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.imageReportURL;

public class Report extends FirebaseActivity {

    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;
    private ReportDTO report;
    private AVLoadingIndicatorView avi;
    private Context mContext;
    private Report mActivity;
    private LinearLayout imagesReport;
    private Dialog myDialog;

    private TextView dateCreate;
    private TextView programName;
    private TextView tidy;
    private TextView dirt;
    private TextView openDoor;
    private TextView viewMembers;
    private TextView configuration;
    private TextView location;
    private TextView description;
    private TextView dateRevision;
    private TextView active;
    private TextView answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mActivity = this;
        mContext = this;

        prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();


        String jsonReport = getIntent().getStringExtra(REPORT_JSON);
        Gson gson = new Gson();

        if (jsonReport != null && jsonReport != "") {
            report = gson.fromJson(jsonReport, ReportDTO.class);
        } else {
            //take last report selected
            report = gson.fromJson(prefs.getString(REPORT_JSON, ""), ReportDTO.class);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);

        dateCreate = (TextView) bottomSheet.findViewById(R.id.dateCreate);
        programName = (TextView) bottomSheet.findViewById(R.id.programName);
        tidy = (TextView) bottomSheet.findViewById(R.id.tidy);
        dirt = (TextView) bottomSheet.findViewById(R.id.dirt);
        configuration = (TextView) bottomSheet.findViewById(R.id.configuration);
        openDoor = (TextView) bottomSheet.findViewById(R.id.openDoor);
        viewMembers = (TextView) bottomSheet.findViewById(R.id.viewMembers);
        location = (TextView) bottomSheet.findViewById(R.id.location);
        description = (TextView) bottomSheet.findViewById(R.id.description);
        dateRevision = (TextView) bottomSheet.findViewById(R.id.dateRevision);
        active = (TextView) bottomSheet.findViewById(R.id.active);
        answer = (TextView) bottomSheet.findViewById(R.id.answer);

        if (report != null) {
            getSupportActionBar().setTitle(DateUtils.formatDate(report.getDateCreate(), DateUtils.FORMAT_DISPLAY));

            dateCreate.setText(DateUtils.formatDate(report.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            programName.setText(String.valueOf(report.getProgram().getName()));
            tidy.setText(String.valueOf(report.getTidy()));
            dirt.setText(String.valueOf(report.getDirt()));
            configuration.setText(String.valueOf(report.getConfiguration()));
            openDoor.setText(FileUtils.formatBoolean(mContext, report.isOpenDoor()));
            viewMembers.setText(FileUtils.formatBoolean(mContext, report.isViewMembers()));
            location.setText(report.getLocation());
            description.setText(report.getDescription());
            dateRevision.setText(DateUtils.formatDate(report.getDateRevision(), DateUtils.FORMAT_DISPLAY));
            active.setText(FileUtils.formatBoolean(mContext, report.isActive()));
            answer.setText(report.getAnswer());
            imagesReport = (LinearLayout) findViewById(R.id.images_report);

            // get token firebase
            FirebaseUtils firebase = new FirebaseUtils(this);
            firebase.execute();

            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            avi.show();
        }

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.report_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void setToken(String token) {
        for (String nameFile : report.getFiles()) {
            String url = imageReportURL + report.getId() + "?imageName=" + nameFile + "&token=" + token;

            // ImageView image = new ImageView(this);
            ImageView image = newImageView();
            imagesReport.addView(image);
            Picasso.with(mContext).load(url).into(image);
        }
    }

    // Create new ImageView with parameters
    private ImageView newImageView() {
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {//stop media player
            if (intent.getBooleanExtra("stopService", false)) {
                if (!intent.getBooleanExtra("notificationSkip", false)) {
                    Intent i = new Intent(Report.this, PodcastingService.class);
                    stopService(i);
                    //notifyAdapterToRefresh();
                }
            }
        }
        super.onNewIntent(intent);
    }


    /**
     * function to show a dialog popup while asynctask is working
     *
     * @param stringId
     */
    public void showLoadingDialog(int stringId) {
        //displaying loading asyncTask message
        myDialog = new AlertDialog.Builder(mContext)
                .setMessage(stringId)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void hideDialog() {
        //closing loading asyncTask message
        myDialog.dismiss();
    }

}
