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

package justforcommunity.radiocom.utils;

import android.os.Environment;

import com.google.firebase.auth.FirebaseUser;

import justforcommunity.radiocom.activities.App;

/**
 * Created by iver on 2/9/16.
 */
public class GlobalValues {

    //configurations you con update
    public static final String baseURLCUACWEB = "https://cuacfm.org/asociacion-cuac/historia/";
    public static final String baseURLWEB = "https://cuacfm.org/";
    public static final String baseURL = "http://radiocom.stamplayapp.com/api/cobject/v1/";
    //this will be the final url when we make changes on RADIOCO
    //public static final String baseURL = "https://cuacfm.org/radioco/api/1/radiocom/";
    public static final String prefName = "commradio";

    // Test 10.0.2.2:8080, Real cuacfm.org
    // public static String membersURL = "https://cuacfm.org/members/";
    public static final String membersURL = "http://10.0.2.2:8080/members/";
    public static final String signupURL = membersURL + "signup";
    public static final String reportsURL = membersURL + "api/reportList/";
    public static final String reportsUserURL = membersURL + "api/reportUserList/";
    public static final String createReportURL = reportsURL + "reportCreate";
    public static final String imageReportURL = reportsURL + "image/";
    public static final String programsURL = membersURL + "api/programList/";

    public static final String colorHTML = "#ff9900 !important";

    public static final String city = "A Coruña";

    public static final String twitter = "https://twitter.com/";
    public static final String twitterName = "cuacfm";
    public static final String facebookName = "https://www.facebook.com/cuacfm";
    public static final String facebookId = "209865152388997";
    //end configuration you can update

    //things that you may not change
    public static final String EXTRA_MESSAGE = "radioStation";
    public static final String EXTRA_PROGRAM = "programme";
    public static final String EXTRA_CONTENT = "webviewContent";
    public static final String EXTRA_TITLE = "webviewTitle";
    public static final String REPORT_JSON = "reportJson";

    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString() + "/Android/data/" + App.PACKAGE_NAME + "/downloaded/";

    public static final int SEND_REPORT_REQUEST = 3000;
    public static final int MAX_FILES = 10;
}
