package crysoftdynamics.timeline;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Maxx on 5/19/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs=NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Home homeTab= new Home();
                return homeTab;
            case 1:
                AddFriends addFriends = new AddFriends();
                return addFriends;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
