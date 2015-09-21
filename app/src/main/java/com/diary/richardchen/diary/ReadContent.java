package com.diary.richardchen.diary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RichardChen on 2015/3/12.
 */
public class ReadContent extends Activity {
    DBManager databasemanager;
    String[] date ;
    int mid = 0;
    final DataRestore restoredata = new DataRestore();
    EditText modifytext;
    private Uri imageUri; //圖片路徑
    private String filename; //圖片名稱
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.readandmodifyarticle);
        final TextView mon = (TextView)findViewById(R.id.newmon);
        final TextView day = (TextView)findViewById(R.id.newmonday);
        TextView readedittext = (TextView)findViewById(R.id.ShowHistoryText);
        Button back = (Button)findViewById(R.id.back);
        Button modify = (Button)findViewById(R.id.modifyarticle);
        Button delete = (Button)findViewById(R.id.delete);
        //UI Object Mapping
        databasemanager = new DBManager(this,null,null,1);
        // Receive data
        Bundle recieve_bundle = getIntent().getExtras();
        mid = recieve_bundle.getInt("id");
        String alldate = recieve_bundle.getString("date");
        content = recieve_bundle.getString("content");
        Log.d("Richard receive", "id is "+mid+alldate+content);
        // Start to restore data
        date = alldate.split("/");
        mon.setText(date[1]);
        day.setText(date[2]);
        restoredata.restoredatatotextview(readedittext,getApplicationContext(),content);

        //Button Actions
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databasemanager.delete(mid);
                finish();
            }
        });
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add View for update content
                LayoutInflater layoutInflater = LayoutInflater.from(ReadContent.this);
                final View modifylayout = layoutInflater.inflate(R.layout.newarticle,null);
                final RelativeLayout mainlayout = (RelativeLayout)findViewById(R.id.readarticle);
                mainlayout.addView(modifylayout);
                TextView oldmon = (TextView)findViewById(R.id.mon);
                TextView oldday = (TextView)findViewById(R.id.monday);

                modifytext = (EditText)findViewById(R.id.editText);
                Button saveandback = (Button)findViewById(R.id.button1);
                oldmon.setText(date[1]);
                oldday.setText(date[2]);
                DataRestore.restoretextandphoto(modifytext,getApplicationContext(),content);
                registerForContextMenu(findViewById(R.id.editText));

                //When Click back button, reset data and remove layout Edit Text
                saveandback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String modifiedtext = modifytext.getText().toString();
                        content=modifiedtext;
                        update_content_and_date(modifiedtext);
                        mainlayout.removeView(modifylayout);
                    }
                });
            }
        });
        super.onCreate(savedInstanceState);
    }
    public void update_content_and_date(String modifiedtext){

        TextView readedittext = (TextView)findViewById(R.id.ShowHistoryText);
        databasemanager.update(mid,modifiedtext);
        String new_content = databasemanager.getContentbyID(mid);
        restoredata.restoredatatotextview(readedittext,getApplicationContext(),new_content);
    }

    //++Modify EditText function Start here.

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("插入圖片");
        menu.add(0, AddNewArticle.MENU_CHOOSE_FROM_ALABM, 0, "從相簿中選取");
        menu.add(0, AddNewArticle.TAKE_PHOTO ,0,"拍照");
        menu.add(0,AddNewArticle.MENU_INSERT_EMOTION,0,"插入表情");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case AddNewArticle.MENU_CHOOSE_FROM_ALABM:
                Intent photopickerIntent = new Intent(Intent.ACTION_PICK);
                photopickerIntent.setType("image/*");
                startActivityForResult(photopickerIntent, AddNewArticle.CHOOSE_PICTURE_FROM_ALABM);
                break;
            case AddNewArticle.MENU_TAKE_A_PHOTO:
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
                startActivityForResult(takephotoIntent,AddNewArticle.TAKE_PHOTO);
                break;
            case AddNewArticle.MENU_INSERT_EMOTION:
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
                        modifytext.append(spanString);
                        mainlayout.removeView(emotionlayout);
                    }
                });

                break;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case AddNewArticle.CHOOSE_PICTURE_FROM_ALABM:
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
                    modifytext.append(spannableString);
                }
                break;
            case AddNewArticle.TAKE_PHOTO:
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
                startActivityForResult(intent, AddNewArticle.CROP_PHOTO); //设置裁剪参数显示图片至ImageView
                break;
            case AddNewArticle.CROP_PHOTO:
                try {
                    //图片解析成Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //Using ASUS default Crop function failed, mark temporary
                    //PictureResize resize = new PictureResize();
                    //Bitmap resizedbitmap = resize.resize_picture(bitmap,220,200);
                    ImageSpan imageSpan =new ImageSpan(this,bitmap,ImageSpan.ALIGN_BOTTOM);
                    SpannableString spannableString = new SpannableString(imageUri.getPath());
                    spannableString.setSpan(imageSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    modifytext.append(spannableString);
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }
}
