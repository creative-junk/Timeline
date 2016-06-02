package crysoftdynamics.timeline;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NearMe.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NearMe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearMe extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /*
   * Define a request code to send to Google Play services This code is returned in
   * Activity.onActivityResult
   */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 900;
    /**
     * Constants for location reminder updates
     */
    private static final int MILLISECONDS_PER_SECOND = 1000;
    //Update Interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    //Fast Interval Ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;
    //Update interval in milliseconds
    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    //Fast update interval in Milliseconds for when the app is visible
    private static final int FAST_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    /**
     * Constants for handling location results
     */
    //Feet to Meters
    private static final float METERS_PER_FEET = 0.3048f;

    //KM to Meters
    private static final int METERS_PER_KILOMETER = 1000;

    //Initial offest for calcualting map bounds
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;

    //Maximum Results returned from a parse query
    private static final int MAX_SEARCH_RESULTS = 20;

    //Maximum search radius for map in Kilometers for Friends Near Me
    private static final int MAX_NEARME_SEARCH_DISTANCE = 10;

    private OnFragmentInteractionListener mListener;

    /**
     * other useful variables
     */
    //Map fragment
    private SupportMapFragment mapFragment;

    //Circle around the map
    private Circle mapCircle;

    //Fields for the map radius in feet
   // private float radius = Application.getSearchDistance();
    private float lastRadius;

    //fields for helping process map and location changes
    private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();
    private int mostRecentMapUpdate;
    private boolean hasSetUpInitialLocation;
    private String selectedUserObjectId;
    private Location lastLocation;
    private Location currentLocation;

    //Request to connect to Location Services
    private LocationRequest locationRequest;

    //Store for the current instantiation of the location client in this object
    private GoogleApiClient locationClient;

    //Adapter for the Parse Query
   // private ParseQueryAdapter<UserLocation> userQueryAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NearMe.
     */
    // TODO: Rename and change types and number of parameters
    public static NearMe newInstance(String param1, String param2) {
        NearMe fragment = new NearMe();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NearMe() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nearme, container, false);

        //lastRadius = radius;
        getActivity().setContentView(R.layout.fragment_nearme);

        //Create a new Global location parameters object
        locationRequest = LocationRequest.create();
        //Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        //Use high accuracy
        locationRequest.setFastestInterval(locationRequest.PRIORITY_HIGH_ACCURACY);
        //Set Interval ceiling to one second
        locationRequest.setFastestInterval(FAST_CEILING_IN_MILLISECONDS);
        //Create a new location Client, using the enclosing class to handle callbacks
        locationClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //Setup the Customized Query
        ParseQuery<ParseUser> query =
                ParseUser.getQuery();
        query.whereWithinKilometers("location", new ParseGeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude()),MAX_NEARME_SEARCH_DISTANCE);
        query.setLimit(MAX_SEARCH_RESULTS);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e==null){
                    Log.i("Results","Found" + objects.size() + "Users Near You");
                    for (ParseUser users : objects)
                        Log.i("Results:","User Found "+ users.getUsername());
                }else{
                    Log.i("Results", "No Users Near You");
                }
            }
        });
        return v;
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

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
