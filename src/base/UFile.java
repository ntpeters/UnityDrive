package base;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a "UnityFile" node.
 * Internal file type that abstracts away the differences between each services internal file types.
 * Contains all relevant file information to our app.
 *
 * @author ejrinkus
 */
public class UFile{
    private String name;            // Name of the file (e.g. document.docx)
    private String id;              // ID of the file
    private boolean isFolder;       // If the UFile object is a folder
    private List<UFile> children;   // List of children of this UFile
    private UFile parent;           // Parent of this UFile
    private String origin;          // Service of origin (format: "username-service")

    public UFile(){
        children = new ArrayList<UFile>();
    }

    public UFile(String name){
        this.name = name;
        children = new ArrayList<UFile>();
    }

    /**
     * Gets the name of this file
     *
     * @return  The name of this file
     */
    public String getName(){
        return name;
    }

    /**
     * Sets the name of the current file
     *
     * @param name  The name of this file
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Sets whether this is a folder or not
     *
     * @param f     Whether this is a folder or not
     */
    public void isFolder(boolean f){
        isFolder = f;
    }

    /**
     * Checks if this is a folder or not
     *
     * @return      Whether or not this is a folder
     */
    public boolean isFolder(){
        return isFolder;
    }

    /**
     * Gets the parent of this file node
     *
     * @return      The parent of this file node
     */
    public UFile getParent(){
        return parent;
    }

    /**
     * Sets the parent of this file node
     *
     * @param parent    The parent of this file node
     */
    public void setParent(UFile parent){
        this.parent = parent;
    }

    /**
     * Gets the originating service of this file
     *
     * @return      The originating service of this file
     */
    public String getOrigin(){
        return origin;
    }

    /**
     * Sets the originating service of this file
     *
     * @param origin    The origination service of this file
     */
    public void setOrigin(String origin){
        this.origin = origin;
    }

    /**
     * Gets the file id
     * Typically unique within a given service
     *
     * @return  The file id
     */
    public String getId(){
        return id;
    }

    /**
     * Sets the file id
     * Typically unique within a given service
     *
     * @param id    The file id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Adds a child file node to this one
     *
     * @param child     Child file node to add
     */
    public void addChild(UFile child){
        children.add(child);
    }

    /**
     * Removes a child file node from this one
     *
     * @param child     The child file node to remove
     * @return          The file node removed
     */
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

    /**
     * Removes all child nodes of this node
     */
    public void clearChildren(){
        children.clear();
    }

    /**
     * Gets an iterable list of all child nodes of this node
     *
     * @return      List of all child nodes of this node
     */
    public List<UFile> getChildren() {
        return this.children;
    }
}