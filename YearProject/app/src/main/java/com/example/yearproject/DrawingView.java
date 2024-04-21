package com.example.yearproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DrawingView extends View {

    //TO DO ::

    // 1. make the green dots buttons // Later
    //      1.1 make the green buttons show an image to the screen
    // 2. Add seconnd and third floor // IMPORTANTS 1 // Done
    //      2.2 modify the algorithm to make it work with multiple floors // Done

    private int floorwelookat=0;
    private Bitmap image;
    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];
    private static final float MIN_ZOOM_SCALE = 0.7f;
    private static final float MAX_ZOOM_SCALE = 3f;
    private float imageWidth, imageHeight;
    private float lastTouchX;
    private float lastTouchY;
    private float lastDistance = 0;
    List<Integer> path;
    private Node[] nodes;
    private int changed=0;
    private final Paint paint = new Paint();
    private final Paint paint2 = new Paint();

    private Bitmap goDownBitmap;
    private Bitmap goUpstairsBitmap;


    private int startNodeId = -1;
    private int endNodeId = -1;


    private static double distance = 0;

    private LinearLayout distanceTimeBox;
    private TextView distanceTextView;
    private TextView timeTextView;

    private MainActivity mainActivity;

    private PopupWindow popupWindow;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setDistanceTimeTextViews(final TextView distanceTextView, final TextView timeTextView) {
        this.distanceTextView = distanceTextView;
        this.timeTextView = timeTextView;

        // Initialize distanceTimeBox
        distanceTimeBox = new LinearLayout(getContext());

        // Set orientation and other properties as needed
        distanceTimeBox.setOrientation(LinearLayout.VERTICAL);

        // Add touch listener to the distanceTimeBox
        if (distanceTimeBox != null) {
            distanceTimeBox.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Consume the touch events to prevent them from reaching the underlying DrawingView
                    return true;
                }
            });
        }
    }


    //Relate the menu to the final and start variables

    public void setStartLocation(String position) {
        startNodeId = changeStartandFinish(position);
        invalidate();
    }

    public void setEndLocation(String position) {
        endNodeId = changeStartandFinish(position);
        invalidate();
    }

    public void setDistanceandCalculateTime(int costInSeconds) {
        // Convert seconds to minutes and seconds
        int minutes = costInSeconds / 60;
        int seconds = costInSeconds % 60;

        // Display the time in the format "mm:ss"
        String timeText = String.format("%02d:%02d", minutes, seconds);

        distance = costInSeconds; // Assuming costInSeconds is in seconds

        if (distanceTextView != null && timeTextView != null) {
            showDistanceTimeBox(String.valueOf(distance), timeText);
        }
    }


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

    public String givemename(int position)
    {
        if((!nodes[position].getname().equals("0")) && (!nodes[position].getname().equals("-1")))
        {
            return nodes[position].getname();
        }
        else if((!nodes[position].getroomnr().equals("0")) && (!nodes[position].getroomnr().equals("0")))
        {
            return nodes[position].getroomnr();
        }

        return "Random_Spot";
    }
    //Get Node id
    public int changeStartandFinish(String position)
    {
        System.out.println("I am here : " + position);

        position = position.toUpperCase();
        // Check by RoomNr
        for (int i = 0; i < nodes.length; i++) {
            if (String.valueOf(nodes[i].getroomnr()).equals(position)) {
                System.out.println("I am here 2: " + i);
                return i;
            }
        }

        // Check by SecondName
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getname().equals(position)) {
                System.out.println("I am here 2: " + i);
                return i;
            }
        }

        // Check by id
        for (int i = 1; i < nodes.length; i++) {
            if (nodes[i].getId() == Integer.parseInt(position)) {
                System.out.println("I am here 3: " + i);
                return i;
            }
        }

        return -1;
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

        paint.setColor(getResources().getColor(android.R.color.holo_green_dark));
        paint.setStyle(Paint.Style.FILL);

        // PRINT NODES
        if (nodes != null) {
            for (int i = 0; i < nodes.length; i++) {
                float[] canvasPosition = {(float) (nodes[i].getX()), (float) (nodes[i].getY())};
                matrix.mapPoints(canvasPosition);
                if (nodes[i].getFloor() == floorwelookat) {

                    if (i == startNodeId) {
                        paint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                    } else if (i == endNodeId) {
                        paint.setColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        paint.setColor(getResources().getColor(android.R.color.holo_green_dark));
                    }

                    canvas.drawCircle(canvasPosition[0], canvasPosition[1], 25, paint);

                    paint2.setColor(getResources().getColor(android.R.color.black));
                    paint2.setTextSize(30);

                    //canvas.drawText(String.valueOf(nodes[i].getId()), canvasPosition[0], canvasPosition[1], paint2);
                    if ((!nodes[i].getroomnr().equals("-1")) && (!nodes[i].getroomnr().equals("0"))) {
                        canvas.drawText(String.valueOf(nodes[i].getroomnr()), canvasPosition[0], canvasPosition[1], paint2);
                    }
                    if ((!nodes[i].getname().equals("-1")) && (!nodes[i].getname().equals("0"))) {
                        canvas.drawText(String.valueOf(nodes[i].getname()), canvasPosition[0] - 70, canvasPosition[1] + 50, paint2);
                    }
                }
            }
        }

        // DRAW LINE
        if (path != null && path.size() >= 2) {
            paint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            paint.setStrokeWidth(10);


            for (int i = 0; i < path.size() - 1; ++i) {
                int first = path.get(i);
                int second = path.get(i + 1);

                // Ensure the nodes are on the same floor and are the intended nodes
                if (nodes[first].getFloor() == floorwelookat && nodes[second].getFloor() == floorwelookat && first == path.get(i) && second == path.get(i + 1)) {

                    float[] startPoint = {nodes[first].getX(), nodes[first].getY()};
                    matrix.mapPoints(startPoint);
                    float[] endPoint = {nodes[second].getX(), nodes[second].getY()};
                    matrix.mapPoints(endPoint);


                    canvas.drawLine(startPoint[0], startPoint[1], endPoint[0], endPoint[1], paint);

                    if (i == path.size() - 2) {
                        drawArrowHead(canvas, paint, startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
                    }
                }
                else if ((nodes[first].getroomnr().equals("-1")) && (nodes[first].getFloor() == 0) && (floorwelookat == 0)) {
                    if ((nodes[second].getroomnr().equals("-1")) && (nodes[second].getFloor() == 1)) {
                        // Draw go_upstairs.png
                        System.out.println("Draw go_upstairs.png");
                        float[] upstairsPosition = {(nodes[first].getX() - 40), (nodes[first].getY() - 40)};
                        matrix.mapPoints(upstairsPosition);
                        canvas.drawBitmap(goUpstairsBitmap, upstairsPosition[0], upstairsPosition[1], null);
                    }
                }
                else if ((nodes[first].getroomnr().equals("-1")) && (nodes[first].getFloor() == 1) && (floorwelookat == 1)) {
                    if ((nodes[second].getroomnr().equals("-1")) && (nodes[second].getFloor() == 2)) {
                        // Draw go_upstairs.png
                        System.out.println("Draw go_upstairs.png");
                        float[] upstairsPosition = {(nodes[first].getX() - 40), (nodes[first].getY() - 40)};
                        matrix.mapPoints(upstairsPosition);
                        canvas.drawBitmap(goUpstairsBitmap, upstairsPosition[0], upstairsPosition[1], null);
                    }
                }
                else if ((nodes[first].getroomnr().equals("-1")) && (nodes[first].getFloor() == 2) && (floorwelookat == 2)) {
                    if ((nodes[second].getroomnr().equals("-1")) && (nodes[second].getFloor() == 1)) {
                        // Draw go_down.png
                        System.out.println("Draw go_down.png");
                        float[] downstairsPosition = {nodes[second].getX() - 40, nodes[second].getY() - 40};
                        matrix.mapPoints(downstairsPosition);
                        canvas.drawBitmap(goDownBitmap, downstairsPosition[0], downstairsPosition[1], null);
                    }
                }
                else if ((nodes[first].getroomnr().equals("-1")) && (nodes[first].getFloor() == 1) && (floorwelookat == 1)) {
                    if ((nodes[second].getroomnr().equals("-1")) && (nodes[second].getFloor() == 0)) {
                        // Draw go_down.png
                        System.out.println("Draw go_down.png 222222222222222");
                        float[] downstairsPosition = {nodes[second].getX() - 40, nodes[second].getY() - 40};
                        matrix.mapPoints(downstairsPosition);
                        canvas.drawBitmap(goDownBitmap, downstairsPosition[0], downstairsPosition[1], null);
                    }
                }

            }
        }

    }







    private void showDistanceTimeBox(final String distanceText, final String timeText) {
        // Dismiss any existing PopupWindow
        dismissDistanceTimeBox();

        // Create a new PopupWindow
        popupWindow = new PopupWindow(getContext());

        // Set the custom layout for the PopupWindow
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.distance_time_popup, null);
        popupWindow.setContentView(popupView);

        // Find TextViews inside the custom layout
        final TextView distanceTextView = popupView.findViewById(R.id.distanceTextView);
        final TextView timeTextView = popupView.findViewById(R.id.timeTextView);

        // Set the distance and time texts
        distanceTextView.setText("Distance: " + distanceText + " steps");
        timeTextView.setText("Time: " + timeText + "min");

        // Set the width and height of the PopupWindow
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // Set focusable and background drawable (using the custom background)
        popupWindow.setFocusable(false); // Prevent losing focus
        popupWindow.setOutsideTouchable(false); // Prevent dismissing when clicking outside

        // Calculate position for the left-down corner
        int offsetX = 0; // Distance from left edge of the screen
        int offsetY = getWindowHeight() - popupWindow.getHeight(); // Distance from bottom of the screen

        // Show the PopupWindow at the calculated position
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, offsetX, offsetY);
    }

    private void dismissDistanceTimeBox() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }




    // Helper method to get the height of the screen
    private int getWindowHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }





    private void drawArrowHead(@NonNull Canvas canvas, Paint paint, float startX, float startY, float endX, float endY) {
        float arrowSize = 30; // Size of the arrowhead

        float angle = (float) Math.atan2(endY - startY, endX - startX);
        float arrowX = (float) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
        float arrowY = (float) (endY - arrowSize * Math.sin(angle - Math.PI / 6));

        canvas.drawLine(endX, endY, arrowX, arrowY, paint);

        arrowX = (float) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
        arrowY = (float) (endY - arrowSize * Math.sin(angle + Math.PI / 6));

        canvas.drawLine(endX, endY, arrowX, arrowY, paint);

        //showDistanceTimeBox(String.valueOf(distance), String.valueOf(distance / 100));
        //setDistanceandCalculateTime((int) distance);

    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                checkButtonPress(lastTouchX, lastTouchY);
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

//        for (int i = 0; i < nodes.length; i++) {
//            float distance = (float) Math.abs(Math.sqrt(Math.pow((lastTouchX) - nodes[i].getX(), 2) + Math.pow((lastTouchY) - nodes[i].getY(), 2)));
//
//            if ((distance <= 50) && (nodes[i].getFloor() == floorwelookat)) {
//                // Show the pop-up menu
//                showNodePopupMenu(i, lastTouchX, lastTouchY);
//                break;
//            }
//        }

        invalidate(); // Redraw the view
        return true;
    }



    private void checkButtonPress(float touchX, float touchY) {
        if (nodes == null) {
            return; // Exit the method if nodes are not initialized
        }

        //showPopUpMessage(touchX + " / " + touchY);

        Matrix inverse = new Matrix();
        matrix.invert(inverse);

        float[] touchPoint = { touchX, touchY};
        inverse.mapPoints(touchPoint);

        //showPopUpMessage((float) (touchX /2.40) + " / " + (float) (touchY/2.40));
        //showPopUpMessage((float) (touchX - 400) + " / " + (float) (touchY+ 100));
        //showPopUpMessage((touchX) + " / " + (touchY));

        for (int i = 0; i < nodes.length; i++) {
            float distance = (float) Math.abs(Math.sqrt(Math.pow((touchPoint[0]) - nodes[i].getX(), 2) + Math.pow((touchPoint[1]) - nodes[i].getY(), 2)));

            if ((distance <= 20) && (nodes[i].getFloor() == floorwelookat)) {
                // Green dot/button pressed, handle accordingly
                // For now, let's just show a message
                //showPopUpMessage(nodes[i].getroomnr());
                showPopUpMessage(String.valueOf(nodes[i].getId()));

                // Show the pop-up menu
                showNodePopupMenu(i, touchX, touchY);
                break;
            }
        }
    }

    private void showPopUpMessage(String message) {
        // You can replace this with your desired pop-up message implementation
        // For example, displaying a Toast message
        Toast.makeText(getContext(), "Green dot/button pressed at: " + message, Toast.LENGTH_SHORT).show();
    }

    private void showNodePopupMenu(final int nodeId, final float touchX, final float touchY) {
        // Create a new PopupWindow
        PopupWindow popupWindow = new PopupWindow(getContext());

        // Set the custom layout for the PopupWindow
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.node_popup_menu, null);
        popupWindow.setContentView(popupView);

        // Find views inside the custom layout
        TextView menuSetStart = popupView.findViewById(R.id.menu_set_start);
        TextView menuSetEnd = popupView.findViewById(R.id.menu_set_end);

        // Set click listeners for menu items
        menuSetStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartLocation(String.valueOf(nodeId));

                if (mainActivity != null)
                {
                    //mainActivity.newstartlocation(nodes[startNodeId].getroomnr());
                    mainActivity.newstartlocation(Integer.toString(startNodeId));
                    if ((mainActivity != null) && (endNodeId > 0)){
                        mainActivity.performAStarAlgorithm();
                    }
                }

                popupWindow.dismiss();
            }
        });

        menuSetEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndLocation(String.valueOf(nodeId));

                if (mainActivity != null) {
                    //mainActivity.newendlocation(nodes[endNodeId].getroomnr());
                    mainActivity.newendlocation(Integer.toString(endNodeId));
                    if ((mainActivity != null) && (startNodeId > 0)) {
                        mainActivity.performAStarAlgorithm();
                    } else {
                        Log.e("DrawingView", "MainActivity is null");
                    }
                }
                popupWindow.dismiss();
            }
        });

        // Set the width and height of the PopupWindow
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // Set focusable and background drawable (using the custom background)
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_popup_background));

        // Calculate the center position for the PopupWindow
        int offsetX = (int) touchX;
        int offsetY = (int) touchY;

        // Show the PopupWindow at the calculated position
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, offsetX, offsetY);
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