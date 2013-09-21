package base;

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
    private String name; //Name of the file (e.g. document.docx)
<<<<<<< HEAD:src/UFile.java
    private String id;  //ID of the file
    private boolean isFolder; //If the UFile object is a folder
    private List<UFile> children; //List of children of this UFile
    private UFile parent; //Parent of this UFile
=======
    private java.io.File file; //Java file object
    private boolean isFolder; //If the base.UFile object is a folder
    private List<UFile> children; //List of children of this base.UFile
    private UFile parent; //Parent of this base.UFile
>>>>>>> c498e9f7f75cc35e062dceb0c0afb387275ef3a0:src/base/UFile.java
    private String origin; //Service of origin (format: "username-service")

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

    public String getOrigin(){
        return origin;
    }

    public void setOrigin(String origin){
        this.origin = origin;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
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