package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The commit directory.
     */
    public static final File COMIT_DIR = join(GITLET_DIR, "commits");
    /**
     * The staging area.
     */
    public static final File STAGING_DIR = join(GITLET_DIR, ".staging");
    /**
     * Current commit: the commit in front of the currentBranch.
     */
//    private Commit currentCommit;
    /**
     * Current branch of commit tree.
     */
    private String currentBranch;
    /**
     * Temporary storage of commits - HashMap.
     */
    private HashMap<String, LinkedList<Commit>> branches;
    /**
     * The files in staging area.
     */
    private HashSet<File> stagingArea;
    /**
     * The tracking area that has the files being tracked with its SHA-1.
     */
    private HashMap<String, String> trackingArea;
    /**
     * Treat sha-1 as a reference.
     */
    private HashMap<String, File> sha2file;
    /**
     * SHA-1 to commits
     */
    private HashMap<String, Commit> sha2commit;
    /**
     * Commits to SHA-1;
     */
    private HashMap<Commit, String> Commit2Sha = new HashMap<>();
    /**
     * Set used to record what files have been staged for removal.
     */
    private HashSet<String> removingArea;

    public Repository() {
        LinkedList<Commit> commits = new LinkedList<>();
        Commit initialCommit = new Commit("initial commit", "1970/02/01 00:00:00");

        branches = new HashMap<>();
        branches.put("master", commits);
        currentBranch = "master";
        stagingArea = new HashSet<>();
        sha2file = new HashMap<>();
        sha2commit = new HashMap<>();
        trackingArea = new HashMap<>();
        removingArea = new HashSet<>();

        commit2sha(initialCommit);
        commits.addFirst(initialCommit);
    }

    public static Repository init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        COMIT_DIR.mkdir();
        STAGING_DIR.mkdir();
        return new Repository();
    }
    private Commit getCurrentCommit(){
       return branches.get(currentBranch).getFirst();
    }
    public void add(String filename) {
//        System.out.println("Current commit is: " + currentCommit.toString());
        File augend = join(CWD, filename);
        File stagingFile = join(STAGING_DIR, filename);
        if (!augend.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        byte[] contents = readContents(augend);
        String fileSha = sha1((Object) contents);
        String existSha = getCurrentCommit().getFilesha(filename);
        if (existSha != null && existSha.equals(fileSha)) {
            System.out.println("File is already committed.");
            if (stagingFile.exists()) {
                removeStage(stagingFile);
                stagingFile.delete();
            }
            System.exit(0);
        }
        stagingArea.add(stagingFile);
        trackingArea.put(filename, fileSha);
        writeContents(stagingFile, (Object) contents);
//        System.out.println("The files you have staged are: " + stagingArea.toString());
    }

    public void commit(String message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Commit newCommit = new Commit(trackingArea, message, dtf.format(LocalDateTime.now()));
        if (stagingArea.isEmpty() && removingArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        removingArea.clear();
        for (File X : stagingArea) {
            String S = sha1((Object) readContents(X));
            newCommit.addFile(X.getName(), S);
//            sha2file.put(S, X);
        }
        branches.get(currentBranch).addFirst(newCommit);
//        currentCommit = newCommit;
//        System.out.println("New currentCommit is: " + newCommit);
//        System.out.println("Commit tree updated! The current commit tree is: \n" + branches.get(currentBranch).toString());
        String commitSha = commit2sha(newCommit);
//        System.out.println("The new commit sha is "+commitSha);
//        sha2commit.put(commitSha, newCommit);

        File D = join(COMIT_DIR, commitSha);
        D.mkdir();
        HashSet<File> tep = new HashSet<>(stagingArea);
        for (File X : tep) {
            File file = join(D, X.getName());
            byte[] contents = readContents(X);
            String S = sha1((Object) contents);
            writeContents(file, (Object) contents);
            sha2file.put(S, file);
            removeStage(X);
            X.delete();
        }
    }

    public void rm(String filename) {
        File file = join(STAGING_DIR, filename);
        if (stagingArea.isEmpty() && trackingArea.isEmpty()) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (stagingArea.contains(file)) {
            removeStage(file);
        }
        if (getCurrentCommit().getFilesha(filename) != null) {    // tracking area will always be same with currentCommit.ids
            System.out.println("Notice: " + filename + " will be deleted");
            removeTrack(filename);
        }
        removingArea.add(filename);
    }

    public void log() {
        //TODO: log may not start from the very first of the current branch.
        LinkedList<Commit> tep = branches.get(currentBranch);
        for (Commit x : tep) {
            System.out.println(x);
        }
    }

    public void global_log() {
        for (Commit x : Commit2Sha.keySet()) {
            System.out.println(x);
        }
    }

    public void find(String message) {
        boolean flag = false;
        for (Commit x : Commit2Sha.keySet()) {
            if (x.getMessage().equals(message)) {
                System.out.println(commit2sha(x));
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Found no commit with that message.");
        }
        System.exit(0);
    }

    public void status(){
        System.out.println("=== Branches ===");
        for (String x: new TreeSet<>(branches.keySet())){
            if (x.equals(currentBranch)){
                System.out.print('*');
            }
            System.out.println(x);
        }

        // TODO: This method is too ugly. Improve it.
        System.out.println("\n=== Staged Files ===");
        List<String> tep = new ArrayList<>();
        for (File f : stagingArea){
            tep.add(f.getName());
        }
        Collections.sort(tep);
        for (String s : tep){
            System.out.println(s);
        }

        System.out.println("\n=== Removed Files ===");
        for (String f: new TreeSet<>(removingArea)){
            System.out.println(f);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        TreeSet<String> allFiles = new TreeSet<>();
        TreeSet<String> cwdFiles = new TreeSet<>(Objects.requireNonNull(plainFilenamesIn(CWD)));
        allFiles.addAll(cwdFiles);
        allFiles.addAll(trackingArea.keySet());
        for (File f : stagingArea){
            allFiles.add(f.getName());
        }

        for (String f: allFiles){
            File stagingFile = join(STAGING_DIR, f);
            File cwdFile = join(CWD, f);
            // trackingSha refers to the f that was tracked in the last commit(current commit)
            // stagingSha refers to the f that is staged in the present commit(newCommit)
            String trackingSha = getCurrentCommit().getFilesha(f);
            String stagingSha = trackingArea.get(f);
            // TODO: Is stagingArea can changed to type of String?
            boolean isStaging = stagingArea.contains(stagingFile);
            boolean isTracking = (trackingSha != null);
            boolean inCwd = cwdFiles.contains(f);

            if (inCwd) {
                // if there is a modified file that is not staged, it will be marked modified.
                // if there is a file that is staged but not same with the tracked version, it will be marked modified.
                String cwdSha = sha1((Object) readContents(cwdFile));
                if (isTracking && !isStaging && !cwdSha.equals(trackingSha)
                || isStaging && !cwdSha.equals(stagingSha)){
                    System.out.println(f + "(modified)");
                }
            }
            else {
                if (isTracking || isStaging) {
                    System.out.println(f + "(deleted)");
                }
            }
        }

        System.out.println("\n=== Untracked Files ===");
        for (String f: cwdFiles){
            boolean isTracking = (trackingArea.get(f) != null);
            if (!isTracking){
                System.out.println(f + "(untracked)");
            }
        }
    }

    public void checkoutFile(String commitId, String filename){
        Commit previousCommit = (commitId == null)
                ? getCurrentCommit()
                : sha2commit.get(commitId);
        if (commitId != null && sha2commit.get(commitId) == null){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        String sourceSha = previousCommit.getFilesha(filename);
        File sourceFile = sha2file.get(sourceSha);
        if (sourceFile == null){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File cwdFile = join(CWD, filename);
        writeContents(cwdFile, (Object) readContents(sourceFile));
        removeStage(join(STAGING_DIR, filename));
    }

    public void checkoutBranch(String branchName){
        if (!branches.containsKey(branchName)){
            System.out.println("No such branch exists.");
        }
        if (branchName.equals(currentBranch)){
            System.out.println("No need to checkout the current branch.");
        }
        Commit checkoutCommit = branches.get(branchName).getFirst();
        Commit currentCommit = getCurrentCommit();
//        System.out.println("checkoutCommit is: " + checkoutCommit);
//        System.out.println("currentCommit is " + currentCommit);
        Set<String> checkoutFiles = checkoutCommit.getFiles();
        Set<String> currentFiles = getCurrentCommit().getFiles();
        List<String> cwdFiles = Objects.requireNonNull(plainFilenamesIn(CWD));
//        System.out.println("checkout files: " + checkoutFiles.toString());
//        System.out.println("current files: " + currentFiles.toString());
        System.out.println(cwdFiles);
        for (String f : checkoutFiles){
            if (cwdFiles.contains(f) && !currentFiles.contains(f)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String f : currentFiles){
            if (!checkoutFiles.contains(f)){
                File file = join(CWD, f);
                file.delete();
            }
        }
        for (String f : checkoutFiles) {
            File cwdFile = join(CWD, f);
            File commitFile = sha2file.get(checkoutCommit.getFilesha(f));
            writeContents(cwdFile, (Object) readContents(commitFile));
        }
        currentBranch = branchName;
    }

    public void branch(String branchName){
        if (branches.containsKey(branchName)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        LinkedList<Commit> newBranch = new LinkedList<>(branches.get(currentBranch));
        branches.put(branchName, newBranch);
    }

    // Bellowed are some helper functions.
    /**
     * Removes the file from the stagingArea.
     * @param filename the file to be removed
     */
    private void removeStage(File filename) {
        if (!stagingArea.contains(filename)) {
            System.out.println(filename + " was already removed.");
            return;
        }
        stagingArea.remove(filename);
    }

    /**
     * Removes the file from the trackingArea and the workingArea.
     * @param filename the file to be removed
     */
    private void removeTrack(String filename) {
        File workingFile = join(CWD, filename);
        trackingArea.remove(filename);
        workingFile.delete();
    }

    private String commit2sha(Commit commit) {
        String t = Commit2Sha.get(commit);
        if (t == null) {
            t = commit.getSha();
            Commit2Sha.put(commit, t);
            sha2commit.put(t, commit);
        }
        return t;
    }
    /**
     * Unstage the file if it is currently staged for addition.
     * Or remove the file in tracking area.
     * @param filename The file to be removed.
     */
    /* TODO: fill in the rest of this class. */
}
