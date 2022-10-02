package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static final File REPO_DIR = join(Repository.GITLET_DIR, ".repo");

    public static void main(String[] args) {
        // Done: what if args is empty? --- Exit the program.
        if (args.length == 0) {
            System.out.println("Please input arguments!!!");
            System.exit(0);
        }

        String firstArg = args[0];
        Repository repo = REPO_DIR.exists() ? readRepo() : null;

        switch (firstArg) {
            case "init":
                // TODO: handle the `init` command
                repo = Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (repo == null) {
                    System.out.println("Repository hasn't been initiated.");
                    System.exit(0);
                }
                if (args.length < 2) {
                    System.out.println("Please input the file to be added.");
                    System.exit(0);
                }
                repo.add(args[1]);
                break;
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                assert repo != null;
                repo.commit(args[1]);
                break;
            case "rm":
                if (args.length < 2) {
                    System.out.println("Please enter a file to be removed.");
                    System.exit(0);
                }
                assert repo != null;
                repo.rm(args[1]);
                // TODO: FILL THE REST IN
        }
        writeObject(REPO_DIR, repo);
    }

    private static void saveRepo(Repository repo) {
        writeObject(REPO_DIR, repo);
    }

    private static Repository readRepo() {
        return readObject(REPO_DIR, Repository.class);
    }
}
