package com.example.hkharton.testcanvas.view;

import android.app.AlertDialog;
import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//import android.graphics.Rect;
//import android.util.AttributeSet;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import com.example.hkharton.testcanvas.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
//import java.util.Random;

//import android.graphics.drawable.ShapeDrawable;
//import android.graphics.drawable.shapes.RectShape;

public class DrawTest extends View {
    // Main bitmap for background
    // private Bitmap mBitmap = null;
    // private Rect mMeasuredRect;

    // Stores data about the rectangles
    private static class RectangleArea {
        int topCoord;
        int bottomCoord;
        int leftCoord;
        int rightCoord;
        int rectangleSize;
        Paint rectanglePaint;

        RectangleArea(int topCoord, int bottomCoord, int leftCoord, int rightCoord, Paint rectanglePaint, int rectangleSize){
            this.topCoord = topCoord;
            this.bottomCoord = bottomCoord;
            this.leftCoord = leftCoord;
            this.rightCoord = rightCoord;
            this.rectanglePaint = rectanglePaint;
            this.rectangleSize = rectangleSize;
        }

        @Override
        public String toString() {
            return "Rectangle[" + topCoord + ", " + bottomCoord + ", " + leftCoord + ", " + rightCoord + "]";
        }
    }

    // Paint to draw rectangles
    // private Paint mRectanglePaint;

    private static final String TAG = "DrawTest";

    // Limit of Rectangles
    private static final int RECTANGLES_LIMIT = 100;

    // Rectangle size
    private static final int DEFAULT_RECTANGLES_SIZE = 150;
    private static final int MIN_RECTANGLE_SIZE = 75;
    private static final int MAX_RECTANGLE_SIZE = 300;

    // Default Rectangle Coordinate
    private static final int DEFAULT_Y = DEFAULT_RECTANGLES_SIZE + 100;
    private static final int DEFAULT_X = DEFAULT_RECTANGLES_SIZE + 100;

    // Keep track of all rectangles information
    private ArrayList<RectangleArea> mRectangles = new ArrayList<RectangleArea>(RECTANGLES_LIMIT);
    private SparseArray<RectangleArea> mRectanglePointer = new SparseArray<RectangleArea>(RECTANGLES_LIMIT);

    // gesture detector
    GestureDetector gestureDetector;
    ScaleGestureDetector scaleDetector;
    private boolean handlerControl = false;
    private static final int VELOCITY_THRESHOLD = 5000;

    private int screenWidth = 0;
    private int screenHeight = 0;

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public DrawTest(final Context ct) {
        super(ct);

        init(ct);
    }

    /*
    public DrawTest(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);

        init(ct);
    }

    public DrawTest(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);

        init(ct);
    }
    */

    /**
     * Initialization Method
     *
     * @param ct
     */
    private void init(final Context ct) {
        // Generate bitmap used for background
        // mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.ic_launcher);

        // initialize paint for the rectangle
        //mRectanglePaint = new Paint();
        //mRectanglePaint.setColor(Color.BLUE);
        //mRectanglePaint.setStrokeWidth(40);
        //mRectanglePaint.setStyle(Paint.Style.FILL);

        // Decode the pattern
        Bitmap patternBMP = BitmapFactory.decodeResource(ct.getResources(), R.drawable.ic_launcher);

        // Create the shader
        BitmapShader patternBMPshader = new BitmapShader(patternBMP, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ct.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels - 110;

        // initialized scale gesture detector
        scaleDetector = new ScaleGestureDetector(ct, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector detector) {
                Log.e("", "Scale detected");

                int xTouch = (int) detector.getFocusX();
                int yTouch = (int) detector.getFocusY();

                // check if we've touched inside some rectangle
                RectangleArea touchedRectangle = obtainTouchedRectangle(xTouch, yTouch);

                float scale = 1f;
                scale *= detector.getScaleFactor();
                scale = Math.max(0.1f, Math.min(scale, 5.0f));

                if(touchedRectangle != null) {
                    int reducedRectangleSize = (int) (touchedRectangle.rectangleSize * scale);
                    Log.e("TEST", "SIZE: " + reducedRectangleSize);

                    if (reducedRectangleSize > MIN_RECTANGLE_SIZE && reducedRectangleSize < MAX_RECTANGLE_SIZE) {
                        touchedRectangle.rectangleSize = reducedRectangleSize;

                        int xCenter = (touchedRectangle.leftCoord + touchedRectangle.rightCoord) / 2;
                        int yCenter = (touchedRectangle.topCoord + touchedRectangle.bottomCoord) / 2;

                        touchedRectangle.topCoord = yCenter - touchedRectangle.rectangleSize;
                        touchedRectangle.bottomCoord = yCenter + touchedRectangle.rectangleSize;
                        touchedRectangle.leftCoord = xCenter - touchedRectangle.rectangleSize;
                        touchedRectangle.rightCoord = xCenter + touchedRectangle.rectangleSize;
                    }

                    invalidate();
                    handlerControl = true;
                }

                return true;
            }
        });

        // initialized gesture detector
        gestureDetector = new GestureDetector(ct, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                Log.e("", "Longpress detected");

                int xTouch = (int) event.getX(0);
                int yTouch = (int) event.getY(0);

                // check if we've touched inside some rectangle
                RectangleArea touchedRectangle = obtainTouchedRectangle(xTouch, yTouch);

                if(touchedRectangle != null){
                    mRectangles.remove(touchedRectangle);
                    //handlerControl = true;
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.e("", "Fling detected; vx=" + velocityX + "; vy:" + velocityY);

                if((Math.abs(velocityX) > VELOCITY_THRESHOLD) || (Math.abs(velocityY) > VELOCITY_THRESHOLD)) {
                    int xTouch = (int) e2.getX(0);
                    int yTouch = (int) e2.getY(0);

                    // check if we've touched inside some rectangle
                    RectangleArea touchedRectangle = obtainTouchedRectangle(xTouch, yTouch);

                    if (touchedRectangle != null) {
                        mRectangles.remove(touchedRectangle);
                        //handlerControl = true;
                        return true;
                    }
                }

                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent event){
                Log.e("TEST", "Double Tap Detected");

                int xTouch = (int) event.getX(0);
                int yTouch = (int) event.getY(0);

                // check if we've touched inside some rectangle
                RectangleArea touchedRectangle = obtainTouchedRectangle(xTouch, yTouch);

                if(touchedRectangle != null){
                    int arraySize = mRectangles.size();
                    for(int walk = 0; walk < arraySize; walk++){
                        int newPosition = walk + 1;

                        if(mRectangles.get(walk).equals(touchedRectangle) && newPosition < arraySize){
                            mRectangles.remove(touchedRectangle);
                            mRectangles.add(newPosition, touchedRectangle);
                            //handlerControl = true;
                            return true;
                        }
                    }
                }

                return false;
            }
        });

        // Set the color and shader
        //mRectanglePaint.setColor(0xFFFFFFFF);
        //mRectanglePaint.setShader(patternBMPshader);

        /*
        mCirclePaint = new Paint();

        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStrokeWidth(40);
        mCirclePaint.setStyle(Paint.Style.FILL);
        */
    }

    @Override
    public void onDraw(final Canvas canvas) {
        // background bitmap to cover all area
        // canv.drawBitmap(mBitmap, null, mMeasuredRect, null);

        // Draw all the rectangles
        for (RectangleArea rectangle : mRectangles) {
            canvas.drawRect(rectangle.leftCoord, rectangle.topCoord, rectangle.rightCoord, rectangle.bottomCoord, rectangle.rectanglePaint);

            //Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
            //drawable.setBounds(rectangle);
            //drawable.draw(canvas);
        }

        /*
        for (CircleArea circle : mCircles) {
            canv.drawCircle(circle.centerX, circle.centerY, circle.radius, mCirclePaint);
        }
        */
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;
        handlerControl = false;

        RectangleArea touchedRectangle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // detecting special touch event
        gestureDetector.onTouchEvent(event);

        //if(handlerControl != true) {
            scaleDetector.onTouchEvent(event);
        //}

        // get touch event coordinates and make transparent rectangle from it
        if(handlerControl != true) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.w(TAG, "Down");

                    // it's the first pointer, so clear all existing pointers data
                    clearRectanglePointer();

                    // get the touched x & y coordinate
                    xTouch = (int) event.getX(0);
                    yTouch = (int) event.getY(0);

                    // check if we've touched inside some rectangle
                    touchedRectangle = obtainTouchedRectangleCenter(xTouch, yTouch);

                    // keep track of the rectangle pointer
                    mRectanglePointer.put(event.getPointerId(0), touchedRectangle);

                    invalidate();
                    handled = true;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.w(TAG, "Pointer down - Action Index: " + actionIndex);

                    // It secondary pointers, so obtain their ids and check circles
                    pointerId = event.getPointerId(actionIndex);

                    // get the touched x & y coordinate
                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    // check if we've touched inside some circle
                    touchedRectangle = obtainTouchedRectangleCenter(xTouch, yTouch);

                    // keep track of the rectangle pointer
                    mRectanglePointer.put(pointerId, touchedRectangle);

                    invalidate();
                    handled = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.w(TAG, "Move");

                    // get all pointers count
                    final int pointerCount = event.getPointerCount();

                    // iterate through all pointers
                    for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                        // Some pointer has moved, search it by pointer id
                        pointerId = event.getPointerId(actionIndex);

                        int historySize = event.getHistorySize();
                        int xHistory = -1;
                        int yHistory = -1;
                        if(historySize >= 1){
                            xHistory = (int) event.getHistoricalX(historySize - 1);
                            yHistory = (int) event.getHistoricalY(historySize - 1);
                        }

                        // get the touched x & y coordinate
                        xTouch = (int) event.getX(actionIndex);
                        yTouch = (int) event.getY(actionIndex);

                        // get the pointer to the touched rectangle by pointer id
                        touchedRectangle = mRectanglePointer.get(pointerId);

                        // move the rectangle to where the x & y input coordinate has been moved
                        if (touchedRectangle != null) {
                            // calculate coordinate movement
                            // int xMove = xTouch - xHistory;
                            // int yMove = yTouch - yHistory;

                            // int xCenter = (touchedRectangle.leftCoord + touchedRectangle.rightCoord) / 2;
                            // int yCenter = (touchedRectangle.topCoord + touchedRectangle.bottomCoord) / 2;

                            int updatedTopCoord = yTouch - touchedRectangle.rectangleSize;
                            int updatedBottomCoord = yTouch + touchedRectangle.rectangleSize;
                            int updatedLeftCoord = xTouch - touchedRectangle.rectangleSize;
                            int updatedRightCoord = xTouch + touchedRectangle.rectangleSize;

                            if(updatedTopCoord > 0 && updatedBottomCoord < screenHeight) {
                                touchedRectangle.topCoord = updatedTopCoord;
                                touchedRectangle.bottomCoord = updatedBottomCoord;

                                Log.e("TEST", "TOP: " + updatedTopCoord);
                                Log.e("TEST", "BOTTOM: " + updatedBottomCoord);
                            }

                            if(updatedLeftCoord > 0 && updatedRightCoord < screenWidth){
                                touchedRectangle.leftCoord = updatedLeftCoord;
                                touchedRectangle.rightCoord = updatedRightCoord;
                            }
                        }
                    }
                    invalidate();
                    handled = true;
                    break;
                case MotionEvent.ACTION_UP:
                    clearRectanglePointer();
                    invalidate();
                    handled = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    // not general pointer was up
                    pointerId = event.getPointerId(actionIndex);
                    mRectanglePointer.remove(pointerId);
                    invalidate();
                    handled = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    handled = true;
                    break;
                default:
                    // do nothing
                    break;
            }
        }

        /*
        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointer();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "Pointer down");
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                final int pointerCount = event.getPointerCount();

                Log.w(TAG, "Move");

                for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                    // Some pointer has moved, search it by pointer id
                    pointerId = event.getPointerId(actionIndex);

                    xTouch = (int) event.getX(actionIndex);
                    yTouch = (int) event.getY(actionIndex);

                    touchedCircle = mCirclePointer.get(pointerId);

                    if (null != touchedCircle) {
                        touchedCircle.centerX = xTouch;
                        touchedCircle.centerY = yTouch;
                    }
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
                clearCirclePointer();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // not general pointer was up
                pointerId = event.getPointerId(actionIndex);

                mCirclePointer.remove(pointerId);
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }
        */

        return super.onTouchEvent(event) || handled;
    }

    /**
     * Clears all RectangleArea - pointer id relations
     */
    private void clearRectanglePointer() {
        mRectanglePointer.clear();
        Log.w(TAG, "clearRectanglePointer");
    }

    public void addNewColorTile(Bitmap patternBitmap){
        if(mRectangles.size() < RECTANGLES_LIMIT) {
            // create the paint object
            Paint rectanglePaint = new Paint();

            // Create the shader
            BitmapShader patternBMPshader = new BitmapShader(patternBitmap, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);

            // Set the color and shader
            rectanglePaint.setColor(0xFFFFFFFF);
            rectanglePaint.setShader(patternBMPshader);

            // create a new rectangle object
            RectangleArea newRectangle = new RectangleArea(DEFAULT_Y - DEFAULT_RECTANGLES_SIZE, DEFAULT_Y + DEFAULT_RECTANGLES_SIZE, DEFAULT_X - DEFAULT_RECTANGLES_SIZE, DEFAULT_X + DEFAULT_RECTANGLES_SIZE, rectanglePaint, DEFAULT_RECTANGLES_SIZE);

            // add the rectangle to the rectangle hash set
            mRectangles.add(newRectangle);
            Log.w(TAG, "Added rectangle " + newRectangle);
        }else{
            Log.w(TAG, "Reach rectangle limit, cannot add new rectangles");
        }
    }

    /**
     * This method will check if any of the existing rectangle is touched.
     * If yes, it will return the touched RectangleArea object,
     * otherwise it will create a new RectangleArea object.
     * @param xTouch
     * @param yTouch
     * @return
     */
    private RectangleArea obtainTouchedRectangle(final int xTouch, final int yTouch) {
        // check if any existing rectangle is touched
        RectangleArea touchedRectangle = getTouchedRectangle(xTouch, yTouch, -1);
        return touchedRectangle;
    }

    private RectangleArea obtainTouchedRectangleCenter(final int xTouch, final int yTouch) {
        // check if any existing rectangle is touched
        RectangleArea touchedRectangle = getTouchedRectangle(xTouch, yTouch, 50);
        return touchedRectangle;
    }

    /**
     * This method will retrieve the process the x & y input coordinate, and
     * it will return the touched RectangleArea object if applicable.
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private RectangleArea getTouchedRectangle(final int xTouch, final int yTouch, final int maxCenterDistancePercent) {
        // initialized
        RectangleArea touchedRectangle = null;

        // iterate through all RectangleArea object to see if any of the is touched
        int rectangleCount = mRectangles.size() - 1;
        for(int walk = rectangleCount; walk >= 0; walk--) {
            RectangleArea eachRectangle = mRectangles.get(walk);

            // get x and y center coordinate
            int xCenter = (eachRectangle.leftCoord + eachRectangle.rightCoord) / 2;
            int yCenter = (eachRectangle.topCoord + eachRectangle.bottomCoord) / 2;

            int topThreshold = 0;
            int bottomThreshold = 0;
            int leftThreshold = 0;
            int rightThreshold = 0;

            if(maxCenterDistancePercent == -1){
                topThreshold = yCenter - eachRectangle.rectangleSize;
                bottomThreshold = yCenter + eachRectangle.rectangleSize;
                leftThreshold = xCenter - eachRectangle.rectangleSize;
                rightThreshold = xCenter + eachRectangle.rectangleSize;
            }else{
                topThreshold = yCenter - ((maxCenterDistancePercent * eachRectangle.rectangleSize) / 100);
                bottomThreshold = yCenter + ((maxCenterDistancePercent * eachRectangle.rectangleSize) / 100);
                leftThreshold = xCenter - ((maxCenterDistancePercent * eachRectangle.rectangleSize) / 100);
                rightThreshold = xCenter + ((maxCenterDistancePercent * eachRectangle.rectangleSize) / 100);
            }

            if(topThreshold < yTouch && bottomThreshold > yTouch && leftThreshold < xTouch && rightThreshold > xTouch){
                touchedRectangle = eachRectangle;
                break;
            }
        }

        /*
        for(RectangleArea eachRectangle : mRectangles) {
            if(eachRectangle.topCoord < yTouch && eachRectangle.bottomCoord > yTouch && eachRectangle.leftCoord < xTouch && eachRectangle.rightCoord > xTouch){
                touchedRectangle = eachRectangle;
                break;
            }
        }
        */

        return touchedRectangle;
    }

    /*
    // Stores data about single circle
    private static class CircleArea {
        int radius;
        int centerX;
        int centerY;

        CircleArea(int centerX, int centerY, int radius) {
            this.radius = radius;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        @Override
        public String toString() {
            return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
        }
    }

    // Paint to draw circles
    private Paint mCirclePaint;

    private final Random mRadiusGenerator = new Random();
    // Radius limit in pixels
    private final static int RADIUS_LIMIT = 100;

    private static final int CIRCLES_LIMIT = 3;

    // All available circles
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);
    */

    /**
     * Clears all CircleArea - pointer id relations
     */
    /*
    private void clearCirclePointer() {
        Log.w(TAG, "clearCirclePointer");

        mCirclePointer.clear();
    }
    */

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * //@param xTouch int x of touch
     * //@param yTouch int y of touch
     *
     * @return obtained {@link //CircleArea}
     */
    /*
    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, mRadiusGenerator.nextInt(RADIUS_LIMIT) + RADIUS_LIMIT);

            if (mCircles.size() == CIRCLES_LIMIT) {
                Log.w(TAG, "Clear all circles, size is " + mCircles.size());
                // remove first circle
                mCircles.clear();
            }

            Log.w(TAG, "Added circle " + touchedCircle);
            mCircles.add(touchedCircle);
        }

        return touchedCircle;
    }
    */

    /**
     * Determines touched circle
     *
     * //@param xTouch int x touch coordinate
     * //@param yTouch int y touch coordinate
     *
     * @return {@link //CircleArea} touched circle or null if no circle has been touched
     */
    /*
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (CircleArea circle : mCircles) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
                touched = circle;
                break;
            }
        }

        return touched;
    }
    */

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }


    /****** FIRST ATTEMPT ******/
    /*
    private ShapeDrawable rectangle;
    private Paint paint;
    private float currX, currY;
    private Rect blue, gray;

    public DrawTest (Context context){
        super(context);

        currX = 1;
        currY = 1;

        gray = new Rect(50, 30, 200, 150);
        blue = new Rect(200, 200, 400, 150);

        paint = new Paint();
        rectangle = new ShapeDrawable(new RectShape());
    }

    @Override
    public boolean isFocused(){
        Log.d(TAG, "View's on focused is called!");
        return super.isFocused();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        currX = event.getX();
        currY = event.getY();
        invalidate();
        Log.d(TAG, "View's on touch is called! X= " + currX + ", Y= " + currY);
        return super.onTouchEvent(event);
    }

    public void onDraw(final Canvas canvas){
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        //Custom View
        rectangle.getPaint().setColor(Color.GRAY);
        rectangle.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        rectangle.getPaint().setStrokeWidth(3);
        gray.set((int)(50+currX), (int)(30+currY), (int)(200+currX), (int)(150+currY));
        rectangle.setBounds(gray);
        gray = rectangle.getBounds();
        rectangle.draw(canvas);

        rectangle.getPaint().setColor(Color.BLUE);
        rectangle.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        rectangle.getPaint().setStrokeWidth(3);
        blue.set((int)(200+currX), (int)(200+currY), (int)(400+currX), (int)(350+currY));
        rectangle.setBounds(blue);
        blue = rectangle.getBounds();
        rectangle.draw(canvas);
    }
    */
}
