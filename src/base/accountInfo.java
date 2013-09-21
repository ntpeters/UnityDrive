package base;

/**
 * Created with IntelliJ IDEA.
 * User: nate
 * Date: 9/21/13
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccountInfo {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(double totalSize) {
        this.totalSize = totalSize;
    }

    public double getUsedSize() {
        return usedSize;
    }

    public void setUsedSize(double usedSize) {
        this.usedSize = usedSize;
    }

    String username;
    double totalSize;
    double usedSize;
}