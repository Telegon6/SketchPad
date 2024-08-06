package com.chessytrooper.sketchpad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class SketchPadView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = Color.BLACK;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize;
    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Paint> paints = new ArrayList<>();
    private ArrayList<Path> undonePaths = new ArrayList<>();
    private ArrayList<Paint> undonePaints = new ArrayList<>();

    public SketchPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing() {
        brushSize = 20;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.reset();
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                paths.add(drawPath);
                paints.add(new Paint(drawPaint));
                drawPath = new Path();
                undonePaths.clear();
                undonePaints.clear();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(int newColor) {
        paintColor = newColor;
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void clearCanvas() {
        paths.clear();
        paints.clear();
        undonePaths.clear();
        undonePaints.clear();
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    public boolean undo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            undonePaints.add(paints.remove(paints.size() - 1));
            invalidate();
            return true;
        }
        return false;
    }

//    public boolean redo() {
//        if (undonePaths.size() > 0) {
//            paths.add(undonePaths.remove(undonePaths.size() - 1));
//            paints.add(undonePaints.remove(undonePaints.size() - 1));
//            invalidate();
//            return true;
//        }
//        return false;
//    }

    public Bitmap getCanvasBitmap() {
        return canvasBitmap;
    }
}