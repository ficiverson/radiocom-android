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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.ReserveDTO;
import justforcommunity.radiocom.task.Reserve.Reserve.SendAnswerReserve;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.utils.FileUtils;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;

import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;
import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_JSON;

public class Reserve extends AppCompatActivity {

    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;
    private ReserveDTO reserve;
    private AVLoadingIndicatorView avi;
    private Context mContext;
    private Reserve mActivity;
    private Button answer_button;
    private Dialog myDialog;

    private TextView dateCreate;
    private TextView elementName;
    private TextView accountName;
    private TextView dateStart;
    private TextView dateEnd;
    private TextView dateRevision;
    private TextView dateApproval;
    private TextView description;
    private TextView state;
    private TextView active;
    private TextView answer;
    private EditText answer_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

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

        String jsonReserve = getIntent().getStringExtra(RESERVE_JSON);
        Gson gson = new Gson();

        if (jsonReserve != null && jsonReserve != "") {
            reserve = gson.fromJson(jsonReserve, ReserveDTO.class);
        } else {
            //take last reserve selected
            reserve = gson.fromJson(prefs.getString(RESERVE_JSON, ""), ReserveDTO.class);
        }

        // Listener send reserve
        answer_button = (Button) findViewById(R.id.answer_button);
        answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAnswer(null);
            }
        });

        Button accept_button = (Button) this.findViewById(R.id.accept_button);
        Button deny_button = (Button) this.findViewById(R.id.deny_button);

        // Only manager
        if (getIntent().getBooleanExtra(MANAGE, false)) {
            accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAnswer(true);
                }
            });
            deny_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createAnswer(false);
                }
            });
        } else {
            accept_button.setVisibility(View.GONE);
            deny_button.setVisibility(View.GONE);
        }

        dateCreate = (TextView) findViewById(R.id.dateCreate);
        elementName = (TextView) findViewById(R.id.elementName);
        accountName = (TextView) findViewById(R.id.accountName);
        description = (TextView) findViewById(R.id.description);
        dateStart = (TextView) findViewById(R.id.dateStart);
        dateEnd = (TextView) findViewById(R.id.dateEnd);
        dateRevision = (TextView) findViewById(R.id.dateRevision);
        dateApproval = (TextView) findViewById(R.id.dateApproval);
        state = (TextView) findViewById(R.id.state);
        active = (TextView) findViewById(R.id.active);
        answer = (TextView) findViewById(R.id.answer);
        answer_view = (EditText) findViewById(R.id.answer_new);

        if (reserve != null) {
            getSupportActionBar().setTitle(DateUtils.formatDate(reserve.getDateCreate(), DateUtils.FORMAT_DISPLAY));

            dateCreate.setText(DateUtils.formatDate(reserve.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            elementName.setText(reserve.getElement().getName());
            accountName.setText(reserve.getAccount().getFullName());
            description.setText(reserve.getDescription());
            dateStart.setText(DateUtils.formatDate(reserve.getDateStart(), DateUtils.FORMAT_DISPLAY));
            dateEnd.setText(DateUtils.formatDate(reserve.getDateEnd(), DateUtils.FORMAT_DISPLAY));
            dateRevision.setText(DateUtils.formatDate(reserve.getDateRevision(), DateUtils.FORMAT_DISPLAY));
            dateApproval.setText(DateUtils.formatDate(reserve.getDateApproval(), DateUtils.FORMAT_DISPLAY));
            state.setText(FileUtils.getState(mContext, reserve.getState()));
            active.setText(FileUtils.formatBoolean(mContext, reserve.isActive()));
            answer.setText(reserve.getAnswer());

            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            //avi.show();
            avi.hide();
        }

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.reserve_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
                    Intent i = new Intent(Reserve.this, PodcastingService.class);
                    stopService(i);
                    //notifyAdapterToRefresh();
                }
            }
        }
        super.onNewIntent(intent);
    }

    /**
     * function to show a dialog popup while async task is working
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

    // Show toast if send reserve is success
    public void resultOK(ReserveDTO reserve) {
        Toast.makeText(this, getResources().getString(R.string.reserve_send_answer_success), Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESERVE_JSON, new Gson().toJson(reserve));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Show toast if send reserve is fail
    public void resultKO() {
        Toast.makeText(this, getResources().getString(R.string.reserve_send_answer_fail), Toast.LENGTH_SHORT).show();
    }

    // Create new answer with form
    private void createAnswer(Boolean manage) {

        if (TextUtils.isEmpty(answer_view.getText().toString())) {
            answer_view.setError(getResources().getString(R.string.required));
            Toast.makeText(this, getResources().getString(R.string.reserve_complete_fields), Toast.LENGTH_SHORT).show();
        } else {
            SendAnswerReserve sendAnswerReserve = new SendAnswerReserve(mContext, mActivity, reserve.getId(), answer_view.getText().toString(), manage);
            sendAnswerReserve.execute();
        }
    }

}
