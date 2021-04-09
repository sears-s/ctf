package org.jivesoftware.smackx.caps.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.stringencoder.Base32;
import org.jivesoftware.smack.util.stringencoder.StringEncoder;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

public class SimpleDirectoryPersistentCache implements EntityCapsPersistentCache {
    private static final Logger LOGGER = Logger.getLogger(SimpleDirectoryPersistentCache.class.getName());
    private final File cacheDir;
    private final StringEncoder filenameEncoder;

    public SimpleDirectoryPersistentCache(File cacheDir2) {
        this(cacheDir2, Base32.getStringEncoder());
    }

    public SimpleDirectoryPersistentCache(File cacheDir2, StringEncoder filenameEncoder2) {
        String str = "Cache directory \"";
        if (!cacheDir2.exists()) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(cacheDir2);
            sb.append("\" does not exist");
            throw new IllegalStateException(sb.toString());
        } else if (cacheDir2.isDirectory()) {
            this.cacheDir = cacheDir2;
            this.filenameEncoder = filenameEncoder2;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(cacheDir2);
            sb2.append("\" is not a directory");
            throw new IllegalStateException(sb2.toString());
        }
    }

    public void addDiscoverInfoByNodePersistent(String nodeVer, DiscoverInfo info) {
        File nodeFile = getFileFor(nodeVer);
        try {
            if (nodeFile.createNewFile()) {
                writeInfoToFile(nodeFile, info);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write disco info to file", e);
        }
    }

    public DiscoverInfo lookup(String nodeVer) {
        File nodeFile = getFileFor(nodeVer);
        if (!nodeFile.isFile()) {
            return null;
        }
        DiscoverInfo info = null;
        try {
            info = restoreInfoFromFile(nodeFile);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Coud not restore info from file", e);
        }
        return info;
    }

    private File getFileFor(String nodeVer) {
        return new File(this.cacheDir, this.filenameEncoder.encode(nodeVer));
    }

    public void emptyCache() {
        File[] files = this.cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    private static void writeInfoToFile(File file, DiscoverInfo info) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
        try {
            dos.writeUTF(info.toXML((String) null).toString());
        } finally {
            dos.close();
        }
    }

    private static DiscoverInfo restoreInfoFromFile(File file) throws Exception {
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        try {
            String fileContent = dis.readUTF();
            if (fileContent == null) {
                return null;
            }
            return (DiscoverInfo) PacketParserUtils.parseStanza(fileContent);
        } finally {
            dis.close();
        }
    }
}
