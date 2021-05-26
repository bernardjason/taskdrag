package org.bjason.taskdrag.javafx;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testfx.framework.junit5.ApplicationExtension;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class Hook extends ApplicationExtension implements  ExtensionContext.Store.CloseableResource , BeforeAllCallback{

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if ( System.getProperty("os.name","").compareToIgnoreCase("linux") == 0 ) {
            System.out.println("Setting jdk.gtk.version=2 https://bugs.openjdk.java.net/browse/JDK-8211302");
            System.setProperty("jdk.gtk.version","2") ;
        }

        if (!started) {
            started = true;
            try {
                BackendRunner.startBackend();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("*********** STARTED " );
            context.getRoot().getStore(GLOBAL).put("gui testing..", this);
        }
    }

    @Override
    public void close() {
        try {
            BackendRunner.forceClose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

