package org.jivesoftware.smack.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileUtils {
    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    public static InputStream getStreamForClasspathFile(String path, ClassLoader loader) throws IOException {
        List<ClassLoader> classLoaders = getClassLoaders();
        if (loader != null) {
            classLoaders.add(0, loader);
        }
        for (ClassLoader classLoader : classLoaders) {
            InputStream is = classLoader.getResourceAsStream(path);
            if (is != null) {
                return is;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unable to get '");
        sb.append(path);
        sb.append("' from classpath. Tried ClassLoaders:");
        sb.append(classLoaders);
        throw new IOException(sb.toString());
    }

    public static InputStream getStreamForUri(URI uri, ClassLoader loader) throws IOException {
        if (uri.getScheme().equals("classpath")) {
            return getStreamForClasspathFile(uri.getSchemeSpecificPart(), loader);
        }
        return uri.toURL().openStream();
    }

    public static List<ClassLoader> getClassLoaders() {
        ClassLoader[] classLoaders = {FileUtils.class.getClassLoader(), Thread.currentThread().getContextClassLoader()};
        List<ClassLoader> loaders = new ArrayList<>(classLoaders.length);
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                loaders.add(classLoader);
            }
        }
        return loaders;
    }

    public static boolean addLines(String uriString, Set<String> set) throws MalformedURLException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getStreamForUri(URI.create(uriString), null), StringUtils.UTF8));
        while (true) {
            String readLine = br.readLine();
            String line = readLine;
            if (readLine == null) {
                return true;
            }
            set.add(line);
        }
    }

    public static String readFileOrThrow(File file) throws IOException {
        Reader reader = null;
        try {
            Reader reader2 = new FileReader(file);
            char[] buf = new char[8192];
            StringBuilder s = new StringBuilder();
            while (true) {
                int read = reader2.read(buf);
                int len = read;
                if (read >= 0) {
                    s.append(buf, 0, len);
                } else {
                    String sb = s.toString();
                    reader2.close();
                    return sb;
                }
            }
        } catch (Throwable th) {
            if (reader != null) {
                reader.close();
            }
            throw th;
        }
    }

    public static String readFile(File file) {
        String str = "readFile";
        try {
            return readFileOrThrow(file);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, str, e);
            return null;
        } catch (IOException e2) {
            LOGGER.log(Level.WARNING, str, e2);
            return null;
        }
    }

    public static void writeFileOrThrow(File file, CharSequence content) throws IOException {
        FileWriter writer = new FileWriter(file, false);
        try {
            writer.write(content.toString());
        } finally {
            writer.close();
        }
    }

    public static boolean writeFile(File file, CharSequence content) {
        try {
            writeFileOrThrow(file, content);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "writeFile", e);
            return false;
        }
    }
}
