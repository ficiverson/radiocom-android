/*
 *
 *  * Copyright © 2016 @ Fernando Souto González
 *  * Copyright © 2017 @ Pablo Grela
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

public class GlobalValues {

    //configurations you con update
    public static final String prefName = "commradio";

    //public static final String baseURL = "http://radiocom.stamplayapp.com/api/cobject/v1/";
    public static final String baseURLWEB = "https://cuacfm.org/";
    public static final String baseURLCUACWEB = baseURLWEB + "asociacion-cuac/historia/";

    ///////////////    RADIOCO   ///////////////////////////
    public static final String radiocoURL = baseURLWEB + "radioco/";
    public static final String radioStationURL = radiocoURL + "api/2/radiocom/radiostation";
    public static final String programmesURL = radiocoURL + "api/2/radiocom/programmes";
    public static final String transmissionsURL = radiocoURL + "api/2/radiocom/transmissions";
    public static final String transmissionNowURL = radiocoURL + "api/2/radiocom/transmissions/now";

    ///////////////    MEMBERS   ///////////////////////////
    public static String membersURL = "";
    public static final String signupURL = "signup";
    public static final String membersAPI = "api/";

    // Reports
    public static final String reportsURL = membersAPI + "reportList/";
    public static final String reportsUserURL = membersAPI + "reportUserList/";
    public static final String createReportURL = reportsURL + "reportCreate";
    public static final String imageReportURL = reportsURL + "image/";
    public static final String sendAnswerReportURL = reportsURL + "reportAnswer/";

    // Books
    public static final String booksURL = membersAPI + "bookList/";
    public static final String booksUserURL = membersAPI + "bookUserList/";
    public static final String createBookURL = booksURL + "bookCreate";
    public static final String sendAnswerBookURL = booksURL + "bookAnswer/";

    // Programs
    public static final String programsURL = membersAPI + "programList/";
    public static final String programsUserURL = membersAPI + "programUserList/";

    // Elements
    public static final String elementsURL = membersAPI + "elementList/";

    // Accounts
    public static final String accountsURL = membersAPI + "accountList/";
    public static final String accountURL = accountsURL + "account";

    // Get variables
    public static final String addToken = "?token=";
    public static final String addDeviceToken = "&deviceToken=";
    public static final String addImageName = "&imageName=";

    public static final String colorHTML = "#ff9900 !important";
    public static final String city = "A Coruña";

    public static final String twitter = "https://twitter.com/";
    public static final String twitterName = "cuacfm";
    public static final String facebookName = "https://www.facebook.com/cuacfm";
    public static final String facebookId = "209865152388997";
    //end configuration you can update

    //things that you may not change
    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString() + "/Android/data/" + App.PACKAGE_NAME + "/downloaded/";

    public static final String EXTRA_MESSAGE = "radioStation";
    public static final String EXTRA_PROGRAM = "programme";
    public static final String EXTRA_CONTENT = "webviewContent";
    public static final String EXTRA_TITLE = "webviewTitle";

    public static final String JSON_ACCOUNT = "jsonAccount";
    public static final String JSON_PODCAST = "jsonPodcast";
    public static final String JSON_BOOK = "jsonBook";
    public static final String JSON_REPORT = "jsonReport";

    public static final String REPORT = "REPORT";
    public static final String BOOK = "BOOK";
    public static final String MEMBERS = "MEMBERS";
    public static final String MANAGE = "manage";

    public static final int AUTH_REQUEST = 2000;
    public static final int REPORT_REQUEST = 3000;
    public static final int REPORT_ANSWER_REQUEST = 3001;
    public static final int BOOK_REQUEST = 3010;
    public static final int BOOK_ANSWER_REQUEST = 3011;
    public static final int MAX_FILES = 10;

    public static final String NOTIFICATION_COUNT = "notificationCount";
    public static final String ROLE_REPORT = "ROLE_REPORT";
    public static final String ROLE_BOOK = "ROLE_BOOK";
    public static final String ROLE_TRAINER = "ROLE_TRAINER";
    public static final String REST_URL = "REST_URL";
}
