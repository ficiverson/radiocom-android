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

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.fragments.HomePageFragment;
import justforcommunity.radiocom.fragments.NoticiasPageFragment;
import justforcommunity.radiocom.fragments.PodcastPageFragment;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.StreamingService;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private StationDTO station;
    private Boolean playing=false;
    private NavigationView navigationView;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private FloatingActionButton fab_media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mContext= this;

        prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab_media =  (FloatingActionButton)findViewById(R.id.fab_media);
        fab_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioController();
            }
        });

        if(prefs.getBoolean("isMediaPlaying",false)){//playing is on so we neeed to put menu correctly
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming_stop));
            navigationView.getMenu().getItem(2).setIcon(R.drawable.streamingstop);
            fab_media.setImageResource(R.drawable.streamingstopwhite);
            playing=true;
        }


        String jsonStation = getIntent().getStringExtra(GlobalValues.EXTRA_MESSAGE);
        Gson gson = new Gson();
        if(jsonStation!=null && jsonStation!="") {
            station = gson.fromJson(jsonStation, StationDTO.class);
        }
        else{
            station = gson.fromJson(prefs.getString("jsonStation",""),StationDTO.class);
        }

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HomePageFragment homeFragment = new HomePageFragment();
        homeFragment.setStation(station);
        fragmentTransaction.replace(R.id.content_frame, homeFragment);
        fragmentTransaction.commit();


        getSupportActionBar().setTitle(mContext.getString(R.string.action_station));

        boolean hasPermissionChange = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionChange) {
            edit.putBoolean("writeSDGranted",true);
            edit.commit();
        }

        if(prefs.getBoolean("writeSDGranted",true)) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //do no ask again
                    edit.putBoolean("writeSDGranted",false);
                    edit.commit();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if(intent!=null) {//stop media player
            if(intent.getBooleanExtra("stopService",false)) {
                if(!intent.getBooleanExtra("notificationSkip",false)) {
                    Intent i = new Intent(Home.this, StreamingService.class);
                    stopService(i);
                    navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming));
                    navigationView.getMenu().getItem(2).setIcon(R.drawable.streaming);
                    fab_media.setImageResource(R.drawable.streamingwhite);
                    playing = false;
                    edit.putBoolean("isMediaPlaying", playing);
                    edit.commit();
                }
            }
            else{
                Intent i = new Intent(Home.this, StreamingService.class);
                stopService(i);
                navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming));
                navigationView.getMenu().getItem(2).setIcon(R.drawable.streaming);
                fab_media.setImageResource(R.drawable.streamingwhite);
                playing = false;
                edit.putBoolean("isMediaPlaying",playing);
                edit.commit();
                finish();
            }
        }

        super.onNewIntent(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_emisora) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            HomePageFragment homeFragment = new HomePageFragment();
            homeFragment.setStation(station);
            fragmentTransaction.replace(R.id.content_frame, homeFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(mContext.getString(R.string.action_station));

        } else if (id == R.id.nav_galeria) {

            Gson gson = new Gson();
            String jsonInString = gson.toJson(station);

            Intent intent = new Intent(this, Gallery.class);
            intent.putExtra(GlobalValues.EXTRA_MESSAGE, jsonInString);
            startActivity(intent);


        } else if (id == R.id.nav_emision) {

            audioController();

        } else if (id == R.id.nav_noticias) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            NoticiasPageFragment noticiasFragment = new NoticiasPageFragment();
            noticiasFragment.setStation(station);
            fragmentTransaction.replace(R.id.content_frame, noticiasFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(mContext.getString(R.string.action_news));

        } else if (id == R.id.nav_podcast) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            PodcastPageFragment podcastFragment = new PodcastPageFragment();
            fragmentTransaction.replace(R.id.content_frame, podcastFragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(mContext.getString(R.string.action_podcast));

        }else if (id == R.id.nav_twitter) {
            Intent intent = null;
            try {
                // get the Twitter app if possible
                mContext.getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name="+GlobalValues.twitterName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                // no Twitter app, revert to browser
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+GlobalValues.twitterName));
            }
            startActivity(intent);

        }else if (id == R.id.nav_facebook) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalValues.facebookName));
            startActivity(browserIntent);
        }
        else if (id == R.id.nav_cuac) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalValues.baseURLWEB));
            startActivity(browserIntent);
        }
        else if (id == R.id.nav_dev) {

            AlertDialog.Builder bld = new AlertDialog.Builder(mContext);
            bld.setTitle(mContext.getString(R.string.titledialog));
            bld.setMessage(mContext.getString(R.string.msgdialog));
            bld.setCancelable(true);

            bld.setPositiveButton(
                    mContext.getString(R.string.okdialog),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = bld.create();
            alert11.show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void audioController() {
        if (!playing) {
            Intent localIntent2 = new Intent(Home.this, StreamingService.class);
            localIntent2.putExtra("audio", station.getStream_url());
            localIntent2.putExtra("text", getCityByCoords(station.getLatitude(),station.getLongitude()));
            localIntent2.putExtra("title", station.getStation_name());
            startService(localIntent2);
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming_stop));
            navigationView.getMenu().getItem(2).setIcon(R.drawable.streamingstop);
            fab_media.setImageResource(R.drawable.streamingstopwhite);
            playing = true;
            edit.putBoolean("isMediaPlaying", playing);
            edit.commit();
        } else {
            Intent i = new Intent(Home.this, StreamingService.class);
            stopService(i);
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming));
            navigationView.getMenu().getItem(2).setIcon(R.drawable.streaming);
            fab_media.setImageResource(R.drawable.streamingwhite);
            playing = false;
            edit.putBoolean("isMediaPlaying", playing);
            edit.commit();
        }
    }

    public String getCityByCoords(String lat,String longi){
        return GlobalValues.city;
    }
}
