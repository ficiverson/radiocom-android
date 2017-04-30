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

import justforcommunity.radiocom.fragments.PodcastPageFragment;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.service.ServicePrograms;
import justforcommunity.radiocom.utils.InternetConnection;


public class GetPrograms extends AsyncTask<Boolean, Float, Boolean> {

    private Context mContext;
    private PodcastPageFragment fragment;
    private ServicePrograms servicePrograms;
    private Locale locale;
    private List<ProgramDTO> programsDTO;

    public GetPrograms(Context context, PodcastPageFragment fragment) {
        this.fragment = fragment;
        this.mContext = context;

        locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        servicePrograms = new ServicePrograms(locale);
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;

        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                programsDTO = servicePrograms.getPrograms().getData();
                res = true;

            } catch (RestClientException e) {
                programsDTO = null;
                res = false;
            } catch (Exception e) {
                programsDTO = null;
                res = false;
            }
        }
        return res;
    }


    protected void onPostExecute(Boolean result) {

        if (result) {
            fragment.listChannels(programsDTO);
        } else {
            fragment.resultKO();
        }
    }

}
