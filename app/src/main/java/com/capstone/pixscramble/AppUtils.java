package com.capstone.pixscramble;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class AppUtils {

    /** Holds th App object. */
    private static Pix mApp;


    private AppUtils() {
    }

    public static void setAppStates(List<Pix> apps) {
        if (apps != null) {
            for (Pix app : apps) {
                setAppStates(app);
            }
        }
    }


    public static void setAppStates(Pix app) {

    }

    /**
     * Returns the round image bitmap ofany bitmap.
     *
     * @param bitmap Bitmap to make round shape.
     * @return
     */
    public static Bitmap getRoundedShape(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    /**
     * Returns UTF8 format string.
     *
     * @param string string to format
     */
    public static String getUTFString(String string) {
        String utfString = "";
        try {
            utfString = new String(string.getBytes("ISO-8859-1"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        utfString = Html.fromHtml(utfString).toString();
        return utfString;
    }

    /**
     * Return sthe dialog.
     *
     * @param context Holds the context
     * @param title Holds the dialog title to set
     * @param message Holds the dialog message to set
     * @param isOKButtonDialog Whether to display OK button or not
     * @param isCANCELButtonDialog Whether to display CANCEL button or not
     * @param appCenterDialogListener
     * @return Th Dialog to show
     */
    public static Dialog getAppCenterDialog(Context context, String title, String message, boolean isOKButtonDialog, boolean isCANCELButtonDialog, final IAppCenterDialogListener appCenterDialogListener){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.appcenter_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setCanceledOnTouchOutside(false);

        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        TextView dialogTitleTextView =  (TextView) dialog.findViewById(R.id.dialog_title_textview);
        TextView dialogMessageTextView =  (TextView) dialog.findViewById(R.id.dialog_message_textview);

        dialogTitleTextView.setText(title);
        dialogMessageTextView.setText(message);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (appCenterDialogListener != null){
                    appCenterDialogListener.onOkButtonClick();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        if (!isOKButtonDialog){
            okButton.setVisibility(View.GONE);
        }
        if (!isCANCELButtonDialog){
            cancelButton.setVisibility(View.GONE);
        }

        return dialog;
    }

}
