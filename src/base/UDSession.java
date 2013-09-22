package base;

import java.util.List;

/**
 * All the session types should implement this
 */
public interface UDSession {
    public boolean authenticate( String userID ) throws UDException;
    public AccountInfo getAccountInfo() throws UDException;
    public UFile getFileList() throws UDException;
    public List<UFile> searchFiles( String searchString ) throws UDException;
    public UFile upload( String filename) throws UDException;
    public UFile download( String fileID ) throws UDException;
    public String getSessionType();
}
