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

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.IncidenceDTO;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.task.GetProgramsUser;
import justforcommunity.radiocom.task.SendIncidence;
import justforcommunity.radiocom.utils.StringUtils;


public class CreateIncidence extends AppCompatActivity {

    private IncidenceDTO incidence;
    private Context mContext;
    private CreateIncidence mActivity;

    private Spinner programName;
    private RadioGroup tidy_radioButtons;
    private RadioGroup dirt_radioButtons;
    private RadioGroup configuration_radioButtons;
    private RadioGroup openDoor_radioButtons;
    private RadioGroup viewMembers_radioButtons;
    private EditText description;

    private LinearLayout imagesIncidence;

    private Dialog myDialog;
    private Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_incidence);

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

        // Listener send incidence
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createIncidence();
            }
        });

        mActivity = this;
        mContext = this;

        // Get programs of user
        GetProgramsUser programsUser = new GetProgramsUser(mContext, mActivity);
        programsUser.execute();
        programName = (Spinner) findViewById(R.id.programName);

        tidy_radioButtons = (RadioGroup) findViewById(R.id.tidy_radioButtons);
        addRadioButton(tidy_radioButtons);
        dirt_radioButtons = (RadioGroup) findViewById(R.id.dirt_radioButtons);
        addRadioButton(dirt_radioButtons);
        configuration_radioButtons = (RadioGroup) findViewById(R.id.configuration_radioButtons);
        addRadioButton(configuration_radioButtons);
        openDoor_radioButtons = (RadioGroup) findViewById(R.id.openDoor_radioButtons);
        viewMembers_radioButtons = (RadioGroup) findViewById(R.id.viewMembers_radioButtons);
        description = (EditText) findViewById(R.id.description);

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.incidence_activity));
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

    // Show toast if send incidence is success
    public void resultOK(IncidenceDTO incidence) {
        Toast.makeText(this, getResources().getString(R.string.incidence_send_success), Toast.LENGTH_SHORT).show();
        // TODO Maybe pass value incidence to incideceFragments
        onBackPressed();
        //new Home().loadIncidence();
    }

    // Show toast if send incidence is fail
    public void resultKO() {
        Toast.makeText(this, getResources().getString(R.string.incidence_send_fail), Toast.LENGTH_SHORT).show();
    }

    // Add programsUser to view
    public void setProgramsUser(final List<ProgramDTO> programs) {

        List<String> list = new ArrayList<>();
        if (programs.size() > 1) {
            list.add("");
        }
        for (ProgramDTO program : programs) {
            list.add(program.getName());
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        programName.setAdapter(adapter);
    }

    // Add new radioButton to radioGroup
    private void addRadioButton(RadioGroup group) {
        for (int i = 1; i < 6; i++) {
            AppCompatRadioButton radio = new AppCompatRadioButton(this);
            radio.setText(String.valueOf(i));
            radio.setTextColor(Color.WHITE);
            radio.setSupportButtonTintList(ColorStateList.valueOf(Color.WHITE));
            group.addView(radio);
        }
    }

    // Create new incidence with form
    private void createIncidence() {

        incidence = new IncidenceDTO();
        boolean isValid = true;

        int pos = programName.getChildCount() - 1;
        if (!TextUtils.isEmpty(programName.getSelectedItem().toString())) {
            incidence.setProgram(new ProgramDTO(programName.getSelectedItem().toString()));
            ((TextView) programName.getChildAt(pos)).setError(null);
        } else {
            ((TextView) programName.getChildAt(pos)).setError(getResources().getString(R.string.required));
        }

        pos = tidy_radioButtons.getChildCount() - 1;
        if (tidy_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_tidy = tidy_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_tidy = (RadioButton) tidy_radioButtons.findViewById(radioButtonID_tidy);
            incidence.setDirt(Integer.valueOf(radioButton_tidy.getText().toString()));
            radioButton_tidy.setError(null);
            ((RadioButton) tidy_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) tidy_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = dirt_radioButtons.getChildCount() - 1;
        if (dirt_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_dirt = dirt_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_dirt = (RadioButton) dirt_radioButtons.findViewById(radioButtonID_dirt);
            incidence.setTidy(Integer.valueOf(radioButton_dirt.getText().toString()));
            ((RadioButton) dirt_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) dirt_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = configuration_radioButtons.getChildCount() - 1;
        if (configuration_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_configuration = configuration_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_configuration = (RadioButton) configuration_radioButtons.findViewById(radioButtonID_configuration);
            incidence.setConfiguration(Integer.valueOf(radioButton_configuration.getText().toString()));
            ((RadioButton) configuration_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) configuration_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = openDoor_radioButtons.getChildCount() - 1;
        if (openDoor_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_openDoor = openDoor_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_openDoor = (RadioButton) openDoor_radioButtons.findViewById(radioButtonID_openDoor);
            incidence.setOpenDoor(StringUtils.formatBoolean(mContext, radioButton_openDoor.getText().toString()));
            ((RadioButton) openDoor_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) openDoor_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = viewMembers_radioButtons.getChildCount() - 1;
        if (viewMembers_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_viewMembers = viewMembers_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_viewMembers = (RadioButton) viewMembers_radioButtons.findViewById(radioButtonID_viewMembers);
            incidence.setViewMembers(StringUtils.formatBoolean(mContext, radioButton_viewMembers.getText().toString()));
            ((RadioButton) viewMembers_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) viewMembers_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        incidence.setDescription(description.getText().toString());

        if (!isValid) {
            Toast.makeText(this, getResources().getString(R.string.incidence_complete_fields), Toast.LENGTH_SHORT).show();
        } else {
            // Send Incidence
            SendIncidence sendIncidence = new SendIncidence(mContext, mActivity, incidence);
            sendIncidence.execute();
        }
    }
}
