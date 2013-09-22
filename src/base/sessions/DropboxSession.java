package base.sessions;

import base.*;
import com.dropbox.core.*;

import java.awt.*;
import java.io.*;
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
    private AccountInfo accountInfo;
    private UFile directoryTree;    // Root node of the tree representing the directory structure

    public DropboxSession() {
        appInfo = new DbxAppInfo( APP_KEY, APP_SECRET);
        accountInfo = new AccountInfo();

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
            accountInfo.totalSize = client.getAccountInfo().quota.total;
            accountInfo.usedSize = client.getAccountInfo().quota.normal;
            accountInfo.username = client.getAccountInfo().displayName;
        } catch( DbxException e ) {
            throw new UDException( "Unable to get account info!", e );
        }
        accountInfo.sessionType = this.sessionType;

        return true;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    @Override
    public UFile getFileList() throws UDException {
        if( directoryTree != null ) {
            return directoryTree;
        }

        ArrayList<UFile> returnList = new ArrayList<UFile>();
        DbxEntry.WithChildren files;

        UFile root = new UFile();
        root.setParent( null );
        root.setName( "/" );
        root.isFolder( true );
        root.setId( null );
        root.setOrigin( accountInfo.username + "-" + accountInfo.sessionType );

        try {
            files = client.getMetadataWithChildren( "/" );
        } catch( DbxException e ) {
            throw new UDException( "Unable to get file list metadata!", e );
        }

        for( DbxEntry file : files.children ) {
            UFile tempFile= new UFile();

            if( file.isFile() ) {
                tempFile.isFolder( false );
                tempFile.setName( file.name );
                tempFile.setOrigin( accountInfo.username + "-" + accountInfo.sessionType );
                tempFile.setParent( root );
                tempFile.setId( file.name );
            } else if( file.isFolder() ) {
                UFile folder = new UFile();
                folder.setParent( root );
                folder.setName(file.name);
                folder.isFolder(true);
                folder.setId(file.name);
                folder.setOrigin(accountInfo.username + "-" + accountInfo.sessionType);

                try {
                    tempFile = addChildren( folder, client.getMetadataWithChildren( file.path ) );
                } catch( DbxException e ) {
                    throw new UDException( "Unable to get file list metadata!", e );
                }
            }

            root.addChild( tempFile );
        }

        directoryTree = root;
        return root;
    }

    private UFile addChildren( UFile root, DbxEntry.WithChildren folder ) throws UDException {
        ArrayList<UFile> returnList = new ArrayList<UFile>();

        for( DbxEntry file : folder.children ) {
            UFile tempFile= new UFile();

            if( file.isFile() ) {
                tempFile.isFolder( false );
                tempFile.setName( file.name );
                tempFile.setOrigin(accountInfo.username + "-" + accountInfo.sessionType);
                tempFile.setParent(root);
                tempFile.setId(file.name);
            } else if( file.isFolder() ) {
                UFile nextFolder = new UFile();
                nextFolder.setParent( root );
                nextFolder.setName( file.name );
                nextFolder.isFolder( true );
                nextFolder.setId( file.name );
                nextFolder.setOrigin( accountInfo.username + "-" + accountInfo.sessionType );

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
    public List<UFile> searchFiles(String searchString) throws UDException {
        ArrayList<UFile> returnList = new ArrayList<UFile>();
        UFile root = getFileList();
        getMatches( root, returnList, searchString );
        return returnList;
    }

    private void getMatches( UFile root, List<UFile> matches, String searchString ) {
        for(UFile file : root.getChildren() ) {
            if( file.getName().contains(searchString)) {
                matches.add( file );
            }

            if( file.isFolder() ) {
                getMatches(file, matches, searchString);
            }
        }
    }

    @Override
    public UFile upload(String filename) throws UDException {
        File inputFile = new File(filename);
        FileInputStream inputStream;
        UFile returnFile = new UFile();

        try {
            inputStream = new FileInputStream(inputFile);

            DbxEntry.File uploadedFile = this.client.uploadFile( "/" + filename, DbxWriteMode.add(), inputFile.length(), inputStream );

            returnFile.setName( uploadedFile.name );
            returnFile.setOrigin( accountInfo.username + "-" + accountInfo.sessionType );
            returnFile.setId( uploadedFile.name );
            returnFile.isFolder( false );

            inputStream.close();
        } catch( FileNotFoundException e ) {
            throw new UDException( "File '" + filename + "' not found!", e );
        } catch( DbxException e ) {
            throw new UDException( "Upload to Dropbox failed!", e );
        } catch( IOException e ) {
            throw new UDException( "Unable to read file!", e );
        }

        return returnFile;
    }

    @Override
    public UFile download(String fileID ) throws UDException {
        FileOutputStream outputStream;
        UFile returnFile = new UFile();

        try {
            outputStream = new FileOutputStream(fileID);

            DbxEntry.File downloadedFile = client.getFile( "/" + fileID, null, outputStream);

            returnFile.setName( downloadedFile.name );
            returnFile.setOrigin( accountInfo.username + "-" + accountInfo.sessionType );
            returnFile.setId( downloadedFile.name );
            returnFile.isFolder( false );

            outputStream.close();
        } catch( FileNotFoundException e ) {
            throw new UDException( "File '" + fileID + "' not found!", e );
        } catch( DbxException e ) {
            throw new UDException( "Upload to Dropbox failed!", e );
        } catch( IOException e ) {
            throw new UDException( "Unable to write file!", e );
        }

        return returnFile;
    }

    @Override
    public String getSessionType() {
        return sessionType;
    }
}