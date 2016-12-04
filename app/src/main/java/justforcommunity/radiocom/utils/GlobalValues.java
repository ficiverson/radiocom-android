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
    public static String baseURLWEB = "https://cuacfm.org/";
    public static String baseURL = "https://cuacfm.org/radioco/api/1/radiocom/";

    public static String prefName = "commradio";

    public static String colorHTML = "#ff9900 !important";

    public static String city = "A Coruña";

    public static String twitterName = "cuacfm";
    public static String facebookName = "https://www.facebook.com/cuacfm";
    //end configuration you can update

    //things that you may not cange
    public static String EXTRA_MESSAGE = "radioStation";
    public static String EXTRA_PROGRAM = "programme";
    public static String EXTRA_CONTENT = "webviewContent";
    public static String EXTRA_TITLE = "webviewTitle";

    public static String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString() + "/Android/data/" + App.PACKAGE_NAME + "/downloaded/";

}
