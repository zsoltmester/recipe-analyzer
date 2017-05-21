package hu.elte.recipeanalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * It's managing the resources in the jar.
 */
final class ResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceManager.class);

    /**
     * The root directory of the resources.
     */
    static final String RESOURCES_DIRECTORY = "target";

    static final File THIS_JAR = new File(ResourceManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());

    private ResourceManager() {
        throw new IllegalStateException("You cannot instantiate the resource manager.");
    }

    /**
     * Copies the resources from the given jar to the {@link #RESOURCES_DIRECTORY}.
     *
     * @param fromJar             The jar with resources.
     * @param resourcesPathPrefix Path prefix for the resources to copy.
     * @throws IOException If the failed to copy the resources due to I/O exception.
     */
    static void copyResourcesFromJar(File fromJar, String resourcesPathPrefix) throws IOException {

        JarFile jar = new JarFile(fromJar);
        Enumeration<JarEntry> jarEntries = jar.entries();
        while (jarEntries.hasMoreElements()) {
            String jarResourcePath = "/" + jarEntries.nextElement().getName();
            if (jarResourcePath.startsWith(resourcesPathPrefix) && !jarResourcePath.endsWith("/")) {
                LOGGER.debug("Copying " + jarResourcePath + " ...");
                copyJarResourceToFile(jarResourcePath, new File(RESOURCES_DIRECTORY + jarResourcePath));
            }
        }
        jar.close();
    }

    /**
     * Copies the resource from the given path in the jar, to the given file.
     *
     * @param jarResourcePath The resource path in the jar.
     * @param toFile          The target file.
     * @throws IOException If the failed to copy the resource due to I/O exception.
     */
    private static void copyJarResourceToFile(String jarResourcePath, File toFile) throws IOException {
        toFile.getParentFile().mkdirs();
        OutputStream toFileOutputStream = new FileOutputStream(toFile);
        InputStream jarResourceInputStream = ResourceManager.class.getResourceAsStream(jarResourcePath);
        int numberOfReadBytes;
        byte[] readBytes = new byte[1024];
        while ((numberOfReadBytes = jarResourceInputStream.read(readBytes)) != -1) {
            toFileOutputStream.write(readBytes, 0, numberOfReadBytes);
        }
    }

}
