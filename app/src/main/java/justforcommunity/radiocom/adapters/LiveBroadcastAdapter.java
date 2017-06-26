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

package justforcommunity.radiocom.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import java.util.Date;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.LiveBroadcastDTO;
import justforcommunity.radiocom.utils.DateUtils;

public class LiveBroadcastAdapter extends ArrayAdapter<LiveBroadcastDTO> implements Filterable {

    private Context mContext;
    private ItemFilter mFilter = new ItemFilter();
    private List<LiveBroadcastDTO> originalData = null;
    private List<LiveBroadcastDTO> filteredData = null;

    public LiveBroadcastAdapter(Context context, int resource, List<LiveBroadcastDTO> transmissions) {
        super(context, resource, transmissions);
        mContext = context;
        originalData = transmissions;
        filteredData = transmissions;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView hourTextView;
        ImageView photoImageView;
        View colorTransparency;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public LiveBroadcastDTO getItem(int position) {
        return filteredData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            v = vi.inflate(R.layout.listitem_transmissions, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) v.findViewById(R.id.transmission_name);
            holder.hourTextView = (TextView) v.findViewById(R.id.transmission_hour);
            holder.photoImageView = (ImageView) v.findViewById(R.id.transmission_image);
            holder.colorTransparency = v.findViewById(R.id.transmission_color);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        LiveBroadcastDTO liveBroadcastDTO = getItem(position);
        if (liveBroadcastDTO != null) {

            Date actualDate = new Date();
            if (liveBroadcastDTO.getStart().before(actualDate) && liveBroadcastDTO.getEnd().after(actualDate)) {
                holder.colorTransparency.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSecondary85));
            } else {
                holder.colorTransparency.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark85));
            }

            holder.nameTextView.setText(liveBroadcastDTO.getName());
            holder.hourTextView.setText(DateUtils.formatDate(liveBroadcastDTO.getStart(), DateUtils.FORMAT_HOUR)
                    + " - "
                    + DateUtils.formatDate(liveBroadcastDTO.getEnd(), DateUtils.FORMAT_HOUR));

            if (holder.photoImageView != null) {
                if (liveBroadcastDTO.getLogo_url() == null) {
                    Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
                } else {
                    if (liveBroadcastDTO.getLogo_url() == "") {
                        Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
                    } else {
                        Picasso.with(mContext).load(liveBroadcastDTO.getLogo_url()).into(holder.photoImageView);
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
            final List<LiveBroadcastDTO> list = originalData;
            int count = list.size();
            final ArrayList<LiveBroadcastDTO> nList = new ArrayList<>(count);
            LiveBroadcastDTO filterableString;

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
            filteredData = (ArrayList<LiveBroadcastDTO>) results.values;
            notifyDataSetChanged();
        }

    }

}