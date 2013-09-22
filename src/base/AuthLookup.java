package base;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Used for storage and lookup of authentication tokens for each supported service
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

    /**
     * Add a Google auth token
     *
     * @param userID        User ID to add token for
     * @param refreshToken  Token to add
     */
    public void addGoogleToken( String userID, String refreshToken ) {
        GoogleDriveStore.put( userID, refreshToken );
    }

    /**
     * Add a Dropbox auth token
     *
     * @param userID        User ID to add token for
     * @param refreshToken  Token to add
     */
    public void addDropboxToken( String userID, String refreshToken ) {
        DropboxStore.put( userID, refreshToken );
    }

    /**
     * Add a Box auth token
     *
     * @param userID        User ID to add token for
     * @param refreshToken  Token to add
     */
    public void addBox( String userID, String refreshToken ) {
        BoxStore.put( userID, refreshToken );
    }

    /**
     * Get stored Google auth token
     *
     * @param userID    User ID to get token for
     * @return          Auth token
     */
    public String getGoogleToken( String userID ) {
        return GoogleDriveStore.get( userID );
    }

    /**
     * Get stored Dropbox auth token
     *
     * @param userID    User ID to get token for
     * @return          Auth token
     */
    public String getDropboxToken( String userID) {
        return DropboxStore.get( userID );
    }

    /**
     * Get stored Box auth token
     *
     * @param userID    User ID to get token for
     * @return          Auth token
     */
    public String getBoxToken( String userID ) {
        return BoxStore.get( userID );
    }
}
