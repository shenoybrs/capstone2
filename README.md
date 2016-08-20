Design of the project is as below :-

The intent service is used to hit the flickr server for images , store in a simple database through a
content provider and when loader is invoked from the fragment the loader call back hooks the cursor
to the adpater and the images disappear after 15 sec , as the user selects the image the position
is compared to validate the image matches v/s not.

Please copy the below text in your .gradle folder

For example like this C:\Users\santosh.shenoy\.gradle\gradle.properties

RELEASE_STORE_FILE=C:/Users/santosh.shenoy/.android/santosh.keystore
RELEASE_STORE_PASSWORD=android
RELEASE_KEY_ALIAS=androiddebugkey
RELEASE_KEY_PASSWORD=android

DEBUG_STORE_FILE=C:/Users/santosh.shenoy/.android/santosh.keystore
DEBUG_STORE_PASSWORD=android
DEBUG_KEY_ALIAS=androiddebugkey
DEBUG_KEY_PASSWORD=android


copy santosh.keystore from app folder to your .android home folder
for example mine is in
C:\Users\santosh.shenoy\.android

# capstone2
