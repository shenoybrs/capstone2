package com.capstone.offerbank;

import android.os.Environment;

/**
 * Created by apple on 02/08/15.
 */
class ConstantsCls {
    /** Tag used for debugging/logging */
    static final String TAG ;

    /** The column that used to be used for the HTTP method of the request */
    static final String RETRY_AFTER_X_REDIRECT_COUNT ;

    /** The column that used to be used for the magic OTA update filename */
    static final String OTA_UPDATE ;

    /** The column that used to be used to reject system filetypes */
    static final String NO_SYSTEM_FILES ;

    /** The column that is used for the downloads's ETag */
    static final String ETAG ;

    /** The column that is used for the initiating app's UID */
    static final String UID ;

    /** The column that is used to remember whether the media scanner was invoked */
    static final String MEDIA_SCANNED ;

    /** The intent that gets sent when the service must wake up for a retry */
    static final String ACTION_RETRY ;

    /** the intent that gets sent when clicking a successful download */
    static final String ACTION_OPEN ;

    /** the intent that gets sent when clicking an incomplete/failed download  */
    public static final String ACTION_LIST ;

    /** the intent that gets sent when deleting the notification of a completed download */
    static final String ACTION_HIDE ;

    /** The default base name for downloaded files if we can't get one at the HTTP level */
    static final String DEFAULT_DL_FILENAME ;

    /** The default extension for html files if we can't get one at the HTTP level */
    public static final String DEFAULT_DL_HTML_EXTENSION ;

    /** The default extension for text files if we can't get one at the HTTP level */
    static final String DEFAULT_DL_TEXT_EXTENSION ;

    /** The default extension for binary files if we can't get one at the HTTP level */
    static final String DEFAULT_DL_BINARY_EXTENSION ;

    static final String PROVIDER_PACKAGE_NAME ;

    /**
     * When a number has to be appended to the filename, this string is used to separate the
     * base filename from the sequence number
     */
    static final String FILENAME_SEQUENCE_SEPARATOR ;

    /** Where we store downloaded files on the external storage */
    static final String DEFAULT_DL_SUBDIR ;

    /** A magic filename that is allowed to exist within the system cache */
    static final String RECOVERY_DIRECTORY ;

    /** Holds the action name for download resume on reboot. */
    public static final String ACTION_RESUME_DOWNLOAD;

    static{
        /** Tag used for debugging/logging */
        TAG = "DownloadManager";

        /** The column that used to be used for the HTTP method of the request */
        RETRY_AFTER_X_REDIRECT_COUNT = "method";

        /** The column that used to be used for the magic OTA update filename */
        OTA_UPDATE = "otaupdate";

        /** The column that used to be used to reject system filetypes */
        NO_SYSTEM_FILES = "no_system";

        /** The column that is used for the downloads's ETag */
        ETAG = "etag";

        /** The column that is used for the initiating app's UID */
        UID = "uid";

        /** The column that is used to remember whether the media scanner was invoked */
        MEDIA_SCANNED = "scanned";

        /** The intent that gets sent when the service must wake up for a retry */
        ACTION_RETRY = "com.bank.appstore.action.DOWNLOAD_WAKEUP";

        /** the intent that gets sent when clicking a successful download */
        ACTION_OPEN = "com.bank.appstore.action.DOWNLOAD_OPEN";

        /** the intent that gets sent when clicking an incomplete/failed download  */
        ACTION_LIST = "com.bank.appstore.action.DOWNLOAD_LIST";

        /** the intent that gets sent when deleting the notification of a completed download */
        ACTION_HIDE = "com.bank.appstore.action.DOWNLOAD_HIDE";

        /** The default base name for downloaded files if we can't get one at the HTTP level */
        DEFAULT_DL_FILENAME = "downloadfile";

        /** The default extension for html files if we can't get one at the HTTP level */
        DEFAULT_DL_HTML_EXTENSION = ".html";

        /** The default extension for text files if we can't get one at the HTTP level */
        DEFAULT_DL_TEXT_EXTENSION = ".txt";

        /** The default extension for binary files if we can't get one at the HTTP level */
        DEFAULT_DL_BINARY_EXTENSION = ".bin";

        PROVIDER_PACKAGE_NAME = "com.android.providers.downloads";

        /**
         * When a number has to be appended to the filename, this string is used to separate the
         * base filename from the sequence number
         */
        FILENAME_SEQUENCE_SEPARATOR = "-";

        /** Where we store downloaded files on the external storage */
        DEFAULT_DL_SUBDIR = "/" + Environment.DIRECTORY_DOWNLOADS;

        /** A magic filename that is allowed to exist within the system cache */
        RECOVERY_DIRECTORY = "recovery";

        /** Holds the action name for download resume. */
        ACTION_RESUME_DOWNLOAD = "com.bank.action.download.resume";
    }

}
