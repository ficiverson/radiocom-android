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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import justforcommunity.radiocom.R;
import justforcommunity.radiocom.activities.Home;
import justforcommunity.radiocom.model.BookDTO;
import justforcommunity.radiocom.utils.DateUtils;
import justforcommunity.radiocom.utils.FileUtils;

public class BookListAdapter extends ArrayAdapter<BookDTO> implements Filterable {

    private Context mContext;
    private ItemFilter mFilter = new ItemFilter();
    private List<BookDTO> originalData = null;
    private List<BookDTO> filteredData = null;
    private boolean management;

    public BookListAdapter(Home mActivity, Context context, int resource, List<BookDTO> bookDTOs, boolean management) {
        super(context, resource, bookDTOs);
        this.management = management;
        this.mContext = context;
        this.originalData = bookDTOs;
        this.filteredData = bookDTOs;
    }

    static class ViewHolder {
        TextView elementName;
        TextView accountName;
        TextView dateCreate;
        TextView dateStart;
        TextView dateEnd;
        TextView state;
        View colorTransparency;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public BookDTO getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listitem_book, null);

            holder = new ViewHolder();
            holder.dateCreate = (TextView) v.findViewById(R.id.dateCreate);
            holder.elementName = (TextView) v.findViewById(R.id.elementName);
            holder.accountName = (TextView) v.findViewById(R.id.accountName);
            holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
            holder.dateEnd = (TextView) v.findViewById(R.id.dateEnd);
            holder.state = (TextView) v.findViewById(R.id.state);
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

        BookDTO bookDTO = getItem(position);

        if (bookDTO != null) {
            holder.elementName.setText(String.valueOf(bookDTO.getElement().getName()));
            holder.accountName.setText(String.valueOf(bookDTO.getAccount().getFullName()));
            holder.dateCreate.setText(DateUtils.formatDate(bookDTO.getDateCreate(), DateUtils.FORMAT_DISPLAY));
            holder.dateStart.setText(DateUtils.formatDate(bookDTO.getDateStart(), DateUtils.FORMAT_DISPLAY));
            holder.dateEnd.setText(DateUtils.formatDate(bookDTO.getDateEnd(), DateUtils.FORMAT_DISPLAY));
            holder.state.setText(FileUtils.getState(mContext, String.valueOf(bookDTO.getState())));
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
            final List<BookDTO> list = originalData;
            int count = list.size();
            final ArrayList<BookDTO> nList = new ArrayList<>(count);
            BookDTO filterableString;
            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getElement().getName().toLowerCase().contains(filterString) ||
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
            filteredData = (ArrayList<BookDTO>) results.values;
            notifyDataSetChanged();
        }
    }

}