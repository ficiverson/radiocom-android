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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.model.IncidenceDTO;
import justforcommunity.radiocom.utils.DateUtils;

public class IncidenceListAdapter extends ArrayAdapter<IncidenceDTO> implements Filterable {

    private Context mContext;
    private Home mActivity;
    private ItemFilter mFilter = new ItemFilter();
    private List<IncidenceDTO> originalData = null;
    private List<IncidenceDTO> filteredData = null;

    public IncidenceListAdapter(Home mActivity, Context context, int resource, List<IncidenceDTO> incidenceDTOs) {
        super(context, resource, incidenceDTOs);
        this.mContext = context;
        this.mActivity = mActivity;
        originalData = incidenceDTOs;
        filteredData = incidenceDTOs;
    }

    static class ViewHolder {
        TextView programName;
        TextView dateCreate;
        TextView tidy;
        TextView dirt;
        TextView configuration;
        ImageView photoImageView;
        View viewtrans;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public IncidenceDTO getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listitem_incidence, null);

            holder = new ViewHolder();
            holder.programName = (TextView) v.findViewById(R.id.programName);
            holder.dateCreate = (TextView) v.findViewById(R.id.dateCreate);
            holder.tidy = (TextView) v.findViewById(R.id.tidy);
            holder.dirt = (TextView) v.findViewById(R.id.dirt);
            holder.configuration = (TextView) v.findViewById(R.id.configuration);
            holder.photoImageView = (ImageView) v.findViewById(R.id.channel_image);
            holder.viewtrans = (View) v.findViewById(R.id.viewtrans);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        IncidenceDTO incidenceDTO = getItem(position);


        if (incidenceDTO != null) {

            holder.programName.setText(String.valueOf(incidenceDTO.getProgram().getName()));
            holder.dateCreate.setText(DateUtils.formatDate(incidenceDTO.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            holder.tidy.setText(String.valueOf(incidenceDTO.getTidy()));
            holder.dirt.setText(String.valueOf(incidenceDTO.getDirt()));
            holder.configuration.setText(String.valueOf(incidenceDTO.getConfiguration()));


//            if (holder.nameTextView != null) {
//                if (incidenceDTO.getDescription() == null) {
//                    holder.nameTextView.setText("");
//                } else {
//                    holder.nameTextView.setText(incidenceDTO.getDescription());
//                }
//            }

//            if (holder.photoImageView != null) {
//                if (incidenceDTO.getLogo_url() == null) {
//                    Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
//                } else {
//                    if (incidenceDTO.getLogo_url() == "") {
//                        Picasso.with(mContext).load(R.drawable.logo_nav_header).into(holder.photoImageView);
//                    } else {
//                        Picasso.with(mContext).load(incidenceDTO.getLogo_url()).into(holder.photoImageView);
//                    }
//                }
//
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
            final List<IncidenceDTO> list = originalData;
            int count = list.size();
            final ArrayList<IncidenceDTO> nlist = new ArrayList<>(count);
            IncidenceDTO filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getDescription().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<IncidenceDTO>) results.values;
            notifyDataSetChanged();
        }
    }

}