package base;

import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.restclientv2.exceptions.BoxRestException;

import java.util.List;

/**
 * All the session types should implement this
 */
public interface UDSession {
    public boolean authenticate( String userID ) throws BoxServerException, AuthFatalFailureException, BoxRestException;
    public AccountInfo getAccountInfo();
    public List<UFile> getFileList();
    public List<UFile> searchFiles( String searchString );
    public boolean upload( String filename );
    public boolean download( String fileID );
    public String getSessionType();
}
