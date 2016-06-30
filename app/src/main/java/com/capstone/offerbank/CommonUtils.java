/**
 * 
 */
package com.capstone.offerbank;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtils {

    private static boolean mVerifiedContactForAccountStatus = false;

    private static final int PWD_VALIDATION_SUCCESS = 0;
    private static final int  PWD_VALIDATION_FAILED_WITH_LENGTH= 3;
    private static final int PWD_VALIDATION_FAILED_WITH_ALPHABET = 1;
    private static final int PWD_VALIDATION_FAILED_WITH_DIGIT = 2;
    private static final int PWD_VALIDATION_FAILED_WITH_NOT_ALLOWED_SPECIAL_CHAR = 4;
    private static final int PWD_VALIDATION_FAILED_WITH_CONSECUTIVE_CHARACTERS = 6;
    private static final int PWD_VALIDATION_FAILED_WITH_SPACE_CHARACTER=7;

    public static boolean networkAvailable(Context context) {
        boolean networkAvailable = false;

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Service.CONNECTIVITY_SERVICE);

        if (null != connMgr) {
            NetworkInfo nwInfo = connMgr.getActiveNetworkInfo();
            networkAvailable = ((null != nwInfo) && (nwInfo.isAvailable() || nwInfo
                    .isConnectedOrConnecting()));

        }

        Log.d("CommonUtils.class", "networkAvailable: " + networkAvailable);

        return networkAvailable;
    }

    public static boolean isDeviceConnectedTo2Gor3G(Context context) {
        NetworkInfo active_network = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (active_network != null && active_network.isConnected() &&
                (active_network.getType() == ConnectivityManager.TYPE_MOBILE)) {
            TelephonyManager mTelephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = mTelephonyManager.getNetworkType();
            Log.d("CommonUtils.class", "networkType is  " + networkType);
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true;//2G//3G
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;//4G
                default:
                    return false;
            }
        }
        return false;
    }

    public  interface IcaptivePortalListener{
        /**
         * implement this callback to know the status if captive portal
         * @param lockStatus
         */
        void onResponse(boolean lockStatus);
    }















    public static String getAppNameByPID(Context context, int pid) {
        // Log.i(getClass(), "context: " + context + "; pid: " + pid);
        String appName = "";

        try {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);

            for (RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    appName = processInfo.processName;
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("CommonUtils.class", "context: " + context + "; pid: " + pid, e);
        }

        return appName;
    }


    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isMobileNumberValid(String mobileNumber) {
        boolean isValid = false;
        String expression = "[0-9+]{13,13}";
        CharSequence inputStr = mobileNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isJioIdValid(String mobileNumber) {
        boolean isValid = false;
        String expression = "^[a-zA-Z0-9_.-]{6,32}$";
        CharSequence inputStr = mobileNumber;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            if(isAlphaCharExist(mobileNumber)){
                isValid = true;
            }
        }

        return isValid;
    }

    private static boolean isAlphaCharExist(String string){
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);

            if (IsLetter(ch)) {
                return true;
            }
        }
        return false;
    }

//    public static int isPasswordValid(String password) {
//        String specialCharacters = "£`|~!@#$%^&*()_+-={}[];':<>?,./\\\"";
//        boolean isAlphabetExists = false;
//        boolean isNumberExists = false;
//        boolean isNotValidSpecialCharacterExists = false;
//
//        for (int i = 0; i < password.length(); i++) {
//            char ch = password.charAt(i);
//
//            if (IsLetter(ch)) {
//                isAlphabetExists = true;
//            } else if (IsDigit(ch)) {
//                isNumberExists = true;
//            } else if (!specialCharacters.contains(Character.toString(ch))) {
//                    isNotValidSpecialCharacterExists = true;
//        }
//        }
//        if (!(password.length() > 7 && password.length() < 31)) {
//            return PWD_VALIDATION_FAILED_WITH_LENGTH ;//password does not meet length restiction
//        }
//        if(!isAlphabetExists && !isNumberExists){
//            return PWD_VALIDATION_FAILED_WITH_ONLY_SPECIAL_CHARACTER ;// password has only special character
//        }
//        if (!isAlphabetExists) {
//            return PWD_VALIDATION_FAILED_WITH_ALPHABET ;//password does not have at least 1 one alphabet
//        }
//        if (!isNumberExists) {
//            return PWD_VALIDATION_FAILED_WITH_DIGIT;//password does not have at least 1 number
//        }
//        if (isNotValidSpecialCharacterExists) {
//            return PWD_VALIDATION_FAILED_WITH_NOT_ALLOWED_SPECIAL_CHAR ;//password has not allowed special character
//        }
//        return PWD_VALIDATION_SUCCESS;
//    }

    public static int isPasswordValid(String password) {

        // check for empty string
        if (TextUtils.isEmpty(password)) {
            return PWD_VALIDATION_FAILED_WITH_LENGTH;
        }

        // check for minimum length
        if (password.length() < 8 || password.length() > 30) {
            return PWD_VALIDATION_FAILED_WITH_LENGTH;
        }

        boolean isAlphabetExists = false;
        boolean isNumberExists = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);

            if (IsLetter(ch)) {
                isAlphabetExists = true;
            } else if (IsDigit(ch)) {
                isNumberExists = true;
            }
        }

        // check if string has at least one alphabet character
        if (!isAlphabetExists) {
            return PWD_VALIDATION_FAILED_WITH_ALPHABET;
        }

        // check if string has at least one numeric character
        if (!isNumberExists) {
            return PWD_VALIDATION_FAILED_WITH_DIGIT;
        }

        // check for consecutive characters
        if(hasConsecutiveChars(password.toLowerCase())){
            return PWD_VALIDATION_FAILED_WITH_CONSECUTIVE_CHARACTERS;
        }

        if(hasSequentialNos(password) || hasReverseSequentialNos(password)){
            return PWD_VALIDATION_FAILED_WITH_CONSECUTIVE_CHARACTERS;
        }

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(password);
        boolean found = matcher.find();

        if (found) {
            return PWD_VALIDATION_FAILED_WITH_SPACE_CHARACTER ;
        }

        return PWD_VALIDATION_SUCCESS;
    }
   private static boolean IsLetter(char c)
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean IsDigit(char c)
    {

        return c >= '0' && c <= '9';
    }

    private static boolean hasConsecutiveChars(String input){
        for(int i = 0; i < input.length() - 2; i ++){
            if((input.charAt(i) == input.charAt(i + 1))
                && (input.charAt(i) == input.charAt(i + 2))){
                return true;
            }
        }

        return false;
    }

    private static boolean hasSequentialNos(String input){
        for(int i = 0; i < input.length() - 2; i ++){
            char firstChar = input.charAt(i);
            char secondChar = input.charAt(i + 1);
            char thirdChar = input.charAt(i + 2);
            if(IsDigit(firstChar)){
                if(IsDigit(secondChar)){
                    if(secondChar - firstChar == 1){
                        if(IsDigit(thirdChar)){
                            if(thirdChar - secondChar == 1){
                                return true;
                            }
                        }
                    }

                }
            }
        }

        return false;
    }

    private static boolean hasReverseSequentialNos(String input){
        for(int i = 0; i < input.length() - 2; i ++){
            char firstChar = input.charAt(i);
            char secondChar = input.charAt(i + 1);
            char thirdChar = input.charAt(i + 2);
            if(IsDigit(firstChar)){
                if(IsDigit(secondChar)){
                    if((firstChar - secondChar == 1) || (firstChar - secondChar == -9)){
                        if(IsDigit(thirdChar)){
                            if(secondChar - thirdChar == 1){
                                return true;
                            }
                        }
                    }

                }
            }
        }

        return false;
    }


    public static Boolean getVerifiedContactForAccountStatus()
    {
        return mVerifiedContactForAccountStatus;
    }

    public static void setVerifiedContactForAccountStatus(Boolean accountContactStatus)
    {
        mVerifiedContactForAccountStatus = accountContactStatus;
    }

    public static String giveMeTitleCase(String inputString )
    {
        if (inputString == null)
            return null;
        String[] words = inputString.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
       return (sb != null ? sb.toString(): null);
    }

    // get foreground application package name
   public static String getForegroundPackageName(Context cxt)
    {
        String foregroundTaskPackageName = null;
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        // The first in list of RunningTasks is foreground task, so only ask for 1 return

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ) {

            if (am.getRunningTasks(1) == null || am.getRunningTasks(1).size() == 0) {
                return null;
            }
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            if (foregroundTaskInfo != null) {
                foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
            }
        }
        else
        {
            // for lollipop and above
            final int START_TASK_TO_FRONT = 2;
            RunningAppProcessInfo currentInfo = null;
            Field field = null;
            try {
                field = RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }

            List< RunningAppProcessInfo> appList = am.getRunningAppProcesses();
            for ( RunningAppProcessInfo app : appList) {
                if (app.importance ==  RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Integer state = null;
                    try {
                        state = field.getInt( app );
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return null;
                    }
                    if (state != null && state == START_TASK_TO_FRONT) {
                        currentInfo = app;
                        break;
                    }
                }
            }
            if (currentInfo != null && currentInfo.pkgList != null && currentInfo.pkgList.length >0)
            {
                foregroundTaskPackageName = currentInfo.pkgList[0];
            }

        }
        return foregroundTaskPackageName;
    }

    // get foreground application name
    public static String getFGAppName(PackageManager pm, String fgTaskPackageName) {
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(fgTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // get the FG app name
        String foregroundAppName = foregroundAppPackageInfo.applicationInfo
                .loadLabel(pm).toString();

        if (foregroundAppName == null) {
            foregroundAppName = "";
        }

        return foregroundAppName;
    }

    public static boolean isAppInstalled(String packageName,Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }


    public static final boolean isTelephonyFeatureAvailable(Context context) {

        final PackageManager pm = context.getPackageManager();

        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }
}
