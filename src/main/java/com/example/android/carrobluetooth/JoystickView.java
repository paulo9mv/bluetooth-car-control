package com.example.android.carrobluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener{

    public float centerX;
    public float centerY;
    public float baseRadius;
    public float hatRadius;

    public JoystickListener joystickListener;

    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickListener = (JoystickListener) context;
        }
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickListener = (JoystickListener) context;
        }
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickListener = (JoystickListener) context;
        }
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if(context instanceof JoystickListener){
            joystickListener = (JoystickListener) context;
        }
    }

    private void drawJoystick(float newX, float newY){
        if(getHolder().getSurface().isValid()){
            Canvas myCanvas = getHolder().lockCanvas();
            Paint paint = new Paint();

            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            paint.setARGB(255,50 , 50 ,50);
            myCanvas.drawCircle(centerX, centerY, baseRadius, paint);

            paint.setARGB(255, 255, 10, 10);
            myCanvas.drawCircle(newX, newY, hatRadius, paint);

            getHolder().unlockCanvasAndPost(myCanvas);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(centerX, centerY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void setupDimensions(){
        centerX = getWidth()/2;
        centerY = getHeight()/2;
        baseRadius = Math.min(getWidth(), getHeight()) / 3;
        hatRadius = Math.min(getWidth(), getHeight()) / 5;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //Apenas toQues no surfaceview/joystick
        if(v.equals(this)){
            //Dedo pressionado
            if(event.getAction() != MotionEvent.ACTION_UP){

                float diametro = (float) Math.sqrt((event.getX() - centerX)*(event.getX() - centerX) + (event.getY() - centerY)*(event.getY() - centerY));
                if(diametro <= baseRadius) {
                    drawJoystick(event.getX(), event.getY());
                    this.joystickListener.onJoystickMoved((event.getX() - centerX) / baseRadius, (event.getY() - centerY) / baseRadius, getId());
                }
                else{
                    float ratio = baseRadius / diametro;
                    float constrainedX = centerX + (event.getX() - centerX) * ratio;

                    float constrainedY = centerY + (event.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    joystickListener.onJoystickMoved((constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius, getId());
                }

            }
            else{
                drawJoystick(centerX, centerY);
                joystickListener.onJoystickMoved(0, 0, getId());
            }
        }
        return true;
    }

    public interface JoystickListener{

        public void onJoystickMoved(float x, float y, int source);



    }

}
