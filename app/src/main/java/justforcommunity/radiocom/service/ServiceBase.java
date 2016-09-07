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


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by iver on 7/3/16.
 */

public class ServiceBase {


    protected static final String PARAM_FORMAT = "_format";

    protected Locale _idioma;
    private HttpHeaders _requestHeaders;
    private RestTemplate _restTemplate;

    public ServiceBase(Locale idioma) {
        _idioma = idioma;
        _requestHeaders = new HttpHeaders();
        _restTemplate = new RestTemplate();

        if (_restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
            SimpleClientHttpRequestFactory  rf = (SimpleClientHttpRequestFactory)_restTemplate.getRequestFactory();
            rf.setReadTimeout(20*1000); // 20 seconds
            rf.setConnectTimeout(20*1000); // 20 seconds
        } else if (_restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
            HttpComponentsClientHttpRequestFactory rf = (HttpComponentsClientHttpRequestFactory)_restTemplate.getRequestFactory();
            rf.setReadTimeout(20*1000); // 20 seconds
            rf.setConnectTimeout(20*1000); // 20 seconds
        }
    }

    /**
     * Establece las cabeceras por defecto de todas las llamadas a los servicios web.
     * @param cabeceras Cabeceras para la peticiÃ³n HTTP.
     */
    protected void agregarCabeceras(HttpHeaders cabeceras)
    {
        cabeceras.setAccept(Collections.singletonList(new MediaType("application","json")));
        cabeceras.setAcceptCharset(Collections.singletonList(Charset.forName("UTF-8")));
        cabeceras.setAcceptLanguage(_idioma.getLanguage());
        //cabeceras.add("Api-Key", API_KEY);
        //cabeceras.setContentType(new MediaType("multipart", "form-data"));
        //cabeceras.setContentType(new MediaType("application", "x-www-form-urlencoded"));

    }

    protected Locale getIdioma() {
        return _idioma;
    }

    protected void setIdioma(Locale idioma) {
        this._idioma = idioma;
    }

    protected HttpHeaders getRequestHeaders() {
        return _requestHeaders;
    }

    protected void setRequestHeaders(HttpHeaders requestHeaders) {
        this._requestHeaders = requestHeaders;
    }

    protected RestTemplate getRestTemplate() {
        return _restTemplate;
    }

    protected void setRestTemplate(RestTemplate restTemplate) {
        this._restTemplate = restTemplate;
    }

    protected String restQueryString(String[] parameters) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < parameters.length - 1; i++) {
            sb.append(parameters[i] + "={" + parameters[i] + "}&");
        }
        sb.append(parameters[i] + "={" + parameters[i] + "}");
        return sb.toString();
    }

    protected String restQueryString(String[] parameters, Object[] theValues, List<Object> sendValues) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < parameters.length; i++) {
            if (theValues[i] == null)
                continue;
            String ampersand = "&";
            if (i == (parameters.length - 1))
                ampersand = "";
            sb.append(parameters[i] + "={" + parameters[i] + "}" + ampersand);
            sendValues.add(theValues[i]);
        }
        return sb.toString();
    }
}

