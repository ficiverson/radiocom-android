/*
 *
 *  * Copyright (C) 2016 @ Pablo Grela
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

package justforcommunity.radiocom.utils;


import android.content.Context;

import justforcommunity.radiocom.R;


public class StringUtils {


    public static String formatBoolean(Context mContext, boolean value) {

        String true_value = mContext.getString(R.string.true_value);
        String false_value = mContext.getString(R.string.false_value);

        return value ? true_value : false_value;
    }

    public static Boolean formatBoolean(Context mContext, String value) {

        String true_value = mContext.getString(R.string.true_value);

        return value.equals(true_value) ? true : false;
    }

}
