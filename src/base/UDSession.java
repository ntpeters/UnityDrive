package base;

import java.io.File;
import java.util.List;

/**
 * This interface will be implemented by all session types.
 * This interface will ensure that each session for each service
 *  will provide a means to complete common tasks required for basic
 *  cloud storage actions.
 *
 *  @author ntpeters
 */
public interface UDSession {
    /**
     * Authenticates a session with the current service
     *
     * @param userID        The id of the user, such as username, for the current service
     * @return              The auth token received from the API
     * @throws UDException
     */
    public String authenticate( String userID ) throws UDException;

    /**
     * Gets the account info for the user logged into the current session of the current service
     *
     * @return              An AccountInfo object containing all relevant user information
     * @throws UDException
     */
    public AccountInfo getAccountInfo() throws UDException;

    /**
     * Builds a tree of the directory structure for the current service and session
     *
     * @return              The root node of the tree representing the directory structure
     * @throws UDException
     */
    public UFile getFileList() throws UDException;

    /**
     * Searches the files/folders of the current session for the searchString
     * Any word containing the searchString is returned
     *
     * @param searchString  String to search for in all file/folder names
     * @return               List of all files/folders matching the search
     * @throws UDException
     */
    public List<UFile> searchFiles( String searchString ) throws UDException;

    /**
     * Uploads a file to the current service
     *
     * @param filename      The name of the file to upload to the service
     * @return              The file that was uploaded
     * @throws UDException
     */
    public UFile upload( String filename) throws UDException;

    /**
     * Downloads a file to the current service
     *
     *
     *
     * @param fileID        The id of the file to download
     * @return              The file that was downloaded
     * @throws UDException
     */
    public File download(String fileID) throws UDException;

    /**
     * Gets the type of session that this is
     *
     *@return               The session type
     */
    public String getSessionType();
}
