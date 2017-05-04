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

package justforcommunity.radiocom.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.springframework.web.client.RestClientException;

import java.util.Locale;

import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.model.AccountDTO;
import justforcommunity.radiocom.service.ServiceAccounts;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.InternetConnection;

import static justforcommunity.radiocom.utils.GlobalValues.ACCOUNT_JSON;

public class GetAccount extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetAccount";
    private Context mContext;
    private Home mActivity;
    private ServiceAccounts serviceAccounts;
    private Locale locale;
    private AccountDTO accountDTO;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public GetAccount(Context context) {
        this.mContext = context;
        this.locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        this.serviceAccounts = new ServiceAccounts(locale);
        this.prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        this.edit = prefs.edit();
    }

    public GetAccount(Context context, Home mActivity) {
        this(context);
        this.mActivity = mActivity;
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                accountDTO = serviceAccounts.getAccount();
                res = true;
            } catch (RestClientException e) {
                Log.e(TAG, "doInBackground()", e);
                accountDTO = null;
                res = false;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground()", e);
                accountDTO = null;
                res = false;
            }
        }
        return res;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            edit.putString(ACCOUNT_JSON, new Gson().toJson(accountDTO));
        } else {
            edit.remove(ACCOUNT_JSON);
        }
        edit.apply();
        if (mActivity != null) {
            mActivity.updateUserInfo();
        }
    }

    public void removeAccount() {
        edit.remove(ACCOUNT_JSON);
        edit.apply();
    }
}
