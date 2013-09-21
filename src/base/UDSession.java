package base;

import java.util.List;

/**
 * All the session types should implement this
 */
public interface UDSession {
    public boolean authenticate( String userID ) throws UDException;
    public AccountInfo getAccountInfo();
    public List<UFile> getFileList();
    public List<UFile> searchFiles( String searchString );
    public boolean upload( String filename );
    public boolean download( String fileID );
    public String getSessionType();
}
