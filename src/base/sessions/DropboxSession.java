package base.sessions;

import base.*;
import com.dropbox.core.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dropbox session implementation
 */
public class DropboxSession implements UDSession {
    private static final String APP_KEY = "a1boy1ymao44h8q";
    private static final String APP_SECRET = "yfbvljacppmqx6t";
    private static final String sessionType = "Dropbox";

    private DbxAppInfo appInfo;
    private DbxClient client;
    private AccountInfo accounInfo;

    public DropboxSession() {
        appInfo = new DbxAppInfo( APPCONSTANTS.Dropbox.APP_KEY, APPCONSTANTS.Dropbox.APP_SECRET);
        accounInfo = new AccountInfo();

    }

    @Override
    public boolean authenticate(String userID) throws UDException {


        DbxRequestConfig config = new DbxRequestConfig(
                "UnityDrive/1.0", Locale.getDefault().toString());
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        String code;
        DbxAuthFinish authFinish;
        try {
            Desktop.getDesktop().browse( new URI( webAuth.start() ) );

        } catch( URISyntaxException e ) {
            throw new UDException( "Unable to browse to authentication URL", e );
        } catch( IOException e ) {
            throw new UDException( "Unable to open web browser", e );
        }

        try {
            code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
        } catch( IOException e ) {
            throw new UDException( "Unable to read input stream", e );
        }

        try {
            authFinish = webAuth.finish(code);
        } catch( DbxException e ) {
            throw new UDAuthenticationException( "Authentication failed!", e );
        }

        client = new DbxClient(config, authFinish.accessToken);

        try {
            accounInfo.totalSize = client.getAccountInfo().quota.total;
            accounInfo.usedSize = client.getAccountInfo().quota.normal;
            accounInfo.username = client.getAccountInfo().displayName;
        } catch( DbxException e ) {
            throw new UDException( "Unable to get account info!", e );
        }
        accounInfo.sessionType = this.sessionType;

        return true;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return accounInfo;
    }

    @Override
    public UFile getFileList() throws UDException {
        ArrayList<UFile> returnList = new ArrayList<UFile>();
        DbxEntry.WithChildren files;

        UFile root = new UFile();
        root.setParent( null );
        root.setName( "/" );
        root.isFolder( true );
        root.setId( null );
        root.setOrigin( accounInfo.username + "-" + accounInfo.sessionType );

        try {
            files = client.getMetadataWithChildren( "/" );
        } catch( DbxException e ) {
            throw new UDException( "Unable to get file list metadata!", e );
        }

        for( DbxEntry file : files.children ) {
            UFile tempFile = new UFile();

            if( file.isFile() ) {
                tempFile.isFolder( false );
                tempFile.setName( file.name );
                tempFile.setOrigin( accounInfo.username + "-" + accounInfo.sessionType );
                tempFile.setParent( root );
                tempFile.setId( file.name );
            } else if( file.isFolder() ) {
                UFile folder = new UFile();
                folder.setParent( root );
                folder.setName(file.name);
                folder.isFolder(true);
                folder.setId(file.name);
                folder.setOrigin(accounInfo.username + "-" + accounInfo.sessionType);

                try {
                    tempFile = addChildren( folder, client.getMetadataWithChildren( file.path ) );
                } catch( DbxException e ) {
                    throw new UDException( "Unable to get file list metadata!", e );
                }
            }

            root.addChild( tempFile );
        }

        return root;
    }

    private UFile addChildren( UFile root, DbxEntry.WithChildren folder ) throws UDException {
        ArrayList<UFile> returnList = new ArrayList<UFile>();

        for( DbxEntry file : folder.children ) {
            UFile tempFile= new UFile();

            if( file.isFile() ) {
                tempFile.isFolder( false );
                tempFile.setName( file.name );
                tempFile.setOrigin(accounInfo.username + "-" + accounInfo.sessionType);
                tempFile.setParent(root);
                tempFile.setId(file.name);
            } else if( file.isFolder() ) {
                UFile nextFolder = new UFile();
                nextFolder.setParent( root );
                nextFolder.setName( file.name );
                nextFolder.isFolder( true );
                nextFolder.setId( file.name );
                nextFolder.setOrigin( accounInfo.username + "-" + accounInfo.sessionType );

                try {
                    tempFile =  addChildren( nextFolder, client.getMetadataWithChildren( file.path ) );
                } catch( DbxException e ) {
                    throw new UDException( "Unable to get file list metadata!", e );
                }
            }

            root.addChild( tempFile );
        }

        return root;
    }

    @Override
    public List<UFile> searchFiles(String searchString) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean upload(String filename) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean download(String fileID) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSessionType() {
        return sessionType;
    }
}