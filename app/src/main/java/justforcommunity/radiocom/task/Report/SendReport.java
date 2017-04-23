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

package justforcommunity.radiocom.task.Report;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.client.RestClientException;

import java.util.Locale;

import justforcommunity.radiocom.activities.CreateReport;
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.service.ServiceReports;
import justforcommunity.radiocom.utils.ConexionInternet;


public class SendReport extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "SendReport";
    private Context mContext;
    private CreateReport activity;
    private ServiceReports serviceReports;
    private Locale locale;
    private ReportDTO report;
    private String photosGson;

    public SendReport(Context context, CreateReport activity, ReportDTO report, String photosGson) {
        this.activity = activity;
        this.mContext = context;
        this.report = report;
        this.photosGson = photosGson;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceReports = new ServiceReports(locale);
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        ConexionInternet cnn = new ConexionInternet();

        if (cnn.isConnected(mContext)) {

            try {
                report = serviceReports.sendReport(report, photosGson);
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
            activity.resultOK(report);
        } else {
            activity.resultKO();
        }
    }
}
