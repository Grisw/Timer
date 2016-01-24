package pers.missingno.timer;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;

public class TimerView extends LinearLayout {

    private static final int PUSH=0;
    private static final int PULL=1;

    private static Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==PUSH){
                TimerView view = (TimerView) msg.obj;
                view.pushTenMillis();
                return true;
            }else if(msg.what==PULL){
                TimerView view = (TimerView) msg.obj;
                view.pullTenMillis();
                view.progressBar.setProgress(msg.arg1);
                if(view.time.minutes==0&&view.time.seconds==0&&view.time.millis==0){
                    view.stop();
                    view.progressBar.setProgress(0);
                    if(view.stop_button!=null){
                        view.stop_button.setText(R.string.end);
                        view.stop_button.setEnabled(false);
                    }
                }
                return true;
            }
            return false;
        }
    });

    private Time time=new Time(0,0,0);

    private TextView minutes_view;
    private TextView seconds_view;
    private TextView millis_view;
    private Button stop_button;
    private ProgressBar progressBar;

    private Thread thread;
    private boolean isCounting=false;
    private boolean isTypeCountDown=false;

    public TimerView(Context context) {
        super(context);
        init(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context,R.layout.timer_view,this);
        minutes_view= (TextView) view.findViewById(R.id.timer_view_minutes);
        seconds_view= (TextView) view.findViewById(R.id.timer_view_seconds);
        millis_view= (TextView) view.findViewById(R.id.timer_view_millis);

        DecimalFormat format=new DecimalFormat("00");
        millis_view.setText(format.format(time.millis));
        seconds_view.setText(format.format(time.seconds));
        minutes_view.setText(format.format(time.minutes));
    }

    private void pushTenMillis(){
        time.millis+=10;
        if(time.millis==100){
            time.millis=0;
            time.seconds+=1;
        }
        if(time.seconds==60){
            time.seconds=0;
            time.minutes+=1;
        }

        DecimalFormat format=new DecimalFormat("00");
        millis_view.setText(format.format(time.millis));
        seconds_view.setText(format.format(time.seconds));
        minutes_view.setText(format.format(time.minutes));
    }

    private void pullTenMillis(){
        time.millis-=10;
        if(time.millis<0){
            time.millis=90;
            time.seconds-=1;
        }
        if(time.seconds<0){
            time.seconds=59;
            time.minutes-=1;
        }
        if(time.minutes<0){
            time.minutes=0;
        }

        DecimalFormat format=new DecimalFormat("00");
        millis_view.setText(format.format(time.millis));
        seconds_view.setText(format.format(time.seconds));
        minutes_view.setText(format.format(time.minutes));
    }

    public void startCount(){
        if(thread==null){
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    isCounting=true;
                    try {
                        while(isCounting){
                            handler.sendMessage(handler.obtainMessage(PUSH, TimerView.this));
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    public void startCountDown(final Button stopButton,ProgressBar progressBar){
        if(thread==null){
            stop_button=stopButton;
            this.progressBar=progressBar;
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    isCounting=true;
                    try {
                        while(isCounting){
                            int time=(TimerView.this.time.getMinutes()*60+TimerView.this.time.getSeconds())*10+TimerView.this.time.getMillis()/10;
                            handler.sendMessage(handler.obtainMessage(PULL,time,0,TimerView.this));
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    public void stop(){
        isCounting=false;
        thread=null;
    }

    public boolean isRunning(){
        return isCounting;
    }

    public boolean isTypeCountDown() {
        return isTypeCountDown;
    }

    public void setIsTypeCountDown(boolean isTypeCountDown) {
        this.isTypeCountDown = isTypeCountDown;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Time getTime() {
        return time;
    }

    public static class Time{
        private int minutes=0;
        private int seconds=0;
        private int millis=0;

        public Time(int millis, int minutes, int seconds) {
            this.millis = millis;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        public int getMinutes() {
            return minutes;
        }

        public void setMinutes(int minutes) {
            this.minutes = minutes;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public int getMillis() {
            return millis;
        }

        public void setMillis(int millis) {
            this.millis = millis;
        }
    }
}
