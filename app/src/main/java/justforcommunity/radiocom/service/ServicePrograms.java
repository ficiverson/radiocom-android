/*
 *
 *  * Copyright (C) 2016 @ Fernando Souto González
 *  * Copyright (C) 2017 @ Pablo Grela
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.model.ProgramDTO;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;

import static justforcommunity.radiocom.task.FirebaseUtils.getTokenFirebase;
import static justforcommunity.radiocom.utils.GlobalValues.addToken;
import static justforcommunity.radiocom.utils.GlobalValues.programmesURL;

public class ServicePrograms extends ServiceBase {

    public ServicePrograms(Locale language) {
        super(language);
    }

    public List<ProgramDTO> getPrograms() throws RestClientException, WebServiceStatusFailException {

        ResponseEntity<String> response;
        String decoded = "";

        try {
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
            getRestTemplate().getMessageConverters().add(converter);

            response = getRestTemplate().exchange(programmesURL, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }

            // Decode String, in some cases, it may not be necessary
            decoded = response.getBody();
            decoded = new String(response.getBody().getBytes("ISO-8859-1"));

        } catch (RestClientException e) {
            Log.d("ServicePrograms", "getPrograms", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            Log.d("ServiceTransmissions", "fail decoded", e);
        }

        Type listType = new TypeToken<ArrayList<ProgramDTO>>() {
        }.getType();

        return new Gson().fromJson(decoded, listType);
    }


    public List<ProgramDTO> getPrograms(String restURL) throws RestClientException, WebServiceStatusFailException {

        String url = restURL + addToken + getTokenFirebase();
        ResponseEntity<String> response;

        try {
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }
        } catch (RestClientException e) {
            Log.d("ServicePrograms", "getProgramsUser", e);
            throw e;
        }

        Type listType = new TypeToken<ArrayList<ProgramDTO>>() {
        }.getType();
        List<ProgramDTO> programsDTO = new Gson().fromJson(response.getBody(), listType);

        return programsDTO;
    }
}
