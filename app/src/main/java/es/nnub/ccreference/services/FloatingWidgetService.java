package es.nnub.ccreference.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.nnub.ccreference.R;
import es.nnub.ccreference.descriptions.BaseDescriptionAdapter;
import es.nnub.ccreference.descriptions.EnemyDescription;
import es.nnub.ccreference.descriptions.ItemDescription;

public class FloatingWidgetService extends Service implements View.OnClickListener {

    public class FloatingWidgetBinder extends Binder {
        public FloatingWidgetService getService() {
            return FloatingWidgetService.this;
        }
    }

    public interface FloatingWidgetCallbacks {
        MediaController onMediaControllerRequested();
        void openSearchLink(String query);
        void onPressCloseButton();
    }

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private View background;
    private ImageView collapsedIcon;
    private boolean expanded;
    private Context context;

    private String resName;
    private ScrollView baseScrollView;
    private Intent inte;
    private WindowManager.LayoutParams params;
    private FloatingWidgetBinder binder;
    private FloatingWidgetCallbacks activityCallbacks;

    public FloatingWidgetService() {
        binder = new FloatingWidgetBinder();
    }

    public void registerCallbacks(FloatingWidgetCallbacks callbacks) {
        activityCallbacks = callbacks;
    }

    public void ready() {
        initializeLayoutFromBaseResourceName(resName, baseScrollView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Bundle extras = intent.getExtras();
        resName = extras.getString("name");
        inte = intent;

        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        resName = extras.getString("name");
        inte = intent;

        initializeLayoutFromBaseResourceName(resName, baseScrollView);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget_layout, null);
        background = mFloatingView.findViewById(R.id.floating_bg);

        //setting the layout parameters
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        collapsedIcon = mFloatingView.findViewById(R.id.collapsed_iv);

        //getting the collapsed and expanded view from the floating view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        baseScrollView = mFloatingView.findViewById(R.id.baseScrollView);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener(this);

        final View parent = mFloatingView.findViewById(R.id.relativeLayoutParent);

        //adding an touchlistener to make drag movement of the floating widget
        parent.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private int savedPositionX;
            private int savedPositionY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean moving = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        if (!moving) {
                            if (!expanded) {
                                savedPositionX = params.x;
                                savedPositionY = params.y;
                                params.x = 0;
                                params.y = 0;
                                mWindowManager.updateViewLayout(mFloatingView, params);
                                showExpanded();
                            } else {
                                hideExpanded();
                                params.x = savedPositionX;
                                params.y = savedPositionY;
                                mWindowManager.updateViewLayout(mFloatingView, params);
                            }
                        }
                        moving = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        moving = true;
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    protected void initializeLayoutFromBaseResourceName(String name, ScrollView root) {
        BaseDescriptionAdapter descriptionAdapter;
        String contentType;
        String contentName;

        Pattern pattern = Pattern.compile("^([a-z]+)_([a-z_]+)$");
        Matcher matcher = pattern.matcher(name);
        if(matcher.find()){
            MatchResult matchResult = matcher.toMatchResult();
            contentType = matchResult.group(1);
            contentName = matchResult.group(2);
        } else {
            throw new RuntimeException("WRONGLY CRAFTED LINK: " + name);
        }

        switch (contentType) {
            case "enemy":
                descriptionAdapter = new EnemyDescription(this);
                break;
            case "item":
                descriptionAdapter = new ItemDescription(this);
                break;
            default:
                throw new RuntimeException("UNKNOWN TYPE:" + contentType);
        }

        Drawable iconImage = context.getResources().getDrawable(
                context.getResources().getIdentifier(
                    "desc_" + contentType + "_" + contentName + "_icon", "drawable",
                    context.getPackageName()),
                null
        );
        iconImage.setFilterBitmap(false);
        collapsedIcon.setImageDrawable(iconImage);

        descriptionAdapter.loadResourcesFromName("desc_" + contentType + "_" + contentName);
        descriptionAdapter.initializeView(this, root, activityCallbacks);
    }

    protected void hideExpanded() {
        expandedView.setVisibility(View.GONE);
        expanded = false;
    }

    protected void showExpanded() {
        if (!expanded) {
            expanded = true;
            expandedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClose:
                // Closing the widget
                activityCallbacks.onPressCloseButton();
                break;
        }
    }
}
