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

package justforcommunity.radiocom.task.Book;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;

import justforcommunity.radiocom.fragments.BookPageFragment;
import justforcommunity.radiocom.model.BookDTO;
import justforcommunity.radiocom.service.ServiceBooks;
import justforcommunity.radiocom.utils.InternetConnection;

public class GetBooks extends AsyncTask<Boolean, Float, Boolean> {

    private static final String TAG = "GetBooks";
    private Context mContext;
    private BookPageFragment fragment;
    private ServiceBooks serviceBooks;
    private Locale locale;
    private String restURL;
    private List<BookDTO> booksDTO;

    public GetBooks(Context context, BookPageFragment fragment, String restURL) {
        this.fragment = fragment;
        this.mContext = context;
        this.locale = new Locale(mContext.getResources().getConfiguration().locale.toString());
        this.serviceBooks = new ServiceBooks(locale);
        this.restURL = restURL;
    }

    protected Boolean doInBackground(Boolean... urls) {
        boolean res = false;
        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                booksDTO = serviceBooks.getBooks(restURL);
                res = true;

            } catch (RestClientException e) {
                Log.d(TAG, "doInBackground()", e);
                booksDTO = null;
                res = false;
            } catch (Exception e) {
                Log.d(TAG, "doInBackground()", e);
                booksDTO = null;
                res = false;
            }
        }
        return res;
    }

    protected void onPostExecute(Boolean result) {
        if (result) {
            fragment.setBookList(booksDTO);
        } else {
            fragment.resultKO();
        }
    }
}
