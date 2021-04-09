package com.badguy.terrortime;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jivesoftware.smack.util.TLSUtils;
import org.json.JSONObject;

public class ClientCredentialTokenRequest {
    private Context appContext;
    private String audience;
    private String authorization;
    private String clientId;
    private String grantType;
    private JSONObject jsonTokenResponse;
    private int port = 443;
    private JSONObject postNameValues = new JSONObject();
    private String scope;
    private String secret;
    private URL site;
    private Long tokenExpiration;
    private String tokenValue;

    private static class tokenHelper {
        Integer expiration;
        byte[] value;

        public tokenHelper(Integer expiration2, byte[] value2) {
            this.expiration = expiration2;
            this.value = value2;
        }
    }

    public ClientCredentialTokenRequest(Context context, String site2, String clientId2, String secret2, String grantType2, String scope2, String audience2, int port2) throws Exception {
        if (site2 == null) {
            throw new RuntimeException("URL site String was NULL");
        } else if (clientId2 == null) {
            throw new RuntimeException("client id String was NULL");
        } else if (secret2 == null) {
            throw new RuntimeException("secret String was NULL");
        } else if (scope2 == null) {
            throw new RuntimeException("scope String was NULL");
        } else if (grantType2 == null) {
            throw new RuntimeException("grant type String was NULL");
        } else if (audience2 == null) {
            throw new RuntimeException("audience String was NULL");
        } else if (context != null) {
            try {
                this.appContext = context;
                this.site = new URL(site2);
                this.clientId = clientId2;
                this.secret = secret2;
                this.grantType = grantType2;
                this.scope = scope2;
                this.audience = audience2;
                StringBuilder sb = new StringBuilder();
                sb.append("Basic ");
                sb.append(getBase64AuthorizationString(this.clientId, this.secret));
                this.authorization = sb.toString();
                this.port = port2;
                this.postNameValues.put("audience", BuildConfig.FLAVOR);
                this.postNameValues.put("grant_type", this.grantType);
                this.postNameValues.put("scope", this.scope);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("context was NULL");
        }
    }

    private SSLContext createAcceptAllCertsContext() throws NoSuchAlgorithmException, NullPointerException, KeyManagementException {
        String str = "EXCEPTION LOG";
        TrustManager[] tms = {new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try {
            SSLContext context = SSLContext.getInstance(TLSUtils.TLS);
            context.init(null, tms, null);
            return context;
        } catch (NoSuchAlgorithmException e) {
            Log.e(str, e.getMessage());
            throw new NoSuchAlgorithmException(e);
        } catch (NullPointerException e2) {
            Log.e(str, e2.getMessage());
            throw new NullPointerException();
        } catch (KeyManagementException e3) {
            Log.e(str, e3.getMessage());
            throw new KeyManagementException(e3);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:87:0x02ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.badguy.terrortime.ClientCredentialTokenRequest.tokenHelper requestAccessToken() throws java.lang.Exception {
        /*
            r27 = this;
            r1 = r27
            java.lang.String r0 = "UTF-8"
            java.lang.String r2 = "TOKENREQUEST"
            r3 = 0
            java.net.URL r4 = new java.net.URL
            java.net.URL r5 = r1.site
            java.lang.String r5 = r5.getProtocol()
            java.net.URL r6 = r1.site
            java.lang.String r6 = r6.getHost()
            int r7 = r1.port
            java.net.URL r8 = r1.site
            java.lang.String r8 = r8.getFile()
            r4.<init>(r5, r6, r7, r8)
            javax.net.ssl.SSLContext r5 = r27.createAcceptAllCertsContext()
            java.net.URLConnection r6 = r4.openConnection()
            javax.net.ssl.HttpsURLConnection r6 = (javax.net.ssl.HttpsURLConnection) r6
            javax.net.ssl.SSLSocketFactory r7 = r5.getSocketFactory()
            r6.setSSLSocketFactory(r7)
            com.badguy.terrortime.ClientCredentialTokenRequest$2 r7 = new com.badguy.terrortime.ClientCredentialTokenRequest$2
            r7.<init>()
            r6.setHostnameVerifier(r7)
            java.lang.String r7 = "POST"
            r6.setRequestMethod(r7)
            java.lang.String r7 = "Content-Type"
            java.lang.String r8 = "application/x-www-form-urlencoded"
            r6.setRequestProperty(r7, r8)
            java.lang.String r7 = r1.authorization
            java.lang.String r8 = "Authorization"
            r6.setRequestProperty(r8, r7)
            java.lang.String r7 = "X-Server-Select"
            java.lang.String r8 = "oauth"
            r6.setRequestProperty(r7, r8)
            r7 = 1
            r6.setDoInput(r7)
            r6.setDoOutput(r7)
            r7 = 0
            r6.setUseCaches(r7)
            r8 = 30000(0x7530, float:4.2039E-41)
            r6.setReadTimeout(r8)
            r6.setConnectTimeout(r8)
            org.json.JSONObject r8 = r1.postNameValues     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            byte[] r8 = r8.getBytes(r0)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            org.json.JSONObject r9 = r1.postNameValues     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            org.json.JSONObject r10 = r1.postNameValues     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.util.Iterator r10 = r10.keys()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r11.<init>()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
        L_0x0081:
            boolean r12 = r10.hasNext()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            if (r12 == 0) goto L_0x00c6
            java.lang.Object r12 = r10.next()     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            org.json.JSONObject r13 = r1.postNameValues     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            java.lang.String r13 = r13.getString(r12)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            r14.<init>()     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            r14.append(r12)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            java.lang.String r15 = "="
            r14.append(r15)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            r14.append(r13)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            r11.append(r14)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            boolean r14 = r10.hasNext()     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            if (r14 == 0) goto L_0x00b5
            java.lang.String r14 = "&"
            r11.append(r14)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
        L_0x00b5:
            goto L_0x0081
        L_0x00b6:
            r0 = move-exception
            r21 = r3
            r23 = r4
            r24 = r5
            goto L_0x02aa
        L_0x00bf:
            r0 = move-exception
            r23 = r4
            r24 = r5
            goto L_0x02a1
        L_0x00c6:
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.Long r12 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r13.<init>()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r14 = "Current systemtime: "
            r13.append(r14)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            long r14 = r12.longValue()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r16 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r16
            r13.append(r14)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            android.util.Log.d(r2, r13)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            if (r12 == 0) goto L_0x027e
            java.io.BufferedOutputStream r13 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.io.OutputStream r14 = r6.getOutputStream()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r13.<init>(r14)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r14 = r11.toString()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            byte[] r0 = r14.getBytes(r0)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r14.<init>()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r15 = "Requesting token. Destination: "
            r14.append(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.net.URL r15 = r1.site     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r14.append(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r15 = ". Authorization property: "
            r14.append(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r15 = r1.authorization     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r14.append(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r15 = ". Request: "
            r14.append(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r15 = r11.toString()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r14.append(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            android.util.Log.d(r2, r14)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            int r14 = r0.length     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r13.write(r0, r7, r14)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r13.close()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.lang.StringBuffer r7 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r7.<init>()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.io.InputStream r14 = r6.getInputStream()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            java.io.InputStreamReader r15 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r15.<init>(r14)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r18 = r0
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r0.<init>(r15)     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
        L_0x0146:
            java.lang.String r19 = r0.readLine()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r20 = r19
            if (r19 == 0) goto L_0x0158
            r19 = r0
            r0 = r20
            r7.append(r0)     // Catch:{ Exception -> 0x00bf, all -> 0x00b6 }
            r0 = r19
            goto L_0x0146
        L_0x0158:
            r19 = r0
            r0 = r20
            r20 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r0.<init>()     // Catch:{ Exception -> 0x029a, all -> 0x0292 }
            r21 = r3
            java.lang.String r3 = "Received token: "
            r0.append(r3)     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            java.lang.String r3 = r7.toString()     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            r0.append(r3)     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            android.util.Log.d(r2, r0)     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            if (r0 == 0) goto L_0x0264
            java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            r3.<init>(r0)     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            r1.jsonTokenResponse = r3     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            boolean r3 = r27.isValidResponse()     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            r22 = r0
            java.lang.String r0 = "EXCEPTION"
            if (r3 == 0) goto L_0x0253
            org.json.JSONObject r3 = r1.jsonTokenResponse     // Catch:{ Exception -> 0x0276, all -> 0x0270 }
            r23 = r4
            java.lang.String r4 = "expires_in"
            long r3 = r3.getLong(r4)     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            long r3 = r3 * r16
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            r1.tokenExpiration = r3     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            java.lang.Long r3 = r1.tokenExpiration     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            long r3 = r3.longValue()     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            long r24 = r12.longValue()     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            long r3 = r3 + r24
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            r1.tokenExpiration = r3     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            java.lang.Long r3 = r1.tokenExpiration     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            long r3 = r3.longValue()     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            long r3 = r3 / r16
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            r1.tokenExpiration = r3     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            org.json.JSONObject r3 = r1.jsonTokenResponse     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            java.lang.String r4 = "access_token"
            java.lang.String r3 = r3.getString(r4)     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            r1.tokenValue = r3     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            com.badguy.terrortime.ClientCredentialTokenRequest$tokenHelper r3 = new com.badguy.terrortime.ClientCredentialTokenRequest$tokenHelper     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            java.lang.Long r4 = r1.tokenExpiration     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            int r4 = r4.intValue()     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x024d, all -> 0x0248 }
            r24 = r5
            java.lang.String r5 = r1.tokenValue     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            byte[] r5 = r5.getBytes()     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            r3.<init>(r4, r5)     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0246 }
            r4.<init>()     // Catch:{ Exception -> 0x0246 }
            java.lang.String r5 = "Token: "
            r4.append(r5)     // Catch:{ Exception -> 0x0246 }
            java.lang.String r5 = r1.tokenValue     // Catch:{ Exception -> 0x0246 }
            r4.append(r5)     // Catch:{ Exception -> 0x0246 }
            java.lang.String r5 = ". Expiration: "
            r4.append(r5)     // Catch:{ Exception -> 0x0246 }
            java.lang.Long r5 = r1.tokenExpiration     // Catch:{ Exception -> 0x0246 }
            r4.append(r5)     // Catch:{ Exception -> 0x0246 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0246 }
            android.util.Log.d(r2, r4)     // Catch:{ Exception -> 0x0246 }
            java.lang.Long r2 = r1.tokenExpiration     // Catch:{ Exception -> 0x0246 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0246 }
            long r4 = (long) r2     // Catch:{ Exception -> 0x0246 }
            long r25 = r12.longValue()     // Catch:{ Exception -> 0x0246 }
            long r25 = r25 / r16
            int r2 = (r4 > r25 ? 1 : (r4 == r25 ? 0 : -1))
            if (r2 < 0) goto L_0x0239
            java.lang.Long r2 = r1.tokenExpiration     // Catch:{ Exception -> 0x0246 }
            long r4 = r2.longValue()     // Catch:{ Exception -> 0x0246 }
            r16 = 4294967295(0xffffffff, double:2.1219957905E-314)
            int r2 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r2 > 0) goto L_0x0239
            r15.close()     // Catch:{ Exception -> 0x0246 }
            if (r14 == 0) goto L_0x0233
            r14.close()     // Catch:{ Exception -> 0x0246 }
        L_0x0233:
            if (r6 == 0) goto L_0x0238
            r6.disconnect()
        L_0x0238:
            return r3
        L_0x0239:
            java.lang.String r2 = "Invalid Token Expiration."
            android.util.Log.e(r0, r2)     // Catch:{ Exception -> 0x0246 }
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x0246 }
            java.lang.String r2 = "CLIENTCREDENTIALSTOKENREQUEST: Invalid token expiration"
            r0.<init>(r2)     // Catch:{ Exception -> 0x0246 }
            throw r0     // Catch:{ Exception -> 0x0246 }
        L_0x0246:
            r0 = move-exception
            goto L_0x02a1
        L_0x0248:
            r0 = move-exception
            r24 = r5
            goto L_0x02aa
        L_0x024d:
            r0 = move-exception
            r24 = r5
            r3 = r21
            goto L_0x02a1
        L_0x0253:
            r23 = r4
            r24 = r5
            java.lang.String r2 = "Response not valid."
            android.util.Log.e(r0, r2)     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            java.lang.String r2 = "Did not receive valid response to token request"
            r0.<init>(r2)     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            throw r0     // Catch:{ Exception -> 0x028e, all -> 0x028c }
        L_0x0264:
            r23 = r4
            r24 = r5
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            java.lang.String r2 = "Did not receive response to token request"
            r0.<init>(r2)     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            throw r0     // Catch:{ Exception -> 0x028e, all -> 0x028c }
        L_0x0270:
            r0 = move-exception
            r23 = r4
            r24 = r5
            goto L_0x02aa
        L_0x0276:
            r0 = move-exception
            r23 = r4
            r24 = r5
            r3 = r21
            goto L_0x02a1
        L_0x027e:
            r21 = r3
            r23 = r4
            r24 = r5
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            java.lang.String r2 = "Failed to acquire system time."
            r0.<init>(r2)     // Catch:{ Exception -> 0x028e, all -> 0x028c }
            throw r0     // Catch:{ Exception -> 0x028e, all -> 0x028c }
        L_0x028c:
            r0 = move-exception
            goto L_0x02aa
        L_0x028e:
            r0 = move-exception
            r3 = r21
            goto L_0x02a1
        L_0x0292:
            r0 = move-exception
            r21 = r3
            r23 = r4
            r24 = r5
            goto L_0x02aa
        L_0x029a:
            r0 = move-exception
            r21 = r3
            r23 = r4
            r24 = r5
        L_0x02a1:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ all -> 0x02a7 }
            r2.<init>(r0)     // Catch:{ all -> 0x02a7 }
            throw r2     // Catch:{ all -> 0x02a7 }
        L_0x02a7:
            r0 = move-exception
            r21 = r3
        L_0x02aa:
            if (r6 == 0) goto L_0x02af
            r6.disconnect()
        L_0x02af:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badguy.terrortime.ClientCredentialTokenRequest.requestAccessToken():com.badguy.terrortime.ClientCredentialTokenRequest$tokenHelper");
    }

    private boolean isValidResponse() throws Exception {
        JSONObject jSONObject = this.jsonTokenResponse;
        if (jSONObject != null) {
            try {
                String token = jSONObject.getString("access_token");
                Integer expiration = Integer.valueOf(this.jsonTokenResponse.getInt("expires_in"));
                String scope2 = this.jsonTokenResponse.getString("scope");
                String tokenType = this.jsonTokenResponse.getString("token_type");
                if (token == null || token.length() <= 0 || expiration == null || expiration.intValue() <= 0 || scope2 == null || !scope2.equals(this.scope) || tokenType == null || !tokenType.equals("bearer")) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Unknown token response error.");
        }
    }

    private String getBase64AuthorizationString(String clientId2, String secret2) {
        StringBuilder sb = new StringBuilder();
        sb.append(clientId2);
        sb.append(":");
        sb.append(secret2);
        return Base64.encodeToString(sb.toString().getBytes(), 10);
    }

    public byte[] getValidTokenAsByteArray(Client client, Context context) throws Exception {
        String str = BuildConfig.FLAVOR;
        Long systemEpoch = Long.valueOf((System.currentTimeMillis() / 1000) + Long.valueOf(60).longValue());
        if (client == null || client.getEncryptPin() == null) {
            throw new RuntimeException("Null client or null encryptPin");
        }
        if (client.getOAuth2AccessToken(client.getEncryptPin()) == null || client.getOAuth2AccessTokenExpiration().intValue() == 0 || ((long) client.getOAuth2AccessTokenExpiration().intValue()) < systemEpoch.longValue()) {
            try {
                tokenHelper token = requestAccessToken();
                if (token == null || token.expiration == null || token.value == null) {
                    throw new RuntimeException("Token request failed.");
                }
                client.setOAuth2AccessToken(client.getEncryptPin(), token.value);
                client.setOAuth2AccessTokenExpiration(token.expiration);
                ClientDBHandlerClass clientDB = ClientDBHandlerClass.getInstance(client.getEncryptPin(), context.getApplicationContext());
                if (clientDB != null) {
                    clientDB.addOrUpdateClient(client);
                    clientDB.close();
                } else {
                    throw new RuntimeException("Failed to connect to database");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return client.getOAuth2AccessToken(client.getEncryptPin());
    }
}
