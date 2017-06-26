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

import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;

import static justforcommunity.radiocom.task.FirebaseUtils.getTokenFirebase;
import static justforcommunity.radiocom.utils.GlobalValues.JSON_REPORT;
import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;
import static justforcommunity.radiocom.utils.GlobalValues.addToken;
import static justforcommunity.radiocom.utils.GlobalValues.createReportURL;
import static justforcommunity.radiocom.utils.GlobalValues.sendAnswerReportURL;

public class ServiceReports extends ServiceBase {

    public ServiceReports(Locale language) {
        super(language);
    }

    public List<ReportDTO> getReports(String restURL) throws RestClientException, WebServiceStatusFailException {

        String url = restURL + addToken + getTokenFirebase();
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
            Log.d("ServiceReports", "getReports()", e);
            throw e;
        }

        Type listType = new TypeToken<ArrayList<ReportDTO>>() {
        }.getType();
        List<ReportDTO> reportsDTO = new Gson().fromJson(response.getBody(), listType);

        return reportsDTO;
    }

    // Send report to members
    public ReportDTO sendReport(ReportDTO report, String photosJson) throws RestClientException, WebServiceStatusFailException {

        ResponseEntity<String> response;

        try {
            HttpEntity<?> request;

            // Create the request body as a MultiValueMap
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("token", getTokenFirebase());
            body.add(JSON_REPORT, new Gson().toJson(report));
            body.add("photos", photosJson);
            request = new HttpEntity<Object>(body, getRequestHeaders());

            response = getRestTemplate().exchange(createReportURL, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new WebServiceStatusFailException();
            }

            report = new Gson().fromJson(response.getBody(), ReportDTO.class);

        } catch (RestClientException e) {
            Log.d("ServiceReports", "sendReport()", e);
            throw e;
        }
        return report;
    }


    // Send answer to members
    public ReportDTO sendAnswerReport(Long reportId, String answer, Boolean manage) throws RestClientException, WebServiceStatusFailException {

        ReportDTO report;

        String url = sendAnswerReportURL + reportId;
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

            report = new Gson().fromJson(response.getBody(), ReportDTO.class);

        } catch (RestClientException e) {
            Log.d("ServiceReports", "sendReport()", e);
            throw e;
        }
        return report;
    }
}
