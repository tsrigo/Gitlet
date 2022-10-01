package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory. */
    public static final File COMIT_DIR = join(GITLET_DIR, "commits");
    /** The staging area. */
    public static final File STAGING_DIR = join(GITLET_DIR, ".staging");
    /** Current branch of commit tree. */
    private LinkedList<Commit> currentBranch;
    /** Temporary storage of commits - HashMap. */
    private HashMap<String, LinkedList<Commit>> branches;
    public Repository(){
        LinkedList<Commit> commits = new LinkedList<>();
        commits.add(new Commit("initial commit", "1970/2/1-00:00:00"));
        currentBranch = commits;

        branches = new HashMap<>();
        branches.put("master", commits);
    }

    public static Repository init(){
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMIT_DIR.mkdir();
        STAGING_DIR.mkdir();
        return new Repository();
    }

    public void add(String filename){
        File augend = join(CWD, filename);
        File stagingFile = join(STAGING_DIR, filename);
        if (!augend.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        byte[] contents = readContents(augend);
        String fileSha = sha1((Object) contents);
        String existSha = currentBranch.getFirst().getFilesha(filename);
        if (existSha != null &&existSha.equals(fileSha)){
            System.out.println("File is already committed.");
            if (stagingFile.exists()){
                restrictedDelete(stagingFile);
            }
            System.exit(0);
        }

        writeContents(stagingFile, (Object) contents);
    }

    /* TODO: fill in the rest of this class. */
}
