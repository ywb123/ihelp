package com.image.setcorner;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageSetCorner {
	 /**
     * ��ͼƬ��ȡΪԲ��ͼƬ
     * @param bitmap ԭͼƬ
     * @param ratio ��ȡ�����������8����Բ�ǰ뾶�ǿ�ߵ�1/8�������2������Բ��ͼƬ
     * @return Բ�Ǿ���ͼƬ
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {
            
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                            bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, bitmap.getWidth()/ratio, 
                            bitmap.getHeight()/ratio, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
    }

}
