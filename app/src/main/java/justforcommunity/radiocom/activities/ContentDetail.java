/*
 *
 *  * Copyright (C) 2016 @ Appeiros Mobile Development S.L.
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.utils.GlobalValues;

/**
 * Created by iver on 5/9/16.
 */
public class ContentDetail extends AppCompatActivity {


    private String content;
    private WebView detail_web;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_contentdetail);
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

        prefs = this.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();

        detail_web = (WebView)findViewById(R.id.detail_web);

        content = getIntent().getStringExtra(GlobalValues.EXTRA_CONTENT);

        detail_web.setBackgroundColor(Color.WHITE);

        String title = getIntent().getStringExtra(GlobalValues.EXTRA_TITLE);

        getSupportActionBar().setTitle(title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        try{
            content = content.replaceAll("style=\".+?\"", "");}
        catch(Exception e){

        }

        String fontscript="";
        String script = "<style type='text/css' >p{width:100%;}img{width:100%;height:auto;-webkit-transform: translate3d(0px,0px,0px);}a,h1,h2,h3,h4,h5,h6{color:"+GlobalValues.colorHTML+";}div,p,span,a {max-width: 100%;}iframe{width:100%;height:auto;}</style>";


        detail_web.loadDataWithBaseURL(GlobalValues.baseURLWEB,"<html><head>"+fontscript+script+"</head><body style=\"font-family:HelveticaNeue-Light; \">"+content+"</body></html>", "text/html", "utf-8","");
        detail_web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if ((url != null && url.startsWith("http://") )||( url != null && url.startsWith("https://") )) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                } else {
                    return false;
                }

            }

            public void onPageFinished(WebView view, String url) {
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });
        detail_web.getSettings().setDomStorageEnabled(true);
        detail_web.getSettings().setJavaScriptEnabled(true);


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

        //noinspection SimplifiableIfStatement
        switch(id){

            case R.id.home:
                finish();
                break;
            default:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }



}
