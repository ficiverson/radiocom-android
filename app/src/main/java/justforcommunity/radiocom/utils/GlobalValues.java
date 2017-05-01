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

    // public static final String membersURL = baseURLWEB + "members/";
    public static final String membersURL = "http://10.0.2.2:8080/members/";
    // public static final String membersURL = "http://192.168.137.111:8080/members/";

    public static final String signupURL = membersURL + "signup";
    public static final String membersAPI = membersURL + "api/";

    // Reports
    public static final String reportsURL = membersAPI + "reportList/";
    public static final String reportsUserURL = membersAPI + "reportUserList/";
    public static final String createReportURL = reportsURL + "reportCreate";
    public static final String imageReportURL = reportsURL + "image/";
    public static final String sendAnswerReportURL = reportsURL + "reportAnswer/";

    // Reserves
    public static final String reservesURL = membersAPI + "reserveList/";
    public static final String reservesUserURL = membersAPI + "reserveUserList/";
    public static final String createReserveURL = reservesURL + "reserveCreate";
    public static final String sendAnswerReserveURL = reservesURL + "reserveAnswer/";

    // Programs
    public static final String programsURL = membersAPI + "programList/";
    public static final String programsUserURL = membersAPI + "programUserList/";

    // Elements
    public static final String elementsURL = membersAPI + "elementList/";

    // Accounts
    public static final String accountsURL = membersAPI + "aaccountList/";
    public static final String accountURL = accountsURL + "account";


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
    public static final String RESERVE_JSON = "reserveJson";
    public static final String ACCOUNT_JSON = "accountJson";
    public static final String MANAGE = "manage";

    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString() + "/Android/data/" + App.PACKAGE_NAME + "/downloaded/";

    public static final int REPORT_REQUEST = 3000;
    public static final int REPORT_ANSWER_REQUEST = 3001;
    public static final int RESERVE_REQUEST = 3010;
    public static final int RESERVE_ANSWER_REQUEST = 3011;
    public static final int MAX_FILES = 10;
    public static final String ROLE_REPORT = "ROLE_REPORT";
    public static final String ROLE_RESERVE = "ROLE_RESERVE";
    public static final String REST_URL = "REST_URL";
}
