/*
 *
 *  * Copyright (C) 2016 @ Fernando Souto González
 *  *
 *  * Developer Fernando Souto
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

import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.activities.CreateBook;
import justforcommunity.radiocom.model.ElementDTO;
import justforcommunity.radiocom.service.ServiceElements;
import justforcommunity.radiocom.utils.InternetConnection;


public class GetElements extends AsyncTask<Boolean, Float, Boolean> {

    private Context mContext;
    private CreateBook mActivity;
    private ServiceElements serviceElements;
    private Locale locale;
    private List<ElementDTO> elementsDTO;
    private String restURL;

    public GetElements(Context mContext, CreateBook mActivity, String restURL) {
        this.mActivity = mActivity;
        this.mContext = mContext;
        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        serviceElements = new ServiceElements(locale);
        this.restURL = restURL;
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;

        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                elementsDTO = serviceElements.getElements(restURL);
                res = true;

            } catch (RestClientException e) {
                elementsDTO = null;
                res = false;
            } catch (Exception e) {
                elementsDTO = null;
                res = false;
            }
        }
        return res;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            mActivity.setElementsReservable(elementsDTO);
        } else {
            mActivity.failElementsReservable();
        }
    }

}
