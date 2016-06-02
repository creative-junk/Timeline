package crysoftdynamics.timeline;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Maxx on 5/25/2016.
 */
@ParseClassName("Snap")

public class Snap extends ParseObject {
    public Snap() {
        //Default Constructor
    }

    public String getCaption() {
        return getString("caption");
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public ParseUser getAuthor() {
        return getParseUser("author");
    }

    public void setAuthor(ParseUser user) {
        put("author", user);
    }

    public ParseFile getSnapFile() {
        return getParseFile("snap");
    }

    public void setSnapFile(ParseFile file) {
        put("snap", file);
    }
}
