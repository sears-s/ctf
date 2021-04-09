package org.jivesoftware.smack.roster.rosterstore;

import com.badguy.terrortime.BuildConfig;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.roster.packet.RosterPacket.Item;
import org.jivesoftware.smack.roster.provider.RosterPacketProvider;
import org.jivesoftware.smack.util.FileUtils;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smack.util.stringencoder.Base32;
import org.jxmpp.jid.Jid;
import org.xmlpull.v1.XmlPullParserException;

public final class DirectoryRosterStore implements RosterStore {
    private static final String ENTRY_PREFIX = "entry-";
    private static final Logger LOGGER = Logger.getLogger(DirectoryRosterStore.class.getName());
    private static final String STORE_ID = "DEFAULT_ROSTER_STORE";
    private static final String VERSION_FILE_NAME = "__version__";
    private static final FileFilter rosterDirFilter = new FileFilter() {
        public boolean accept(File file) {
            return file.getName().startsWith(DirectoryRosterStore.ENTRY_PREFIX);
        }
    };
    private final File fileDir;

    private DirectoryRosterStore(File baseDir) {
        this.fileDir = baseDir;
    }

    public static DirectoryRosterStore init(File baseDir) {
        DirectoryRosterStore store = new DirectoryRosterStore(baseDir);
        if (store.setRosterVersion(BuildConfig.FLAVOR)) {
            return store;
        }
        return null;
    }

    public static DirectoryRosterStore open(File baseDir) {
        DirectoryRosterStore store = new DirectoryRosterStore(baseDir);
        String s = FileUtils.readFile(store.getVersionFile());
        if (s == null || !s.startsWith("DEFAULT_ROSTER_STORE\n")) {
            return null;
        }
        return store;
    }

    private File getVersionFile() {
        return new File(this.fileDir, VERSION_FILE_NAME);
    }

    public List<Item> getEntries() {
        List<Item> entries = new ArrayList<>();
        for (File file : this.fileDir.listFiles(rosterDirFilter)) {
            Item entry = readEntry(file);
            if (entry == null) {
                return null;
            }
            entries.add(entry);
        }
        return entries;
    }

    public Item getEntry(Jid bareJid) {
        return readEntry(getBareJidFile(bareJid));
    }

    public String getRosterVersion() {
        String s = FileUtils.readFile(getVersionFile());
        if (s == null) {
            return null;
        }
        String[] lines = s.split("\n", 2);
        if (lines.length < 2) {
            return null;
        }
        return lines[1];
    }

    private boolean setRosterVersion(String version) {
        File versionFile = getVersionFile();
        StringBuilder sb = new StringBuilder();
        sb.append("DEFAULT_ROSTER_STORE\n");
        sb.append(version);
        return FileUtils.writeFile(versionFile, sb.toString());
    }

    public boolean addEntry(Item item, String version) {
        return addEntryRaw(item) && setRosterVersion(version);
    }

    public boolean removeEntry(Jid bareJid, String version) {
        return getBareJidFile(bareJid).delete() && setRosterVersion(version);
    }

    public boolean resetEntries(Collection<Item> items, String version) {
        for (File file : this.fileDir.listFiles(rosterDirFilter)) {
            file.delete();
        }
        for (Item item : items) {
            if (!addEntryRaw(item)) {
                return false;
            }
        }
        return setRosterVersion(version);
    }

    public void resetStore() {
        resetEntries(Collections.emptyList(), BuildConfig.FLAVOR);
    }

    private static Item readEntry(File file) {
        try {
            Reader reader = new FileReader(file);
            try {
                Item item = RosterPacketProvider.parseItem(PacketParserUtils.getParserFor(reader));
                reader.close();
                return item;
            } catch (IOException | XmlPullParserException e) {
                String message = "Exception while parsing roster entry.";
                if (file.delete()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(message);
                    sb.append(" File was deleted.");
                    message = sb.toString();
                }
                LOGGER.log(Level.SEVERE, message, e);
                return null;
            }
        } catch (FileNotFoundException e2) {
            LOGGER.log(Level.FINE, "Roster entry file not found", e2);
            return null;
        }
    }

    private boolean addEntryRaw(Item item) {
        return FileUtils.writeFile(getBareJidFile(item.getJid()), item.toXML((String) null));
    }

    private File getBareJidFile(Jid bareJid) {
        String encodedJid = Base32.encode(bareJid.toString());
        File file = this.fileDir;
        StringBuilder sb = new StringBuilder();
        sb.append(ENTRY_PREFIX);
        sb.append(encodedJid);
        return new File(file, sb.toString());
    }
}
