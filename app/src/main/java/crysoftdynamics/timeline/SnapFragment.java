package crysoftdynamics.timeline;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SnapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SnapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnapFragment extends Fragment {

    //The Native Camera
    private Camera mCamera;
    //View to display Camera Output
    private CameraPreview mPreview;
    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    private View mCameraView;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SnapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SnapFragment newInstance(int sectionNumber) {
        SnapFragment fragment = new SnapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SnapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_snap, container, false);
        //Create a preview and set it as the content of our activity
        boolean opened = safeCameraOpenInView(v);
        if (opened == false) {
            Log.i("Camera Guide", "Error, Camera Failed to Open");
            return v;
        }
        //Handle the capture button
        ImageButton captureButton = (ImageButton) v.findViewById(R.id.new_snap_button);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
        return v;
    }

    /**
     * Safe way to Open the Camera
     *
     * @param view
     * @return
     */
    private boolean safeCameraOpenInView(View view) {
        boolean cameraIsOpen = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        mCameraView = view;
        cameraIsOpen = (mCamera != null);

        if (cameraIsOpen) {
            mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera, view);
            FrameLayout preview = (FrameLayout) view.findViewById(R.id.snap_surface_view);
            preview.addView(mPreview);
        }
        return cameraIsOpen;
    }


    /**
     * Safe way to get Camera Instance
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * Clear any pre existing previews
     */
    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mPreview != null) {
            mPreview.destroyDrawingCache();
            mPreview.mCamera = null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Class for the Surface on which the Camera Projects it's results
     */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

        //Surface Holder
        private SurfaceHolder mHolder;
        //The Camera
        private Camera mCamera;
        //The Parent Context
        private Context mContext;
        //Camera Preview Size
        private Camera.Size mPreviewSize;
        //List of supported Preview sizes
        private List<Camera.Size> mSupportedPreviewSizes;
        //Flash Modes supported by this Camera
        private List<String> mSupportedFlashModes;
        //View holding this camera
        private View mCameraView;

        public CameraPreview(Context context, Camera camera, View cameraView) {
            super(context);

            //Capture the context
            mCameraView = cameraView;
            mContext = context;
            setCamera(camera);

            //Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setKeepScreenOn(true);
            //Deprecated but needed for < 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        /**
         * Start The Preview
         */
        public void startCameraPreview() {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * This is in response to errors with Camera Sizes on some devices where the Preview refuses to start
         *
         * @param camera
         */
        private void setCamera(Camera camera) {
            mCamera = camera;
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();

            //Set the Camera to Auto Flash Mode
            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
            }
            requestLayout();
        }

        /**
         * Handover the SurfaceView to the Camera
         *
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Monitor and react to changes in the surface
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //If there is no surface, why are we here? Just go back
            if (mHolder.getSurface() == null) {
                return;
            }
            //Before any changes like rotaion, resize etc are made make sure we stop the preview first
            try {
                Camera.Parameters parameters = mCamera.getParameters();

                //Change the auto-focus mode to continuous
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                //preview size must exist
                if (mPreviewSize != null) {
                    Camera.Size previewSize = mPreviewSize;
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                }

                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * get the layout measurements to help the method above for when the size causes the preview not to start
         *
         * @param widthMeasureSpec
         * @param heightMeasureSpec
         */
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }

        /**
         * Update the Layout based on rotation and orientation
         *
         * @param changed
         * @param left
         * @param top
         * @param right
         * @param bottom
         */

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (changed) {
                final int width = right - left;
                final int height = bottom - top;


                int previewWidth = width;
                int previewHeight = height;

                if (mPreviewSize != null) {
                    Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                    switch (display.getRotation()) {
                        case Surface.ROTATION_0:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            mCamera.setDisplayOrientation(90);
                            break;
                        case Surface.ROTATION_90:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            break;
                        case Surface.ROTATION_180:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            break;
                        case Surface.ROTATION_270:
                            previewWidth = mPreviewSize.width;
                            previewHeight = mPreviewSize.height;
                            mCamera.setDisplayOrientation(180);
                            break;

                    }
                }
                final int scaledChildHeight = previewHeight * width / previewWidth;
                mCameraView.layout(0, height - scaledChildHeight, width, height);

            }
            super.onLayout(changed, left, top, right, bottom);
        }

        /**
         * Destroy the Preview
         *
         * @param holder
         */

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }
    }

    /**
     * @param mSupportedPreviewSizes
     * @param width
     * @param height
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> mSupportedPreviewSizes, int width, int height) {
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        //Try to find a size match for our screen
        for (Camera.Size size : mSupportedPreviewSizes) {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                optimalSize = size;
            }
        }
        //If no suitable size is found,ignore this requirement
        if (optimalSize == null) {
            //Ignore
        }

        return optimalSize;
    }

    /**
     * Custom Picture callback for handling the picture and saving it to file
     */
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputmediaFile();
            if (pictureFile == null) {
                Toast.makeText(getContext(), "Image Retrieval Failed", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(data);
                fileOutputStream.close();

                //restart the Camera Preview
                safeCameraOpenInView(mCameraView);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * @return
     */
    private File getOutputmediaFile() {
        // This location works best since we want the created images to be shared
        // between applications and persist after our app has been uninstalled
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Snapshot");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i("Camera", "Required media storage does not exist");
                return null;
            }
        }

        //Create a Media File Name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");

        Log.i("Camera", "Picture Takes Successfully"+ mediaFile);

        return mediaFile;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
