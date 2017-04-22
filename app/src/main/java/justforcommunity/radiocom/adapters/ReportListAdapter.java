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
import justforcommunity.radiocom.model.ReportDTO;
import justforcommunity.radiocom.utils.DateUtils;

public class ReportListAdapter extends ArrayAdapter<ReportDTO> implements Filterable {

    private Context mContext;
    private Home mActivity;
    private ItemFilter mFilter = new ItemFilter();
    private List<ReportDTO> originalData = null;
    private List<ReportDTO> filteredData = null;

    public ReportListAdapter(Home mActivity, Context context, int resource, List<ReportDTO> reportDTOs) {
        super(context, resource, reportDTOs);
        this.mContext = context;
        this.mActivity = mActivity;
        originalData = reportDTOs;
        filteredData = reportDTOs;
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
    public ReportDTO getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listitem_report, null);

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

        ReportDTO reportDTO = getItem(position);

        if (reportDTO != null) {
            holder.programName.setText(String.valueOf(reportDTO.getProgram().getName()));
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
            final ArrayList<ReportDTO> nlist = new ArrayList<>(count);
            ReportDTO filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getProgram().getName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
                if (filterableString.getAccount().getName().toLowerCase().contains(filterString)) {
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
            filteredData = (ArrayList<ReportDTO>) results.values;
            notifyDataSetChanged();
        }
    }

}