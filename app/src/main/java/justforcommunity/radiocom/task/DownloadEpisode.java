/*
 *
 *  * Copyright © 2016 @ Fernando Souto González
 *  * Copyright © 2017 @ Pablo Grela
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Podcast;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.InternetConnection;

public class DownloadEpisode extends AsyncTask<Boolean, Float, Boolean> {


    private Context mContext;
    private Podcast mActivity;
    private String title;
    private String fileName;
    private String url;
    private AVLoadingIndicatorView avi;
    private ImageView downloadView;


    public DownloadEpisode(ImageView downloadView, AVLoadingIndicatorView avi, Podcast activity, Context context, String title, String url, String filename) {
        this.mActivity = activity;
        this.mContext = context;
        this.title = title;
        this.url = url;
        this.fileName = filename;
        this.avi = avi;
        this.downloadView = downloadView;
    }

    @Override
    protected void onPreExecute() {

    }

    protected Boolean doInBackground(Boolean... urls) {
        Boolean res = false;

        InternetConnection cnn = new InternetConnection();
        if (cnn.isConnected(mContext)) {
            try {
                File file = new File(GlobalValues.DOWNLOAD_DIR, fileName);
                if (!file.exists()) {
                    file.delete();
                }
                file.getParentFile().mkdirs();
                file.createNewFile();

                res = saveFile(url, GlobalValues.DOWNLOAD_DIR + fileName);

                if (!res) {//if trouble we delete the file
                    file.delete();
                }

            } catch (Exception e) {
                Log.d("DownloadEpisode", "doInBackground", e);
            }
        }

        return res;
    }


    private Boolean saveFile(String urlDownload, String filename) throws Exception {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlDownload);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            boolean redirect = false;

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
                        || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                        || connection.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER) {
                    redirect = true;
                } else {
                    return false;
                }
            }

            if (redirect) {
                String newUrl = connection.getHeaderField("Location");
                //String cookies = connection.getHeaderField("Set-Cookie");
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
            }

            //int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(filename);

            byte data[] = new byte[4096];
            //long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                //total += count;
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return true;

    }


    protected void onPostExecute(Boolean result) {

        if (avi != null) {
            avi.hide();
            avi.setVisibility(View.GONE);
        }

        if (result) {

            if (mActivity != null) {
                mActivity.notifyAdapterToRefresh();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.downloaded) + title, Toast.LENGTH_SHORT).show();
            }

        } else {
            if (downloadView != null) {
                downloadView.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, mContext.getResources().getString(R.string.error_download) + title, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
