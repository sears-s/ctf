package org.jivesoftware.smack.proxy;

import com.badguy.terrortime.BuildConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;

class HTTPProxySocketConnection implements ProxySocketConnection {
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("HTTP/\\S+\\s(\\d+)\\s(.*)\\s*");
    private final ProxyInfo proxy;

    HTTPProxySocketConnection(ProxyInfo proxy2) {
        this.proxy = proxy2;
    }

    public void connect(Socket socket, String host, int port, int timeout) throws IOException {
        String password;
        String str = host;
        int i = port;
        String proxyhost = this.proxy.getProxyAddress();
        int proxyPort = this.proxy.getProxyPort();
        socket.connect(new InetSocketAddress(proxyhost, proxyPort));
        StringBuilder sb = new StringBuilder();
        sb.append("CONNECT ");
        sb.append(str);
        String str2 = ":";
        sb.append(str2);
        sb.append(i);
        String hostport = sb.toString();
        String username = this.proxy.getProxyUsername();
        if (username == null) {
            password = BuildConfig.FLAVOR;
        } else {
            String password2 = this.proxy.getProxyPassword();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("\r\nProxy-Authorization: Basic ");
            StringBuilder sb3 = new StringBuilder();
            sb3.append(username);
            sb3.append(str2);
            sb3.append(password2);
            sb2.append(Base64.encode(sb3.toString()));
            password = sb2.toString();
        }
        OutputStream outputStream = socket.getOutputStream();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(hostport);
        sb4.append(" HTTP/1.1\r\nHost: ");
        sb4.append(str);
        sb4.append(str2);
        sb4.append(i);
        sb4.append(password);
        sb4.append("\r\n\r\n");
        outputStream.write(sb4.toString().getBytes(StringUtils.UTF8));
        InputStream in = socket.getInputStream();
        StringBuilder got = new StringBuilder(100);
        int nlchars = 0;
        while (true) {
            int inByte = in.read();
            if (inByte != -1) {
                char c = (char) inByte;
                got.append(c);
                String str3 = ", cancelling connection";
                if (got.length() <= 1024) {
                    if ((nlchars == 0 || nlchars == 2) && c == 13) {
                        nlchars++;
                    } else if ((nlchars == 1 || nlchars == 3) && c == 10) {
                        nlchars++;
                    } else {
                        nlchars = 0;
                    }
                    if (nlchars != 4) {
                        String str4 = host;
                        int i2 = port;
                    } else if (nlchars == 4) {
                        String gotstr = got.toString();
                        String response = new BufferedReader(new StringReader(gotstr)).readLine();
                        if (response != null) {
                            Matcher m = RESPONSE_PATTERN.matcher(response);
                            if (m.matches()) {
                                int code = Integer.parseInt(m.group(1));
                                String str5 = gotstr;
                                if (code != 200) {
                                    ProxyType proxyType = ProxyType.HTTP;
                                    StringBuilder sb5 = new StringBuilder();
                                    int i3 = proxyPort;
                                    sb5.append("Error code in proxy response: ");
                                    sb5.append(code);
                                    throw new ProxyException(proxyType, sb5.toString());
                                }
                                return;
                            }
                            int i4 = proxyPort;
                            ProxyType proxyType2 = ProxyType.HTTP;
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("Unexpected proxy response from ");
                            sb6.append(proxyhost);
                            sb6.append(": ");
                            sb6.append(response);
                            throw new ProxyException(proxyType2, sb6.toString());
                        }
                        int i5 = proxyPort;
                        ProxyType proxyType3 = ProxyType.HTTP;
                        StringBuilder sb7 = new StringBuilder();
                        sb7.append("Empty proxy response from ");
                        sb7.append(proxyhost);
                        sb7.append(", cancelling");
                        throw new ProxyException(proxyType3, sb7.toString());
                    } else {
                        ProxyType proxyType4 = ProxyType.HTTP;
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("Never received blank line from ");
                        sb8.append(proxyhost);
                        sb8.append(str3);
                        throw new ProxyException(proxyType4, sb8.toString());
                    }
                } else {
                    ProxyType proxyType5 = ProxyType.HTTP;
                    StringBuilder sb9 = new StringBuilder();
                    sb9.append("Received header of >1024 characters from ");
                    sb9.append(proxyhost);
                    sb9.append(str3);
                    throw new ProxyException(proxyType5, sb9.toString());
                }
            } else {
                throw new ProxyException(ProxyType.HTTP);
            }
        }
    }
}
