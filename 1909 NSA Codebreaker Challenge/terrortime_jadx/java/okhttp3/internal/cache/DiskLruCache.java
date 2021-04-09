package okhttp3.internal.cache;

import com.badguy.terrortime.BuildConfig;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import okhttp3.internal.Util;
import okhttp3.internal.io.FileSystem;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class DiskLruCache implements Closeable, Flushable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final long ANY_SEQUENCE_NUMBER = -1;
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    static final String MAGIC = "libcore.io.DiskLruCache";
    private static final String READ = "READ";
    private static final String REMOVE = "REMOVE";
    static final String VERSION_1 = "1";
    private final int appVersion;
    private final Runnable cleanupRunnable = new Runnable() {
        public void run() {
            synchronized (DiskLruCache.this) {
                if (!(!DiskLruCache.this.initialized) && !DiskLruCache.this.closed) {
                    try {
                        DiskLruCache.this.trimToSize();
                    } catch (IOException e) {
                        DiskLruCache.this.mostRecentTrimFailed = true;
                    }
                    try {
                        if (DiskLruCache.this.journalRebuildRequired()) {
                            DiskLruCache.this.rebuildJournal();
                            DiskLruCache.this.redundantOpCount = 0;
                        }
                    } catch (IOException e2) {
                        DiskLruCache.this.mostRecentRebuildFailed = true;
                        DiskLruCache.this.journalWriter = Okio.buffer(Okio.blackhole());
                    }
                } else {
                    return;
                }
            }
            return;
        }
    };
    boolean closed;
    final File directory;
    private final Executor executor;
    final FileSystem fileSystem;
    boolean hasJournalErrors;
    boolean initialized;
    private final File journalFile;
    private final File journalFileBackup;
    private final File journalFileTmp;
    BufferedSink journalWriter;
    final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<>(0, 0.75f, true);
    private long maxSize;
    boolean mostRecentRebuildFailed;
    boolean mostRecentTrimFailed;
    private long nextSequenceNumber = 0;
    int redundantOpCount;
    private long size = 0;
    final int valueCount;

    public final class Editor {
        private boolean done;
        final Entry entry;
        final boolean[] written;

        Editor(Entry entry2) {
            this.entry = entry2;
            this.written = entry2.readable ? null : new boolean[DiskLruCache.this.valueCount];
        }

        /* access modifiers changed from: 0000 */
        public void detach() {
            if (this.entry.currentEditor == this) {
                for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                    try {
                        DiskLruCache.this.fileSystem.delete(this.entry.dirtyFiles[i]);
                    } catch (IOException e) {
                    }
                }
                this.entry.currentEditor = null;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
            return null;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public okio.Source newSource(int r5) {
            /*
                r4 = this;
                okhttp3.internal.cache.DiskLruCache r0 = okhttp3.internal.cache.DiskLruCache.this
                monitor-enter(r0)
                boolean r1 = r4.done     // Catch:{ all -> 0x0030 }
                if (r1 != 0) goto L_0x002a
                okhttp3.internal.cache.DiskLruCache$Entry r1 = r4.entry     // Catch:{ all -> 0x0030 }
                boolean r1 = r1.readable     // Catch:{ all -> 0x0030 }
                r2 = 0
                if (r1 == 0) goto L_0x0028
                okhttp3.internal.cache.DiskLruCache$Entry r1 = r4.entry     // Catch:{ all -> 0x0030 }
                okhttp3.internal.cache.DiskLruCache$Editor r1 = r1.currentEditor     // Catch:{ all -> 0x0030 }
                if (r1 == r4) goto L_0x0015
                goto L_0x0028
            L_0x0015:
                okhttp3.internal.cache.DiskLruCache r1 = okhttp3.internal.cache.DiskLruCache.this     // Catch:{ FileNotFoundException -> 0x0025 }
                okhttp3.internal.io.FileSystem r1 = r1.fileSystem     // Catch:{ FileNotFoundException -> 0x0025 }
                okhttp3.internal.cache.DiskLruCache$Entry r3 = r4.entry     // Catch:{ FileNotFoundException -> 0x0025 }
                java.io.File[] r3 = r3.cleanFiles     // Catch:{ FileNotFoundException -> 0x0025 }
                r3 = r3[r5]     // Catch:{ FileNotFoundException -> 0x0025 }
                okio.Source r1 = r1.source(r3)     // Catch:{ FileNotFoundException -> 0x0025 }
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                return r1
            L_0x0025:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                return r2
            L_0x0028:
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                return r2
            L_0x002a:
                java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0030 }
                r1.<init>()     // Catch:{ all -> 0x0030 }
                throw r1     // Catch:{ all -> 0x0030 }
            L_0x0030:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0030 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.Editor.newSource(int):okio.Source");
        }

        public Sink newSink(int index) {
            synchronized (DiskLruCache.this) {
                if (this.done) {
                    throw new IllegalStateException();
                } else if (this.entry.currentEditor != this) {
                    Sink blackhole = Okio.blackhole();
                    return blackhole;
                } else {
                    if (!this.entry.readable) {
                        this.written[index] = true;
                    }
                    try {
                        AnonymousClass1 r3 = new FaultHidingSink(DiskLruCache.this.fileSystem.sink(this.entry.dirtyFiles[index])) {
                            /* access modifiers changed from: protected */
                            public void onException(IOException e) {
                                synchronized (DiskLruCache.this) {
                                    Editor.this.detach();
                                }
                            }
                        };
                        return r3;
                    } catch (FileNotFoundException e) {
                        return Okio.blackhole();
                    }
                }
            }
        }

        public void commit() throws IOException {
            synchronized (DiskLruCache.this) {
                if (!this.done) {
                    if (this.entry.currentEditor == this) {
                        DiskLruCache.this.completeEdit(this, true);
                    }
                    this.done = true;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        public void abort() throws IOException {
            synchronized (DiskLruCache.this) {
                if (!this.done) {
                    if (this.entry.currentEditor == this) {
                        DiskLruCache.this.completeEdit(this, false);
                    }
                    this.done = true;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        public void abortUnlessCommitted() {
            synchronized (DiskLruCache.this) {
                if (!this.done && this.entry.currentEditor == this) {
                    try {
                        DiskLruCache.this.completeEdit(this, false);
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    private final class Entry {
        final File[] cleanFiles;
        Editor currentEditor;
        final File[] dirtyFiles;
        final String key;
        final long[] lengths;
        boolean readable;
        long sequenceNumber;

        Entry(String key2) {
            this.key = key2;
            this.lengths = new long[DiskLruCache.this.valueCount];
            this.cleanFiles = new File[DiskLruCache.this.valueCount];
            this.dirtyFiles = new File[DiskLruCache.this.valueCount];
            StringBuilder fileBuilder = new StringBuilder(key2).append('.');
            int truncateTo = fileBuilder.length();
            for (int i = 0; i < DiskLruCache.this.valueCount; i++) {
                fileBuilder.append(i);
                this.cleanFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                this.dirtyFiles[i] = new File(DiskLruCache.this.directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }
        }

        /* access modifiers changed from: 0000 */
        public void setLengths(String[] strings) throws IOException {
            if (strings.length == DiskLruCache.this.valueCount) {
                int i = 0;
                while (i < strings.length) {
                    try {
                        this.lengths[i] = Long.parseLong(strings[i]);
                        i++;
                    } catch (NumberFormatException e) {
                        throw invalidLengths(strings);
                    }
                }
                return;
            }
            throw invalidLengths(strings);
        }

        /* access modifiers changed from: 0000 */
        public void writeLengths(BufferedSink writer) throws IOException {
            for (long length : this.lengths) {
                writer.writeByte(32).writeDecimalLong(length);
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("unexpected journal line: ");
            sb.append(Arrays.toString(strings));
            throw new IOException(sb.toString());
        }

        /* access modifiers changed from: 0000 */
        public Snapshot snapshot() {
            if (Thread.holdsLock(DiskLruCache.this)) {
                Source[] sources = new Source[DiskLruCache.this.valueCount];
                long[] lengths2 = (long[]) this.lengths.clone();
                int i = 0;
                while (i < DiskLruCache.this.valueCount) {
                    try {
                        sources[i] = DiskLruCache.this.fileSystem.source(this.cleanFiles[i]);
                        i++;
                    } catch (FileNotFoundException e) {
                        int i2 = 0;
                        while (i2 < DiskLruCache.this.valueCount && sources[i2] != null) {
                            Util.closeQuietly((Closeable) sources[i2]);
                            i2++;
                        }
                        try {
                            DiskLruCache.this.removeEntry(this);
                        } catch (IOException e2) {
                        }
                        return null;
                    }
                }
                Snapshot snapshot = new Snapshot(this.key, this.sequenceNumber, sources, lengths2);
                return snapshot;
            }
            throw new AssertionError();
        }
    }

    public final class Snapshot implements Closeable {
        /* access modifiers changed from: private */
        public final String key;
        private final long[] lengths;
        private final long sequenceNumber;
        private final Source[] sources;

        Snapshot(String key2, long sequenceNumber2, Source[] sources2, long[] lengths2) {
            this.key = key2;
            this.sequenceNumber = sequenceNumber2;
            this.sources = sources2;
            this.lengths = lengths2;
        }

        public String key() {
            return this.key;
        }

        @Nullable
        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(this.key, this.sequenceNumber);
        }

        public Source getSource(int index) {
            return this.sources[index];
        }

        public long getLength(int index) {
            return this.lengths[index];
        }

        public void close() {
            for (Source in : this.sources) {
                Util.closeQuietly((Closeable) in);
            }
        }
    }

    DiskLruCache(FileSystem fileSystem2, File directory2, int appVersion2, int valueCount2, long maxSize2, Executor executor2) {
        this.fileSystem = fileSystem2;
        this.directory = directory2;
        this.appVersion = appVersion2;
        this.journalFile = new File(directory2, JOURNAL_FILE);
        this.journalFileTmp = new File(directory2, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(directory2, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount2;
        this.maxSize = maxSize2;
        this.executor = executor2;
    }

    public synchronized void initialize() throws IOException {
        if (!this.initialized) {
            if (this.fileSystem.exists(this.journalFileBackup)) {
                if (this.fileSystem.exists(this.journalFile)) {
                    this.fileSystem.delete(this.journalFileBackup);
                } else {
                    this.fileSystem.rename(this.journalFileBackup, this.journalFile);
                }
            }
            if (this.fileSystem.exists(this.journalFile)) {
                try {
                    readJournal();
                    processJournal();
                    this.initialized = true;
                    return;
                } catch (IOException journalIsCorrupt) {
                    Platform platform = Platform.get();
                    StringBuilder sb = new StringBuilder();
                    sb.append("DiskLruCache ");
                    sb.append(this.directory);
                    sb.append(" is corrupt: ");
                    sb.append(journalIsCorrupt.getMessage());
                    sb.append(", removing");
                    platform.log(5, sb.toString(), journalIsCorrupt);
                    delete();
                    this.closed = false;
                } catch (Throwable th) {
                    this.closed = false;
                    throw th;
                }
            }
            rebuildJournal();
            this.initialized = true;
        }
    }

    public static DiskLruCache create(FileSystem fileSystem2, File directory2, int appVersion2, int valueCount2, long maxSize2) {
        if (maxSize2 <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        } else if (valueCount2 > 0) {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp DiskLruCache", true));
            DiskLruCache diskLruCache = new DiskLruCache(fileSystem2, directory2, appVersion2, valueCount2, maxSize2, threadPoolExecutor);
            return diskLruCache;
        } else {
            throw new IllegalArgumentException("valueCount <= 0");
        }
    }

    private void readJournal() throws IOException {
        int lineCount;
        String str = ", ";
        BufferedSource source = Okio.buffer(this.fileSystem.source(this.journalFile));
        try {
            String magic = source.readUtf8LineStrict();
            String version = source.readUtf8LineStrict();
            String appVersionString = source.readUtf8LineStrict();
            String valueCountString = source.readUtf8LineStrict();
            String blank = source.readUtf8LineStrict();
            if (!MAGIC.equals(magic) || !VERSION_1.equals(version) || !Integer.toString(this.appVersion).equals(appVersionString) || !Integer.toString(this.valueCount).equals(valueCountString) || !BuildConfig.FLAVOR.equals(blank)) {
                StringBuilder sb = new StringBuilder();
                sb.append("unexpected journal header: [");
                sb.append(magic);
                sb.append(str);
                sb.append(version);
                sb.append(str);
                sb.append(valueCountString);
                sb.append(str);
                sb.append(blank);
                sb.append("]");
                throw new IOException(sb.toString());
            }
            lineCount = 0;
            while (true) {
                readJournalLine(source.readUtf8LineStrict());
                lineCount++;
            }
        } catch (EOFException e) {
            this.redundantOpCount = lineCount - this.lruEntries.size();
            if (!source.exhausted()) {
                rebuildJournal();
            } else {
                this.journalWriter = newJournalWriter();
            }
            Util.closeQuietly((Closeable) source);
        } catch (Throwable th) {
            Util.closeQuietly((Closeable) source);
            throw th;
        }
    }

    private BufferedSink newJournalWriter() throws FileNotFoundException {
        return Okio.buffer(new FaultHidingSink(this.fileSystem.appendingSink(this.journalFile)) {
            static final /* synthetic */ boolean $assertionsDisabled = false;

            static {
                Class<DiskLruCache> cls = DiskLruCache.class;
            }

            /* access modifiers changed from: protected */
            public void onException(IOException e) {
                DiskLruCache.this.hasJournalErrors = true;
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0090, code lost:
        if (r10.startsWith(r3) != false) goto L_0x0092;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readJournalLine(java.lang.String r10) throws java.io.IOException {
        /*
            r9 = this;
            r0 = 32
            int r1 = r10.indexOf(r0)
            java.lang.String r2 = "unexpected journal line: "
            r3 = -1
            if (r1 == r3) goto L_0x00a8
            int r4 = r1 + 1
            int r0 = r10.indexOf(r0, r4)
            if (r0 != r3) goto L_0x002b
            java.lang.String r5 = r10.substring(r4)
            java.lang.String r6 = "REMOVE"
            int r7 = r6.length()
            if (r1 != r7) goto L_0x002f
            boolean r6 = r10.startsWith(r6)
            if (r6 == 0) goto L_0x002f
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r2 = r9.lruEntries
            r2.remove(r5)
            return
        L_0x002b:
            java.lang.String r5 = r10.substring(r4, r0)
        L_0x002f:
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r6 = r9.lruEntries
            java.lang.Object r6 = r6.get(r5)
            okhttp3.internal.cache.DiskLruCache$Entry r6 = (okhttp3.internal.cache.DiskLruCache.Entry) r6
            if (r6 != 0) goto L_0x0044
            okhttp3.internal.cache.DiskLruCache$Entry r7 = new okhttp3.internal.cache.DiskLruCache$Entry
            r7.<init>(r5)
            r6 = r7
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r7 = r9.lruEntries
            r7.put(r5, r6)
        L_0x0044:
            if (r0 == r3) goto L_0x006a
            java.lang.String r7 = "CLEAN"
            int r8 = r7.length()
            if (r1 != r8) goto L_0x006a
            boolean r7 = r10.startsWith(r7)
            if (r7 == 0) goto L_0x006a
            int r2 = r0 + 1
            java.lang.String r2 = r10.substring(r2)
            java.lang.String r3 = " "
            java.lang.String[] r2 = r2.split(r3)
            r3 = 1
            r6.readable = r3
            r3 = 0
            r6.currentEditor = r3
            r6.setLengths(r2)
            goto L_0x0092
        L_0x006a:
            if (r0 != r3) goto L_0x0082
            java.lang.String r7 = "DIRTY"
            int r8 = r7.length()
            if (r1 != r8) goto L_0x0082
            boolean r7 = r10.startsWith(r7)
            if (r7 == 0) goto L_0x0082
            okhttp3.internal.cache.DiskLruCache$Editor r2 = new okhttp3.internal.cache.DiskLruCache$Editor
            r2.<init>(r6)
            r6.currentEditor = r2
            goto L_0x0092
        L_0x0082:
            if (r0 != r3) goto L_0x0093
            java.lang.String r3 = "READ"
            int r7 = r3.length()
            if (r1 != r7) goto L_0x0093
            boolean r3 = r10.startsWith(r3)
            if (r3 == 0) goto L_0x0093
        L_0x0092:
            return
        L_0x0093:
            java.io.IOException r3 = new java.io.IOException
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            r7.append(r10)
            java.lang.String r2 = r7.toString()
            r3.<init>(r2)
            throw r3
        L_0x00a8:
            java.io.IOException r0 = new java.io.IOException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r10)
            java.lang.String r2 = r3.toString()
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.readJournalLine(java.lang.String):void");
    }

    private void processJournal() throws IOException {
        this.fileSystem.delete(this.journalFileTmp);
        Iterator<Entry> i = this.lruEntries.values().iterator();
        while (i.hasNext()) {
            Entry entry = (Entry) i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < this.valueCount; t++) {
                    this.size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (int t2 = 0; t2 < this.valueCount; t2++) {
                    this.fileSystem.delete(entry.cleanFiles[t2]);
                    this.fileSystem.delete(entry.dirtyFiles[t2]);
                }
                i.remove();
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: 0000 */
    public synchronized void rebuildJournal() throws IOException {
        if (this.journalWriter != null) {
            this.journalWriter.close();
        }
        BufferedSink writer = Okio.buffer(this.fileSystem.sink(this.journalFileTmp));
        try {
            writer.writeUtf8(MAGIC).writeByte(10);
            writer.writeUtf8(VERSION_1).writeByte(10);
            writer.writeDecimalLong((long) this.appVersion).writeByte(10);
            writer.writeDecimalLong((long) this.valueCount).writeByte(10);
            writer.writeByte(10);
            for (Entry entry : this.lruEntries.values()) {
                if (entry.currentEditor != null) {
                    writer.writeUtf8(DIRTY).writeByte(32);
                    writer.writeUtf8(entry.key);
                    writer.writeByte(10);
                } else {
                    writer.writeUtf8(CLEAN).writeByte(32);
                    writer.writeUtf8(entry.key);
                    entry.writeLengths(writer);
                    writer.writeByte(10);
                }
            }
            writer.close();
            if (this.fileSystem.exists(this.journalFile)) {
                this.fileSystem.rename(this.journalFile, this.journalFileBackup);
            }
            this.fileSystem.rename(this.journalFileTmp, this.journalFile);
            this.fileSystem.delete(this.journalFileBackup);
            this.journalWriter = newJournalWriter();
            this.hasJournalErrors = false;
            this.mostRecentRebuildFailed = false;
        } catch (Throwable th) {
            writer.close();
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004d, code lost:
        return r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004f, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized okhttp3.internal.cache.DiskLruCache.Snapshot get(java.lang.String r5) throws java.io.IOException {
        /*
            r4 = this;
            monitor-enter(r4)
            r4.initialize()     // Catch:{ all -> 0x0050 }
            r4.checkNotClosed()     // Catch:{ all -> 0x0050 }
            r4.validateKey(r5)     // Catch:{ all -> 0x0050 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r4.lruEntries     // Catch:{ all -> 0x0050 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x0050 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x0050 }
            r1 = 0
            if (r0 == 0) goto L_0x004e
            boolean r2 = r0.readable     // Catch:{ all -> 0x0050 }
            if (r2 != 0) goto L_0x001a
            goto L_0x004e
        L_0x001a:
            okhttp3.internal.cache.DiskLruCache$Snapshot r2 = r0.snapshot()     // Catch:{ all -> 0x0050 }
            if (r2 != 0) goto L_0x0022
            monitor-exit(r4)
            return r1
        L_0x0022:
            int r1 = r4.redundantOpCount     // Catch:{ all -> 0x0050 }
            int r1 = r1 + 1
            r4.redundantOpCount = r1     // Catch:{ all -> 0x0050 }
            okio.BufferedSink r1 = r4.journalWriter     // Catch:{ all -> 0x0050 }
            java.lang.String r3 = "READ"
            okio.BufferedSink r1 = r1.writeUtf8(r3)     // Catch:{ all -> 0x0050 }
            r3 = 32
            okio.BufferedSink r1 = r1.writeByte(r3)     // Catch:{ all -> 0x0050 }
            okio.BufferedSink r1 = r1.writeUtf8(r5)     // Catch:{ all -> 0x0050 }
            r3 = 10
            r1.writeByte(r3)     // Catch:{ all -> 0x0050 }
            boolean r1 = r4.journalRebuildRequired()     // Catch:{ all -> 0x0050 }
            if (r1 == 0) goto L_0x004c
            java.util.concurrent.Executor r1 = r4.executor     // Catch:{ all -> 0x0050 }
            java.lang.Runnable r3 = r4.cleanupRunnable     // Catch:{ all -> 0x0050 }
            r1.execute(r3)     // Catch:{ all -> 0x0050 }
        L_0x004c:
            monitor-exit(r4)
            return r2
        L_0x004e:
            monitor-exit(r4)
            return r1
        L_0x0050:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.get(java.lang.String):okhttp3.internal.cache.DiskLruCache$Snapshot");
    }

    @Nullable
    public Editor edit(String key) throws IOException {
        return edit(key, -1);
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0022, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized okhttp3.internal.cache.DiskLruCache.Editor edit(java.lang.String r6, long r7) throws java.io.IOException {
        /*
            r5 = this;
            monitor-enter(r5)
            r5.initialize()     // Catch:{ all -> 0x0075 }
            r5.checkNotClosed()     // Catch:{ all -> 0x0075 }
            r5.validateKey(r6)     // Catch:{ all -> 0x0075 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r5.lruEntries     // Catch:{ all -> 0x0075 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x0075 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x0075 }
            r1 = -1
            int r1 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            r2 = 0
            if (r1 == 0) goto L_0x0023
            if (r0 == 0) goto L_0x0021
            long r3 = r0.sequenceNumber     // Catch:{ all -> 0x0075 }
            int r1 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x0023
        L_0x0021:
            monitor-exit(r5)
            return r2
        L_0x0023:
            if (r0 == 0) goto L_0x002b
            okhttp3.internal.cache.DiskLruCache$Editor r1 = r0.currentEditor     // Catch:{ all -> 0x0075 }
            if (r1 == 0) goto L_0x002b
            monitor-exit(r5)
            return r2
        L_0x002b:
            boolean r1 = r5.mostRecentTrimFailed     // Catch:{ all -> 0x0075 }
            if (r1 != 0) goto L_0x006c
            boolean r1 = r5.mostRecentRebuildFailed     // Catch:{ all -> 0x0075 }
            if (r1 == 0) goto L_0x0034
            goto L_0x006c
        L_0x0034:
            okio.BufferedSink r1 = r5.journalWriter     // Catch:{ all -> 0x0075 }
            java.lang.String r3 = "DIRTY"
            okio.BufferedSink r1 = r1.writeUtf8(r3)     // Catch:{ all -> 0x0075 }
            r3 = 32
            okio.BufferedSink r1 = r1.writeByte(r3)     // Catch:{ all -> 0x0075 }
            okio.BufferedSink r1 = r1.writeUtf8(r6)     // Catch:{ all -> 0x0075 }
            r3 = 10
            r1.writeByte(r3)     // Catch:{ all -> 0x0075 }
            okio.BufferedSink r1 = r5.journalWriter     // Catch:{ all -> 0x0075 }
            r1.flush()     // Catch:{ all -> 0x0075 }
            boolean r1 = r5.hasJournalErrors     // Catch:{ all -> 0x0075 }
            if (r1 == 0) goto L_0x0056
            monitor-exit(r5)
            return r2
        L_0x0056:
            if (r0 != 0) goto L_0x0063
            okhttp3.internal.cache.DiskLruCache$Entry r1 = new okhttp3.internal.cache.DiskLruCache$Entry     // Catch:{ all -> 0x0075 }
            r1.<init>(r6)     // Catch:{ all -> 0x0075 }
            r0 = r1
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r1 = r5.lruEntries     // Catch:{ all -> 0x0075 }
            r1.put(r6, r0)     // Catch:{ all -> 0x0075 }
        L_0x0063:
            okhttp3.internal.cache.DiskLruCache$Editor r1 = new okhttp3.internal.cache.DiskLruCache$Editor     // Catch:{ all -> 0x0075 }
            r1.<init>(r0)     // Catch:{ all -> 0x0075 }
            r0.currentEditor = r1     // Catch:{ all -> 0x0075 }
            monitor-exit(r5)
            return r1
        L_0x006c:
            java.util.concurrent.Executor r1 = r5.executor     // Catch:{ all -> 0x0075 }
            java.lang.Runnable r3 = r5.cleanupRunnable     // Catch:{ all -> 0x0075 }
            r1.execute(r3)     // Catch:{ all -> 0x0075 }
            monitor-exit(r5)
            return r2
        L_0x0075:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.edit(java.lang.String, long):okhttp3.internal.cache.DiskLruCache$Editor");
    }

    public File getDirectory() {
        return this.directory;
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized void setMaxSize(long maxSize2) {
        this.maxSize = maxSize2;
        if (this.initialized) {
            this.executor.execute(this.cleanupRunnable);
        }
    }

    public synchronized long size() throws IOException {
        initialize();
        return this.size;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00f6, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void completeEdit(okhttp3.internal.cache.DiskLruCache.Editor r11, boolean r12) throws java.io.IOException {
        /*
            r10 = this;
            monitor-enter(r10)
            okhttp3.internal.cache.DiskLruCache$Entry r0 = r11.entry     // Catch:{ all -> 0x00fd }
            okhttp3.internal.cache.DiskLruCache$Editor r1 = r0.currentEditor     // Catch:{ all -> 0x00fd }
            if (r1 != r11) goto L_0x00f7
            r1 = 0
            if (r12 == 0) goto L_0x0047
            boolean r2 = r0.readable     // Catch:{ all -> 0x00fd }
            if (r2 != 0) goto L_0x0047
            r2 = r1
        L_0x000f:
            int r3 = r10.valueCount     // Catch:{ all -> 0x00fd }
            if (r2 >= r3) goto L_0x0047
            boolean[] r3 = r11.written     // Catch:{ all -> 0x00fd }
            boolean r3 = r3[r2]     // Catch:{ all -> 0x00fd }
            if (r3 == 0) goto L_0x002d
            okhttp3.internal.io.FileSystem r3 = r10.fileSystem     // Catch:{ all -> 0x00fd }
            java.io.File[] r4 = r0.dirtyFiles     // Catch:{ all -> 0x00fd }
            r4 = r4[r2]     // Catch:{ all -> 0x00fd }
            boolean r3 = r3.exists(r4)     // Catch:{ all -> 0x00fd }
            if (r3 != 0) goto L_0x002a
            r11.abort()     // Catch:{ all -> 0x00fd }
            monitor-exit(r10)
            return
        L_0x002a:
            int r2 = r2 + 1
            goto L_0x000f
        L_0x002d:
            r11.abort()     // Catch:{ all -> 0x00fd }
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00fd }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fd }
            r3.<init>()     // Catch:{ all -> 0x00fd }
            java.lang.String r4 = "Newly created entry didn't create value for index "
            r3.append(r4)     // Catch:{ all -> 0x00fd }
            r3.append(r2)     // Catch:{ all -> 0x00fd }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00fd }
            r1.<init>(r3)     // Catch:{ all -> 0x00fd }
            throw r1     // Catch:{ all -> 0x00fd }
        L_0x0047:
        L_0x0048:
            int r2 = r10.valueCount     // Catch:{ all -> 0x00fd }
            if (r1 >= r2) goto L_0x0081
            java.io.File[] r2 = r0.dirtyFiles     // Catch:{ all -> 0x00fd }
            r2 = r2[r1]     // Catch:{ all -> 0x00fd }
            if (r12 == 0) goto L_0x0079
            okhttp3.internal.io.FileSystem r3 = r10.fileSystem     // Catch:{ all -> 0x00fd }
            boolean r3 = r3.exists(r2)     // Catch:{ all -> 0x00fd }
            if (r3 == 0) goto L_0x007e
            java.io.File[] r3 = r0.cleanFiles     // Catch:{ all -> 0x00fd }
            r3 = r3[r1]     // Catch:{ all -> 0x00fd }
            okhttp3.internal.io.FileSystem r4 = r10.fileSystem     // Catch:{ all -> 0x00fd }
            r4.rename(r2, r3)     // Catch:{ all -> 0x00fd }
            long[] r4 = r0.lengths     // Catch:{ all -> 0x00fd }
            r5 = r4[r1]     // Catch:{ all -> 0x00fd }
            r4 = r5
            okhttp3.internal.io.FileSystem r6 = r10.fileSystem     // Catch:{ all -> 0x00fd }
            long r6 = r6.size(r3)     // Catch:{ all -> 0x00fd }
            long[] r8 = r0.lengths     // Catch:{ all -> 0x00fd }
            r8[r1] = r6     // Catch:{ all -> 0x00fd }
            long r8 = r10.size     // Catch:{ all -> 0x00fd }
            long r8 = r8 - r4
            long r8 = r8 + r6
            r10.size = r8     // Catch:{ all -> 0x00fd }
            goto L_0x007e
        L_0x0079:
            okhttp3.internal.io.FileSystem r3 = r10.fileSystem     // Catch:{ all -> 0x00fd }
            r3.delete(r2)     // Catch:{ all -> 0x00fd }
        L_0x007e:
            int r1 = r1 + 1
            goto L_0x0048
        L_0x0081:
            int r1 = r10.redundantOpCount     // Catch:{ all -> 0x00fd }
            r2 = 1
            int r1 = r1 + r2
            r10.redundantOpCount = r1     // Catch:{ all -> 0x00fd }
            r1 = 0
            r0.currentEditor = r1     // Catch:{ all -> 0x00fd }
            boolean r1 = r0.readable     // Catch:{ all -> 0x00fd }
            r1 = r1 | r12
            r3 = 10
            r4 = 32
            if (r1 == 0) goto L_0x00bd
            r0.readable = r2     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            java.lang.String r2 = "CLEAN"
            okio.BufferedSink r1 = r1.writeUtf8(r2)     // Catch:{ all -> 0x00fd }
            r1.writeByte(r4)     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            java.lang.String r2 = r0.key     // Catch:{ all -> 0x00fd }
            r1.writeUtf8(r2)     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            r0.writeLengths(r1)     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            r1.writeByte(r3)     // Catch:{ all -> 0x00fd }
            if (r12 == 0) goto L_0x00db
            long r1 = r10.nextSequenceNumber     // Catch:{ all -> 0x00fd }
            r3 = 1
            long r3 = r3 + r1
            r10.nextSequenceNumber = r3     // Catch:{ all -> 0x00fd }
            r0.sequenceNumber = r1     // Catch:{ all -> 0x00fd }
            goto L_0x00db
        L_0x00bd:
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r1 = r10.lruEntries     // Catch:{ all -> 0x00fd }
            java.lang.String r2 = r0.key     // Catch:{ all -> 0x00fd }
            r1.remove(r2)     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            java.lang.String r2 = "REMOVE"
            okio.BufferedSink r1 = r1.writeUtf8(r2)     // Catch:{ all -> 0x00fd }
            r1.writeByte(r4)     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            java.lang.String r2 = r0.key     // Catch:{ all -> 0x00fd }
            r1.writeUtf8(r2)     // Catch:{ all -> 0x00fd }
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            r1.writeByte(r3)     // Catch:{ all -> 0x00fd }
        L_0x00db:
            okio.BufferedSink r1 = r10.journalWriter     // Catch:{ all -> 0x00fd }
            r1.flush()     // Catch:{ all -> 0x00fd }
            long r1 = r10.size     // Catch:{ all -> 0x00fd }
            long r3 = r10.maxSize     // Catch:{ all -> 0x00fd }
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 > 0) goto L_0x00ee
            boolean r1 = r10.journalRebuildRequired()     // Catch:{ all -> 0x00fd }
            if (r1 == 0) goto L_0x00f5
        L_0x00ee:
            java.util.concurrent.Executor r1 = r10.executor     // Catch:{ all -> 0x00fd }
            java.lang.Runnable r2 = r10.cleanupRunnable     // Catch:{ all -> 0x00fd }
            r1.execute(r2)     // Catch:{ all -> 0x00fd }
        L_0x00f5:
            monitor-exit(r10)
            return
        L_0x00f7:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00fd }
            r1.<init>()     // Catch:{ all -> 0x00fd }
            throw r1     // Catch:{ all -> 0x00fd }
        L_0x00fd:
            r11 = move-exception
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.completeEdit(okhttp3.internal.cache.DiskLruCache$Editor, boolean):void");
    }

    /* access modifiers changed from: 0000 */
    public boolean journalRebuildRequired() {
        int i = this.redundantOpCount;
        return i >= 2000 && i >= this.lruEntries.size();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean remove(java.lang.String r8) throws java.io.IOException {
        /*
            r7 = this;
            monitor-enter(r7)
            r7.initialize()     // Catch:{ all -> 0x0029 }
            r7.checkNotClosed()     // Catch:{ all -> 0x0029 }
            r7.validateKey(r8)     // Catch:{ all -> 0x0029 }
            java.util.LinkedHashMap<java.lang.String, okhttp3.internal.cache.DiskLruCache$Entry> r0 = r7.lruEntries     // Catch:{ all -> 0x0029 }
            java.lang.Object r0 = r0.get(r8)     // Catch:{ all -> 0x0029 }
            okhttp3.internal.cache.DiskLruCache$Entry r0 = (okhttp3.internal.cache.DiskLruCache.Entry) r0     // Catch:{ all -> 0x0029 }
            r1 = 0
            if (r0 != 0) goto L_0x0017
            monitor-exit(r7)
            return r1
        L_0x0017:
            boolean r2 = r7.removeEntry(r0)     // Catch:{ all -> 0x0029 }
            if (r2 == 0) goto L_0x0027
            long r3 = r7.size     // Catch:{ all -> 0x0029 }
            long r5 = r7.maxSize     // Catch:{ all -> 0x0029 }
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 > 0) goto L_0x0027
            r7.mostRecentTrimFailed = r1     // Catch:{ all -> 0x0029 }
        L_0x0027:
            monitor-exit(r7)
            return r2
        L_0x0029:
            r8 = move-exception
            monitor-exit(r7)
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache.DiskLruCache.remove(java.lang.String):boolean");
    }

    /* access modifiers changed from: 0000 */
    public boolean removeEntry(Entry entry) throws IOException {
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }
        for (int i = 0; i < this.valueCount; i++) {
            this.fileSystem.delete(entry.cleanFiles[i]);
            this.size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }
        this.redundantOpCount++;
        this.journalWriter.writeUtf8(REMOVE).writeByte(32).writeUtf8(entry.key).writeByte(10);
        this.lruEntries.remove(entry.key);
        if (journalRebuildRequired()) {
            this.executor.execute(this.cleanupRunnable);
        }
        return true;
    }

    public synchronized boolean isClosed() {
        return this.closed;
    }

    private synchronized void checkNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cache is closed");
        }
    }

    public synchronized void flush() throws IOException {
        if (this.initialized) {
            checkNotClosed();
            trimToSize();
            this.journalWriter.flush();
        }
    }

    public synchronized void close() throws IOException {
        Entry[] entryArr;
        if (this.initialized) {
            if (!this.closed) {
                for (Entry entry : (Entry[]) this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
                    if (entry.currentEditor != null) {
                        entry.currentEditor.abort();
                    }
                }
                trimToSize();
                this.journalWriter.close();
                this.journalWriter = null;
                this.closed = true;
                return;
            }
        }
        this.closed = true;
    }

    /* access modifiers changed from: 0000 */
    public void trimToSize() throws IOException {
        while (this.size > this.maxSize) {
            removeEntry((Entry) this.lruEntries.values().iterator().next());
        }
        this.mostRecentTrimFailed = false;
    }

    public void delete() throws IOException {
        close();
        this.fileSystem.deleteContents(this.directory);
    }

    public synchronized void evictAll() throws IOException {
        initialize();
        for (Entry entry : (Entry[]) this.lruEntries.values().toArray(new Entry[this.lruEntries.size()])) {
            removeEntry(entry);
        }
        this.mostRecentTrimFailed = false;
    }

    private void validateKey(String key) {
        if (!LEGAL_KEY_PATTERN.matcher(key).matches()) {
            StringBuilder sb = new StringBuilder();
            sb.append("keys must match regex [a-z0-9_-]{1,120}: \"");
            sb.append(key);
            sb.append("\"");
            throw new IllegalArgumentException(sb.toString());
        }
    }

    public synchronized Iterator<Snapshot> snapshots() throws IOException {
        initialize();
        return new Iterator<Snapshot>() {
            final Iterator<Entry> delegate = new ArrayList(DiskLruCache.this.lruEntries.values()).iterator();
            Snapshot nextSnapshot;
            Snapshot removeSnapshot;

            public boolean hasNext() {
                if (this.nextSnapshot != null) {
                    return true;
                }
                synchronized (DiskLruCache.this) {
                    if (DiskLruCache.this.closed) {
                        return false;
                    }
                    while (this.delegate.hasNext()) {
                        Snapshot snapshot = ((Entry) this.delegate.next()).snapshot();
                        if (snapshot != null) {
                            this.nextSnapshot = snapshot;
                            return true;
                        }
                    }
                    return false;
                }
            }

            public Snapshot next() {
                if (hasNext()) {
                    this.removeSnapshot = this.nextSnapshot;
                    this.nextSnapshot = null;
                    return this.removeSnapshot;
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                Snapshot snapshot = this.removeSnapshot;
                if (snapshot != null) {
                    try {
                        DiskLruCache.this.remove(snapshot.key);
                    } catch (IOException e) {
                    } catch (Throwable th) {
                        this.removeSnapshot = null;
                        throw th;
                    }
                    this.removeSnapshot = null;
                    return;
                }
                throw new IllegalStateException("remove() before next()");
            }
        };
    }
}
