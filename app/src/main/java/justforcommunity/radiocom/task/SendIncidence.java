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
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.client.RestClientException;

import java.util.Locale;

import justforcommunity.radiocom.activities.CreateIncidence;
import justforcommunity.radiocom.model.IncidenceDTO;
import justforcommunity.radiocom.service.ServiceGetIncidences;
import justforcommunity.radiocom.utils.ConexionInternet;


public class SendIncidence extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetIncidences";
    private Context mContext;
    private CreateIncidence activity;
    private ServiceGetIncidences serviceGetIncidences;
    private Locale locale;
    private IncidenceDTO incidence;

    public SendIncidence(Context context, CreateIncidence activity, IncidenceDTO incidence) {
        this.activity = activity;
        this.mContext = context;
        this.incidence = incidence;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceGetIncidences = new ServiceGetIncidences(locale);
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        ConexionInternet cnn = new ConexionInternet();

        if (cnn.isConnected(mContext)) {

            try {
                incidence = serviceGetIncidences.sendIncidence(incidence);
                res = true;
            } catch (RestClientException e) {
                Log.e(TAG, "doInBackground()", e);
                res = false;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground()", e);
                res = false;
            }
        }
        return res;
    }

    protected void onPostExecute(Boolean result) {

        if (result) {
            activity.resultOK(incidence);
        } else {
            activity.resultKO();
        }
    }
}
