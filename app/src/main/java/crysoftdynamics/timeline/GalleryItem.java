package crysoftdynamics.timeline;

import android.net.Uri;

/**
 * Created by Maxx on 5/26/2016.
 *
 * This Class represents a single Gallery Item
 *
 */
public class GalleryItem {
    private Uri thumbnailUri;
    private Uri fullImageUri;

    public GalleryItem(Uri thumbnailUri, Uri fullImageUri){
        this.thumbnailUri=thumbnailUri;
        this.fullImageUri=fullImageUri;
    }

    //Let's setup a few Setters and Getters
    public Uri getThumbnailUri(){
        return thumbnailUri;
    }

    public Uri getFullImageUri() {
        return fullImageUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public void setFullImageUri(Uri fullImageUri) {
        this.fullImageUri = fullImageUri;
    }
}
