package gitlet;

import java.io.File;
import java.io.Serializable;
import java.lang.module.FindException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    /** Current commit: the new commit's parent. */
    private Commit currentCommit;
    /** Current branch of commit tree. */
    private LinkedList<Commit> currentBranch;
    /** Temporary storage of commits - HashMap. */
    private HashMap<String, LinkedList<Commit>> branches;
    /** The files in staging area. */
    private HashSet<File> stagingArea;
    /** Treat sha-1 as a reference. */
    private HashMap<String, File> sha2file;
    /** SHA-1 to commits */
    private HashMap<String, Commit> sha2commit;
    public Repository(){
        LinkedList<Commit> commits = new LinkedList<>();
        currentCommit = new Commit("initial commit", "1970/2/1 00:00:00");
        commits.addFirst(currentCommit);
        currentBranch = commits;

        branches = new HashMap<>();
        branches.put("master", commits);
        stagingArea = new HashSet<>();
        sha2file = new HashMap<>();
        sha2commit = new HashMap<>();
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
        System.out.println("Current commit is: " + currentCommit.toString());
        File augend = join(CWD, filename);
        File stagingFile = join(STAGING_DIR, filename);
        if (!augend.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        byte[] contents = readContents(augend);
        String fileSha = sha1((Object) contents);
        String existSha = currentCommit.getFilesha(filename);
//        System.out.println(filename + " has SHA-1 of " + fileSha);
//        System.out.println("existSha is " + existSha);
        if (existSha != null && existSha.equals(fileSha)){
            System.out.println("File is already committed.");
            if (stagingFile.exists()){
                removeStage(stagingFile);
            }
            System.exit(0);
        }
        stagingArea.add(stagingFile);
        writeContents(stagingFile, (Object) contents);
        System.out.println("The files you have staged are: " + stagingArea.toString());
    }

    public void commit(String message){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Commit newCommit = new Commit(currentCommit, message, dtf.format(LocalDateTime.now()));
        if (stagingArea.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        for (File X : stagingArea) {
            String S = sha1((Object) readContents(X));
            newCommit.addFile(X.getName(), S);
            sha2file.put(S, X);
        }
        currentBranch.addFirst(newCommit);
        currentCommit = newCommit;
        System.out.println("New currentCommit is: " + newCommit.toString());
        System.out.println("Commit tree updated! The current commit tree is: \n" + currentBranch.toString());
        String commitSha = sha1((Object) serialize(newCommit));
        sha2commit.put(commitSha, newCommit);

        File D = join(COMIT_DIR, commitSha);
        D.mkdir();
        HashSet<File> tep = new HashSet<>(stagingArea);
        for (File X : tep) {
            File file = join(D, X.getName());
            byte[] contents = readContents(X);
            writeContents(file, (Object) contents);
            removeStage(X);
        }
    }

    private void removeStage(File X){
        if (!stagingArea.contains(X)){
            System.out.println(X + " was already removed.");
        }
        stagingArea.remove(X);
        if (!X.delete()){
            throw new IllegalArgumentException("delete fail!!!");
        };
    }
    /* TODO: fill in the rest of this class. */
}
