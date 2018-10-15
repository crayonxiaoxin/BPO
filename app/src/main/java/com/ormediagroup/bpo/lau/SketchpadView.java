package com.ormediagroup.bpo.lau;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Lau on 2018/10/11.
 */

public class SketchpadView extends View {

    private int screenWidth = 0;
    private int screenHeight = 0;
    private float preX;
    private float preY;
    private Path path;
    public Paint paint = null;
    Bitmap cacheBitmap = null;
    Canvas cacheCanvas = null;
    private String setting_filename = "";
    private Bitmap.CompressFormat setting_filetype = Bitmap.CompressFormat.PNG;
    private int setting_filequality = 100;

    public SketchpadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setDrawingCacheEnabled(true);
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
//        cacheBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888); // 豎屏
        cacheBitmap = Bitmap.createBitmap(screenHeight, screenWidth, Bitmap.Config.ARGB_8888); // 橫屏
        cacheCanvas = new Canvas();
        cacheCanvas.drawColor(Color.TRANSPARENT);
        cacheCanvas.setBitmap(cacheBitmap);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);

    }

    public void setCanvasColor(int color) {
        cacheCanvas.drawColor(color);
    }

    public void setPenColor(int color) {
        paint.setColor(color);
    }

    public void setPenSize(int width) {
        paint.setStrokeWidth(width);
    }

    public void setSaveFileName(String filename) {
        setting_filename = filename;
    }

    public void setSaveFileType(Bitmap.CompressFormat format) {
        setting_filetype = format;
    }

    public void setSaveFileQuality(int quality) {
        setting_filequality = quality;
    }

    public Bitmap cropBitmap(int x, int y, int width, int height) {
        return Bitmap.createBitmap(getBitmap(), x, y, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(cacheBitmap, 0, 0, paint);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - preX);
                float dy = Math.abs(y - preY);
                if (dx >= 5 || dy >= 5) {
                    path.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                    preX = x;
                    preY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                cacheCanvas.drawPath(path, paint);
                path.reset();
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 清空画布
     */
    public void clear() {
        if (cacheCanvas != null) {
            path.reset();
            cacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }
    }

    /**
     * 获取bitmap
     *
     * @return
     */
    public Bitmap getBitmap() {
        Bitmap bm = getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(bm);
        destroyDrawingCache();
        return bitmap;
    }

    /**
     * 保存bitmap为对应格式图片
     *
     * @return
     */
    public String save(Bitmap bitmap) {
        if (bitmap != null) {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (dir != null) {
                String fileName = setting_filename != "" ? setting_filename : "signature_" + System.currentTimeMillis() + ".png";
                File file = new File(dir, fileName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(setting_filetype, setting_filequality, fos);
                    fos.flush();
                    return file.getAbsolutePath();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 提醒系统扫描文件
     *
     * @param context
     * @param filePath
     */
    public void scanFile(Context context, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(scanIntent);
    }

    /**
     * 将bitmap编码成base64字符串
     *
     * @param bitmap
     * @return
     */
    public String getEncodeImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.i("ORM", "getEncodeImage: null");
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(setting_filetype, setting_filequality, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

}
