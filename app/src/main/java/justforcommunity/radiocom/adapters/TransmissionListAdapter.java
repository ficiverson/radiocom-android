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

package justforcommunity.radiocom.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.model.TransmissionDTO;
import justforcommunity.radiocom.utils.DateUtils;

public class TransmissionListAdapter extends ArrayAdapter<TransmissionDTO> implements Filterable {

    private ItemFilter mFilter = new ItemFilter();
    private List<TransmissionDTO> originalData = null;
    private List<TransmissionDTO> filteredData = null;

    public TransmissionListAdapter(Context context, int resource, List<TransmissionDTO> transmissions) {
        super(context, resource, transmissions);
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
    public TransmissionDTO getItem(int position) {
        return filteredData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());

            v = vi.inflate(R.layout.listitem_transmmissions, null);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) v.findViewById(R.id.transmission_name);
            holder.hourTextView = (TextView) v.findViewById(R.id.transmission_hour);
            holder.photoImageView = (ImageView) v.findViewById(R.id.transmission_image);
            holder.colorTransparency = v.findViewById(R.id.transmission_color);
            v.setTag(holder);

        } else {
            holder = (ViewHolder) v.getTag();
        }

        TransmissionDTO transmissionDTO = getItem(position);
        if (transmissionDTO != null) {

            Date actualDate = new Date();
            if (transmissionDTO.getStart().before(actualDate) && transmissionDTO.getEnd().after(actualDate)) {
                holder.colorTransparency.setBackgroundColor(Color.BLUE);
            } else {
                holder.colorTransparency.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark85));
            }

            holder.nameTextView.setText(transmissionDTO.getName());
            holder.hourTextView.setText(DateUtils.formatDate(transmissionDTO.getStart(), DateUtils.FORMAT_HOUR)
                    + " - "
                    + DateUtils.formatDate(transmissionDTO.getEnd(), DateUtils.FORMAT_HOUR));

//            if (holder.photoImageView != null) {
//                if (transmissionDTO.getLogo_url() == null) {
//                    Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
//                } else {
//                    if (transmissionDTO.getLogo_url() == "") {
//                        Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
//                    } else {
//                        Picasso.with(mContext).load(transmissionDTO.getLogo_url()).into(holder.photoImageView);
//                    }
//                }
//            }

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
            final List<TransmissionDTO> list = originalData;
            int count = list.size();
            final ArrayList<TransmissionDTO> nList = new ArrayList<>(count);
            TransmissionDTO filterableString;

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
            filteredData = (ArrayList<TransmissionDTO>) results.values;
            notifyDataSetChanged();
        }

    }

}