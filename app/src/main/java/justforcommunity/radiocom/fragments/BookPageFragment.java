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

package justforcommunity.radiocom.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.App;
import justforcommunity.radiocom.activities.Book;
import justforcommunity.radiocom.activities.CreateBook;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.adapters.BookListAdapter;
import justforcommunity.radiocom.model.BookDTO;
import justforcommunity.radiocom.task.Book.GetBooks;
import justforcommunity.radiocom.utils.FileUtils;
import justforcommunity.radiocom.utils.GlobalValues;

import static justforcommunity.radiocom.utils.GlobalValues.MANAGE;
import static justforcommunity.radiocom.utils.GlobalValues.BOOK_ANSWER_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.BOOK_JSON;
import static justforcommunity.radiocom.utils.GlobalValues.BOOK_REQUEST;
import static justforcommunity.radiocom.utils.GlobalValues.REST_URL;
import static justforcommunity.radiocom.utils.GlobalValues.elementsURL;
import static justforcommunity.radiocom.utils.GlobalValues.booksURL;


public class BookPageFragment extends FilterFragment {

    protected Home mActivity;
    protected Context mContext;
    protected ListView bookList;
    protected TextView noElements;
    protected BookListAdapter myAdapterBooks;
    protected AVLoadingIndicatorView avi;
    protected List<BookDTO> books;
    protected boolean manage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_books, container, false);

        mActivity = (Home) getActivity();
        mContext = getContext();

        bookList = (ListView) v.findViewById(R.id.bookList);
        noElements = (TextView) v.findViewById(R.id.no_elements);

        avi = (AVLoadingIndicatorView) v.findViewById(R.id.avi);
        avi.show();

        // Get Books
        this.manage = true;
        GetBooks gp = new GetBooks(mContext, this, booksURL);
        gp.execute();

        // Float button to create new book
        mActivity.fab_media_hide();
        FloatingActionButton button_create = (FloatingActionButton) v.findViewById(R.id.button_create);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CreateBook.class);
                intent.putExtra(REST_URL, elementsURL);
                startActivityForResult(intent, BOOK_REQUEST);
            }
        });

        App application = (App) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getString(R.string.book_view));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == BOOK_REQUEST || requestCode == BOOK_ANSWER_REQUEST)) {
            BookDTO book = new Gson().fromJson((String) data.getExtras().get(BOOK_JSON), BookDTO.class);
            if (requestCode == BOOK_ANSWER_REQUEST) {
                for (BookDTO aux : new ArrayList<>(books)) {
                    if (aux.getId().equals(book.getId())) {
                        books.remove(aux);
                    }
                }
            }
            if (!manage || book.getState().equals(FileUtils.states.MANAGEMENT.toString())) {
                books.add(0, book);
            }
            setBookList(books);
        }
    }

    @Override
    public void filterDataSearch(String query) {
        if (myAdapterBooks != null) {
            myAdapterBooks.getFilter().filter(query);
        }
    }

    public void resultKO() {
        avi.hide();
        noElements.setVisibility(View.VISIBLE);
        bookList.setVisibility(View.GONE);
        avi.setVisibility(View.GONE);
    }

    public void setBookList(final List<BookDTO> books) {
        avi.hide();
        this.books = books;

        if (books == null || books.size() == 0) {
            noElements.setVisibility(View.VISIBLE);
            bookList.setVisibility(View.GONE);
            avi.setVisibility(View.GONE);
        } else {
            noElements.setVisibility(View.GONE);
            bookList.setVisibility(View.VISIBLE);
            avi.setVisibility(View.GONE);

            myAdapterBooks = new BookListAdapter(mActivity, mContext, R.layout.listitem_new, books, manage);
            bookList.setAdapter(myAdapterBooks);

            bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //serialize object book
                    Gson gson = new Gson();
                    String jsonInString = gson.toJson(myAdapterBooks.getItem(position));

                    //save book object on prefs
                    SharedPreferences prefs = mContext.getSharedPreferences(GlobalValues.prefName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString(BOOK_JSON, jsonInString);
                    edit.apply();

                    //launch next activity
                    Intent intent = new Intent(mActivity, Book.class);
                    intent.putExtra(BOOK_JSON, jsonInString);
                    intent.putExtra(MANAGE, manage);
                    startActivityForResult(intent, BOOK_ANSWER_REQUEST);
                }
            });
        }
    }
}