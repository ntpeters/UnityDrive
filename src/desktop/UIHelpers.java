package desktop;

import base.AuthLookup;
import base.UDException;
import base.UDSession;
import base.UFile;
import base.sessions.BoxSession;
import base.sessions.DropboxSession;
import base.sessions.GDSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper functions for the UI programs
 */
public class UIHelpers {

    /**
     * Adds an account to the app for the given service and userID
     *
     * @param accountType   Type of account to add (ie. service you're connecting to)
     * @param userID        User ID to connect with
     * @throws base.UDException
     */
    public static void addAccount( String accountType, String userID, List<UDSession> sessions, AuthLookup tokenStore) throws UDException {
        switch( accountType ) {
            case "Dropbox":
                DropboxSession dSession = new DropboxSession();
                sessions.add( dSession );
                try {
                    tokenStore.addDropboxToken( userID, dSession.authenticate(userID) );
                } catch( UDException e ) {
                    throw new UDException( "Failed to create Dropbox session!", e );
                }
                break;
            case "GoogleDrive":
                GDSession gSession = new GDSession();
                sessions.add( gSession );
                try {
                    tokenStore.addGoogleToken( userID, gSession.authenticate(userID) );
                } catch( UDException e ) {
                    throw new UDException( "Failed to create Google Drive session!", e );
                }
                break;
            case "Box":
                BoxSession bSession = new BoxSession();
                sessions.add( bSession );
                try {
                    tokenStore.addBox( userID,  bSession.authenticate( userID ) );
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
    public static List<UFile> getAggregateList( List<UDSession> sessions ) throws UDException {
        ArrayList<UFile> returnList = new ArrayList<>();
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