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

package justforcommunity.radiocom.task.Transmissions;

import android.content.Context;
import android.os.AsyncTask;

import org.springframework.web.client.RestClientException;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.fragments.TransmissionsPageFragment;
import justforcommunity.radiocom.model.TransmissionDTO;
import justforcommunity.radiocom.service.ServiceTransmissions;
import justforcommunity.radiocom.utils.InternetConnection;


public class GetTransmissions extends AsyncTask<Boolean, Float, Boolean> {

    private Context mContext;
    private TransmissionsPageFragment mActivity;
    private ServiceTransmissions serviceTransmissions;
    private Locale locale;
    private List<TransmissionDTO> transmissionsDTO;
    private String mDateSearch;

    public GetTransmissions(Context mContext, TransmissionsPageFragment mActivity, String dateSearch) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceTransmissions = new ServiceTransmissions(locale);
        mDateSearch = dateSearch;
    }

    protected Boolean doInBackground(Boolean... urls) {

        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                transmissionsDTO = serviceTransmissions.getTransmissions(mDateSearch);
                return true;
            } catch (Exception e) {
                transmissionsDTO = null;
            }
        }
        return false;
    }


    protected void onPostExecute(Boolean result) {
            mActivity.listChannels(transmissionsDTO);
    }

}
