package uk.codingbadgers.bootstrap.tasks;

import uk.codingbadgers.bootstrap.Bootstrap;
import uk.codingbadgers.bootstrap.BootstrapException;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static uk.codingbadgers.bootstrap.BootstrapConstants.*;

public class TaskCheckJavaVersion implements Task {

    @Override
    public void run(Bootstrap bootstrap) {
        try {
            JarFile jar = new JarFile(bootstrap.getInstallerFile());

            Manifest manifest = jar.getManifest();
            Attributes attr = manifest.getAttributes(MANIFEST_SECTION);

            if (attr == null) {
                System.out.println("Cannot get java version details. Assuming valid");
                return;
            }

            float required = Float.parseFloat(attr.getValue(MANIFEST_JAVA_CLASS_VERSION));
            float local = Float.parseFloat(System.getProperty(PROPERTY_JAVA_CLASS_VERSION));

            if (required > local) {
                throw new BootstrapException("Please update to Java " + attr.getValue(MANIFEST_JAVA_VERSION) + ".\nJava " + System.getProperty(PROPERTY_JAVA_VERSION) + " is not supported anymore.");
            }

        } catch (IOException e) {
            throw new BootstrapException(e);
        }
    }

}
