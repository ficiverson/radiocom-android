/*
 *
 *  * Copyright © 2016 @ Fernando Souto González
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

package justforcommunity.radiocom.task;

import android.content.Context;
import android.os.AsyncTask;

import com.pkmmte.pkrss.Article;
import com.pkmmte.pkrss.Callback;
import com.pkmmte.pkrss.PkRSS;

import java.util.List;

import justforcommunity.radiocom.fragments.NewsPageFragment;
import justforcommunity.radiocom.utils.InternetConnection;

public class GetNews extends AsyncTask<Boolean, Float, Boolean> {

    private Context mContext;
    private NewsPageFragment fragment;
    private List<Article> articleList;
    private String newsUrl;

    public GetNews(Context context, NewsPageFragment fragment,String newsUrl) {
        this.fragment = fragment;
        this.mContext = context;
        this.newsUrl = newsUrl;
    }

    protected Boolean doInBackground(Boolean... urls) {
        InternetConnection cnn = new InternetConnection();

        if (cnn.isConnected(mContext)) {

            try {
                articleList = PkRSS.with(mContext).load(newsUrl).callback(new Callback() {
                    @Override
                    public void onPreload() {

                    }

                    @Override
                    public void onLoaded(List<Article> newArticles) {

                    }

                    @Override
                    public void onLoadFailed() {

                    }
                }).get();
                return true;
            } catch (Exception e) {
                articleList = null;
            }
        }
        return false;
    }


    protected void onPostExecute(Boolean result) {
        if (result) {
            fragment.listChannels(articleList);
        } else {
            fragment.listChannels(null);
        }
    }

}
