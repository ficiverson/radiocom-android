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

import justforcommunity.radiocom.activities.Report;
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.service.ServiceReports;
import justforcommunity.radiocom.utils.InternetConnection;


public class SendAnswerReport extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetBooks";
    private Context mContext;
    private Report activity;
    private ServiceReports serviceReports;
    private Locale locale;
    private ReportDTO report;
    private Long reportId;
    private String answer;
    private Boolean manage;

    public SendAnswerReport(Context context, Report activity, Long reportId, String answer, Boolean manage) {
        this.activity = activity;
        this.mContext = context;
        this.reportId = reportId;
        this.answer = answer;
        this.manage = manage;
        this.locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        this.serviceReports = new ServiceReports(locale);
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                report = serviceReports.SendAnswerReport(reportId, answer, manage);
                res = true;
            } catch (RestClientException e) {
                Log.d(TAG, "doInBackground()", e);
                res = false;
            } catch (Exception e) {
                Log.d(TAG, "doInBackground()", e);
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
