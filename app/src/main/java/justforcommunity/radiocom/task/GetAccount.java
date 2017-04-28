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

import justforcommunity.radiocom.model.AccountDTO;
import justforcommunity.radiocom.service.ServiceAccounts;
import justforcommunity.radiocom.utils.ConexionInternet;
import justforcommunity.radiocom.utils.GlobalValues;

import static justforcommunity.radiocom.utils.GlobalValues.ACCOUNT_JSON;

public class GetAccount extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetAccount";
    private Context mContext;
    private ServiceAccounts serviceAccounts;
    private Locale locale;
    private AccountDTO accountDTO;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public GetAccount(Context context) {
        this.mContext = context;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceAccounts = new ServiceAccounts(locale);
        prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
        edit = prefs.edit();
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        ConexionInternet cnn = new ConexionInternet();

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
            String accountJon = new Gson().toJson(accountDTO);
            edit.putString(ACCOUNT_JSON, accountJon);
            edit.apply();
        } else {
            // todo....
        }
    }

    public void removeAccount() {
        edit.remove(ACCOUNT_JSON);
        edit.apply();
    }
}
