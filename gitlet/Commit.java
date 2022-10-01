package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of the Commit. */
    private String timestamp;
    /** The reference to the files. */
    private HashMap<String, String> ids;
    /** The reference to parent. */
    private ArrayList<String> parents;

    public Commit(String message, String timestamp){
        this.message = message;
        this.timestamp = timestamp;
        ids = new HashMap<>();
        parents = new ArrayList<>();
    }

    public Commit(Commit parent, String message, String timestamp){
        this.message = message;
        this.timestamp = timestamp;
        this.ids = new HashMap<>(parent.ids);
        parents = new ArrayList<>();
        parents.add(Utils.sha1(parent));
    }

    public String getFilesha(String filename){
        return ids.get(filename);
    }

    /* TODO: fill in the rest of this class. */
}
