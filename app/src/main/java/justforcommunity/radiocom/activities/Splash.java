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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.task.GetStation;
import justforcommunity.radiocom.utils.GlobalValues;

public class Splash extends AppCompatActivity {

    private AVLoadingIndicatorView avi;
    private View snackView;
    private Context mContext;
    private Splash mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;
        mActivity = this;

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.show();

        snackView = (View)findViewById(R.id.snackView);

        launchGetStations(mContext,mActivity);

        App appliaction = (App) getApplication();
        Tracker mTracker = appliaction.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.splash_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    public void launchGetStations(Context contex,Splash activity){
        GetStation stationTask = new GetStation(contex,activity);
        stationTask.execute();
    }


    public void resultOK(StationDTO stationDTO){
        avi.hide();


        //serialize objecy station
        Gson gson = new Gson();
        String jsonInString = gson.toJson(stationDTO);

        //save station object on prefs
        SharedPreferences prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("jsonStation",jsonInString);
        edit.commit();

        //launch next activity
        Intent intent = new Intent(this, Home.class);
        intent.putExtra(GlobalValues.EXTRA_MESSAGE, jsonInString);
        startActivity(intent);
    }

    public void resultKO(){

        Snackbar snackbar = Snackbar
                .make(snackView, "No internet connection!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        launchGetStations(mContext,mActivity);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();

    }


}
