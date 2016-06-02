package crysoftdynamics.timeline;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import crysoftdynamics.timeline.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SnapActivity extends AppCompatActivity implements GalleryFragment.OnFragmentInteractionListener,SnapFragment.OnFragmentInteractionListener {
    //For use if we ever decide to split the Caption and the Photo into two fragments
    private Snap snap;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    /**
     * Actions
     */
    public static final int SELECT_PHOTO_ACTION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Instantiate our snap variable
        //snap = new Snap();
        //Request a window with no Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Get the window and set it to fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_snap);

        /*FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);

        //Add the new snap fragment to the stack
        if (fragment == null){
            fragment = new NewSnapFragment();
            fragmentManager.beginTransaction().add(R.id.fragmentContainer,fragment).commit();
        }*/
        //Set up View Pager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        //Set up the Tabs
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Add the Fragments
        adapter.addFragment(new GalleryFragment(),"GALLERY");
        adapter.addFragment(new NewSnapFragment(), "PHOTO");

       // adapter.addFragment(new NewVideoFragment(), "VIDEO");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction(int actionId) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

   /* public Snap getCurrentSnap(){
        return snap;
    }*/
    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /** Create a file for saving an image or video */
    public static File getOutputMediaFile(int type){
        //To be on the safe side, we should make sure the SD Card is mounted
        //by using Environment.getExternalStorageState() before proceeding: we'll think about that in another release

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Snapshot");

        //This location works best so we allow the created images to be shared between
        // other applications and persist after our app has been uninstalled

        //Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Snapshot", "failed to create directory");
                return null;
            }
        }

        //Create the media file name
        String timeStamp = new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        }else if(type == 2){
            mediaFile =  new File((mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4"));
        }else{
            return null;
        }
        return mediaFile;
    }



}
