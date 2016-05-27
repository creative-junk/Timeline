package crysoftdynamics.timeline;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Maxx on 5/25/2016.
 */
public class SnapshotApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /*
		 * As per Parse Documentation, we'll subclass ParseObject for convenience to
		 * create and modify Snap objects
		 */
        ParseObject.registerSubclass(Snap.class);

		/*
		 * Fill in this section with your Parse credentials
		 */
        //Parse.initialize(this, "photochat2vc09", "spq2vc1LVg09");

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                        .applicationId("photochat2vc09")
                        .clientKey("spq2vc1LVg09")
                        .server("http://photochat.herokuapp.com/parse/")
                        .build()
        );
		/*
		 * This app lets an anonymous user create and save photos of meals
		 * they've eaten. An anonymous user is a user that can be created
		 * without a username and password but still has all of the same
		 * capabilities as any other ParseUser.
		 *
		 * After logging out, an anonymous user is abandoned, and its data is no
		 * longer accessible. In your own app, you can convert anonymous users
		 * to regular users so that data persists.
		 *
		 * Learn more about the ParseUser class:
		 * https://www.parse.com/docs/android_guide#users
		 */
        ParseUser.enableAutomaticUser();

		/*
		 * For more information on app security and Parse ACL:
		 * https://www.parse.com/docs/android_guide#security-recommendations
		 */
        ParseACL defaultACL = new ParseACL();

		/*
		 * If you would like all objects to be private by default, remove this
		 * line
		 */
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);


    }


}
