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

package justforcommunity.radiocom.model;

import java.util.List;

import justforcommunity.radiocom.utils.GlobalValues;

/**
 * Created by iver on 2/9/16.
 */
public class StationDTO {

    private String big_icon_url;
    private String history;
    private String icon_url;
    private String latitude;
    private String longitude;
    private String news_rss;
    private String station_name;
    private List<String> station_photos;
    private String stream_url;
    private String facebook_url;
    private String twitter_url;

    public String getBig_icon_url() {
        return big_icon_url;
    }

    public void setBig_icon_url(String big_icon_url) {
        this.big_icon_url = big_icon_url;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNews_rss() {
        return news_rss;
    }

    public void setNews_rss(String news_rss) {
        this.news_rss = news_rss;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public List<String> getStation_photos() {
        return station_photos;
    }

    public void setStation_photos(List<String> station_photos) {
        this.station_photos = station_photos;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }


    public String getFacebook_url() {
        if (facebook_url != null && !facebook_url.isEmpty()) {
            return facebook_url;
        } else {
            return GlobalValues.facebookName;
        }
    }

    public void setFacebook_url(String facebook_url) {
        this.facebook_url = facebook_url;
    }

    public String getTwitter_url() {
        if (twitter_url != null && !twitter_url.isEmpty()) {
            return twitter_url;
        } else {
            return GlobalValues.twitter + GlobalValues.twitterName;
        }
    }

    public void setTwitter_url(String twitter_url) {
        this.twitter_url = twitter_url;
    }

    public String getTwitter_user() {
        if (!twitter_url.isEmpty()) {
            return twitter_url.split(GlobalValues.twitter)[1];
        } else {
            return GlobalValues.twitterName;
        }
    }

}
