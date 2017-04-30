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

package justforcommunity.radiocom.task.Reserve.Reserve;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.fragments.ReservePageFragment;
import justforcommunity.radiocom.model.ReserveDTO;
import justforcommunity.radiocom.service.ServiceReserves;
import justforcommunity.radiocom.utils.InternetConnection;

public class GetReserves extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetReserves";
    private Context mContext;
    private ReservePageFragment fragment;
    private ServiceReserves serviceReserves;
    private Locale locale;
    private String restURL;
    private List<ReserveDTO> reservesDTO;

    public GetReserves(Context context, ReservePageFragment fragment, String restURL) {
        this.fragment = fragment;
        this.mContext = context;
        this.locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        this.serviceReserves = new ServiceReserves(locale);
        this.restURL = restURL;
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                reservesDTO = serviceReserves.getReserves(restURL);
                res = true;

            } catch (RestClientException e) {
                Log.e(TAG, "doInBackground()", e);
                reservesDTO = null;
                res = false;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground()", e);
                reservesDTO = null;
                res = false;
            }
        }
        return res;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            fragment.setReserveList(reservesDTO);
        } else {
            fragment.resultKO();
        }
    }
}
