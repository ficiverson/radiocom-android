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
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.fragments.ReportPageFragment;
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.service.ServiceReports;
import justforcommunity.radiocom.utils.ConexionInternet;

public class GetReports extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetReports";
    private Context mContext;
    private ReportPageFragment fragment;
    private ServiceReports serviceReports;
    private Locale locale;
    private List<ReportDTO> reportsDTO;

    public GetReports(Context context, ReportPageFragment fragment) {
        this.fragment = fragment;
        this.mContext = context;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceReports = new ServiceReports(locale);
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        ConexionInternet cnn = new ConexionInternet();

        if (cnn.isConnected(mContext)) {

            try {
                reportsDTO = serviceReports.getReports();
                res = true;

            } catch (RestClientException e) {
                Log.e(TAG, "doInBackground()", e);
                reportsDTO = null;
                res = false;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground()", e);
                reportsDTO = null;
                res = false;
            }
        }
        return res;
    }

    protected void onPostExecute(Boolean result) {

        if (result) {
            fragment.setReportList(reportsDTO);
        } else {
            fragment.resultKO();
        }
    }
}
