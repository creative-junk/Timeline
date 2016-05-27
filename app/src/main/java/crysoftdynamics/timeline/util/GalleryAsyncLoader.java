package crysoftdynamics.timeline.util;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

import crysoftdynamics.timeline.GalleryItem;

/**
 * Created by Maxx on 5/26/2016.
 */
public class GalleryAsyncLoader extends AsyncTaskLoader<List<GalleryItem>> {

    //Persistent list of photo list items
    private List<GalleryItem> mGalleryListItems;

    /**
     * Default Empty Constructor
     *
     * @param context
     */
    public GalleryAsyncLoader(Context context) {
        super(context);
    }

    /**
     * Same as in MediaProvider.computeBucketValues
     *
     * @return
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    /**
     * Load the image items in the background
     * <p/>
     * This methid does the bulk of the work. It works ina  background thread and generates new set of data for the loader
     *
     * @return
     */

    @Override
    public List<GalleryItem> loadInBackground() {
        final Context context = getContext();
        List<GalleryItem> images = GalleryImageProvider.getAlbumThumbnails(context);
        return images;
    }

    /**
     * This method is called when there is new data to be delivered to the client. The super Class takes care of delivering it, here     *  we just a bit more logic to it
     */
    @Override
    public void deliverResult(List<GalleryItem> newGalleryListItems) {
        if (isReset()) {
            //Async came while the loader was stopped.The result is not needed
            if (newGalleryListItems != null) {
                onReleaseResources(newGalleryListItems);
            }
        }
        List<GalleryItem> oldItems = mGalleryListItems;
        mGalleryListItems = newGalleryListItems;

        if (isStarted()) {
            //The loader is started, let's deliver the results
            super.deliverResult(newGalleryListItems);
        }
        //At this point w are just cleaning up so let's release the resourses since we've done our job
        if (oldItems != null) {
            onReleaseResources(oldItems);
        }
    }

    /**
     * Handles the request to start the Loader
     */

    @Override
    protected void onStartLoading() {
        if (mGalleryListItems != null) {
            //we have a result so deliver it
            deliverResult(mGalleryListItems);
        } else {
            //data seems to have changed since our last load so lets force a new one
            forceLoad();
        }
    }

    /**
     * Handles the request to stop the Loader
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<GalleryItem> galleryListItems) {
        super.onCanceled(galleryListItems);
        //Just release resources, we dont need them anymore
        onReleaseResources(galleryListItems);
    }

    @Override
    protected void onReset() {
        super.onReset();
        //Ensure the Loader is stopped
        onStopLoading();

        //Release Resources as usual
        if (mGalleryListItems != null){
            onReleaseResources(mGalleryListItems);
            mGalleryListItems = null;
        }
    }
    /**
     * This function releases resources associated with a loaded active data set
     */
    protected void onReleaseResources(List<GalleryItem> galleryItems){
        //Nothing to do since we are using a Simple List<
    }
}
