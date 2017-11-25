/*
 *
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

import justforcommunity.radiocom.model.BookDTO;
import justforcommunity.radiocom.service.exceptions.UserAlreadyBookException;
import justforcommunity.radiocom.service.exceptions.WebServiceStatusFailException;
import justforcommunity.radiocom.utils.DateUtils;

import static justforcommunity.radiocom.task.FirebaseUtils.getTokenFirebase;
import static justforcommunity.radiocom.utils.GlobalValues.JSON_BOOK;
import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;
import static justforcommunity.radiocom.utils.GlobalValues.addToken;
import static justforcommunity.radiocom.utils.GlobalValues.createBookURL;
import static justforcommunity.radiocom.utils.GlobalValues.membersURL;
import static justforcommunity.radiocom.utils.GlobalValues.sendAnswerBookURL;

public class ServiceBooks extends ServiceBase {

    public ServiceBooks(Locale language) {
        super(language);
    }

    public List<BookDTO> getBooks(String restURL) throws RestClientException, WebServiceStatusFailException {

        String url = membersURL + restURL + addToken + getTokenFirebase();
        ResponseEntity<String> response;

        try {
            HttpEntity<?> request;
            request = new HttpEntity<Object>(getRequestHeaders());

            response = getRestTemplate().exchange(url, HttpMethod.GET, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new WebServiceStatusFailException();
            }
        } catch (RestClientException e) {
            Log.d("ServiceBooks", "getBooks", e);
            throw e;
        }

        Type listType = new TypeToken<ArrayList<BookDTO>>() {
        }.getType();
        List<BookDTO> booksDTO = new Gson().fromJson(response.getBody(), listType);

        return booksDTO;
    }

    // Send book to members
    public BookDTO sendBook(BookDTO book) throws Exception {

        ResponseEntity<String> response;

        try {
            HttpEntity<?> request;

            // Create the request body as a MultiValueMap
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("token", getTokenFirebase());
            Gson gson = new GsonBuilder().setDateFormat(DateUtils.FORMAT_DISPLAY).create();
            body.add(JSON_BOOK, gson.toJson(book));
            request = new HttpEntity<Object>(body, getRequestHeaders());

            response = getRestTemplate().exchange(createBookURL, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.ALREADY_REPORTED) {
                throw new UserAlreadyBookException();
            } else if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new WebServiceStatusFailException();
            }

            book = new Gson().fromJson(response.getBody(), BookDTO.class);

        } catch (RestClientException e) {
            Log.d("ServiceBooks", "sendBook", e);
            throw e;
        }
        return book;
    }


    // Send answer to members
    public BookDTO SendAnswerBook(Long bookId, String answer, Boolean manage) throws RestClientException, WebServiceStatusFailException {

        BookDTO book;

        String url = sendAnswerBookURL + bookId;
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

            book = new Gson().fromJson(response.getBody(), BookDTO.class);

        } catch (RestClientException e) {
            Log.d("ServiceBooks", "SendAnswerBook", e);
            throw e;
        }
        return book;
    }
}
