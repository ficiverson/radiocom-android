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
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.fragments.PodcastPageFragment;
import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.service.ServicePrograms;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.InternetConnection;

import static justforcommunity.radiocom.utils.GlobalValues.JSON_PODCAST;


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
        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                programsDTO = servicePrograms.getPrograms().getData();
                return true;
            } catch (Exception e) {
                programsDTO = null;
            }
        }
        return false;
    }


    protected void onPostExecute(Boolean result) {
        if (result) {
            fragment.listChannels(programsDTO);

            // Save programs in Shared preferences
            SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            Gson gson = new Gson();
            for (ProgramDTO programDTO : programsDTO){
                String jsonInString = gson.toJson(programDTO);
                edit.putString(programDTO.getSlug(), jsonInString);
            }

            edit.commit();
        } else {
            fragment.resultKO();
        }
    }

}
