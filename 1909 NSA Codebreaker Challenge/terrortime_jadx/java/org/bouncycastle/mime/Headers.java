package org.bouncycastle.mime;

import com.badguy.terrortime.BuildConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bouncycastle.util.Iterable;
import org.bouncycastle.util.Strings;

public class Headers implements Iterable<String> {
    private String boundary;
    private final String contentTransferEncoding;
    private String contentType;
    private Map<String, String> contentTypeParameters;
    private final Map<String, List> headers;
    private final List<String> headersAsPresented;
    private boolean multipart;

    private class KV {
        public final String key;
        public final String value;

        public KV(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        public KV(KV kv) {
            this.key = kv.key;
            this.value = kv.value;
        }
    }

    public Headers(InputStream inputStream, String str) throws IOException {
        this(parseHeaders(inputStream), str);
    }

    public Headers(List<String> list, String str) {
        Map<String, String> map;
        this.headers = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        this.headersAsPresented = list;
        String str2 = BuildConfig.FLAVOR;
        for (String str3 : list) {
            if (str3.startsWith(" ") || str3.startsWith("\t")) {
                StringBuilder sb = new StringBuilder();
                sb.append(str2);
                sb.append(str3.trim());
                str2 = sb.toString();
            } else {
                if (str2.length() != 0) {
                    put(str2.substring(0, str2.indexOf(58)).trim(), str2.substring(str2.indexOf(58) + 1).trim());
                }
                str2 = str3;
            }
        }
        if (str2.trim().length() != 0) {
            put(str2.substring(0, str2.indexOf(58)).trim(), str2.substring(str2.indexOf(58) + 1).trim());
        }
        String str4 = "Content-Type";
        String str5 = getValues(str4) == null ? "text/plain" : getValues(str4)[0];
        int indexOf = str5.indexOf(59);
        if (indexOf < 0) {
            this.contentType = str5;
            map = Collections.EMPTY_MAP;
        } else {
            this.contentType = str5.substring(0, indexOf);
            map = createContentTypeParameters(str5.substring(indexOf + 1).trim());
        }
        this.contentTypeParameters = map;
        String str6 = "Content-Transfer-Encoding";
        if (getValues(str6) != null) {
            str = getValues(str6)[0];
        }
        this.contentTransferEncoding = str;
        if (this.contentType.indexOf("multipart") >= 0) {
            this.multipart = true;
            String str7 = (String) this.contentTypeParameters.get("boundary");
            this.boundary = str7.substring(1, str7.length() - 1);
            return;
        }
        this.boundary = null;
        this.multipart = false;
    }

    private Map<String, String> createContentTypeParameters(String str) {
        String[] split = str.split(";");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int i = 0;
        while (i != split.length) {
            String str2 = split[i];
            int indexOf = str2.indexOf(61);
            if (indexOf >= 0) {
                linkedHashMap.put(str2.substring(0, indexOf).trim(), str2.substring(indexOf + 1).trim());
                i++;
            } else {
                throw new IllegalArgumentException("malformed Content-Type header");
            }
        }
        return Collections.unmodifiableMap(linkedHashMap);
    }

    private static List<String> parseHeaders(InputStream inputStream) throws IOException {
        ArrayList arrayList = new ArrayList();
        LineReader lineReader = new LineReader(inputStream);
        while (true) {
            String readLine = lineReader.readLine();
            if (readLine == null || readLine.length() == 0) {
                return arrayList;
            }
            arrayList.add(readLine);
        }
        return arrayList;
    }

    private void put(String str, String str2) {
        synchronized (this) {
            KV kv = new KV(str, str2);
            List list = (List) this.headers.get(str);
            if (list == null) {
                list = new ArrayList();
                this.headers.put(str, list);
            }
            list.add(kv);
        }
    }

    public boolean containsKey(String str) {
        return this.headers.containsKey(str);
    }

    public void dumpHeaders(OutputStream outputStream) throws IOException {
        for (Object obj : this.headersAsPresented) {
            outputStream.write(Strings.toUTF8ByteArray(obj.toString()));
            outputStream.write(13);
            outputStream.write(10);
        }
    }

    public String getBoundary() {
        return this.boundary;
    }

    public String getContentTransferEncoding() {
        return this.contentTransferEncoding;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Map<String, String> getContentTypeAttributes() {
        return this.contentTypeParameters;
    }

    public Iterator<String> getNames() {
        return this.headers.keySet().iterator();
    }

    public String[] getValues(String str) {
        synchronized (this) {
            List list = (List) this.headers.get(str);
            if (list == null) {
                return null;
            }
            String[] strArr = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                strArr[i] = ((KV) list.get(i)).value;
            }
            return strArr;
        }
    }

    public boolean isEmpty() {
        boolean isEmpty;
        synchronized (this) {
            isEmpty = this.headers.isEmpty();
        }
        return isEmpty;
    }

    public boolean isMultipart() {
        return this.multipart;
    }

    public Iterator<String> iterator() {
        return this.headers.keySet().iterator();
    }
}
