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
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pkmmte.pkrss.Article;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.ContentDetail;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.activities.Podcast;
import justforcommunity.radiocom.task.DownloadEpisode;
import justforcommunity.radiocom.utils.GlobalValues;
import justforcommunity.radiocom.utils.PodcastingService;
import justforcommunity.radiocom.utils.StreamingService;
import justforcommunity.radiocom.utils.TaskHelper;
import justforcommunity.radiocom.views.CircleTransform;

/**
 * Created by iver on 5/9/16.
 */


public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ViewHolder> {

    private Context mContext;
    private Podcast mActivity;
    private List<Article> articles;


    public ProgramListAdapter(Podcast mActivity, Context context, List<Article> articles) {
        this.mContext = context;
        this.mActivity=mActivity;
        this.articles=articles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_program, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        Article article = articles.get(position);
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

            if(holder.playView!=null && holder.downloadView!=null){
                final File f = new File(GlobalValues.DOWNLOAD_DIR+getFileName(articles.get(position).getEnclosure().getUrl()));

                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) holder.playView.getLayoutParams();

                if(f.exists()){
                    holder.playView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(mActivity.prefs.getString("currentTitlePlaying","").equals(articles.get(position).getTitle())){
                                Intent i = new Intent(mActivity, PodcastingService.class);
                                mActivity.stopService(i);

                                mActivity.edit.putString("currentTitlePlaying","");
                                mActivity.edit.commit();

                                holder.playView.setBackground(mContext.getResources().getDrawable(R.drawable.play));
                                holder.downloadView.setBackground(mContext.getResources().getDrawable(R.drawable.download));//play
                                holder.nameTextView.setTextColor(Color.BLACK);
                            }
                            else {

                                Intent localIntent2 = new Intent(mActivity, PodcastingService.class);
                                localIntent2.putExtra("audio", f.toString());
                                localIntent2.putExtra("text", getDate(articles.get(position).getDate()));
                                localIntent2.putExtra("title", articles.get(position).getTitle());
                                mActivity.startService(localIntent2);

                                mActivity.edit.putString("currentTitlePlaying", articles.get(position).getTitle());
                                mActivity.edit.commit();

                                holder.playView.setBackground(mContext.getResources().getDrawable(R.drawable.stopcolored));//stop
                                holder.downloadView.setBackground(mContext.getResources().getDrawable(R.drawable.downloadcolored));
                                holder.nameTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                            }
                            notifyDataSetChanged();

                        }
                    });
                    //take off download
                    holder.downloadView.setVisibility(View.GONE);
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                    holder.playView.setLayoutParams(layoutParams);

                }
                else{
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                    holder.playView.setLayoutParams(layoutParams);


                    holder.playView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(mActivity.prefs.getString("currentTitlePlaying","").equals(articles.get(position).getTitle())){
                                Intent i = new Intent(mActivity, PodcastingService.class);
                                mActivity.stopService(i);

                                mActivity.edit.putString("currentTitlePlaying","");
                                mActivity.edit.commit();

                                holder.playView.setBackground(mContext.getResources().getDrawable(R.drawable.play));
                                holder.downloadView.setBackground(mContext.getResources().getDrawable(R.drawable.download));//play
                                holder.nameTextView.setTextColor(Color.BLACK);
                            }
                            else {
                                Intent localIntent2 = new Intent(mActivity, PodcastingService.class);
                                localIntent2.putExtra("audio", articles.get(position).getEnclosure().getUrl());
                                localIntent2.putExtra("text",  getDate(articles.get(position).getDate()));
                                localIntent2.putExtra("title", articles.get(position).getTitle());
                                mActivity.startService(localIntent2);

                                mActivity.edit.putString("currentTitlePlaying", articles.get(position).getTitle());
                                mActivity.edit.commit();

                                holder.playView.setBackground(mContext.getResources().getDrawable(R.drawable.stopcolored));//stop
                                holder.downloadView.setBackground(mContext.getResources().getDrawable(R.drawable.downloadcolored));
                                holder.nameTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                            }
                            notifyDataSetChanged();
                        }
                    });
                    holder.downloadView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.downloadView.setVisibility(View.INVISIBLE);
                            holder.avi.setVisibility(View.VISIBLE);
                            holder.avi.show();
                            //launch task
                            TaskHelper.execute(new DownloadEpisode(holder.downloadView,holder.avi,mActivity,mContext,articles.get(position).getTitle(),articles.get(position).getEnclosure().getUrl(),getFileName(articles.get(position).getEnclosure().getUrl())));
                        }
                    });
                }
            }

            //default paint
            if(mActivity.prefs.getString("currentTitlePlaying","").equals(articles.get(position).getTitle())){
                holder.playView.setBackground(mContext.getResources().getDrawable(R.drawable.stopcolored));
                holder.downloadView.setBackground(mContext.getResources().getDrawable(R.drawable.downloadcolored));//stop
                holder.nameTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            }
            else{
                holder.playView.setBackground(mContext.getResources().getDrawable(R.drawable.play));
                holder.downloadView.setBackground(mContext.getResources().getDrawable(R.drawable.download));//play
                holder.nameTextView.setTextColor(Color.BLACK);
            }
            holder.itemView.setTag(article);
        }
    }


    @Override
    public int getItemCount() {
        return articles.size();
    }



    public String  getDate(long date){

        try{
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(date));
            return sdf.format(netDate);
        } catch (Exception ignored) {
            return " ";
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public ImageView photoImageView;
        public ImageView downloadView;
        public ImageView playView;
        public AVLoadingIndicatorView avi;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            photoImageView = (ImageView) itemView.findViewById(R.id.channel_image);
            nameTextView = (TextView) itemView.findViewById(R.id.channel_name);
            downloadView = (ImageView) itemView.findViewById(R.id.download_image);
            playView = (ImageView)itemView.findViewById(R.id.play_image);
            avi = (AVLoadingIndicatorView)itemView.findViewById(R.id.avi);

        }

        @Override
        public void onClick(final View view) {

            Intent intent = new Intent(mActivity, ContentDetail.class);
            if(articles.get( getPosition()).getContent()!=null) {
                intent.putExtra(GlobalValues.EXTRA_CONTENT, articles.get(getPosition()).getContent());
            }
            else if(articles.get(getPosition()).getDescription()!=null){
                intent.putExtra(GlobalValues.EXTRA_CONTENT, articles.get(getPosition()).getDescription());
            }
            else{
                intent.putExtra(GlobalValues.EXTRA_CONTENT,  mContext.getString(R.string.no_content));
            }
            intent.putExtra(GlobalValues.EXTRA_TITLE,articles.get( getPosition()).getTitle());
            mActivity.startActivity(intent);

        }
    }
    public String getFileName(String url){

        Uri uri = Uri.parse(url);
        String filename = uri.getLastPathSegment();

        return filename.trim();
    }

}
