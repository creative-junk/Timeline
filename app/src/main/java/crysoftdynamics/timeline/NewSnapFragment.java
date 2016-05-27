package crysoftdynamics.timeline;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewSnapFragment extends Fragment {
    public static final String TAG = "CameraFragment";

    public static final int MEDIA_TYPE_IMAGE =1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Camera camera;
    private SurfaceView surfaceView;
    private ParseFile snapFile;
    private ImageButton snapButton;

    public NewSnapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_snap, container, false);

        snapButton = (ImageButton) v.findViewById(R.id.camera_snap_button);

        if (camera == null){
            try{
                camera = Camera.open();
                snapButton.setEnabled(true);
            }catch(Exception e){
                Log.e(TAG, "No Camera with exception: " + e.getMessage());
                snapButton.setEnabled(false);
                Toast.makeText(getActivity(), "No Camera Detected",Toast.LENGTH_LONG).show();
            }
        }
        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;
                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {

                    }
                }, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        saveScaledSnap(data);

                        File snapFile =((SnapActivity)getActivity()).getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (snapFile == null){
                            Log.d(TAG,"Error Creating Media file, check storage permissions:");
                            return;
                        }
                        try{
                            FileOutputStream fos = new FileOutputStream(snapFile);
                            fos.write(data);
                            fos.close();
                            Uri fileUri=getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            String path = fileUri.getPath();

                            //String thumbnailPath = getThumbnailPath(fileUri);

                            //Log.i("Paths are:", "Image= " +path + "Thumbnail= " + thumbnailPath );
                        } catch (FileNotFoundException e){
                            Log.d(TAG, "File Not Found" + e.getMessage());
                        } catch (IOException e){
                            Log.d(TAG, "Error Accessing File: " + e.getMessage());
                        }
                    }
                });

            }
        });
        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
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
        return v;
    }

    /**
     *
     * get URL from media file
     */
    public Uri getOutputMediaFileUri(int type)
    {
        return Uri.fromFile(((SnapActivity)getActivity()).getOutputMediaFile(type));
    }

    /**
     *
     * get Thumbnail path
     */
    public String getThumbnailPath(Uri uri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        // This method was deprecated in API level 11
        // Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(((SnapActivity)getActivity()), uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        cursor.moveToFirst();
        long imageId = cursor.getLong(column_index);
        //cursor.close();
        String result="";
        cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(((SnapActivity)getActivity()).getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }
        return result;
    }
    /*
    * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
    * they are saved. Let's scale the image now for performance later
    */
    private void saveScaledSnap(byte[] data){

        //Resize photo from camera byte array
        Bitmap snap = BitmapFactory.decodeByteArray(data,0,data.length);
        Bitmap scaledSnap = Bitmap.createScaledBitmap(snap,200,200*snap.getHeight()/snap.getWidth(), false);

        //Override Android default landscape orientation and save potrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedScaledSnap = Bitmap.createBitmap(scaledSnap, 0,0,scaledSnap.getWidth(),scaledSnap.getHeight(),matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledSnap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();
        // TODO: 5/25/2016 At this point we should save the image to device go get the other inputs then come back upload the image to parse after we update the Caption for the Photo
        //Save the scaled image to Parse
        snapFile = new ParseFile("image.jpg", scaledData);
        snapFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Toast.makeText(getActivity(),
                            "Error Uploading: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }else{
                    //addSnapAndContinue(snapFile);

                    Toast.makeText(getActivity(),
                            "Image Saved",
                            Toast.LENGTH_LONG).show();
                }

            }
        });


    }
    // TODO: 5/25/2016 Change this method to be called after all other inputs
    /**
     *
     *
    private void addSnapAndContinue(ParseFile snapFile){
        ((SnapActivity) getActivity()).getCurrentSnap().setSnapFile(snapFile);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("CaptionFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (camera == null){
            try{
                camera = Camera.open();
                snapButton.setEnabled(true);
            }catch(Exception e){
                Log.i(TAG,"No Camera: " + e.getMessage());
                snapButton.setEnabled(false);
                Toast.makeText(getActivity(), "No Camera detected ", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (camera != null){
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }




}
