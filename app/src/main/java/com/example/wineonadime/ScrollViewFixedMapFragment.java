package com.example.wineonadime;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

// This fix was found at:
// https://stackoverflow.com/questions/30525066/how-to-set-google-map-fragment-inside-scroll-view
// I had to employ this fix because the scroll view was conflicting with the map fragment in the
// store page fragment. This fix solved the problem, but since I didn't change much I wanted to
// credit the fix here. -Chris
public class ScrollViewFixedMapFragment extends SupportMapFragment
{
    private OnTouchListener mListener;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance)
    {
        View layout = super.onCreateView(layoutInflater, viewGroup, savedInstance);

        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());

        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT));

        return layout;
    }

    public void setListener(OnTouchListener listener)
    {
        mListener = listener;
    }

    public interface OnTouchListener
    {
        public abstract void onTouch();
    }

    public class TouchableWrapper extends FrameLayout
    {

        public TouchableWrapper(Context context)
        {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    mListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}