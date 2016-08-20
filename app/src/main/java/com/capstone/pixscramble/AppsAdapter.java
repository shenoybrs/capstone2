package com.capstone.pixscramble;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.pixscramble.provider.AppsProvider;


public class AppsAdapter extends CursorAdapter {

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state = PixStoreConstants.TIMER_START;


    public AppsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_card_item, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        Holder h = new Holder(view);
        view.setTag(h);

        final Holder vh = (Holder) view.getTag();

        vh.appIcon.setDefaultImageResId(R.drawable.app_default_logo);
        vh.appIcon.setErrorImageResId(R.drawable.app_default_logo);
        if (state == PixStoreConstants.TIMER_START) {
            if (VolleyService.getInstance(context.getApplicationContext()).getImageLoader() != null) {
                vh.appIcon.setImageUrl(cursor.getString(cursor.getColumnIndex
                                (AppsProvider.JioAppsColumns.ICON_URL)).toString(),
                        VolleyService.getInstance(context.getApplicationContext()).getImageLoader());
            }
        } else if (state == PixStoreConstants.USER_PLAYING) {
            if (cursor.getInt(cursor.getColumnIndex
                    (AppsProvider.JioAppsColumns.READ)) == 0) {
                vh.appIcon.setDefaultImageResId(R.drawable.app_default_logo);
                vh.appIcon.setImageUrl(null, VolleyService.getInstance(context.getApplicationContext()).getImageLoader());

            }
            else
            {
                if (VolleyService.getInstance(context.getApplicationContext()).getImageLoader() != null) {
                    vh.appIcon.setImageUrl(cursor.getString(cursor.getColumnIndex
                                    (AppsProvider.JioAppsColumns.ICON_URL)).toString(),
                            VolleyService.getInstance(context.getApplicationContext()).getImageLoader());
                }
            }
        }


    }


    public interface ContentChangeCallback {
        void onContentChange();
    }


    public class Holder {
        final NetworkImageView appIcon;
        //final RelativeLayout menuView;

        public Holder(View view) {
            appIcon = (NetworkImageView) view.findViewById(R.id.app_jionetwork_icon);

        }
    }
}