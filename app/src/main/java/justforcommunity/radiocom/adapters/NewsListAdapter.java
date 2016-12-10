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

package justforcommunity.radiocom.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.pkrss.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.views.CircleTransform;

public class NewsListAdapter extends ArrayAdapter<Article> {

    private Context mContext;
    private Home mActivity;

    public NewsListAdapter(Home mActivity, Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.mContext = context;
        this.mActivity=mActivity;
    }

    public NewsListAdapter(Home mActivity, Context context, int resource, List<Article> articles) {
        super(context, resource, articles);
        this.mContext = context;
        this.mActivity=mActivity;
    }

    static class ViewHolder{
        TextView nameTextView;
        ImageView photoImageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            v=vi.inflate(R.layout.listitem_new, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) v.findViewById(R.id.channel_name);
            holder.photoImageView = (ImageView) v.findViewById(R.id.channel_image);

            v.setTag(holder);

        }else{
            holder = (ViewHolder) v.getTag();
        }

        Article article = getItem(position);


        if (article != null) {


            if (holder.nameTextView != null) {
                if(article.getTitle() == null){
                    holder.nameTextView.setText("");
                }else{
                    holder.nameTextView.setText(article.getTitle());
                }
            }

            if (holder.photoImageView != null) {
                if(article.getImage() == null) {
                    Picasso.with(mContext).load(R.drawable.logo_nav_header).transform(new CircleTransform()).into(holder.photoImageView);
                }else{
                    if (article.getImage().toString() == "") {
                        Picasso.with(mContext).load(R.drawable.logo_nav_header).transform(new CircleTransform()).into(holder.photoImageView);
                    } else {
                        Picasso.with(mContext).load(article.getImage()).transform(new CircleTransform()).into(holder.photoImageView);
                    }
                }

            }

        }

        return v;
    }

}