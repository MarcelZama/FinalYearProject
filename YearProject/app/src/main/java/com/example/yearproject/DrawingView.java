package com.example.yearproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

public class DrawingView extends View {

    //TO DO ::

    // 1. make the green dots buttons // Later
    //      1.1 make the green buttons show an image to the screen
    // 2. Add seconnd and third floor // IMPORTANTS 1
    //      2.2 modify the algorithm to make it work with multiple floors

    private int floorwelookat=0;
    private Bitmap image;
    private Matrix matrix = new Matrix();
    private float[] matrixValues = new float[9];
    private static final float MIN_ZOOM_SCALE = 0.7f;
    private static final float MAX_ZOOM_SCALE = 3f;
    private float imageWidth, imageHeight;
    private float lastTouchX;
    private float lastTouchY;
    private float lastDistance = 0;
    List<Integer> path;
    private Node[] nodes;
    private int changed=0;
    private Paint paint = new Paint();
    private Paint paint2 = new Paint();

    private Bitmap goDownBitmap;
    private Bitmap goUpstairsBitmap;



    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    //Change Images
    public void changeImage(int imageResourceId) {
        // Change the image when called from MainActivity
        image = BitmapFactory.decodeResource(getResources(), imageResourceId);
        if(imageResourceId == R.drawable.gftest)
        {
            floorwelookat = 0;
            changed = 0;

        }
        else if(imageResourceId == R.drawable.gftest2)
        {
            floorwelookat = 1;
            changed = 1;
        }
        else if(imageResourceId == R.drawable.gftest3)
        {
            floorwelookat = 2;
            changed = 2;
        }
        //add floor 3
        invalidate();
    }

    private void init() {
        image = BitmapFactory.decodeResource(getResources(), R.drawable.gftest);
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();


        float initialZoom = 0.9f;
        matrix.postScale(initialZoom, initialZoom);
        matrix.getValues(matrixValues);

        // Load the go_down and go_upstairs bitmaps
        goDownBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.go_down);
        goUpstairsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.go_upstairs);

    }

    // Set node coordinates received from MainActivity
    public void setNodeCoordinates(Node[] nodes) {
        this.nodes = nodes;
        invalidate(); // Redraw the view
    }

    public void setPathForLine(List<Integer> path)
    {
        this.path = path;
        System.out.println("I GET IT BRO !!!!");
        for (Integer vertex : path) {
            System.out.print(vertex + " ");
        }
        System.out.println();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the image
        canvas.drawBitmap(image, matrix, null);

        //Paint paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.holo_green_dark));
        paint.setStyle(Paint.Style.FILL);

        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                //if (i < nodes.length) {
                float[] canvasPosition = {nodes[i].getX(), nodes[i].getY()};
                matrix.mapPoints(canvasPosition);
                if (nodes[i].getFloor() == floorwelookat) {
                    /*----> This is the green Dot Draw */
                    canvas.drawCircle(canvasPosition[0], canvasPosition[1], 25, paint); // Draw Nodes

                    // Draw the ID text on top of the green dot
                    paint2.setColor(getResources().getColor(android.R.color.black)); // Set text color
                    paint2.setTextSize(40); // Set text size
                    canvas.drawText(String.valueOf(nodes[i].getId()), canvasPosition[0], canvasPosition[1], paint2);
                }
                //}
            }
        }

        // Draw the line between shortest path nodes
        if (path != null && path.size() >= 2) {
            paint.setColor(getResources().getColor(android.R.color.holo_blue_dark)); // Set line color
            paint.setStrokeWidth(5); // Set line width

            for (int i = 0; i < path.size() - 1; i++) {
                int first = path.get(i);
                int second = path.get(i + 1);

                if ((nodes[first].getroomnr() == 0) && (nodes[first].getFloor() == 0) && (floorwelookat == 0)) {
                    if ((nodes[second].getroomnr() == 0) && (nodes[second].getFloor() == 1)) {
                        // Draw go_upstairs.png
                        float[] upstairsPosition = {(nodes[first].getX()-40), (nodes[first].getY()-40)};
                        matrix.mapPoints(upstairsPosition);
                        canvas.drawBitmap(goUpstairsBitmap, upstairsPosition[0], upstairsPosition[1], null);
                    }
                } else if ((nodes[first].getroomnr() == 0) && (nodes[first].getFloor() == 1) && (floorwelookat == 1)) {
                    if ((nodes[second].getroomnr() == 0) && (nodes[second].getFloor() == 0)) {
                        // Draw go_down.png
                        float[] downstairsPosition = {nodes[second].getX(), nodes[second].getY()};
                        matrix.mapPoints(downstairsPosition);
                        canvas.drawBitmap(goDownBitmap, downstairsPosition[0], downstairsPosition[1], null);
                    }
                }
                else
                {
                    // Transform node coordinates using the matrix
                    float[] startPoint = {nodes[first].getX(), nodes[first].getY()};
                    matrix.mapPoints(startPoint);
                    float[] endPoint = {nodes[second].getX(), nodes[second].getY()};
                    matrix.mapPoints(endPoint);

                    // Draw the line only if the floor matches the current floorwelookat
                    if (nodes[first].getFloor() == floorwelookat && nodes[second].getFloor() == floorwelookat) {
                        canvas.drawLine(startPoint[0], startPoint[1], endPoint[0], endPoint[1], paint);

                        // Draw arrowhead at the end of the last line
                        if (i == path.size() - 2) {
                            drawArrowHead(canvas, paint, startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
                        }
                    }
                }
            }
        }
    }

    private void drawArrowHead(Canvas canvas, Paint paint, float startX, float startY, float endX, float endY) {
        float arrowSize = 30; // Size of the arrowhead

        float angle = (float) Math.atan2(endY - startY, endX - startX);
        float arrowX = (float) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
        float arrowY = (float) (endY - arrowSize * Math.sin(angle - Math.PI / 6));

        canvas.drawLine(endX, endY, arrowX, arrowY, paint);

        arrowX = (float) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
        arrowY = (float) (endY - arrowSize * Math.sin(angle + Math.PI / 6));

        canvas.drawLine(endX, endY, arrowX, arrowY, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    float dx = event.getX() - lastTouchX;
                    float dy = event.getY() - lastTouchY;
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                    matrix.postTranslate(dx, dy);
                    checkBounds();
                } else if (event.getPointerCount() == 2) {
                    // Calculate distance between two fingers
                    float newDistance = calculateDistance(event);

                    // Zoom in or out accordingly
                    float scaleFactor = newDistance / lastDistance;
                    zoom(scaleFactor, event.getX(), event.getY());

                    lastDistance = newDistance;
                }
                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                // Add green dot
//                addGreenDot(event.getX(), event.getY());
//                lastDistance = calculateDistance(event);
//                break;
        }

        invalidate(); // Redraw the view
        return true;
    }

    private float calculateDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void zoom(float scaleFactor, float focusX, float focusY) {
        float newScale = scaleFactor * matrixValues[Matrix.MSCALE_X];

        if (newScale < MIN_ZOOM_SCALE || newScale > MAX_ZOOM_SCALE) {
            return; // Prevent zooming out beyond minimum scale or zooming in beyond maximum scale
        }

        matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        matrix.getValues(matrixValues);

        checkBounds();

        float currentWidth = imageWidth * matrixValues[Matrix.MSCALE_X];
        float currentHeight = imageHeight * matrixValues[Matrix.MSCALE_Y];

        float minX = Math.min(0, getWidth() - currentWidth);
        float minY = Math.min(0, getHeight() - currentHeight);

        float maxX = Math.max(0, getWidth() - currentWidth);
        float maxY = Math.max(0, getHeight() - currentHeight);

        if (currentWidth > getWidth()) {
            if (matrixValues[Matrix.MTRANS_X] < minX || matrixValues[Matrix.MTRANS_X] > maxX) {
                matrixValues[Matrix.MTRANS_X] = Math.min(Math.max(matrixValues[Matrix.MTRANS_X], minX), maxX);
            }
        } else {
            matrixValues[Matrix.MTRANS_X] = Math.min(Math.max(matrixValues[Matrix.MTRANS_X], minX), maxX);
        }

        if (currentHeight > getHeight()) {
            if (matrixValues[Matrix.MTRANS_Y] < minY || matrixValues[Matrix.MTRANS_Y] > maxY) {
                matrixValues[Matrix.MTRANS_Y] = Math.min(Math.max(matrixValues[Matrix.MTRANS_Y], minY), maxY);
            }
        } else {
            matrixValues[Matrix.MTRANS_Y] = Math.min(Math.max(matrixValues[Matrix.MTRANS_Y], minY), maxY);
        }

        matrix.setValues(matrixValues);
    }

    private void checkBounds() {
        matrix.getValues(matrixValues);
        float dx = matrixValues[Matrix.MTRANS_X];
        float dy = matrixValues[Matrix.MTRANS_Y];
        float scale = matrixValues[Matrix.MSCALE_X];

        // Calculate the bounds based on the image dimensions and current scale
        float minX = Math.min(0, getWidth() - imageWidth * scale);
        float minY = Math.min(0, getHeight() - imageHeight * scale);
        float maxX = Math.max(0, getWidth() - imageWidth * scale);
        float maxY = Math.max(0, getHeight() - imageHeight * scale);

        // If the image is smaller than the view, center it
        if (imageWidth * scale < getWidth()) {
            dx = (getWidth() - imageWidth * scale) / 2;
        } else {
            // Apply bounds to translation
            dx = Math.min(Math.max(dx, minX), maxX);
        }

        if (imageHeight * scale < getHeight()) {
            dy = (getHeight() - imageHeight * scale) / 2;
        } else {
            // Apply bounds to translation
            dy = Math.min(Math.max(dy, minY), maxY);
        }

        // Smoothly move towards the calculated translation
        matrix.postTranslate((dx - matrixValues[Matrix.MTRANS_X]) * 0.5f, (dy - matrixValues[Matrix.MTRANS_Y]) * 0.5f);

        invalidate();
    }


}
