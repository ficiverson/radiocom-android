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
import justforcommunity.radiocom.model.IncidenceDTO;
import justforcommunity.radiocom.task.FirebaseUtils;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;
import justforcommunity.radiocom.utils.FileUtils;

import static justforcommunity.radiocom.utils.GlobalValues.INCIDENCE_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.imageIncidenceURL;

public class Incidence extends FirebaseActivity {

    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;
    private IncidenceDTO incidence;
    private AVLoadingIndicatorView avi;
    private Context mContext;
    private Incidence mActivity;
    private LinearLayout imagesIncidence;
    private Dialog myDialog;

    private TextView dateCreate;
    private TextView programName;
    private TextView tidy;
    private TextView dirt;
    private TextView openDoor;
    private TextView viewMembers;
    private TextView configuration;
    private TextView description;
    private TextView dateRevision;
    private TextView active;
    private TextView answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incidence);

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


        String jsonIncidence = getIntent().getStringExtra(INCIDENCE_JSON);
        Gson gson = new Gson();

        if (jsonIncidence != null && jsonIncidence != "") {
            incidence = gson.fromJson(jsonIncidence, IncidenceDTO.class);
        } else {
            //take last incidence selected
            incidence = gson.fromJson(prefs.getString(INCIDENCE_JSON, ""), IncidenceDTO.class);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);

        dateCreate = (TextView) bottomSheet.findViewById(R.id.dateCreate);
        programName = (TextView) bottomSheet.findViewById(R.id.programName);
        tidy = (TextView) bottomSheet.findViewById(R.id.tidy);
        dirt = (TextView) bottomSheet.findViewById(R.id.dirt);
        configuration = (TextView) bottomSheet.findViewById(R.id.configuration);
        openDoor = (TextView) bottomSheet.findViewById(R.id.openDoor);
        viewMembers = (TextView) bottomSheet.findViewById(R.id.viewMembers);
        description = (TextView) bottomSheet.findViewById(R.id.description);
        dateRevision = (TextView) bottomSheet.findViewById(R.id.dateRevision);
        active = (TextView) bottomSheet.findViewById(R.id.active);
        answer = (TextView) bottomSheet.findViewById(R.id.answer);

        if (incidence != null) {
            getSupportActionBar().setTitle(DateUtils.formatDate(incidence.getDateCreate(), DateUtils.FORMAT_DISPLAY));

            dateCreate.setText(DateUtils.formatDate(incidence.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            programName.setText(String.valueOf(incidence.getProgram().getName()));
            tidy.setText(String.valueOf(incidence.getTidy()));
            dirt.setText(String.valueOf(incidence.getDirt()));
            configuration.setText(String.valueOf(incidence.getConfiguration()));
            openDoor.setText(FileUtils.formatBoolean(mContext, incidence.isOpenDoor()));
            viewMembers.setText(FileUtils.formatBoolean(mContext, incidence.isViewMembers()));
            description.setText(incidence.getDescription());
            dateRevision.setText(DateUtils.formatDate(incidence.getDateRevision(), DateUtils.FORMAT_DISPLAY));
            active.setText(FileUtils.formatBoolean(mContext, incidence.isActive()));
            answer.setText(incidence.getAnswer());
            imagesIncidence = (LinearLayout) findViewById(R.id.images_incidence);

            // get token firebase
            FirebaseUtils firebase = new FirebaseUtils(this);
            firebase.execute();

            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            avi.show();
        }

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.incidence_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void setToken(String token) {
        for (String nameFile : incidence.getFiles()) {
            String url = imageIncidenceURL + incidence.getId() + "?imageName=" + nameFile + "&token=" + token;

            // ImageView image = new ImageView(this);
            ImageView image = newImageView();
            imagesIncidence.addView(image);
            Picasso.with(mContext).load(url).into(image);
        }
    }

    // Create new ImageView with parameters
    private ImageView newImageView() {
        ImageView imageView = new ImageView(this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, 0, 0, 10);
//        lp.gravity = Gravity.FILL_HORIZONTAL;
//        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
//        imageView.setMinimumHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
//        imageView.setScaleType(ImageView.ScaleType.CENTER);
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
                    Intent i = new Intent(Incidence.this, PodcastingService.class);
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
