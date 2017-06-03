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
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.utils.DateUtils;

public class ReportListAdapter extends ArrayAdapter<ReportDTO> implements Filterable {

    private ItemFilter mFilter = new ItemFilter();
    private List<ReportDTO> originalData = null;
    private List<ReportDTO> filteredData = null;
    private boolean management;

    public ReportListAdapter(Context context, int resource, List<ReportDTO> reportDTOs, boolean management) {
        super(context, resource, reportDTOs);
        this.originalData = reportDTOs;
        this.filteredData = reportDTOs;
        this.management = management;
    }

    static class ViewHolder {
        TextView programName;
        TextView accountName;
        TextView dateCreate;
        TextView tidy;
        TextView dirt;
        TextView configuration;
        ImageView photoImageView;
        View colorTransparency;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public ReportDTO getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listitem_report, null);

            holder = new ViewHolder();
            holder.programName = (TextView) v.findViewById(R.id.programName);
            holder.accountName = (TextView) v.findViewById(R.id.accountName);
            holder.dateCreate = (TextView) v.findViewById(R.id.dateCreate);
            holder.tidy = (TextView) v.findViewById(R.id.tidy);
            holder.dirt = (TextView) v.findViewById(R.id.dirt);
            holder.configuration = (TextView) v.findViewById(R.id.configuration);
            holder.photoImageView = (ImageView) v.findViewById(R.id.channel_image);
            holder.colorTransparency = v.findViewById(R.id.viewtrans);

            if (management) {
                holder.accountName.setVisibility(View.VISIBLE);
            } else {
                holder.accountName.setVisibility(View.GONE);
            }
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        ReportDTO reportDTO = getItem(position);

        if (reportDTO != null) {
            holder.programName.setText(String.valueOf(reportDTO.getProgram().getName()));
            holder.accountName.setText(String.valueOf(reportDTO.getAccount().getFullName()));
            holder.dateCreate.setText(DateUtils.formatDate(reportDTO.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            holder.tidy.setText(String.valueOf(reportDTO.getTidy()));
            holder.dirt.setText(String.valueOf(reportDTO.getDirt()));
            holder.configuration.setText(String.valueOf(reportDTO.getConfiguration()));
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
            final List<ReportDTO> list = originalData;
            int count = list.size();
            final ArrayList<ReportDTO> nList = new ArrayList<>(count);
            ReportDTO filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getProgram().getName().toLowerCase().contains(filterString) ||
                        filterableString.getAccount().getFullName().toLowerCase().contains(filterString)) {
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
            filteredData = (ArrayList<ReportDTO>) results.values;
            notifyDataSetChanged();
        }
    }

}