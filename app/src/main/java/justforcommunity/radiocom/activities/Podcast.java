/*
 *
 *  * Copyright © 2016 @ Fernando Souto González
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

package justforcommunity.radiocom.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.adapters.ProgramListAdapter;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;

import static justforcommunity.radiocom.utils.GlobalValues.JSON_PODCAST;

public class Podcast extends AppCompatActivity {

    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;
    private ProgramDTO program;
    private AVLoadingIndicatorView avi;
    private RecyclerView programsList;
    private TextView noElements;
    private ProgramListAdapter myAdapterProgram;
    private Context mContext;
    private Podcast mActivity;
    private ImageView image_podcast_bck;
    private Dialog myDialog;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FloatingActionButton fab_info;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast);

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

        String jsonPodcast = getIntent().getStringExtra(GlobalValues.EXTRA_PROGRAM);
        Gson gson = new Gson();

        if (jsonPodcast != null && jsonPodcast != "") {
            program = gson.fromJson(jsonPodcast, ProgramDTO.class);
        } else {
            //take last podcast selected
            program = gson.fromJson(prefs.getString(JSON_PODCAST, ""), ProgramDTO.class);
        }

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        description = (TextView) bottomSheet.findViewById(R.id.info_data);

        fab_info = (FloatingActionButton) findViewById(R.id.fab_info);
        fab_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        if (program != null) {
            getSupportActionBar().setTitle(program.getName());

            programsList = (RecyclerView) findViewById(R.id.programslist);
            noElements = (TextView) findViewById(R.id.no_elements);
            image_podcast_bck = (ImageView) findViewById(R.id.image_podcast_bck);

            if (program.getDescription() != null) {
                //description.setText(program.getDescription());
                description.setText(Html.fromHtml(program.getDescription()));
            }

            Picasso.with(mContext).load(program.getLogo_url()).into(image_podcast_bck);

            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            avi.show();

            Callback callback = new Callback() {

                @Override
                public void onPreload() {
                }

                @Override
                public void onLoaded(List<Article> newArticles) {
                    listEpisodes(newArticles);
                }

                @Override
                public void onLoadFailed() {
                    listEpisodes(null);
                }
            };

            if (program.getRss_url() != null && !program.getRss_url().isEmpty()) {
                PkRSS.with(this).load(program.getRss_url()).callback(callback).async();
            } else {
                listEpisodes(null);
            }
        }

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.podcast_activity));
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

    public void listEpisodes(final List<Article> episodes) {
        avi.hide();

        if (episodes == null || episodes.size() == 0) {
            noElements.setVisibility(View.VISIBLE);
            programsList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);

        } else {
            noElements.setVisibility(View.GONE);
            programsList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            programsList.setNestedScrollingEnabled(false);

            myAdapterProgram = new ProgramListAdapter(mActivity, mContext, episodes);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            programsList.setLayoutManager(layoutManager);
            programsList.setItemAnimator(new DefaultItemAnimator());
            programsList.setAdapter(myAdapterProgram);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {//stop media player
            if (intent.getBooleanExtra("stopService", false)) {
                if (!intent.getBooleanExtra("notificationSkip", false)) {
                    Intent i = new Intent(Podcast.this, PodcastingService.class);
                    stopService(i);
                    notifyAdapterToRefresh();
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

    public void notifyAdapterToRefresh() {
        if (myAdapterProgram != null) {
            myAdapterProgram.notifyDataSetChanged();
        }
    }
}
