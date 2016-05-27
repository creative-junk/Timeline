package crysoftdynamics.timeline;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import crysoftdynamics.timeline.util.GalleryAsyncLoader;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<GalleryItem>> {


    protected OnFragmentInteractionListener mListener;
    protected AbsListView mListView;
    protected GalleryAdapter mAdapter;
    protected ArrayList<GalleryItem> mGalleryListItem;
    protected TextView mEmptyTextView;
    protected ProgressDialog mLoadingProgressDialog;
    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    public GalleryFragment() {
        // Required empty public constructor
        super();
    }

    /**
     * Static Factory Method
     *
     * @param sectionNumber
     * @return
     */
    public static GalleryFragment newInstance(int sectionNumber) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Create an empty loader and pre-initialize the photo list items as an empty list
        Context context = getActivity().getBaseContext();

        //Set up an empty mAdapter
        mGalleryListItem = new ArrayList<GalleryItem>();
        mAdapter = new GalleryAdapter(context,
                R.layout.gallery_item, mGalleryListItem, false);
        //Prepare the loader. Either re-connect with an existing one or start a new one
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        //Set the mAdapter
        mListView = (AbsListView) v.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        mEmptyTextView = (TextView) v.findViewById(R.id.empty);

        //Show the empty text/ message
        resolveEmptyText();

        //Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return v;
    }

    /**
     * This is used to show a generic empty text warning
     */
    protected void resolveEmptyText() {
        if (mAdapter.isEmpty()) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            //show a progress dialog
            mLoadingProgressDialog = new ProgressDialog(getActivity());
            mLoadingProgressDialog.setMessage("Loading Photos...");
            mLoadingProgressDialog.setCancelable(true);
            mLoadingProgressDialog.show();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        cancelProgressDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelProgressDialog();
    }


    /**
     * Loader Handlers for loading the photos in the background
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<List<GalleryItem>> onCreateLoader(int id, Bundle args) {
        //This method is called when a new Loader needs to be created.
        return new GalleryAsyncLoader(getActivity());
    }

    /**
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished
            (Loader<List<GalleryItem>> loader, List<GalleryItem> data) {
        mGalleryListItem.clear();

        for (int i=0; i < data.size(); i++){
            GalleryItem item = data.get(i);
            mGalleryListItem.add(item);
        }

        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
        cancelProgressDialog();

    }

    @Override
    public void onLoaderReset(Loader<List<GalleryItem>> loader) {
        //Clear the data in the mAdapter
        mGalleryListItem.clear();
        mAdapter.notifyDataSetChanged();
        resolveEmptyText();
        cancelProgressDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            GalleryItem galleryItem = (GalleryItem) this.mAdapter.getItem(position);
            String imagePath = galleryItem.getThumbnailUri().getPath();
            String fullImagePath = galleryItem.getFullImageUri().getPath();
            mListener.onFragmentInteraction(SnapActivity.SELECT_PHOTO_ACTION);

            Intent i=new Intent(getContext(), SaveActivity.class);
            i.putExtra("thumbnailPath",imagePath);
            i.putExtra("fullimagePath",fullImagePath);
            startActivity(i);

        }
    }

    /*
    * Used when the back button is hit to reset the mFagment UI state
     */
    protected void resetFragmentState() {
        //Clear the view
        getActivity().invalidateOptionsMenu();
        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     * The default for this Fragment is a TextView that is shown when there are no photos. This method can be called to supply the      * text of that Text View
     */
    public void setmEmptyText() {
        mEmptyTextView.setText("No Photos!");
    }

    private void cancelProgressDialog(){
        if (mLoadingProgressDialog != null){
            if (mLoadingProgressDialog.isShowing()){
                mLoadingProgressDialog.cancel();
            }
        }
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
        public void onFragmentInteraction(String id);
        public void onFragmentInteraction(int actionId);
    }


}
