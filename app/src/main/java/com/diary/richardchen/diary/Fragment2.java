package com.diary.richardchen.diary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.lang.reflect.Constructor;

/**
 * Created by RichardChen on 2015/2/24.
 */
public class Fragment2 extends Fragment {

    //Constructor
    ListView listView;
    SearchView searchView;
    EditText seachedittext;
    ImageView searchimg;
    DBManager databasemanager;
    Cursor mCursor;
    ListViewAdapter contentadapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment1_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        init_view();
        databasemanager = new DBManager(getActivity().getApplicationContext(), null, null, 0);

        mCursor = databasemanager.select();
        searchimg.setImageResource(android.R.drawable.ic_menu_search);

        listView.setTextFilterEnabled(true);
        contentadapter =  new ListViewAdapter(getActivity().getApplicationContext(),R.layout.listviewadapter,mCursor,new String[] {}, new int[] {}, 0);
        listView.setAdapter(contentadapter);
        /*  Search method implement with setFilterQueryProvider and
        *   addTextChangedListener
        */
        contentadapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Log.d("Richard runQuery is ",constraint.toString());
                Cursor c = databasemanager.filter_query(constraint);
                return c;
            }
        });
        seachedittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Richard debug", s.toString());
                contentadapter.getFilter().filter(s.toString());
                contentadapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int getid = (int)contentadapter.getItemId(position);
                String returncontent    = databasemanager.getContentbyID(getid);
                String returndate       = databasemanager.returndatebyID(getid);

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


    public void init_view(){
        listView = (ListView)getActivity().findViewById(R.id.listView3);
        seachedittext = (EditText)getActivity().findViewById(R.id.seachedittext);
        searchimg = (ImageView)getActivity().findViewById(R.id.seachimage);
    }

}
