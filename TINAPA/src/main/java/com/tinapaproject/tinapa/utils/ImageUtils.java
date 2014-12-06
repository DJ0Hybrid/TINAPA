package com.tinapaproject.tinapa.utils;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.tinapaproject.tinapa.R;

/**
 * Created by Ethan on 12/6/2014.
 */
public class ImageUtils {
    public static void loadImage(ImageView imageView, String imagePath, boolean altUseDefaultImage) {
        if (imagePath != null && !imagePath.isEmpty()) {
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.outHeight = imageView.getHeight();
            imageOptions.outWidth = imageView.getWidth();
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath, imageOptions));
        } else if (altUseDefaultImage) {
            imageView.setImageResource(R.drawable.default_image);
        }
    }
}
