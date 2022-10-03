package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

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
    private final String timestamp;
    // /** The SHA-1 of the commit. */
    // private String SHA;
    /**
     * The reference to the files SHA-1.
     */
    private HashMap<String, String> ids;
    /**
     * The reference to parent. SHA-1 of the parent commit are stored in it.
     */
    private ArrayList<String> parents;


    public Commit(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        ids = new HashMap<>();
        parents = new ArrayList<>();
    }

    public Commit(HashMap<String, String> ids, String message, String timestamp) {
        this.ids = new HashMap<>(ids);
        this.message = message;
        this.timestamp = timestamp;
    }

    public Commit(Commit copy, String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.ids = new HashMap<>(copy.ids);
        parents = new ArrayList<>();
        // parents.add(Utils.sha1(serialize(parent)));
    }

    public Commit(String message) {
        this.message = message;
        this.timestamp = "hhh";
    }

    public Commit(Commit parent) {
        this.ids = new HashMap<>(parent.ids);
        this.message = null;
        this.timestamp = null;
        parents = new ArrayList<>();
        parents.add(Utils.sha1(serialize(parent)));
    }


    public static void main(String[] args) {
        LinkedList<Commit> C = new LinkedList<>();
        C.addFirst(new Commit("1"));
        C.addFirst(new Commit("2"));
        C.addFirst(new Commit("3"));
        LinkedList<Commit> H = new LinkedList<>(C);
        System.out.println(H.getFirst());
        C.addFirst(new Commit("4"));
        C.addFirst(new Commit("5"));
        System.out.println(C.getFirst());
        System.out.println(H.getFirst());
    }

    public Set<String> getFiles(){
        return ids.keySet();
    }
    public String getMessage() {
        return this.message;
    }

    public String getFilesha(String filename) {
        return ids.get(filename);
    }
    /* TODO: fill in the rest of this class. */

    public void addFile(String filename, String sha) {
        ids.put(filename, sha);
    }

    public void addParent(String parentSha) {
        parents.add(parentSha);
    }

//    public void setMessage(String message){
//        if (this.message != null) {
//            throw new IllegalArgumentException("Message is already set");
//        }
//        this.message = message;
//    }
//
//    public void setTimestamp(String timestamp){
//        if (this.timestamp != null) {
//            throw new IllegalArgumentException("Timestamp is already set");
//        }
//        this.timestamp = timestamp;
//    }


    public void removeFile(String filename) {
        ids.remove(filename);
    }

    public String getSha(){
        String idsString, parentsString;
        idsString = (ids == null)
                ? ""
                : ids.toString();
        parentsString = (parents == null)
                ? ""
                : parents.toString();
        return sha1(idsString+message+timestamp+parentsString);
    }
    @Override
    public String toString() {
        return "\n===\n"
                + "commit " + this.getSha() + '\n'
                + "Date: " + this.timestamp + '\n'
                + this.message + '\n'
                + "Tracking Files: " + this.ids.keySet();
    }
}
