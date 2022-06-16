package redcoder.rctexteditor.support;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageResources {

    private static final Logger LOGGER = Logger.getLogger(ImageResources.class.getName());
    private static final Map<String, Image> IMAGES = new HashMap<>();

    public static Image getImage(String imageName) {
        Image image = IMAGES.get(imageName);
        if (image == null) {
            InputStream inputStream = ImageResources.class.getResourceAsStream(imageName);
            if (inputStream == null) {
                inputStream = ImageResources.class.getClassLoader().getResourceAsStream(imageName);
            }
            if (inputStream != null) {
                image = new Image(inputStream);
                IMAGES.put(imageName, image);
            }
        }
        return image;
    }

    private static void loadImages() {
        try {
            ClassLoader classLoader = ImageResources.class.getClassLoader();
            URL url = classLoader.getResource("images");
            if (url == null) {
                return;
            }
            String str = url.toString();
            if (str.startsWith("file")) {
                loadLocalFile(url);
            } else if (str.startsWith("jar")) {
                LOGGER.info(() -> "Loading images from jar.");
                loadJarFile(classLoader, url);
            } else {
                LOGGER.log(Level.WARNING, "Unknown URL[ " + str + "], we can't handle it.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "failed to load images.", e);
        }
    }

    private static void loadLocalFile(URL url) throws Exception {
        File file = new File(url.toURI());
        loadImage(file.listFiles());
    }

    private static void loadImage(File[] files) throws Exception {
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                doLoadImage(f);
            } else {
                loadImage(f.listFiles());
            }
        }
    }

    private static void doLoadImage(File file) throws Exception {
        String imageName = file.getName();
        try (InputStream is = new FileInputStream(file)) {
            IMAGES.put(imageName, new Image(is));
        }
    }

    private static void loadJarFile(ClassLoader classLoader, URL url) throws Exception {
        JarURLConnection connection = (JarURLConnection) url.openConnection();
        JarFile jarFile = connection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            String name = entry.getName();
            String suffix = getEntryNameSuffix(name);
            if(SUPPORTED_IMAGE_FORMAT.contains(suffix)){
                InputStream is = classLoader.getResourceAsStream(name);
                if (is != null) {
                    Image image = new Image(is);
                    String imageName = name.substring(name.lastIndexOf("/") + 1);
                    IMAGES.put(imageName, image);
                    LOGGER.info(() -> String.format("%s loaded", imageName));
                }
            }
        }
    }

    private static String getEntryNameSuffix(String name) {
        int i = name.lastIndexOf(".");
        if (i > -1) {
            return name.substring(i).toLowerCase();
        }
        return "";
    }

    private static final List<String> SUPPORTED_IMAGE_FORMAT = Arrays.asList(".gif", ".png", ".jpg", ".jpeg");
    static {
        loadImages();
    }
}
