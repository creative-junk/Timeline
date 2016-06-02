package crysoftdynamics.timeline;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Maxx on 5/27/2016.
 * This class makes available File URI which on some phones get lost on resume and cause crashes
 *
 *
 */
public class CameraActivity extends ActionBarActivity {
    //storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    //Required for Camera operations in order to save image file on resume
    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null){
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY,mCurrentPhotoPath);
        }
        if (mCapturedImageURI!= null){
            savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY,mCapturedImageURI.toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)){
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)){
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
    /**
     * Getters & Setters
     */
    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }

    public Uri getCapturedImageURI() {
        return mCapturedImageURI;
    }

    public void setCapturedImageURI(Uri mCapturedImageURI) {
        this.mCapturedImageURI = mCapturedImageURI;
    }
}
