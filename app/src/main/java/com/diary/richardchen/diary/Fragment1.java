package com.diary.richardchen.diary;

import android.app.ActionBar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by RichardChen on 2015/2/24.
 */
public class Fragment1 extends Fragment {
    private View v;
    ImageButton imgbutton;
    ImageButton deletebtn;
    DBManager databasemanager;
    Cursor mCursor;
    ListAdapter contentadapter;
    ListAdapter update_content_adapter;
    ListView readarticlelist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        databasemanager = new DBManager(getActivity().getApplicationContext(), null, null, 1);

        mCursor = databasemanager.select();
        contentadapter = new ListViewAdapter(getActivity().getApplicationContext(),R.layout.listviewadapter,mCursor,new String[] {}, new int[] {},0);
        readarticlelist = (ListView)getActivity().findViewById(R.id.listView);
        readarticlelist.setAdapter(contentadapter);
        imgbutton = (ImageButton)getActivity().findViewById(R.id.imagebtn1);
        deletebtn = (ImageButton)getActivity().findViewById(R.id.imagebtn_delete);
        //Button action to add new article
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AddNewArticle.class);
                startActivity(intent);
            }
        });
        //Button action to delete all article and update List view
        deletebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                databasemanager.remove_all_data();
                updatelistview();
            }
        });
        //Action for click items
        readarticlelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int getid = (int)update_content_adapter.getItemId(position);
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

    @Override
    public void onResume() {
        updatelistview();
        super.onResume();
    }

    @Override
    public void onStop() {
        //TO DO implement dispose object function to dispose Adapter
        super.onStop();
    }

    @Override
    public void onDestroy() {
        databasemanager.close();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        v = inflater.inflate(R.layout.addnewarticle, container, false);
        return v;
    }

    public void updatelistview(){
        mCursor = databasemanager.select();
        update_content_adapter = new ListViewAdapter(getActivity().getApplicationContext(),R.layout.listviewadapter,mCursor,new String[] {}, new int[] {},0);
        readarticlelist = (ListView)getActivity().findViewById(R.id.listView);
        readarticlelist.setAdapter(update_content_adapter);
    }
}
