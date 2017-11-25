/*
 *
 *  * Copyright © 2017 @ Pablo Grela
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

import java.util.Locale;

import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.model.LiveBroadcastDTO;
import justforcommunity.radiocom.service.ServiceTransmissions;
import justforcommunity.radiocom.utils.InternetConnection;

public class GetLiveBroadcast extends AsyncTask<Boolean, Float, Boolean> {

    private Context mContext;
    private Home mActivity;
    private ServiceTransmissions serviceTransmissions;
    private Locale locale;
    private LiveBroadcastDTO liveBroadcastDTO;

    public GetLiveBroadcast(Context mContext, Home mActivity) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceTransmissions = new ServiceTransmissions(locale);
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;

        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                liveBroadcastDTO = serviceTransmissions.getTransmissionNow();
                res = true;

            } catch (RestClientException e) {
                liveBroadcastDTO = null;
                res = false;
            } catch (Exception e) {
                liveBroadcastDTO = null;
                res = false;
            }
        }
        return res;
    }


    protected void onPostExecute(Boolean result) {
        if (result) {
            mActivity.setTransmissionDTO(liveBroadcastDTO);
        } else {
            mActivity.setTransmissionDTO(null);
        }
    }

}
