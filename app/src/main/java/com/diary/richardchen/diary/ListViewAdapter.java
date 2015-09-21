package com.diary.richardchen.diary;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by RichardChen on 2015/3/10.
 */
public class ListViewAdapter extends SimpleCursorAdapter implements Filterable {
    //private DataFilter filter;
    //Search column name define
    public static final String TABLE_NAME = "diary";
    public static final String CREATE_ID = "_id";
    public static final String CREATE_TIME = "datetime";
    public static final String CREATE_CONTENT = "content";

    public ListViewAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
    /*
    @Override
    public Filter getFilter() {
        if (filter==null){
            filter = new DataFilter();
        }
        return filter;
    }*/

    @Override
    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {

        super.setFilterQueryProvider(filterQueryProvider);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView icon          = (ImageView)view.findViewById(R.id.listimageView);
        TextView date_text      = (TextView)view.findViewById(R.id.date_text);
        TextView content_text   = (TextView)view.findViewById(R.id.content_text);
        //Show default launch Image temp.
        icon.setImageResource(R.drawable.ic_launcher);
        date_text.setText(cursor.getString(cursor.getColumnIndexOrThrow(CREATE_TIME)));
        content_text.setText(cursor.getString(cursor.getColumnIndexOrThrow(CREATE_CONTENT)));
        super.bindView(view, context, cursor);
    }

    /*private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d("Richard filter test","performFiltering");
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }*/
}
