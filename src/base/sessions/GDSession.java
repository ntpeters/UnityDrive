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

    private static String CLIENT_ID = "304793868911-vokk592ddao58s9oqm8f6jhj2udm6po5.apps.googleusercontent.com";
    private static String CLIENT_SECRET = "pK_uV6wVb0M13cXCIgdYP9lQ";
    private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private static Drive service;
    private static GoogleCredential credential;
    private static GoogleTokenResponse response;

    private static AccountInfo info;
    private static String username;

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
    public String authenticate(String userID) throws UDException {
        username = userID;
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();

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

        try {
            response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        } catch (IOException e) {
            throw new UDException( "Unable to authenticate", e );
        }

        credential = new GoogleCredential().setFromTokenResponse(response);

        //Create a new authorized API client
        service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        return credential.getRefreshToken();
    }

    @Override
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
    public UFile getFileList() throws UDException {
        Drive.Files.List list = null;
        try {
            list = service.files().list();
        } catch (IOException e) {
            throw new UDException("Unable to access the files in Drive",e);
        }
        list.setQ("trashed = false");
        FileList files = null;
        try {
            files = list.execute();
        } catch (IOException e) {
            throw new UDException("Unable to retrieve the file list in Drive",e);
        }
        UFile root = new UFile();
        root.setName("root");
        root.setOrigin(username + "-GoogleDrive");
        Map<UFile,String> fileMap = new HashMap<UFile,String>();
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
                }
            }
        }
        return root;
    }

    @Override
    public List<UFile> searchFiles(String searchString) {
        return null;
    }

    @Override
    public UFile upload(String filename) {
        // File's metadata.
        File body = new File();
        body.setTitle(filename);
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

            // Uncomment the following line to print the File ID.
            // System.out.println("File ID: %s" + file.getId());

            return null;
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
            return null;
        }
    }

    @Override
    public UFile download(String fileID) {
        try{
            File file = null;
            for(File f : service.files().list().execute().getItems()){
                if(f.getId().equals(fileID)){
                    file = f;
                    break;
                }
            }

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
                    writer.write(line);
                }
                writer.close();
                reader.close();
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
    public String getSessionType() {
        return "Google Drive";
    }

}