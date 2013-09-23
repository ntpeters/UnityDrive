package desktop;

import base.AuthLookup;
import base.UDException;
import base.UDSession;
import base.UFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The command line interface for Unity Drive
 */
public class CLIMain {
    public static AuthLookup tokenStore;            // Stores all auth tokens
    public static ArrayList<UDSession> sessions;    // Keeps track of all active sessions

    public static void main( String[] args ) throws UDException {
        tokenStore = new AuthLookup();
        sessions = new ArrayList<>();

        menu();
    }

    /**
     * The menu for the command line interface
     *
     * @throws UDException
     */
    public static void menu() throws UDException {
        Scanner in = new Scanner(System.in);

        System.out.println("Main Menu");
        System.out.println("----------");
        System.out.println("1. Add account");
        System.out.println("2. List all files");
        System.out.println("3. Search files");
        System.out.println("4. Upload file");
        System.out.println("5. Download file");
        System.out.println("6. Exit");
        System.out.println("Selection: ");
        int sel = in.nextInt();

        switch( sel ) {
            case 1:
                System.out.println("Service: ");
                String service = in.next();
                System.out.println("Username: ");
                String user = in.next();

                UIHelpers.addAccount( service, user, sessions, tokenStore );
                break;
            case 2:
                List<UFile> files = UIHelpers.getAggregateList( sessions );
                for( UFile file : files ) {
                    System.out.println( "[" + file.getOrigin() + "]" + " " + file.getName() +
                            " {" + file.getId() + "}");
                }
                break;
            case 3:
                System.out.println("Search term: ");
                String term = in.next();
                List<UFile> search = UIHelpers.getAggregateList( sessions );
                for( UFile file : search ) {
                    if( file.getName().toLowerCase().contains( term.toLowerCase() )) {
                        System.out.println( "[" + file.getOrigin() + "]" + " " + file.getName() );
                    }
                }
                break;
            case 4:
                System.out.println("Service: ");
                String upservice = in.next();
                System.out.println("Username: ");
                String upuser = in.next();
                System.out.println("File (include the full path!): ");
                String upfile = in.next();
                for(UDSession s : sessions){
                    if(s.getSessionType().equals(upservice) &&
                            s.getAccountInfo().getUsername().equals(upuser)){
                        s.upload(upfile);
                        break;
                    }
                }
                break;
            case 5:
                System.out.println("Service: ");
                String downservice = in.next();
                System.out.println("Username: ");
                String downuser = in.next();
                System.out.println("File ID: ");
                String downfile = in.next();
                for(UDSession s : sessions){
                    if(s.getSessionType().equals(downservice) &&
                            s.getAccountInfo().getUsername().equals(downuser)){
                        s.download(downfile);
                        break;
                    }
                }
                break;
            case 6:
                System.exit(0);
        }

        menu();
    }
}
