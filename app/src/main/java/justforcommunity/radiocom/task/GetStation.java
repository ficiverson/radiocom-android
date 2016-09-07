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
import android.util.Log;


import org.springframework.web.client.RestClientException;

import java.util.Locale;

import justforcommunity.radiocom.activities.Splash;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.service.ServiceGetStation;
import justforcommunity.radiocom.utils.ConexionInternet;
import justforcommunity.radiocom.utils.GlobalValues;


public class GetStation extends AsyncTask<Boolean, Float, Boolean>{


	private Context mContext;
	private Splash mActivity;
	private ServiceGetStation serviceGetStation;
	private Locale locale;
	private StationDTO stationDTO;

	public GetStation(Context context, Splash activity){
		this.mActivity = activity;
		this.mContext = context;

		locale= new Locale(mContext.getResources().getConfiguration().locale.toString());
		serviceGetStation = new ServiceGetStation(locale);
		
	}

	
	protected Boolean doInBackground(Boolean... urls) {
		boolean res = false;




		ConexionInternet cnn = new ConexionInternet();

		if (cnn.isConnected(mContext)) {

				try {
					stationDTO = serviceGetStation.getStation().getData().get(0);//get first station
					res = true;

				} catch (RestClientException e) {
					stationDTO = null;
					res = false;
				} catch (Exception e) {
					stationDTO = null;
					res = false;
				}
		}

		return res;
	}
	
	
	
	protected void onPostExecute(Boolean result){

			if(result) {
				mActivity.resultOK(stationDTO);
			}
			else{
				mActivity.resultKO();
			}

	}
	

	
}
