package backend;

import java.util.List;

/**
 * All the session types should implement this
 */
public interface UDSession {
    public boolean authenticate( String userID );
    public accountInfo getAccountInfo();
    public List<UFILE> getFileList();
    public List<UFILE> searchFiles( String searchString );
    public boolean upload( String filename );
    public boolean download( String fileID );
    public String getSesssionType();
}
