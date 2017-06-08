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

package justforcommunity.radiocom.service;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.model.TransmissionDTO;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;

import static justforcommunity.radiocom.utils.GlobalValues.transmissionNowURL;
import static justforcommunity.radiocom.utils.GlobalValues.transmissionsURL;


public class ServiceTransmissions extends ServiceBase {

    public ServiceTransmissions(Locale language) {
        super(language);
    }

    public TransmissionDTO getTransmissionNow() throws RestClientException, WebServiceStatusFailException {

        ResponseEntity<String> response;
        String decoded = "";

        try {
            HttpEntity<?> request = new HttpEntity<Object>(getRequestHeaders());

            response = getRestTemplate().exchange(transmissionNowURL, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }

            // Decode String, in some cases, it may not be necessary
            decoded = response.getBody();
            decoded = new String(response.getBody().getBytes("ISO-8859-1"));

        } catch (RestClientException e) {
            Log.d("ServiceTransmissions", "getTransmissionNow", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            Log.d("ServiceTransmissions", "fail decoded", e);
        }

        return new Gson().fromJson(decoded, TransmissionDTO.class);
    }

    public List<TransmissionDTO> getTransmissions(String dateSearch) throws RestClientException, WebServiceStatusFailException {

//        Calendar startDate = Calendar.getInstance();
//        startDate.add(Calendar.DAY_OF_MONTH, 0);
//        Calendar endDate = Calendar.getInstance();
//        endDate.add(Calendar.DAY_OF_MONTH, 0);
//        String url = transmissionsURL + "?after=" + DateUtils.formatDate(startDate, DateUtils.FORMAT_DATE_GET) + "&before=" + DateUtils.formatDate(endDate, DateUtils.FORMAT_DATE_GET);

        String url = transmissionsURL + "?after=" + dateSearch + "&before=" + dateSearch;
        ResponseEntity<String> response;
        String decoded = "";

        try {
            HttpEntity<?> request = new HttpEntity<Object>(getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }

            // Decode String, in some cases, it may not be necessary
            decoded = response.getBody();
            decoded = new String(response.getBody().getBytes("ISO-8859-1"));

        } catch (RestClientException e) {
            Log.d("ServiceTransmissions", "getTransmissions", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            Log.d("ServiceTransmissions", "fail decoded", e);
        }

        Type listType = new TypeToken<ArrayList<TransmissionDTO>>() {
        }.getType();

        return new Gson().fromJson(decoded, listType);
    }
}
