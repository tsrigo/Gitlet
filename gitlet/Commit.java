package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static gitlet.Utils.join;
import static gitlet.Utils.serialize;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private final String message;
    /**
     * The timestamp of the Commit.
     */
    private String timestamp;
    // /** The SHA-1 of the commit. */
    // private String SHA;
    /**
     * The reference to the files SHA-1.
     */
    private HashMap<String, String> ids;
    /**
     * The reference to parent.
     */
    private ArrayList<String> parents;

    public Commit(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        ids = new HashMap<>();
        parents = new ArrayList<>();
    }

    public Commit(Commit parent, String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.ids = new HashMap<>(parent.ids);
        parents = new ArrayList<>();
        parents.add(Utils.sha1(serialize(parent)));
    }

    public Commit() {
        this.message = "hhh";
    }

    public static void main(String[] args) {
        File file = join(Repository.COMIT_DIR, "hhh.txt");
        // writeContents(file, "hhh");
        System.out.println("hhh!!!");
        file.delete();
    }

    public String getFilesha(String filename) {
        return ids.get(filename);
    }
    /* TODO: fill in the rest of this class. */

    public void addFile(String filename, String sha) {
        ids.put(filename, sha);
    }

    @Override
    public String toString() {
        return "Time stamp: " + this.timestamp + "  Message: " + this.message
                + " Files: " + ids.keySet() + '\n';
    }
}
