package base;

import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.restclientv2.exceptions.BoxRestException;

import java.util.List;

/**
 * All the session types should implement this
 */
public interface UDSession {
    public boolean authenticate( String userID ) throws UDException;
    public AccountInfo getAccountInfo() throws BoxServerException, AuthFatalFailureException, BoxRestException, UDException;
    public UFile getFileList() throws UDException;
    public List<UFile> searchFiles( String searchString );
    public boolean upload( String filename );
    public boolean download( String fileID ) throws UDException;
    public String getSessionType();
}
