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
import com.google.gson.GsonBuilder;
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

import justforcommunity.radiocom.model.ReserveDTO;
import justforcommunity.radiocom.service.exceptions.UserAlreadyReserveException;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;
import justforcommunity.radiocom.utils.DateUtils;

import static justforcommunity.radiocom.task.FirebaseUtils.getTokenFirebase;
import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;
import static justforcommunity.radiocom.utils.GlobalValues.RESERVE_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.createReserveURL;
import static justforcommunity.radiocom.utils.GlobalValues.sendAnswerReserveURL;

public class ServiceReserves extends ServiceBase {

    public ServiceReserves(Locale language) {
        super(language);
    }

    public List<ReserveDTO> getReserves(String restURL) throws RestClientException, WebServiceStatusFailException {

        String url = restURL + "?token=" + getTokenFirebase();
        ResponseEntity<String> response;

        try {
            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }
        } catch (RestClientException e) {
            Log.e("ServiceReserves", "getReserves", e);
            throw e;
        }

        Type listType = new TypeToken<ArrayList<ReserveDTO>>() {
        }.getType();
        List<ReserveDTO> reservesDTO = new Gson().fromJson(response.getBody(), listType);

        return reservesDTO;
    }

    // Send reserve to members
    public ReserveDTO sendReserve(ReserveDTO reserve) throws Exception {

        ResponseEntity<String> response;

        try {
            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;

            // Create the request body as a MultiValueMap
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("token", getTokenFirebase());
            Gson gson = new GsonBuilder().setDateFormat(DateUtils.FORMAT_DISPLAY).create();
            body.add(RESERVE_JSON, gson.toJson(reserve));
            request = new HttpEntity<Object>(body, getRequestHeaders());

            response = getRestTemplate().exchange(createReserveURL, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.ALREADY_REPORTED) {
                throw new UserAlreadyReserveException();
            } else if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new WebServiceStatusFailException();
            }

            reserve = new Gson().fromJson(response.getBody(), ReserveDTO.class);

        } catch (RestClientException e) {
            Log.e("ServiceReserves", "sendReserve", e);
            throw e;
        }
        return reserve;
    }


    // Send answer to members
    public ReserveDTO SendAnswerReserve(Long reserveId, String answer, Boolean manage) throws RestClientException, WebServiceStatusFailException {

        ReserveDTO reserve;

        String url = sendAnswerReserveURL + reserveId;
        ResponseEntity<String> response;

        try {
            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;

            // Create the request body as a MultiValueMap
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("token", getTokenFirebase());
            body.add("answer", answer);
            body.add(MANAGE, manage);
            request = new HttpEntity<Object>(body, getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new WebServiceStatusFailException();
            }

            reserve = new Gson().fromJson(response.getBody(), ReserveDTO.class);

        } catch (RestClientException e) {
            Log.e("ServiceReserves", "SendAnswerReserve", e);
            throw e;
        }
        return reserve;
    }
}
