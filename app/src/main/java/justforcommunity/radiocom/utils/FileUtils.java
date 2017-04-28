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


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;


public class FileUtils {

    public static String formatBoolean(Context mContext, boolean value) {
        String true_value = mContext.getString(R.string.true_value);
        String false_value = mContext.getString(R.string.false_value);
        return value ? true_value : false_value;
    }

    public static Boolean formatBoolean(Context mContext, String value) {
        String true_value = mContext.getString(R.string.true_value);
        return value.equals(true_value) ? true : false;
    }

    public static List<byte[]> bitmapToByte(List<Bitmap> photos) {
        List<byte[]> strings = new ArrayList<>();
        for (Bitmap bitmap : photos) {
            strings.add(bitmapToByte(bitmap));
        }
        return strings;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static void processBuilder(Context mContext, Activity mActivity, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.addDefaultShareMenuItem();
        builder.enableUrlBarHiding();
        builder.setStartAnimations(mActivity, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        builder.setExitAnimations(mActivity, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        builder.setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(mActivity, Uri.parse(url));
    }
}
