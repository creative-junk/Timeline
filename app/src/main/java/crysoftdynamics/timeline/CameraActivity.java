package crysoftdynamics.timeline;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by Maxx on 5/27/2016.
 * This class makes available File URI which on some phones get lost on rsume and cause crashes
 *
 *
 */
public class CameraActivity extends ActionBarActivity {
    //storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    //Required for Camera operations in order to save image file on resume
    private String mCurrentPhotoPath =null;

}
