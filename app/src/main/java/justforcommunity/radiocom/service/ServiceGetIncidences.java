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

package justforcommunity.radiocom.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.model.IncidenceDTO;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;

import static justforcommunity.radiocom.task.FirebaseUtils.getTokenFirebase;
import static justforcommunity.radiocom.utils.GlobalValues.INCIDENCE_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.createIncidenceURL;
import static justforcommunity.radiocom.utils.GlobalValues.incidencesURL;

public class ServiceGetIncidences extends ServiceBase {

    public ServiceGetIncidences(Locale language) {
        super(language);
    }

    public List<IncidenceDTO> getIncidences() throws RestClientException, WebServiceStatusFailException {

        Object[] theValues = {};
        String[] parameters = {};

        List<Object> sendValues = new ArrayList<>();
        String url = incidencesURL + "?token=" + getTokenFirebase() + restQueryString(parameters, theValues, sendValues);
        ResponseEntity<String> response;

        try {
            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, String.class, sendValues.toArray());

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }
        } catch (RestClientException e) {
            Log.e("ServiceGetIncidences", "getIncidences()", e);
            throw e;
        }

        Type listType = new TypeToken<ArrayList<IncidenceDTO>>(){}.getType();
        List<IncidenceDTO> incidencesDTO = new Gson().fromJson(response.getBody(), listType);

        return incidencesDTO;
    }

    // Send incidence to members
    public IncidenceDTO sendIncidence(IncidenceDTO incidence) throws RestClientException, WebServiceStatusFailException {

        Object[] theValues = {};
        String[] parameters = {};

        List<Object> sendValues = new ArrayList<>();
        String url = createIncidenceURL + restQueryString(parameters, theValues, sendValues);
        ResponseEntity<String> response;

        try {

            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;

            // Create the request body as a MultiValueMap
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("token", getTokenFirebase());
            body.add(INCIDENCE_JSON, new Gson().toJson(incidence));
            request = new HttpEntity<Object>(body, getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.POST, request, String.class, sendValues.toArray());

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new WebServiceStatusFailException();
            }

            incidence = new Gson().fromJson(response.getBody(), IncidenceDTO.class);

        } catch (RestClientException e) {
            Log.e("ServiceGetIncidences", "sendIncidence()", e);
            throw e;
        }
        return incidence;
    }
}
