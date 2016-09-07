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

package justforcommunity.radiocom.utils;

import android.os.Environment;

import justforcommunity.radiocom.activities.App;

/**
 * Created by iver on 2/9/16.
 */
public class GlobalValues {


    public static String baseURL = "http://radiocom.stamplayapp.com/api/cobject/v1/";

    public static String prefName="commradio";

    public static String EXTRA_MESSAGE = "radioStation";
    public static String EXTRA_PROGRAM = "programme";
    public static String EXTRA_CONTENT = "webviewContent";
    public static String EXTRA_TITLE = "webviewTitle";

    public static String colorHTML = "#ff9900 !important";
    public static String baseURLWEB ="https://cuacfm.org/";

    public static String city ="A Coruña";

    public static String twitterName="cuacfm";
    public static String facebookName="https://www.facebook.com/cuacfm";


    public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString()+"/Android/data/" + App.PACKAGE_NAME + "/downloaded/";

}
