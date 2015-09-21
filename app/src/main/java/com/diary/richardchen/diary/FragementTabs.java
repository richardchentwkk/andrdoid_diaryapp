package com.diary.richardchen.diary;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;

public class FragementTabs extends FragmentActivity {
    private TabHost mTabhost;
    private TabManager mTabManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
    }

    public void setView(){
        setContentView(R.layout.activity_main);
        mTabhost = (TabHost)findViewById(android.R.id.tabhost);
        mTabhost.setup();

        mTabManager = new TabManager(this, mTabhost, R.id.realtabcontent);
        mTabhost.setCurrentTab(0);
        /* Bugs here, Icon and Text can not shown, only one of them can shown.*/
        mTabManager.addTab(
                mTabhost.newTabSpec("Fragment1").setIndicator("",this.getResources().getDrawable(android.R.drawable.ic_menu_edit)),Fragment1.class, null);
        mTabManager.addTab(
                mTabhost.newTabSpec("Fragment2").setIndicator("",this.getResources().getDrawable(android.R.drawable.ic_menu_search)),Fragment2.class, null);
        mTabManager.addTab(
                mTabhost.newTabSpec("Fragment3").setIndicator("",this.getResources().getDrawable(android.R.drawable.ic_menu_my_calendar)),Fragment3.class, null);
        mTabManager.addTab(
                mTabhost.newTabSpec("Fragment4").setIndicator("",this.getResources().getDrawable(android.R.drawable.ic_menu_preferences)),Fragment4.class, null);

        /*設定Tab大小 */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm); //先取得螢幕解析度
        int screenWidth = dm.widthPixels;   //取得螢幕的寬

        TabWidget tabWidget = mTabhost.getTabWidget();   //取得tab的物件
        int count = tabWidget.getChildCount();   //取得tab的分頁有幾個
        if (count > 3) {
            for (int i = 0; i < count; i++) {
                tabWidget.getChildTabViewAt(i)
                        .setMinimumWidth((screenWidth)/4);//設定每一個分頁最小的寬度
            }
        }

    }
}




