import base.AuthLookup;
import base.UDException;
import base.UDSession;
import base.UFile;
import base.sessions.BoxSession;
import base.sessions.DropboxSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Main program
 */
public class UnityDrive {
    public static AuthLookup tokenStore;
    public static ArrayList<UDSession> sessions;

    public static void main( String[] args ) {
        tokenStore = new AuthLookup();
        sessions = new ArrayList<UDSession>();
    }

    /**
     * Adds an account to the app for the given service and userID
     *
     * @param accountType   Type of account to add (ie. service you're connecting to)
     * @param userID        User ID to connect with
     * @throws UDException
     */
    public static void addAccount( String accountType, String userID ) throws UDException {
        switch( accountType ) {
            case "Dropbox":
                sessions.add( new DropboxSession() );
                try {
                    tokenStore.addDropboxToken( userID, sessions.get( sessions.size() - 1 ).authenticate( userID ) );
                } catch( UDException e ) {
                    throw new UDException( "Failed to create Dropbox session!", e );
                }
                break;
            case "Google Drive":
                //sessions.add( new GDSession() );
                try {
                    tokenStore.addGoogleToken( userID, sessions.get( sessions.size() - 1 ).authenticate( userID ) );
                } catch( UDException e ) {
                    throw new UDException( "Failed to create Google Drive session!", e );
                }
                break;
            case "Box":
                sessions.add( new BoxSession() );
                try {
                    tokenStore.addBox( userID,  sessions.get( sessions.size() - 1 ).authenticate( userID ) );
                } catch( UDException e ) {
                    throw new UDException( "Failed to create Box session!", e );
                }
                break;
        }
    }

    /**
     * Get the full file list for all of your connected services
     *
     * @return  List of files
     */
    public static List<UFile> getAggregateList() throws UDException {
        ArrayList<UFile> returnList = new ArrayList<UFile>();
        for( UDSession session : sessions ) {
            try {
                getFiles( session.getFileList(), returnList );
            } catch( UDException e ) {
                throw new UDException( "Failed to get file list!", e );
            }
        }

        return returnList;
    }

    /**
     * Private method to recurse into all folders and build file list
     *
     * @param root  Root file node of current subtree
     * @param list  List of files being constructed
     */
    private static void getFiles( UFile root, List<UFile> list ) {
        for(UFile file : root.getChildren() ) {
            if( !file.isFolder() ) {
                list.add( file );
            } else {
                getFiles( file, list );
            }
        }
    }
}
