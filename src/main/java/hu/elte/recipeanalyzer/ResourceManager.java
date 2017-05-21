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

    public static final String RESOURCES_DIRECTORY = "target";

    private ResourceManager() {
        throw new IllegalStateException("You cannot instantiate the resource manager.");
    }

    public static void copyResourcesFromJar(File fromJar, String resourcesPathPrefix) throws IOException {
        JarFile jar = new JarFile(fromJar);
        Enumeration<JarEntry> entries = jar.entries();
        while(entries.hasMoreElements()) {
            String resourcePath = "/" + entries.nextElement().getName();
            if (resourcePath.startsWith(resourcesPathPrefix) && !resourcePath.endsWith("/")) {
                LOGGER.debug("Copying " + resourcePath + " ...");
                copyJarResourceToFile(resourcePath, new File(RESOURCES_DIRECTORY + resourcePath));
            }
        }
        jar.close();
    }

    private static void copyJarResourceToFile(String jarResourcePath, File toFile) throws IOException {
        toFile.getParentFile().mkdirs();
        OutputStream toFileOutputStream = new FileOutputStream(toFile);
        InputStream jarResourceInputStream = PrepareWordVector.class.getResourceAsStream(jarResourcePath);
        int numberOfReadBytes;
        byte[] readBytes = new byte[1024];
        while ((numberOfReadBytes = jarResourceInputStream.read(readBytes)) != -1) {
            toFileOutputStream.write(readBytes, 0, numberOfReadBytes);
        }
    }
}
