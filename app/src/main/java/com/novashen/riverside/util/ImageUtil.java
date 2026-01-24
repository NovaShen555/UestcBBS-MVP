package com.novashen.riverside.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.novashen.riverside.R;


import java.io.File;
import java.util.List;

import cc.shinichi.library.ImagePreview;

//import cc.shinichi.library.ImagePreview;

public class ImageUtil {

    public static Bitmap getBitmapFromDisk(String path) {
        Bitmap bitmap = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void showImages(Context context, List<String> urls, int selected) {
//        List<MediaEntity> entities = new ArrayList<>();
//        for (int i = 0; i < urls.size(); i ++) {
//            MediaEntity entity = new MediaEntity();
//            entity.setNet(true);
//            entity.setUri(Uri.parse(urls.get(i)));
//            entities.add(entity);
//        }
//        ImageViewer.Companion.getINSTANCE().with(context)
//                .setEnterIndex(selected)
//                .setMediaEntity(entities)
//                .show();
        ImagePreview
                .getInstance()
                .setContext(context)
                .setIndex(selected)
                .setImageList(urls)
                .setShowDownButton(true)
                .setDownIconResId(R.drawable.ic_save)
                .setEnableDragClose(true)
                .setEnableUpDragClose(true)
                .start();
    }

    public static int[] getImagePx(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path);
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;

        int w = opts.outWidth;
        int h = opts.outHeight;

        return new int[]{w, h};
    }
}
