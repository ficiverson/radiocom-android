/*
 *
 *  * Copyright (C) 2016 @ Appeiros Mobile Development S.L.
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

package justforcommunity.radiocom.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.model.ResponseProgramDTO;
import justforcommunity.radiocom.model.ResponseStationDTO;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;
import justforcommunity.radiocom.utils.GlobalValues;

/**
 * Created by iver on 7/3/16.
 */
public class ServiceGetPrograms extends ServiceBase {

    public ServiceGetPrograms(Locale idioma) {
        super(idioma);

    }

    /**

     */
    public ResponseProgramDTO getPrograms()
            throws RestClientException, WebServiceStatusFailException
    {


        Object[] theValues = {
        };
        String[] parameters = {
        };


        List<Object> sendValues = new ArrayList<Object>();

        String url = GlobalValues.baseURL+"program" + restQueryString(parameters, theValues, sendValues);

        ResponseEntity<ResponseProgramDTO> response = null;

        try {
            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            getRestTemplate().getMessageConverters().add(new GsonHttpMessageConverter());

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, ResponseProgramDTO.class, sendValues.toArray());
            if (response.getStatusCode() != HttpStatus.OK)
            {
                throw new WebServiceStatusFailException();
            }
        } catch (RestClientException e) {
            throw e;
        }

        return response.getBody();
    }
}
