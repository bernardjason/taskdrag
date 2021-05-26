package org.bjason.taskdrag.javafx;

import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class BackendRunner {

    public static String GOOD_USER = "bernard";
    public static String PASSWORD = "jason";

    private static Process p;

    static int totalTests = 0;

    static boolean hasProcess() {
        return (p != null);
    }

    static boolean portAvailable(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

    static void startBackend() throws Exception {

        if (BackendRunner.portAvailable(8080) == true) {
            File db = new File("testing/themanager.db");
            db.delete();

            File fullPath = new File("../web/target/web-0.0.1-SNAPSHOT.jar");
            ProcessBuilder pb = new ProcessBuilder("java", "-Dspring.profiles.active=prod", "-jar", fullPath.getAbsolutePath());
            File testingDir = new File("testing");
            testingDir.mkdir();
            pb.directory(testingDir);
            pb.redirectOutput(new File("testing/std.txt"));
            pb.redirectError(new File("testing/err.txt"));
            BackendRunner.setProcess(pb.start());

            for (int i = 0; i < 50; i++) {
                if (BackendRunner.portAvailable(8080) == false) {
                    System.out.println("Spring started");
                    break;
                }
                System.out.println("Waiting for Spring backend to start");
                Thread.sleep(1000);
            }
        }
        if (BackendRunner.portAvailable(8080) == true) {
            System.out.println("Did not start back end");
            System.exit(0);
        }
    }

    static void forceClose() throws InterruptedException {
        System.out.println("******************* TESTS END **************************");
        if ( BackendRunner.p != null ) {
            BackendRunner.p.destroy();
            BackendRunner.p = null;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("***************** should be stopped");
            for (int i = 0; i < 50; i++) {
                if (portAvailable(8080) == true) {
                    System.out.println("***************** Backend down");
                    return;
                }
                System.out.println("Wait for Spring backend to shutdown");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            System.out.println("***** Spring should be stopped");
            Thread.sleep(5000);
            Assert.fail("Spring process on 8080 already running");
        }
    }


    public static void setProcess(Process start) {
        p = start;
    }
}
