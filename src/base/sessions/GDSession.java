package base.sessions;

import base.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.awt.Desktop;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.net.URI;

public class GDSession implements UDSession {

    /**
     * Client Id DONT CHANGE
     */
    private static final String CLIENT_ID = "304793868911-vokk592ddao58s9oqm8f6jhj2udm6po5.apps.googleusercontent.com";
    /**
     * Client Super Secret Code DONT CHANGE
     */
    private static final String CLIENT_SECRET = "pK_uV6wVb0M13cXCIgdYP9lQ";
    /**
     * Redirect URI DONT CHANGE
     */
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    /**
     * Google drive service object
     */
    private Drive service;
    /**
     * Credential object containing the user credentials for this session
     */
    private GoogleCredential credential;
    /**
     * Token response received back from the server
     */
    private GoogleTokenResponse response;

    /**
     * Contains information about the user and their storage space
     */
    private AccountInfo info;
    /**
     * The current user's id for this session
     */
    private String username;

    /**
     * Mapping of accepted file types to supported MIME types
     */
    private String[][] mimeTypes = {{"xls","application/vnd.ms-excel"},
        {"xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
        {"xml","text/xml"},
        {"ods","application/vnd.oasis.opendocument.spreadsheet"},
        {"csv","text/plain"},
        {"tmpl","text/plain"},
        {"pdf", "application/pdf"},
        {"php","application/x-httpd-php"},
        {"jpg","image/jpeg"},
        {"png","image/png"},
        {"gif","image/gif"},
        {"bmp","image/bmp"},
        {"txt","text/plain"},
        {"doc","application/msword"},
        {"js","text/js"},
        {"swf","application/x-shockwave-flash"},
        {"mp3","audio/mpeg"},
        {"zip","application/zip"},
        {"rar","application/rar"},
        {"tar","application/tar"},
        {"arj","application/arj"},
        {"cab","application/cab"},
        {"html","text/html"},
        {"htm","text/html"},
        {"default","application/octet-stream"},
        {"folder","application/vnd.google-apps.folder"}};

    @Override
    /**
     * Authenticates a session with the current service
     *
     * @param userID        The id of the user, such as username, for the current service
     * @return              Refresh token for this session
     * @throws UDException
     */
    public String authenticate(String userID) throws UDException {
        username = userID;
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        //Set up the authorization flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();

        //Connect to the authorization url so that the user can enter the authorization code
        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");
        System.out.println("  " + url);
        try{
            Desktop.getDesktop().browse(new URI(url));
        } catch( URISyntaxException e ) {
            throw new UDException( "Unable to browse to authentication URL", e );
        } catch( IOException e ) {
            throw new UDException( "Unable to open web browser", e );
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code;
        try{
            code = br.readLine();
        } catch( IOException e ) {
            throw new UDException( "Unable to read the authentication token", e );
        }

        //Receive the response token
        try {
            response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        } catch (IOException e) {
            throw new UDException( "Unable to authenticate", e );
        }

        //Set up the user credentials
        credential = new GoogleCredential().setFromTokenResponse(response);

        //Create a new authorized API client
        service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        return credential.getRefreshToken();
    }

    @Override
    /**
     * Gets the account info for the user logged into the current session of the current service
     *
     * @return              An AccountInfo object containing all relevant user information
     * @throws UDException
     */
    public AccountInfo getAccountInfo() {
        info = new AccountInfo();
        About about = null;
        try {
            about = service.about().get().execute();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        info.setSessionType("Google Drive");
        info.setTotalSize(about.getQuotaBytesTotal());
        info.setUsedSize(about.getQuotaBytesUsed());
        info.setUsername(username);
        return info;
    }

    @Override
    /**
     * Builds a tree of the directory structure for the current service and session
     *
     * @return              The root node of the tree representing the directory structure
     * @throws UDException
     */
    public UFile getFileList() throws UDException {
        //Get the list of files in Google Drive
        Drive.Files.List list = null;
        try {
            list = service.files().list();
        } catch (IOException e) {
            throw new UDException("Unable to access the files in Drive",e);
        }
        //Exclude files in the trash
        list.setQ("trashed = false");
        FileList files = null;
        try {
            files = list.execute();
        } catch (IOException e) {
            throw new UDException("Unable to retrieve the file list in Drive",e);
        }

        //Initialize the root UFile (the hierarchy will be represented as a tree from this root)
        UFile root = new UFile();
        root.setName("root");
        root.setOrigin(username + "-GoogleDrive");
        //This map allows us to properly place files under their parents
        Map<UFile,String> fileMap = new HashMap<UFile,String>();
        //Initialize all UFiles and place the first level files in the tree
        for(File f : files.getItems()){
            UFile entry = new UFile();
            entry.setId(f.getId());
            entry.isFolder(f.getMimeType().equals("application/vnd.google-apps.folder"));
            entry.setName(f.getTitle());
            entry.setOrigin(username + "-GoogleDrive");
            if(f.getParents() == null || f.getParents().size() == 0 || f.getParents().get(0).getIsRoot()){
                root.addChild(entry);
                entry.setParent(root);
            } else {
                fileMap.put(entry,f.getParents().get(0).getId());
            }
        }
        //Loop through remaining files and place them under their proper parents
        for(UFile u : fileMap.keySet()){
            boolean found = false;
            for(UFile p : root.getChildren()){
                if(p.getId().equals(fileMap.get(u))){
                    p.addChild(u);
                    u.setParent(p);
                    found = true;
                }
            }
            if(found) continue;
            for(UFile p : fileMap.keySet()){
                if(p.getId().equals(fileMap.get(u))){
                    p.addChild(u);
                    u.setParent(p);
                    found = true;
                }
            }
            if(found) continue;
            else
                root.addChild(u);
        }
        return root;
    }

    @Override
    /**
     * Searches the files/folders of the current session for the searchString
     * Any word containing the searchString is returned
     *
     * @param searchString  String to search for in all file/folder names
     * @return               List of all files/folders matching the search
     * @throws UDException
     */
    public List<UFile> searchFiles(String searchString) {
        try{
            //Populate the file list
            UFile root = getFileList();
            List<UFile> list = new ArrayList<UFile>();
            searchHelper(root, searchString, list);
            return list;
        } catch(UDException e){
            e.printStackTrace();
            return null;
        }
    }

    public void searchHelper(UFile u, String match, List<UFile> list){
        if(u.getName().contains(match)){
            list.add(u);
        }
        for(UFile c : u.getChildren()){
            searchHelper(c,match,list);
        }
    }

    @Override
    /**
     * Uploads a file to the current service
     *
     * @param filename      The name of the file to upload to the service
     * @return              The file that was uploaded
     * @throws UDException
     */
    public UFile upload(String filename) {
        // File's metadata.
        File body = new File();
        int lastSlash = filename.lastIndexOf('\\');
        if(lastSlash == 0) lastSlash = filename.lastIndexOf('/');
        body.setTitle(filename.substring(lastSlash+1,filename.length()));
        body.setDescription("");
        for(int i = 0; i < mimeTypes.length; i++){
            if(filename.endsWith(mimeTypes[i][0])){
                body.setMimeType(mimeTypes[i][1]);
                break;
            }
        }

        // File's content.
        java.io.File fileContent = new java.io.File(filename);
        FileContent mediaContent = new FileContent(body.getMimeType(), fileContent);
        try {
            File file = service.files().insert(body, mediaContent).execute();
            return null;
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
            return null;
        }
    }

    @Override
    /**
     * Downloads a file to the current service
     *
     * @param fileID        The id of the file to download
     * @return              The file that was downloaded
     * @throws UDException
     */
    public java.io.File download(String fileID) throws UDException {
        try{
            //Find the file
            File file = null;
            for(File f : service.files().list().execute().getItems()){
                if(f.getId().equals(fileID)){
                    file = f;
                    break;
                }
            }

            //Download the file
            if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
                HttpResponse resp =
                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                InputStream stream = resp.getContent();
                java.io.File realFile = new java.io.File("C:/Users/Eric/Desktop/"+file.getTitle());
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                BufferedWriter writer = new BufferedWriter(new FileWriter(realFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line+System.lineSeparator());
                }
                writer.write('\032');
                stream.close();
                writer.close();
                reader.close();
                return realFile;
            } else {
                // The file doesn't have any content stored on Drive.
            }
            return null;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    /**
     * Gets the type of session that this is
     *
     *@return               The session type
     */
    public String getSessionType() {
        return "GoogleDrive";
    }

}