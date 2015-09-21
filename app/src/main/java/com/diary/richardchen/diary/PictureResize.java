package com.diary.richardchen.diary;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by RichardChen on 2015/3/3.
 */
public class PictureResize {
    public Bitmap resize_picture(Bitmap src, int result_height, int result_width ){
        int oldwidth = src.getWidth();
        int oldheight = src.getHeight();
        float scalewidth = result_width/(float)oldwidth;
        float scaleheigh = result_height/(float)oldheight;
        Matrix matrix = new Matrix();
        matrix.postScale(scalewidth, scaleheigh);
        Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, oldwidth,oldheight, matrix, true);
        return resizedBitmap;
    }
}
