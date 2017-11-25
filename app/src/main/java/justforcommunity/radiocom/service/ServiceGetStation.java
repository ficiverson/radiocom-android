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

package justforcommunity.radiocom.service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.model.ResponseStationDTO;
import justforcommunity.radiocom.model.StationDTO;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;
import justforcommunity.radiocom.utils.GlobalValues;

/**
 * Created by iver on 7/3/16.
 */
public class ServiceGetStation extends ServiceBase {

    public ServiceGetStation(Locale idioma) {
        super(idioma);

    }

    /**

     */
    public ResponseStationDTO getStation()
            throws RestClientException, WebServiceStatusFailException
    {


        Object[] theValues = {
        };
        String[] parameters = {
        };


        List<Object> sendValues = new ArrayList<Object>();

        String url = GlobalValues.baseURL + "radiostation" + restQueryString(parameters, theValues, sendValues);
        url = "https://cdn.rawgit.com/ficiverson/66825ca3a0ac74db67515595be7a9429/raw/bb5044afd0681e0e75508e2a476c1f3b140a42a8/station.json";

        ResponseEntity<ResponseStationDTO> response = null;

        try {
            agregarCabeceras(getRequestHeaders());
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
            converter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
            getRestTemplate().getMessageConverters().add(converter);

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, ResponseStationDTO.class, sendValues.toArray());
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
