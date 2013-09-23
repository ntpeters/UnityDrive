import base.UDException;
import desktop.CLIMain;
import desktop.GUIMain;

/**
 * Main program
 * If '--cli' argument is passed, the command line interface is executed
 * Otherwise, the GUI is executed
 */
public class UnityDrive {

    public static void main( String[] args ) throws UDException {
        if( args.length > 0 && args[0].equals( "--cli" ) ) {
            CLIMain.main( args );
        } else {
            GUIMain.main( args );
        }
    }
}