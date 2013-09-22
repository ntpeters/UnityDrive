package base.sessions;

import base.*;
import com.box.boxjavalibv2.BoxClient;
import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.dao.BoxUser;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.boxjavalibv2.requests.requestobjects.BoxDefaultRequestObject;
import com.box.boxjavalibv2.requests.requestobjects.BoxOAuthRequestObject;
import com.box.restclientv2.exceptions.BoxRestException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: mgrimes
* Date: 9/21/13
* Time: 1:43 PM
* To change this template use File | Settings | File Templates.
*/
public class BoxSession implements UDSession {

    private BoxClient client;

    private static final String APP_KEY = "h0b9azeqj3mtmy787iqwpucev2gqqskf";
    private static final String APP_SECRET = "STRVZ1ftmCrzUzor08gPP2h2hYVaTNWG";
    public static final String URL = "https://www.box.com/api/oauth2/authorize?" +
            "response_type=code&client_id=" + APP_KEY;
    private static final int PORT = 4000;
    private static final String sessionType = "Box";

    public BoxSession() {}

    @Override
    public boolean authenticate(String userID) throws UDException {

        String code = "";

        try {
            Desktop.getDesktop().browse(java.net.URI.create(URL));
            code = getCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            client = getAuthenticatedClient(code);
        } catch( BoxRestException e ) {
            throw new UDException( "Rest communication failed!", e );
        } catch( BoxServerException e ) {
            throw new UDException( "Unable to connect to server!", e );
        } catch( AuthFatalFailureException e ) {
            throw new UDException( "Authentication failed!", e );
        }

        System.out.println("We are authenticated");

        return client.isAuthenticated();

    }

    @Override
    public AccountInfo getAccountInfo() throws UDException {

        BoxUser bUser = null;
        try {
            bUser = client.getUsersManager().getCurrentUser(new BoxDefaultRequestObject());
        } catch (BoxRestException e) {
            throw new UDException("Box rest exception!");
        } catch (BoxServerException e) {
            throw new UDException("Box server exception!");
        } catch (AuthFatalFailureException e) {
           throw new UDAuthenticationException("Failed to authenticate box session!");
        }

        /*
        System.out.println("name: " + bUser.getName());
        System.out.println("login: " + bUser.getLogin());
        System.out.println("total size: " + readableFileSize();
        System.out.println("used space: " + readableFileSize(bUser.getSpaceUsed().longValue()));
        */

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.username = bUser.getLogin();
        accountInfo.sessionType = this.getSessionType();
        accountInfo.totalSize = bUser.getSpaceAmount().longValue();
        accountInfo.usedSize = bUser.getSpaceUsed().longValue();
        return accountInfo;
    }

    @Override
    public UFile getFileList() {

    }

    @Override
    public List<UFile> searchFiles(String searchString) {
        return null;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean upload(String filename) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean download(String fileID) {
        return false;  // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getSessionType() {
        return BoxSession.sessionType;
    }

    private static BoxClient getAuthenticatedClient(String code) throws BoxRestException,
            BoxServerException, AuthFatalFailureException {
        BoxClient client = new BoxClient(APP_KEY, APP_SECRET);
        BoxOAuthRequestObject obj = BoxOAuthRequestObject.createOAuthRequestObject(
                code, APP_KEY, APP_SECRET, "http://localhost:" + PORT);
        BoxOAuthToken bt =  client.getOAuthManager().createOAuth(obj);
        client.authenticate(bt);
        return client;
    }

    private static String getCode() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket = serverSocket.accept();
        BufferedReader in = new BufferedReader (new InputStreamReader(socket.getInputStream ()));
        while (true)
        {
            String code = "";
            try
            {
                code = in.readLine ();
                System.out.println (code);
                String match = "code";
                int loc = code.indexOf(match);

                if( loc > 0 ) {
                    int httpstr = code.indexOf("HTTP") - 1;
                    code = code.substring(code.indexOf(match), httpstr);
                    String parts[] = code.split("=");
                    code=parts[1];
                }

                return code;
            }
            catch (IOException e) {
                //error ("System: " + "Connection to server lost!");
                System.exit (1);
                break;
            }
        }
        return "";
    }


    /**
     *  Method to format a long integer that represents a # of bytes into a readable string.
     *  like this: 1024 -> 1KB
     * @param size # of bytes
     * @return string representing using SI byte notation or
     */
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) +
                " " + units[digitGroups];
    }

}
