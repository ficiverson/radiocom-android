/*
 *
 *  * Copyright © 2016 @ Fernando Souto González
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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.tutorial.DiscrollvableLastLayout;
import justforcommunity.radiocom.utils.GlobalValues;

public class Tutorial extends AppCompatActivity {

    private Context mContext;
    private String jsonStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);
        mContext = this;
        jsonStation = getIntent().getStringExtra(GlobalValues.EXTRA_MESSAGE);

        DiscrollvableLastLayout continuetutorial = (DiscrollvableLastLayout) findViewById(R.id.continuetutorial);
        continuetutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do not show again
                SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("showTutorial", false);
                edit.commit();

                //launch next activity
                Intent intent = new Intent(mContext, Home.class);
                intent.putExtra(GlobalValues.EXTRA_MESSAGE, jsonStation);
                startActivity(intent);
            }
        });

        App appliaction = (App) getApplication();
        Tracker mTracker = appliaction.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.tutorial_activity));
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

}
