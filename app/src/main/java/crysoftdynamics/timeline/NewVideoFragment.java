package crysoftdynamics.timeline;


import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewVideoFragment extends Fragment {
    public static final String TAG = "VideoFragment";

    public static final int MEDIA_TYPE_IMAGE =1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Camera camera;
    private SurfaceView mSurfaceView;
    private ImageButton videoButton;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;


    public NewVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_new_video, container, false);
        videoButton = (ImageButton) v.findViewById(R.id.camera_video_button);

        if (camera == null){
            try{
                camera = Camera.open();
                videoButton.setEnabled(true);
            }catch(Exception e){
                Log.e(TAG, "No Camera with exception: " + e.getMessage());
                videoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No Camera Detected", Toast.LENGTH_LONG).show();
            }
        }
        mSurfaceView = (SurfaceView) v.findViewById(R.id.video_surface_view);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //nothing to do
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //nothing here either
            }
        });
        camera.startPreview();
        videoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            //Stop and Release Camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            camera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                            setCaptureButtonText("Capture");
                            isRecording = false;
                        }else{
                            //Initialize Video Camera
                            if (prepareVideoRecorder()){
                                //Camera is available and unlocked, MediaRecorder is Prepared, lets start Recording
                                mMediaRecorder.start();

                                //Inform the user we are Live
                                setCaptureButtonText("Stop");
                                isRecording=true;
                            }else{
                                //Something Went Wrong
                                releaseMediaRecorder();
                                //Tell the user
                                setCaptureButtonText("Failed");
                            }
                        }
                    }
                }
        );




        return v;
    }
    private boolean prepareVideoRecorder(){

        camera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediaRecorder.setCamera(camera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(((SnapActivity) getActivity()).getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    private void setCaptureButtonText(String text){

    }
    @Override
    public void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }


}
