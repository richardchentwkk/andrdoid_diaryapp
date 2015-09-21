package com.diary.richardchen.diary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Time;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RichardChen on 2015/2/26.
 */

public class AddNewArticle extends Activity {
    //Constructor
    Button button_back ;
    TextView mon, days;
    EditText editText;
    String savedata;
    private Uri imageUri; //圖片路徑
    private String filename; //圖片名稱

    public static final int CHOOSE_PICTURE_FROM_ALABM = 100;
    public static final int TAKE_PHOTO                = 101;
    public static final int CROP_PHOTO                = 102;

    protected static final int MENU_CHOOSE_FROM_ALABM = Menu.FIRST;
    protected static final int MENU_TAKE_A_PHOTO = Menu.FIRST +1;
    protected static final int MENU_INSERT_EMOTION = Menu.FIRST +2;
    DBManager addarticleDBmanager;
    DataRestore restoredata = new DataRestore();

    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            int id = Integer.parseInt(source);
            Drawable d = getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newarticle);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis());
        final String create_time = format.format(date);
        //Time initialize
        Time t = new Time();
        t.setToNow();
        mon=(TextView)findViewById(R.id.mon);
        days=(TextView)findViewById(R.id.monday);
        mon.setText(Integer.toString(t.month+1));
        days.setText(Integer.toString(t.monthDay));
        button_back = (Button)findViewById(R.id.button1);
        editText = (EditText)findViewById(R.id.editText);

        addarticleDBmanager = new DBManager(this, null, null,1);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savedata = editText.getText().toString();
                restoredata.restoretextandphoto(editText,AddNewArticle.this, savedata);

                Log.d("Richard", "insert data is"+savedata);
                //When no data change do not insert data to database
                if (!savedata.isEmpty()) {
                    addarticleDBmanager.insert(create_time, savedata);
                    Log.d("Richard", "insert data!"+create_time+savedata);
                }
                //Finished Activity
                finish();
            }
        });
        registerForContextMenu(findViewById(R.id.editText));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CHOOSE_PICTURE_FROM_ALABM:
                if(resultCode==RESULT_OK){
                    InputStream imageStream = null;
                    Bitmap yourSeletedImage = null;
                    Uri selectedImage = data.getData();
                    String actualpath = restoredata.getDataColumn(this, selectedImage, null,null);
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        yourSeletedImage = BitmapFactory.decodeStream(imageStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Log.d("Richard selet image is ", selectedImage.getPath()+"Path is"+actualpath);
                    //Insert image using SpanImage
                    PictureResize resize = new PictureResize();
                    Bitmap resizedbitmap = resize.resize_picture(yourSeletedImage,340,340);
                    ImageSpan imageSpan =new ImageSpan(this,resizedbitmap,ImageSpan.ALIGN_BOTTOM);
                    SpannableString spannableString = new SpannableString(actualpath);
                    int length = spannableString.length();

                    spannableString.setSpan(imageSpan, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editText.append(spannableString);
                }
               break;
            case TAKE_PHOTO:
                Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("scale", true);
                //设置宽高比例
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                //设置裁剪图片宽高
                intent.putExtra("outputX", 340);
                intent.putExtra("outputY", 340);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri);
                this.sendBroadcast(intentBc);
                startActivityForResult(intent, CROP_PHOTO); //设置裁剪参数显示图片至ImageView
                break;
            case CROP_PHOTO:
                try {
                    //图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //Using ASUS default Crop function failed, mark temporary
                    //PictureResize resize = new PictureResize();
                    //Bitmap resizedbitmap = resize.resize_picture(bitmap,220,200);
                    ImageSpan imageSpan =new ImageSpan(this,bitmap,ImageSpan.ALIGN_BOTTOM);
                    SpannableString spannableString = new SpannableString(imageUri.getPath());
                    spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    editText.append(spannableString);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

    @Override
     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("插入圖片");
        menu.add(0, MENU_CHOOSE_FROM_ALABM, 0, "從相簿中選取");
        menu.add(0,MENU_TAKE_A_PHOTO ,0,"拍照");
        menu.add(0,MENU_INSERT_EMOTION,0,"插入表情");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_CHOOSE_FROM_ALABM:
                Intent photopickerIntent = new Intent(Intent.ACTION_PICK);
                photopickerIntent.setType("image/*");
                startActivityForResult(photopickerIntent, CHOOSE_PICTURE_FROM_ALABM);
                break;
            case MENU_TAKE_A_PHOTO:
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date(System.currentTimeMillis());
                filename = format.format(date);
                //儲存至DCIM文件夹
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
                File outputImage = new File(path,filename+".jpg");
                try {
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Log.d("Richard", imageUri.getPath());
                Intent takephotoIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                takephotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
                startActivityForResult(takephotoIntent,TAKE_PHOTO);
                break;
            case MENU_INSERT_EMOTION:
                //Show emotion layout firstly, after choose and insert pic remove emotion layout
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View emotionlayout = layoutInflater.inflate(R.layout.emotionlayout,null);
                final RelativeLayout mainlayout = (RelativeLayout)findViewById(R.id.newarticle);
                mainlayout.addView(emotionlayout);
                //Emotion pic initial
                GridView gridview = (GridView) findViewById(R.id.emotionsview);
                final ImageAdapter imageadp = new ImageAdapter(this);
                gridview.setAdapter(imageadp);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String resourcedrawable = String.valueOf(imageadp.getRsourceDrawableint(position));
                        String addtagdone = "R."+resourcedrawable+".R";
                        SpannableString spanString = new SpannableString(addtagdone);
                        Log.d("Richard emotion picture is",addtagdone);
                        Drawable d = getResources().getDrawable(imageadp.getRsourceDrawableint(position));
                        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        editText.append(spanString);
                        mainlayout.removeView(emotionlayout);
                    }
                });

                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        addarticleDBmanager.close();
        super.onDestroy();
    }


}
