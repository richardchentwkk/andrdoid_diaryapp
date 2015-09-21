package com.diary.richardchen.diary;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by RichardChen on 2015/3/10.
 */
public class DataRestore {
    /**
     * Beginning for restore picture and article function
     *          API_name:Purpose
     *          getDataColumn:get actual file path in system
     *          restoretextandphoto: to restore photo and picture back to edittext
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection,selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static void restoretextandphoto(EditText editText, Context parentcontext, String restoredata){
        //Log.d("Richard", editText.getText().toString());
        //***********Restore Function finished for Photo part**********
        // 2015/03/05 Thur:Finished Photo restore
        // 2015/03/06 Fri:Finished Picture restore form album
        //String restoredata = editText.getText().toString();
        SpannableString ss = new SpannableString(restoredata);
        //Temporary Parser for restore photo and picture, can not fit all situation.
        Pattern photopattern = Pattern.compile("/storage/emulated/0/[\\w]{2,}/+[\\d]{14}.(jpg)");
        Pattern albumpattern = Pattern.compile("/storage/emulated/0/[\\w]{2,}/[\\w]{2,}/[\\w]{2,}.(jpg)");
        Pattern emotionpattern = Pattern.compile("R.[0-9]{2,}.R");
        Matcher match_photo = photopattern.matcher(restoredata);
        Matcher match_album = albumpattern.matcher(restoredata);
        Matcher match_emotion = emotionpattern.matcher(restoredata);
        PictureResize resize_return = new PictureResize();
        //Match Photo and restore
        while(match_photo.find()){
            Log.d("Richard",match_photo.group());
            Bitmap bm = BitmapFactory.decodeFile(match_photo.group());
            Bitmap rbm = resize_return.resize_picture (bm, 340, 340);
            ImageSpan span = new ImageSpan(parentcontext,rbm);
            ss.setSpan(span, match_photo.start(), match_photo.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //Match Picture from Sample and restore
        while(match_album.find()){
            Log.d("Richard",match_album.group());
            Bitmap albumbm = BitmapFactory.decodeFile(match_album.group());
            Bitmap albumrbm = resize_return.resize_picture (albumbm, 340, 340);
            ImageSpan span = new ImageSpan(parentcontext,albumrbm);
            ss.setSpan(span, match_album.start(), match_album.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        while(match_emotion.find()){
            Log.d("Richard",match_emotion.group());
            int id = 0;
            Pattern praser = Pattern.compile("[0-9]{2,}");
            Matcher match_num = praser.matcher(match_emotion.group());
            if(match_num.find()) {
                Log.d("Richard",match_num.group());
                id = Integer.parseInt(match_num.group());
            }
            Drawable d = parentcontext.getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, match_emotion.start(), match_emotion.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        editText.setText(ss);
    }

    public static void restoredatatotextview(TextView editText, Context parentcontext, String restoredata){
        //Log.d("Richard", editText.getText().toString());
        //***********Restore Function finished for Photo part**********
        // 2015/03/05 Thur:Finished Photo restore
        // 2015/03/06 Fri:Finished Picture restore form album

        SpannableString ss = new SpannableString(restoredata);
        //Temporary Parser for restore photo and picture, can not fit all situation.
        Pattern photopattern = Pattern.compile("/storage/emulated/0/[\\w]{2,}/+[\\d]{14}.(jpg)");
        Pattern albumpattern = Pattern.compile("/storage/emulated/0/[\\w]{2,}/[\\w]{2,}/[\\w]{2,}.(jpg)");
        Pattern emotionpattern = Pattern.compile("R.[0-9]{2,}.R");
        Matcher match_photo = photopattern.matcher(restoredata);
        Matcher match_album = albumpattern.matcher(restoredata);
        Matcher match_emotion = emotionpattern.matcher(restoredata);
        PictureResize resize_return = new PictureResize();
        //Match Photo and restore
        while(match_photo.find()){
            Log.d("Richard",match_photo.group());
            Bitmap bm = BitmapFactory.decodeFile(match_photo.group());
            Bitmap rbm = resize_return.resize_picture (bm, 340, 340);
            ImageSpan span = new ImageSpan(parentcontext,rbm);
            ss.setSpan(span, match_photo.start(), match_photo.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        //Match Picture from Sample and restore
        while(match_album.find()){
            Log.d("Richard",match_album.group());
            Bitmap albumbm = BitmapFactory.decodeFile(match_album.group());
            Bitmap albumrbm = resize_return.resize_picture (albumbm, 340, 340);
            ImageSpan span = new ImageSpan(parentcontext,albumrbm);
            ss.setSpan(span, match_album.start(), match_album.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        while(match_emotion.find()){
            Log.d("Richard",match_emotion.group());
            int id = 0;
            Pattern praser = Pattern.compile("[0-9]{2,}");
            Matcher match_num = praser.matcher(match_emotion.group());
            if(match_num.find()) {
                Log.d("Richard",match_num.group());
                id = Integer.parseInt(match_num.group());
            }
            Drawable d = parentcontext.getResources().getDrawable(id);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, match_emotion.start(), match_emotion.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        editText.setText(ss);
    }
}
