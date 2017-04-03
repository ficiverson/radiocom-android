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
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.fragments.HomePageFragment;
import justforcommunity.radiocom.fragments.NewsPageFragment;
import justforcommunity.radiocom.fragments.PodcastPageFragment;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.model.UserDTO;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.StreamingService;

import static justforcommunity.radiocom.utils.GlobalValues.FIREBASEUSER;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Context mContext;
    private StationDTO station;
    private Boolean playing = false;
    private NavigationView navigationView;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private FloatingActionButton fab_media;
    private Boolean isSearchable = false;
    private SearchView mSearchView;
    private String mSearchQuery;
    private ImageView nav_authenticate;
    private UserDTO userDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mContext = this;
        prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab_media = (FloatingActionButton) findViewById(R.id.fab_media);
        fab_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioController();
            }
        });


        if (prefs.getBoolean("isMediaPlaying", false)) {//playing is on so we neeed to put menu correctly
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming_stop));
            navigationView.getMenu().getItem(2).setIcon(R.drawable.streamingstop);
            fab_media.setImageResource(R.drawable.streamingstopwhite);
            playing = true;
        }


        String jsonStation = getIntent().getStringExtra(GlobalValues.EXTRA_MESSAGE);
        Gson gson = new Gson();
        if (jsonStation != null && jsonStation != "") {
            station = gson.fromJson(jsonStation, StationDTO.class);
        } else {
            station = gson.fromJson(prefs.getString("jsonStation", ""), StationDTO.class);
        }

        //Load the fragment with the server configuration
        switch (station.getLaunch_screen()) {
            case 0:
                loadStation();
                break;
            case 1:
                loadNews();
                break;
            case 2:
                loadPodcast();
                break;
            default:
                loadStation();
                break;
        }

        boolean hasPermissionChange = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionChange) {
            edit.putBoolean("writeSDGranted", true);
            edit.commit();
        }

        if (prefs.getBoolean("writeSDGranted", true)) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        }

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.home_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        updateUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh state after changing from podcasting status
        if (prefs.getBoolean("isMediaPlaying", false) == false) {//playing is off so we neeed to put menu correctly
            navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming));
            navigationView.getMenu().getItem(2).setIcon(R.drawable.streaming);
            fab_media.setImageResource(R.drawable.streamingwhite);
            playing = false;
        }
    }

    @Override
    public void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do no ask again
                    edit.putBoolean("writeSDGranted", false);
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

        if (intent != null) {//stop media player
            if (intent.getBooleanExtra("stopService", false)) {
                if (!intent.getBooleanExtra("notificationSkip", false)) {
                    Intent i = new Intent(Home.this, StreamingService.class);
                    stopService(i);
                    navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming));
                    navigationView.getMenu().getItem(2).setIcon(R.drawable.streaming);
                    fab_media.setImageResource(R.drawable.streamingwhite);
                    playing = false;
                    edit.putBoolean("isMediaPlaying", playing);
                    edit.commit();
                }
            } else {

                Intent i = new Intent(Home.this, StreamingService.class);
                stopService(i);
                navigationView.getMenu().getItem(2).setTitle(getResources().getString(R.string.drawer_item_streaming));
                navigationView.getMenu().getItem(2).setIcon(R.drawable.streaming);
                fab_media.setImageResource(R.drawable.streamingwhite);
                playing = false;
                edit.putBoolean("isMediaPlaying", playing);
                edit.commit();
                finish();
            }
        }

        super.onNewIntent(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!isSearchable) {
            getMenuInflater().inflate(R.menu.home, menu);
        } else {
            getMenuInflater().inflate(R.menu.home_search, menu);

            final MenuItem searchMenuItem = menu.findItem(R.id.menu_item_search);
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
            mSearchView.setQueryHint(getString(R.string.search));


            final View closeButton = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSearchQuery = "";
                    mSearchView.setQuery("", false);
                    filterSearch("");
                }
            });

            MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    if (mSearchQuery != null && !mSearchQuery.isEmpty()) {
                        mSearchView.post(new Runnable() {
                            @Override
                            public void run() {
                                mSearchView.setQuery(mSearchQuery, false);
                            }
                        });
                        filterSearch(mSearchQuery);
                    }
                    return true;
                }
            });

            mSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchQuery = query;
                    filterSearch(mSearchQuery);
                    mSearchView.setQuery(mSearchQuery, false);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }

        //Set the ontouch listener nav_authenticate
        nav_authenticate = (ImageView) findViewById(R.id.nav_authenticate);
        nav_authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAuthenticate();
            }
        });
        return true;
    }

    public void filterSearch(String query) {
        PodcastPageFragment currentFragment = (PodcastPageFragment) getSupportFragmentManager().findFragmentByTag(mContext.getString(R.string.action_podcast));
        if (currentFragment != null && currentFragment.isVisible()) {
            currentFragment.filterDataSearch(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_emisora:
                loadStation();
                break;
            case R.id.nav_galeria:
                loadGallery();
                break;
            case R.id.nav_emision:
                audioController();
                break;
            case R.id.nav_noticias:
                loadNews();
                break;
            case R.id.nav_podcast:
                loadPodcast();
                break;
            case R.id.nav_map:
                loadMap();
                break;
            case R.id.nav_twitter:
                loadTwitter();
                break;
            case R.id.nav_facebook:
                loadFacebook();
                break;
            case R.id.nav_webpage:
                processBuilder(GlobalValues.baseURLWEB);
                break;
            case R.id.nav_cuac:
                processBuilder(GlobalValues.baseURLCUACWEB);
                break;
            case R.id.nav_dev:
                loadDeveloperInfo();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public String getCityByCoords(String lat, String longi) {
        return GlobalValues.city;
    }


    public void loadGallery() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(station);

        Intent intent = new Intent(this, Gallery.class);
        intent.putExtra(GlobalValues.EXTRA_MESSAGE, jsonInString);
        startActivity(intent);
    }

    public void loadAuthenticate() {
        Intent intent = new Intent(this, Authenticate.class);
        startActivity(intent);
    }

    public void loadStation() {
        isSearchable = false;
        invalidateOptionsMenu();
        HomePageFragment homeFragment = new HomePageFragment();
        homeFragment.setStation(station);
        processFragment(homeFragment, mContext.getString(R.string.action_station));
    }

    public void loadNews() {
        isSearchable = false;
        invalidateOptionsMenu();
        NewsPageFragment noticiasFragment = new NewsPageFragment();
        noticiasFragment.setStation(station);
        processFragment(noticiasFragment, mContext.getString(R.string.action_news));
    }

    public void loadPodcast() {
        isSearchable = true;
        invalidateOptionsMenu();
        PodcastPageFragment podcastFragment = new PodcastPageFragment();
        processFragment(podcastFragment, mContext.getString(R.string.action_podcast));
    }

    public void loadMap() {
        try {
            // Uri with a marker at the target, in google maps
            String uriBegin = "geo:" + station.getLatitude() + "," + station.getLongitude();
            String encodedQuery = Uri.encode(station.getStation_name());
            Uri uri = Uri.parse(uriBegin + "?q=" + encodedQuery + "&z=16");

            Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(mapIntent);

        } catch (Exception e) {
            processBuilder(GlobalValues.baseURLWEB);
        }
    }


    public void loadTwitter() {
        try {
            // get the Twitter app if possible
            mContext.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + station.getTwitter_user()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            processBuilder(station.getTwitter_url());
        }
    }

    public void loadFacebook() {
        try {
            mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            int versionCode = mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            // get the Facebook app if possible
            String facebookUri = "";
            if (versionCode >= 3002850) {
                facebookUri = "fb://facewebmodal/f?href=" + station.getFacebook_url();
            } else {
                facebookUri = "fb://page/" + GlobalValues.facebookId;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUri));
            startActivity(browserIntent);
        } catch (Exception e) {
            processBuilder(station.getFacebook_url());
        }
    }

    public void loadDeveloperInfo() {
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

    private void audioController() {
        if (!playing) {
            Intent localIntent2 = new Intent(Home.this, StreamingService.class);
            localIntent2.putExtra("audio", station.getStream_url());
            localIntent2.putExtra("text", getCityByCoords(station.getLatitude(), station.getLongitude()));
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

    private void processFragment(Fragment fragment, String title) {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment, title);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title);
    }

    private void processBuilder(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        builder.enableUrlBarHiding();
        builder.setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        builder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    private void updateUserInfo() {
        Gson gson = new Gson();
        userDTO = gson.fromJson(prefs.getString(FIREBASEUSER, ""), UserDTO.class);

        if (userDTO != null) {
            navigationView.getMenu().findItem(R.id.management).setVisible(true);
            navigationView.getMenu().findItem(R.id.management).setEnabled(true);
        } else {
            navigationView.getMenu().findItem(R.id.management).setVisible(false);
            navigationView.getMenu().findItem(R.id.management).setEnabled(false);
        }

        if (nav_authenticate != null) {
            if (userDTO != null) {
                // Maybe put personal photo user
                //userDTO.getPhotoUrl();
                nav_authenticate.setImageResource(R.drawable.user_active);
            } else {
                nav_authenticate.setImageResource(R.drawable.user_inactive);
            }
        }
    }
}
