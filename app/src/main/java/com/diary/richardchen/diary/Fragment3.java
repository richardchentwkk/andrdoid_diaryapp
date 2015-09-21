package com.diary.richardchen.diary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;

/**
 * Created by RichardChen on 2015/2/24.
 */
public class Fragment3 extends Fragment {
    ListView mListview;
    CalendarView mCalendarView;
    ListViewAdapter mListViewAdapter;
    Cursor mCursor = null;
    DBManager mDatebasemanager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calander, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initView();
        mDatebasemanager = DBManager.getDbManager(getActivity().getApplicationContext());

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String searchdate = year+"/"+"0"+(month+1)+"/"+dayOfMonth;
                mCursor = mDatebasemanager.FilterByDate(searchdate);
                mListViewAdapter =  new ListViewAdapter(getActivity().getApplicationContext(),R.layout.listviewadapter,mCursor,new String[] {}, new int[] {}, 0);
                mListview.setAdapter(mListViewAdapter);
                Log.d("Richard Date is ",searchdate);
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int getid = (int)mListViewAdapter.getItemId(position);
                String returncontent    = mDatebasemanager.getContentbyID(getid);
                String returndate       = mDatebasemanager.returndatebyID(getid);

                Intent read_intent = new Intent();
                read_intent.setClass(getActivity().getApplicationContext(),ReadContent.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",getid);
                bundle.putString("date",returndate);
                bundle.putString("content",returncontent);
                read_intent.putExtras(bundle);
                startActivity(read_intent);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    void initView(){
        mListview = (ListView)getActivity().findViewById(R.id.calendarlistview);
        mCalendarView = (CalendarView)getActivity().findViewById(R.id.calendarView);
    }

}
