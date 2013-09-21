import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 9/21/13
 * Time: 12:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class UFile{
    private String name;
    private java.io.File file;
    private boolean isFolder;
    private List<UFile> children;
    private UFile parent;

    public UFile(){
        children = new ArrayList<UFile>();
    }

    public UFile(String name){
        this.name = name;
        children = new ArrayList<UFile>();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public java.io.File getFile(){
        return file;
    }

    public void setFile(java.io.File file){
        this.file = file;
    }

    public void isFolder(boolean f){
        isFolder = f;
    }

    public boolean isFolder(){
        return isFolder;
    }

    public UFile getParent(){
        return parent;
    }

    public void setParent(UFile parent){
        this.parent = parent;
    }

    public void addChild(UFile child){
        children.add(child);
    }

    public UFile removeChild(String child){
        UFile toRemove = null;
        for(UFile c:children){
            if(c.getName().equals(child)){
                toRemove = c;
                break;
            }
        }
        children.remove(toRemove);
        return toRemove;
    }

    public void clearChildren(){
        children.clear();
    }
}