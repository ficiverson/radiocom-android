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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.ElementDTO;
import justforcommunity.radiocom.model.ReserveDTO;
import justforcommunity.radiocom.task.GetElements;
import justforcommunity.radiocom.task.Reserve.Reserve.SendReserve;
import justforcommunity.radiocom.utils.DateUtils;

import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.REST_URL;


public class CreateReserve extends AppCompatActivity {

    private Context mContext;
    private CreateReserve mActivity;

    private ReserveDTO reserve;
    private AVLoadingIndicatorView avi;

    private Spinner elementName;
    private Button send_button;
    private String restURL;
    private EditText description;

    private EditText dateStart;
    private Calendar newDateStart;
    private DatePickerDialog dateStartPickerDialog;
    private TimePickerDialog timeStartPickerDialog;

    private Calendar newDateEnd;
    private EditText dateEnd;
    private DatePickerDialog dateEndPickerDialog;
    private TimePickerDialog timeEndPickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reserve);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Listener back
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Listener send reserve
        send_button = (Button) findViewById(R.id.send);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReserve();
            }
        });

        mActivity = this;
        mContext = this;

        // restURL
        restURL = getIntent().getExtras().getString(REST_URL);

        // Get elements of user
        GetElements elementsUser = new GetElements(mContext, mActivity, restURL);
        elementsUser.execute();

        elementName = (Spinner) findViewById(R.id.elementName);
        dateStart = (EditText) findViewById(R.id.dateStart);
        dateStart.setInputType(InputType.TYPE_NULL);
        dateEnd = (EditText) findViewById(R.id.dateEnd);
        dateEnd.setInputType(InputType.TYPE_NULL);
        description = (EditText) findViewById(R.id.description);
        setDateTimeField();

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

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

    // Show toast if send reserve is success
    public void resultOK(ReserveDTO reserve) {
        avi.hide();
        Toast.makeText(this, getResources().getString(R.string.reserve_send_success), Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESERVE_JSON, new Gson().toJson(reserve));
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    // Show toast if send reserve is fail
    public void resultKO(String message) {
        avi.hide();
        if (message != null && message.contains("UserAlreadyReserveException")) {
            Toast.makeText(this, getResources().getString(R.string.reserve_send_fail_already), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.reserve_send_fail), Toast.LENGTH_SHORT).show();
        }
    }

    // Show toast if elements is not available
    public void failElementsReservable() {
        Toast.makeText(this, getResources().getString(R.string.elements_reservable_fail), Toast.LENGTH_SHORT).show();
    }

    // Add elements to view
    public void setElementsReservable(final List<ElementDTO> elements) {

        List<String> list = new ArrayList<>();
        if (elements.size() > 1) {
            list.add("");
        }
        for (ElementDTO element : elements) {
            list.add(element.getName());
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        elementName.setAdapter(adapter);
    }

    // Initialize dialogs of dates
    private void setDateTimeField() {

        //Date Start
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStartPickerDialog.show();
            }
        });

        newDateStart = Calendar.getInstance();
        dateStartPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDateStart.set(year, monthOfYear, dayOfMonth);
                timeStartPickerDialog.show();
            }
        }, newDateStart.get(Calendar.YEAR), newDateStart.get(Calendar.MONTH), newDateStart.get(Calendar.DAY_OF_MONTH));

        timeStartPickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDateStart.set(newDateStart.get(Calendar.YEAR), newDateStart.get(Calendar.MONTH), newDateStart.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                dateStart.setText(DateUtils.formatDate(newDateStart, DateUtils.FORMAT_DISPLAY));
            }
        }, newDateStart.get(Calendar.HOUR_OF_DAY), newDateStart.get(Calendar.MINUTE), false);


        // Date End
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEndPickerDialog.show();
            }
        });
        newDateEnd = Calendar.getInstance();
        dateEndPickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDateEnd.set(year, monthOfYear, dayOfMonth);
                timeEndPickerDialog.show();
            }
        }, newDateEnd.get(Calendar.YEAR), newDateEnd.get(Calendar.MONTH), newDateEnd.get(Calendar.DAY_OF_MONTH));

        timeEndPickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDateEnd.set(newDateEnd.get(Calendar.YEAR), newDateEnd.get(Calendar.MONTH), newDateEnd.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                dateEnd.setText(DateUtils.formatDate(newDateEnd, DateUtils.FORMAT_DISPLAY));
            }
        }, newDateEnd.get(Calendar.HOUR_OF_DAY), newDateEnd.get(Calendar.MINUTE), false);
    }

    // Create new reserve with form
    private void createReserve() {

        reserve = new ReserveDTO();
        boolean isValid = true;

        int pos = elementName.getChildCount() - 1;
        if (elementName.getSelectedItem() != null && !TextUtils.isEmpty(elementName.getSelectedItem().toString())) {
            reserve.setElement(new ElementDTO(elementName.getSelectedItem().toString()));
            ((TextView) elementName.getChildAt(pos)).setError(null);
        } else {
            if (elementName.getChildAt(pos) != null) {
                ((TextView) elementName.getChildAt(pos)).setError(getResources().getString(R.string.required));
            }
            isValid = false;
        }

        if (!TextUtils.isEmpty(dateStart.getText().toString())) {
            reserve.setDateStart(DateUtils.formatDate(dateStart.getText().toString(), DateUtils.FORMAT_DISPLAY));
        } else {
            dateStart.setError(getResources().getString(R.string.required));
            isValid = false;
        }

        if (!TextUtils.isEmpty(dateEnd.getText().toString())) {
            reserve.setDateEnd(DateUtils.formatDate(dateEnd.getText().toString(), DateUtils.FORMAT_DISPLAY));
        } else {
            dateEnd.setError(getResources().getString(R.string.required));
            isValid = false;
        }

        if (reserve.getDateStart().before(new Date())) {
            Toast.makeText(this, getResources().getString(R.string.reserve_fail_dates_now), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (reserve.getDateEnd().before(reserve.getDateStart())) {
            Toast.makeText(this, getResources().getString(R.string.reserve_fail_dates), Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!TextUtils.isEmpty(description.getText().toString())) {
            reserve.setDescription(description.getText().toString());
        } else {
            description.setError(getResources().getString(R.string.required));
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, getResources().getString(R.string.reserve_complete_fields), Toast.LENGTH_SHORT).show();
        } else {
            // Send Reserve
            avi.show();
            SendReserve sendReserve = new SendReserve(mContext, mActivity, reserve);
            sendReserve.execute();
        }
    }
}
