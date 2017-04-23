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
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.task.GetProgramsUser;
import justforcommunity.radiocom.task.Report.SendReport;
import justforcommunity.radiocom.utils.FileUtils;

import static justforcommunity.radiocom.utils.FileUtils.bitmapToByte;
import static justforcommunity.radiocom.utils.GlobalValues.MAX_FILES;
import static justforcommunity.radiocom.utils.GlobalValues.REPORT_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.REPORT_REQUEST;


public class CreateReport extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1000;
    private static final int EXTERNAL_REQUEST = 2000;

    private Context mContext;
    private CreateReport mActivity;

    private ReportDTO report;
    private AVLoadingIndicatorView avi;
    private Map<Integer, byte[]> photosMap;
    private Spinner programName;
    private RadioGroup tidy_radioButtons;
    private RadioGroup dirt_radioButtons;
    private RadioGroup configuration_radioButtons;
    private RadioGroup openDoor_radioButtons;
    private RadioGroup viewMembers_radioButtons;
    private EditText location;
    private EditText description;
    private String[] descriptionAux;
    private LinearLayout imagesReport;
    private Button send_button;
    private int imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

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

        // Listener send report
        send_button = (Button) findViewById(R.id.send);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReport();
            }
        });

        mActivity = this;
        mContext = this;

        // Get programs of user
        GetProgramsUser programsUser = new GetProgramsUser(mContext, mActivity);
        programsUser.execute();
        programName = (Spinner) findViewById(R.id.programName);

        dirt_radioButtons = (RadioGroup) findViewById(R.id.dirt_radioButtons);
        dirt_radioButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                evaluateRadioGroup(0, checkedId, getResources().getString(R.string.report_low_dirt));
            }
        });
        addRadioButton(dirt_radioButtons);

        tidy_radioButtons = (RadioGroup) findViewById(R.id.tidy_radioButtons);
        tidy_radioButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                evaluateRadioGroup(1, checkedId, getResources().getString(R.string.report_low_tidy));
            }
        });
        addRadioButton(tidy_radioButtons);


        configuration_radioButtons = (RadioGroup) findViewById(R.id.configuration_radioButtons);
        configuration_radioButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                evaluateRadioGroup(2, checkedId, getResources().getString(R.string.report_low_configration));
            }
        });
        addRadioButton(configuration_radioButtons);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        openDoor_radioButtons = (RadioGroup) findViewById(R.id.openDoor_radioButtons);
        viewMembers_radioButtons =  (RadioGroup) findViewById(R.id.viewMembers_radioButtons);
        location = (EditText) findViewById(R.id.location);
        description = (EditText) findViewById(R.id.description);
        descriptionAux = new String [3];
        imagesReport = (LinearLayout) findViewById(R.id.images_report);
        photosMap = new HashMap<>();
        imageId = 0;

        //take picture from camera
        Button photoButton = (Button) this.findViewById(R.id.camera_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photosMap.size() >= MAX_FILES) {
                    Toast.makeText(mContext, getResources().getString(R.string.report_max_photos), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        //select picture from external storage
        Button chooseButton = (Button) this.findViewById(R.id.choose_button);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photosMap.size() >= MAX_FILES) {
                    Toast.makeText(mContext, getResources().getString(R.string.report_max_photos), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, EXTERNAL_REQUEST);
            }
        });

        App application = (App) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.report_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // Retrieve photo
        Bitmap photo = null;
        if (requestCode == EXTERNAL_REQUEST) {
            photo = null;
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            photo = (Bitmap) data.getExtras().get("data");
        }

        // Add photo to view and list
        if (requestCode == EXTERNAL_REQUEST || requestCode == CAMERA_REQUEST) {
            photosMap.put(imageId, bitmapToByte(photo));

            RelativeLayout view = new RelativeLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            view.addView(newImageView(photo));
            view.addView(newButtonDelete());
            imagesReport.addView(view);
            imageId++;
        }
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

    // Show toast if send report is success
    public void resultOK(ReportDTO report) {
        avi.hide();
        Toast.makeText(this, getResources().getString(R.string.report_send_success), Toast.LENGTH_SHORT).show();
        // TODO Maybe pass value report to incideceFragments, to refresh report List
        //onBackPressed();

        //Si se crea a Home funciona, pero tiene que ir al fragment
        Intent intent = new Intent(this, Home.class);
        // Intent intent = new Intent(this, ReportPageFragment.class);
        intent.putExtra(REPORT_JSON, new Gson().toJson(report));
        startActivityForResult(intent, REPORT_REQUEST);


//        Intent intent = new Intent(this, ReportPageFragment.class);
//        ReportPageFragment reportsPageFragment = new ReportPageFragment();
//        reportsPageFragment.putExtra(REPORT_JSON, new Gson().toJson(report));
//        startActivityForResult(reportsPageFragment, SEND_INCIDENCE_REQUEST);

//        Intent returnIntent = new Intent();
//        returnIntent.putExtra(REPORT_JSON, new Gson().toJson(report));
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();
    }

    // Show toast if send report is fail
    public void resultKO() {
        avi.hide();
        Toast.makeText(this, getResources().getString(R.string.report_send_fail), Toast.LENGTH_SHORT).show();
    }

    // Show toast if programs users is not available
    public void failProgramUsers() {
        Toast.makeText(this, getResources().getString(R.string.programs_user_fail), Toast.LENGTH_SHORT).show();
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

    // Create new ImageView with parameters
    private ImageView newImageView(Bitmap photo) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(lp);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(photo);
        return imageView;
    }

    // Create new Button to delete image
    private Button newButtonDelete() {
        Button button = new Button(this);
        button.setId(imageId);
        button.setBackground(mContext.getResources().getDrawable(R.drawable.delete));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, getResources().getString(R.string.report_remove_success), Toast.LENGTH_SHORT).show();
                photosMap.remove(v.getId());
                imagesReport.removeView((View) v.getParent());
            }
        });
        return button;
    }

    // Evaluate Radio Group to put message in description
    private void evaluateRadioGroup(int pos, int checkedId, String message) {
        RadioButton rb = (RadioButton) findViewById(checkedId);
        int value = Integer.valueOf(rb.getText().toString());
        if (value < 3) {
            descriptionAux[pos] = message;
        } else {
            descriptionAux[pos] = "";
        }
        // Put text in Hint
        description.setHint("");
        for (String aux : descriptionAux){
            if (aux != null && !aux.isEmpty()) {
                description.setHint(description.getHint() + aux + "\n");
            }
        }
    }

    // Create new report with form
    private void createReport() {

        report = new ReportDTO();
        boolean isValid = true;

        int pos = programName.getChildCount() - 1;
        if (programName.getSelectedItem() != null && !TextUtils.isEmpty(programName.getSelectedItem().toString())) {
            report.setProgram(new ProgramDTO(programName.getSelectedItem().toString()));
            ((TextView) programName.getChildAt(pos)).setError(null);
        } else {
            if (programName.getChildAt(pos) != null) {
                ((TextView) programName.getChildAt(pos)).setError(getResources().getString(R.string.required));
            }
            isValid = false;
        }

        pos = tidy_radioButtons.getChildCount() - 1;
        if (tidy_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_tidy = tidy_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_tidy = (RadioButton) tidy_radioButtons.findViewById(radioButtonID_tidy);
            report.setDirt(Integer.valueOf(radioButton_tidy.getText().toString()));
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
            report.setTidy(Integer.valueOf(radioButton_dirt.getText().toString()));
            ((RadioButton) dirt_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) dirt_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = configuration_radioButtons.getChildCount() - 1;
        if (configuration_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_configuration = configuration_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_configuration = (RadioButton) configuration_radioButtons.findViewById(radioButtonID_configuration);
            report.setConfiguration(Integer.valueOf(radioButton_configuration.getText().toString()));
            ((RadioButton) configuration_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) configuration_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = openDoor_radioButtons.getChildCount() - 1;
        if (openDoor_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_openDoor = openDoor_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_openDoor = (RadioButton) openDoor_radioButtons.findViewById(radioButtonID_openDoor);
            report.setOpenDoor(FileUtils.formatBoolean(mContext, radioButton_openDoor.getText().toString()));
            ((RadioButton) openDoor_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) openDoor_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        pos = viewMembers_radioButtons.getChildCount() - 1;
        if (viewMembers_radioButtons.getCheckedRadioButtonId() != -1) {
            int radioButtonID_viewMembers = viewMembers_radioButtons.getCheckedRadioButtonId();
            RadioButton radioButton_viewMembers = (RadioButton) viewMembers_radioButtons.findViewById(radioButtonID_viewMembers);
            report.setViewMembers(FileUtils.formatBoolean(mContext, radioButton_viewMembers.getText().toString()));
            ((RadioButton) viewMembers_radioButtons.getChildAt(pos)).setError(null);
        } else {
            ((RadioButton) viewMembers_radioButtons.getChildAt(pos)).setError(getResources().getString(R.string.required));
            isValid = false;
        }

        report.setLocation(location.getText().toString());

        // To members, the values is evaluate in server
        // report.setDescription(description.getHint() + description.getText().toString());
        report.setDescription(description.getText().toString());

        if (!isValid) {
            Toast.makeText(this, getResources().getString(R.string.report_complete_fields), Toast.LENGTH_SHORT).show();
        } else {
            // Send Report
            List<byte[]> photos = new ArrayList<>();
            photos.addAll(photosMap.values());
            String photosJson = new Gson().toJson(photos);
            avi.show();
            SendReport sendReport = new SendReport(mContext, mActivity, report, photosJson);
            sendReport.execute();
        }
    }
}
