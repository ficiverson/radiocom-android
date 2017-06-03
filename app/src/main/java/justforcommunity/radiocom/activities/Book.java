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
import justforcommunity.radiocom.model.BookDTO;
import justforcommunity.radiocom.task.Book.SendAnswerBook;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.utils.FileUtils;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;

import static justforcommunity.radiocom.utils.GlobalValues.BOOK_ANSWER_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.JSON_BOOK;
import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;

public class Book extends AppCompatActivity {

    public SharedPreferences prefs;
    public SharedPreferences.Editor edit;
    private BookDTO book;
    private AVLoadingIndicatorView avi;
    private Context mContext;
    private Book mActivity;
    private Button answer_button;
    private Dialog myDialog;
    private boolean manage;

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
        setContentView(R.layout.activity_book);

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

        String jsonBook = getIntent().getStringExtra(JSON_BOOK);
        Gson gson = new Gson();
        if (jsonBook != null && jsonBook != "") {
            book = gson.fromJson(jsonBook, BookDTO.class);
        } else {
            //take last book selected
            book = gson.fromJson(prefs.getString(JSON_BOOK, ""), BookDTO.class);
        }

        // Listener send book
        answer_button = (Button) findViewById(R.id.answer_button);
        answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAnswer(null);
            }
        });

        Button accept_button = (Button) this.findViewById(R.id.accept_button);
        Button deny_button = (Button) this.findViewById(R.id.deny_button);

        // Only manager book (ROLE_BOOK)
        manage = getIntent().getBooleanExtra(MANAGE, false);
        if (manage) {
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

        if (book != null) {
            getSupportActionBar().setTitle(DateUtils.formatDate(book.getDateCreate(), DateUtils.FORMAT_DISPLAY));

            dateCreate.setText(DateUtils.formatDate(book.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            elementName.setText(book.getElement().getName());
            accountName.setText(book.getAccount().getFullName());
            description.setText(book.getDescription());
            dateStart.setText(DateUtils.formatDate(book.getDateStart(), DateUtils.FORMAT_DISPLAY));
            dateEnd.setText(DateUtils.formatDate(book.getDateEnd(), DateUtils.FORMAT_DISPLAY));
            dateRevision.setText(DateUtils.formatDate(book.getDateRevision(), DateUtils.FORMAT_DISPLAY));
            dateApproval.setText(DateUtils.formatDate(book.getDateApproval(), DateUtils.FORMAT_DISPLAY));
            state.setText(FileUtils.getState(mContext, book.getState()));
            active.setText(FileUtils.formatBoolean(mContext, book.isActive()));
            answer.setText(book.getAnswer());

            avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
            //avi.show();
            avi.hide();
        }

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.book_activity));
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
                    Intent i = new Intent(Book.this, PodcastingService.class);
                    stopService(i);
                    //notifyAdapterToRefresh();
                }
            } else if (intent.getStringExtra(JSON_BOOK) != null) {
                Intent newIntent = new Intent(mActivity, Report.class);
                newIntent.putExtra(JSON_BOOK, intent.getStringExtra(JSON_BOOK));
                startActivityForResult(newIntent, BOOK_ANSWER_REQUEST);
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

    // Show toast if send book is success
    public void resultOK(BookDTO book) {
        Toast.makeText(this, getResources().getString(R.string.book_send_answer_success), Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(JSON_BOOK, new Gson().toJson(book));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Show toast if send book is fail
    public void resultKO() {
        Toast.makeText(this, getResources().getString(R.string.book_send_answer_fail), Toast.LENGTH_SHORT).show();
    }

    // Create new answer with form
    private void createAnswer(Boolean manage) {

        if (TextUtils.isEmpty(answer_view.getText().toString())) {
            answer_view.setError(getResources().getString(R.string.required));
            Toast.makeText(this, getResources().getString(R.string.book_complete_fields), Toast.LENGTH_SHORT).show();
        } else {
            new SendAnswerBook(mContext, mActivity, book.getId(), answer_view.getText().toString(), manage).execute();
        }
    }

}
