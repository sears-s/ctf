# NSA Codebreaker 2019 Solutions

## Task 1

Open `terrortime.pcapng` in Wireshark. Go to File -> Export Objects -> HTTP, and save `terrortime.apk` and `README.developer`. Get the SHA256 hash of the APK:

`sha256sum terrortime.apk`

The outputted hash:

`cf0461f3f9cb876eb4247917f3b9561592926c8a7fc946c993a8b976773d780c`

Open `README.developer` to see the contents, including the two client ID and secret combinations:

```
bintou--vhost-1334@terrortime.app -- First Terrortime test account client id
evan--vhost-1334@terrortime.app -- Second Terrortime test account client id
deGuh7VYVc7CPC -- First Terrortime test account client secret
XM9G6BnNHtqkIV -- Second Terrortime test account client secret
```

The task answer:

```
cf0461f3f9cb876eb4247917f3b9561592926c8a7fc946c993a8b976773d780c

bintou--vhost-1334@terrortime.app:deGuh7VYVc7CPC
evan--vhost-1334@terrortime.app:XM9G6BnNHtqkIV
```

## Task 2

Install apktool: https://ibotpeaches.github.io/Apktool/install/

Run apktool on the APK from the PCAP:

`apktool d terrortime.apk`

Open `terrortime/AndroidManifest.xml`,  and notice lines 2-3 are the permissions:

```
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

Install and run apksigner on the APK from the PCAP:

```
sudo apt install apksigner
apksigner verify --print-certs terrortime.apk
```

CN is the certificate owner:

```
Signer #1 certificate DN: CN=dev_terrorTime_639959, OU=TSuite
Signer #1 certificate SHA-256 digest: 34691472af4e28ac9e934ca4255c47b34853a0c1a3678bdf43f838aed51ccf46
Signer #1 certificate SHA-1 digest: 0320cc9e30768d312b7eb7ddf9b47133428b1e75
Signer #1 certificate MD5 digest: 9fd1cbde7761da55da9f144414a69086
```

The task answer:

```
INTERNET
ACCESS_NETWORK_STATE

5688a9f15c2449b0674bf7e1cd5484e69210c86d1e5d5b516a1591ae4f9a97d2

dev_terrorTime_283451
```

## Task 3

Open a SQLite console:

`sqlite3 clientDB.db`

Run this query:

```
sqlite> select xsip,asip from Clients;
chat.terrortime.app|register.terrortime.app
```

`chat.terrortime.app` must be the XMPP server and `register.terrortime.app` must be the OAUTH server. Get the IPs:

```
host chat.terrortime.app
chat.terrortime.app has address 54.91.5.130
host terrortime.app
register.terrortime.app is an alias for codebreaker.ltsnet.net.
codebreaker.ltsnet.net has address 54.197.185.236
```

The task answer:

```
54.197.185.236

54.91.5.130
```

## Task 4

Open a SQLite console:

`sqlite3 clientDB.db`

Get the client ID and checkpin as hex from the database:

```
sqlite> select cid, hex(checkpin) from Clients;
leila--vhost-1334@terrortime.app|187D228E8BA2C7763C77862D6560F85F07A30D60C7ACCBBB5B175C90F3C1DBA1
```

Download JADX: https://github.com/skylot/jadx

Open `terrortime.apk` with JADX. In `com/badguy/terrortime`, notice lines 278-282:

```
hash = MessageDigest.getInstance("SHA-256").digest(ePin.getBytes(StringUtils.UTF8));
if (salt == null) {
	salt = hash;
	this.checkPin.setValue(salt);
}
```

ePin is the PIN input, which must be a 6 digit number, which was determined from using the app in an emulator. Therefore, the checkpin in the database is the SHA256 hash of the 6 digit PIN. Put the hex digits above from the database into a file called `hashes.txt`, then run hashcat:

`hashcat hashes.txt --show -m 1400 -a 3 ?d?d?d?d?d?d`

In this case, the resulting pin was `955859`. Download and install Android Studio, click `Profile or debug APK`, and select `terrortime.apk`. Press the green play button in the top right corner to start the app. With the app closed, click View -> Tool Windows -> Device File Explorer. Go to `/data/data/com.badguy.terrortime/databases/`, right cluck on the databases folder, and click Upload. Select the given `clientDB.db` file. Now open the app, and login with the found client ID (`leila--vhost-1334@terrortime.app`) and PIN (`955859`). You'll see two contacts. One of them calls you sir, and mentions to deliver the ring a day after the holiday at 0050. This contact must be the leader, and in this case their username is `ahmad--vhost-1334@terrortime.app`. The other contact mentions meeting up around April Fool's. Therefore, the event must occur on 1 April 2020 at 0050. Using an online tool, this converts to a Unix timestamp of `1585788600`. The task answer:

```
ahmad--vhost-1334@terrortime.app

1585788600
```

## Task 5

Install and run Uncompyle6:

```
pip install uncompyle6
uncompyle6 auth_verify.pyc > auth_verify.py
```

Configure Burp Suite proxy for HTTP(S) traffic in Android emulator: https://www.thedroidsonroids.com/blog/how-to-debug-https-traffic-for-android-apps-with-burp-proxy

