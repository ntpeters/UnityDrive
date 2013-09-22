package base;

/**
 * Info pertaining to a user account for a given service.
 * This should be unique within a single instance of the app.
 */
public class AccountInfo {
    private String username;     // The username of the user currently signed into this session
    private String sessionType;  // The type of the current users session
    private double totalSize;    // The total space available for this account on this service (in bytes)
    private double usedSize;     // The current amount of used space for this account on this service (in bytes)

    /**
     * Get the username for this session
     *
     * @return  The username of the current user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set the username for this session
     *
     * @param username  The username of the current user
     */
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * Get the current session type
     *
     * @return  The type of the current session
     */
    public String getSessionType() {
        return this.sessionType;
    }

    /**
     * Set the current session type
     *
     * @param sessionType   The type of the current session
     */
    public void setSessionType( String sessionType ) {
        this.sessionType = sessionType;
    }

    /**
     * Get the total available size of the current account on the current service
     *
     * @return  The total available size in bytes
     */
    public double getTotalSize() {
        return this.totalSize;
    }

    /**
     * Set the total available size of the current account on the current service
     *
     * @param totalSize The total available size in bytes
     */
    public void setTotalSize( double totalSize ) {
        this.totalSize = totalSize;
    }

    /**
     * Get the current amount of used space for the current account on the current service
     *
     * @return  The amount of currently used space in bytes
     */
    public double getUsedSize() {
        return this.usedSize;
    }

    /**
     * Set the current amount of used space for the current account on the current service
     *
     * @param usedSize  The amount currently used space in bytes
     */
    public void setUsedSize( double usedSize ) {
        this.usedSize = usedSize;
    }
}