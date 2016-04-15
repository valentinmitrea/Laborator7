package ro.pub.cs.systems.eim.lab07.ocwcoursedisplayer.general;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.apache.http.HttpEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utilities {

    public static BufferedReader getReader(HttpEntity httpEntity) throws IOException {
        return new BufferedReader(new InputStreamReader(httpEntity.getContent()));
    }

    public static Bitmap makeTransparent(Bitmap bitmap) {
        int width =  bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bitmapCopy = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] pixels = new int[bitmapCopy.getHeight() * bitmapCopy.getWidth()];
        bitmap.getPixels(pixels,
                0,
                bitmapCopy.getWidth(),
                0,
                0,
                bitmapCopy.getWidth(),
                bitmapCopy.getHeight());
        bitmapCopy.setPixels(pixels, 0, width, 0, 0, width, height);

        for(int k = 0; k < bitmapCopy.getHeight() * bitmapCopy.getWidth(); k++) {
            if (pixels[k] == -1) {
                pixels[k] = Color.alpha(Color.TRANSPARENT);
            }
        }

        bitmapCopy.setPixels(pixels, 0, bitmapCopy.getWidth(), 0, 0, bitmapCopy.getWidth(), bitmapCopy.getHeight());
        return bitmapCopy;
    }

}
