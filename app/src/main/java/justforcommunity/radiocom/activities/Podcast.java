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

package justforcommunity.radiocom.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.adapters.PodcastListAdapter;
import justforcommunity.radiocom.adapters.ProgramListAdapter;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;
import justforcommunity.radiocom.utils.StreamingService;
import justforcommunity.radiocom.views.CircleTransform;

/**
 * Created by iver on 5/9/16.
 */
public class Podcast extends AppCompatActivity{

    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;
    private ProgramDTO program;
    private AVLoadingIndicatorView avi;
    private RecyclerView programslist;
    private TextView noElements;
    private ProgramListAdapter myAdapterProgram;
    private Context mContext;
    private Podcast mActivity;
    private ImageView image_podcast_bck;
    private Dialog myDialog;

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

        if(jsonPodcast!=null && jsonPodcast!="") {
            program = gson.fromJson(jsonPodcast, ProgramDTO.class);
        }
        else{
            //take last podcast selected
            program = gson.fromJson(prefs.getString("jsonPodcast",""),ProgramDTO.class);
        }

        if(program!=null) {
            getSupportActionBar().setTitle(program.getTitle());

            programslist = (RecyclerView)findViewById(R.id.programslist);
            noElements = (TextView) findViewById(R.id.no_elements);
            image_podcast_bck  = (ImageView)findViewById(R.id.image_podcast_bck);


            Picasso.with(mContext).load(program.getLogo_url()).into(image_podcast_bck);

            avi = (AVLoadingIndicatorView)findViewById(R.id.avi);
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

            PkRSS.with(this).load(program.getRss_url()).callback(callback).async();
        }

    }

    public void listEpisodes(final List<Article> episodes){
        avi.hide();
        if(episodes==null || episodes.size()==0){
            noElements.setVisibility(View.VISIBLE);
            programslist.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);
        }
        else {
            noElements.setVisibility(View.GONE);
            programslist.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            programslist.setNestedScrollingEnabled(false);

            myAdapterProgram = new ProgramListAdapter(mActivity, mContext, episodes);

            LinearLayoutManager layoutManager=new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            programslist.setLayoutManager(layoutManager);
            programslist.setItemAnimator(new DefaultItemAnimator());
            programslist.setAdapter(myAdapterProgram);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if(intent!=null) {//stop media player
            if(intent.getBooleanExtra("stopService",false)) {
                if(!intent.getBooleanExtra("notificationSkip",false)) {
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
     * @param stringId
     */
    public void showLoadingDialog(int stringId){
        //displaying loading asyncTask message
        myDialog= new AlertDialog.Builder(mContext)
                .setMessage(stringId)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void hideDialog(){
        //closing loading asyncTask message
        myDialog.dismiss();
    }

    public void notifyAdapterToRefresh(){
        if(myAdapterProgram!=null) {
            myAdapterProgram.notifyDataSetChanged();
        }
    }
}
