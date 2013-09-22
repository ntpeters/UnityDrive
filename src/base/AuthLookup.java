package base;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 */
public class AuthLookup implements Serializable {
    private HashMap<String, String> GoogleDriveStore;
    private HashMap<String, String> DropboxStore;
    private HashMap<String, String> BoxStore;

    public AuthLookup() {
        GoogleDriveStore    = new HashMap<String, String>();
        DropboxStore        = new HashMap<String, String>();
        BoxStore            = new HashMap<String, String>();
    }

    public void addGoogleToken( String userID, String refreshToken ) {
        GoogleDriveStore.put( userID, refreshToken );
    }

    public void addDropboxToken( String userID, String refreshToken ) {
        DropboxStore.put( userID, refreshToken );
    }

    public void addBox( String userID, String refreshToken ) {
        BoxStore.put( userID, refreshToken );
    }

    public String getGoogleToken( String userID ) {
        return GoogleDriveStore.get( userID );
    }

    public String getDropboxToken( String userID) {
        return DropboxStore.get( userID );
    }

    public String getBoxToken( String userID ) {
        return BoxStore.get( userID );
    }
}
