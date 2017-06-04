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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.ProgramDTO;

import static justforcommunity.radiocom.utils.GlobalValues.radiocoURL;

public class PodcastListAdapter extends ArrayAdapter<ProgramDTO> implements Filterable {

    private Context mContext;
    private ItemFilter mFilter = new ItemFilter();
    private List<ProgramDTO> originalData = null;
    private List<ProgramDTO> filteredData = null;

    public PodcastListAdapter(Context context, int resource, List<ProgramDTO> podcast) {
        super(context, resource, podcast);
        this.mContext = context;
        originalData = podcast;
        filteredData = podcast;
    }

    static class ViewHolder {
        TextView nameTextView;
        ImageView photoImageView;
        View viewtrans;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public ProgramDTO getItem(int position) {
        return filteredData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            v = vi.inflate(R.layout.listitem_podcast, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) v.findViewById(R.id.channel_name);
            holder.photoImageView = (ImageView) v.findViewById(R.id.channel_image);
            holder.viewtrans = (View) v.findViewById(R.id.viewtrans);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        ProgramDTO programDTO = getItem(position);


        if (programDTO != null) {

            if (holder.nameTextView != null) {
                if (programDTO.getName() == null) {
                    holder.nameTextView.setText("");
                } else {
                    holder.nameTextView.setText(programDTO.getName());
                }
            }

            if (holder.photoImageView != null) {
                if (programDTO.getLogo_url() == null) {
                    Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
                } else {
                    if (programDTO.getLogo_url() == "") {
                        Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
                    } else {
                        Picasso.with(mContext).load(radiocoURL + programDTO.getLogo_url()).into(holder.photoImageView);
                    }
                }

            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<ProgramDTO> list = originalData;
            int count = list.size();
            final ArrayList<ProgramDTO> nList = new ArrayList<ProgramDTO>(count);
            ProgramDTO filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getName().toLowerCase().contains(filterString)) {
                    nList.add(filterableString);
                }
            }

            results.values = nList;
            results.count = nList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<ProgramDTO>) results.values;
            notifyDataSetChanged();
        }

    }

}