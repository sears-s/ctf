# NSA Codebreaker Challenge 2020 Solutions

## Task 1 - What's On the Drive? - (Computer Forensics, Command Line, Encryption Tools) (10 points)

### Description

In accordance with USSID18, a collection on an American citizen is permitted in cases where the person is reasonably believed to be held captive by a group engaged in international terrorism. As a result, we have obtained a copy of the home directory from the journalist's laptop and are hoping it will contain information that will help us to locate and rescue the hostage. Your first task is to analyze the data and files available in the journalist's home directory.

### Solution

Extract `home.zip`. It contains a directory named `JakeOtters385`, so this must be the username. Within this directory, `pwfile` looks suspicious:

```
$ file pwfile
pwfile: GPG symmetrically encrypted data (AES256 cipher)
```

Therefore, `pwfile` must be the encrypted file.

### Answers

What is the journalist's username on their computer?

```
JakeOtters385
```

Enter the file name for the encrypted file on the journalist's computer.

```
pwfile
```

## Task 2 - Social Engineering - (Computer Forensics, Metadata Analysis, Encryption Tools) (40 points)

### Description

The name of the encrypted file you found implies that it might contain credentials for the journalist's various applications and accounts. Your next task is to find information on the journalist's computer that will allow you to decrypt this file.

### Solution

`pwHints.txt` contains the following line:

```
keychain: pet name + pet bday
```

`Documents/Blog-Articles/blogIntro.txt` contains the following text:

```
Outside of work, my two favorite things are traveling the world and getting to come home to my favorite furry little friend, and the best friend on the planet, Keanna.
```

Therefore, the pet's name must be `Keanna`. `Pictures/Pets/shenanigans.jpg` contains a birthday related picture. Use `exiftool` to get the picture's metadata:

```
$ exiftool Pictures/Pets/shenanigans.jpg
Date/Time Original              : 2019:01:21 14:40:20
```

Therefore, the birthday must be January 21st. After some trial and error, the following command decrypts `pwfile`:

```
$ gpg --batch --passphrase "Keanna0121" -d pwfile > pwfile.db
```

### Answers

Enter the password that decrypts the encrypted file.

```
Keanna0121
```

## Task 3 - Social Engineering - (Computer Forensics, Metadata Analysis, Encryption Tools) (150 points)

### Description

Good news -- the decrypted key file includes the journalist's password for the Stepinator app. A Stepinator is a wearable fitness device that tracks the number of steps a user walks. Tell us the associated username and password for that account. We might be able to use data from that account to track the journalist's location!

### Solution

The decrypted file is a SQLite database:

```
$ file pwfile.db
pwfile.db: SQLite 3.x database, last written using SQLite version 3027002
```

Open the file with `sqlite3` and list the tables:

```
$ sqlite3 pwfile.db
sqlite> .tables
passwords  services
```

Get the schemas for the tables:

```
sqlite> .schema passwords
CREATE TABLE passwords(
    id integer PRIMARY KEY,
    service integer NOT NULL,
    username text,
    pwd text NOT NULL,
    valid integer NOT NULL,
    FOREIGN KEY (service) REFERENCES services (id)
);
sqlite> .schema services
CREATE TABLE services(
    id integer PRIMARY KEY,
    service text NOT NULL,
    keyused integer,
    keyexpired integer
);
```

Join the tables to get the username and password for Stepinator:

```
sqlite> SELECT passwords.username,passwords.pwd FROM passwords,services WHERE passwords.service=services.id AND services.service="stepinator";
Keanna_Otters_0121|<~<+TKS94_OLDIG4Z1,Ud?1,T~>
```

Googling `<~` and `~>` reveals that this signifies Base85 encoded text. CyberChef is used to decode the password from Base85:

```
TealKeanna09251025
```

### Answers

Enter the username for the Stepinator account

```
Keanna_Otters_0121
```

Enter the password for the Stepinator account

```
TealKeanna09251025
```

## Task 4 - Follow That Car! - (Graph Algorithms, Computer Science) (500 points)

### Description

By using the credentials in the decrypted file, we were able to download the journalist's accelerometer data from their Stepinator device from the time of the kidnapping. Local officials have provided us with a city map and traffic light schedule. Using these along with the journalist's accelerometer data, find the closest intersection to where the kidnappers took their hostage.

### Solution

A Python script (`task4/solve.py`) was written to show the acceleration, speed, and total distance traveled at each time. The output was then manually analyzed with the pictures of the light patterns. The only direction with a green light initially is East. They travel 200m, or 2 blocks, until t=20, where they stop at a light until t=30. After the light turns green, the only possible direction is East. They travel for 2 blocks and stop at another light at t=51. After the light turns green, the only possible direction is East. They travel another block, then slow down to turn at t=72. They travel for 1 block, then stop at a red light at t=84. Either direction from the original turn, they must turn again once it turns green. After traveling another block, they stop at another red light at t=103. The only way they would hit a red light would be if they turned North then West. Once it turns green, they travel 1 block and hit a red light at t=134. They must have turned North previously because going West would result in a green light and going South is where they came from. They travel 1 block and slow to turn at t=163 and again at t=172. Finally, they travel 1 block then stop for a red light at t=184. Based on the light pattern, after the previous red light, they must have gone 1 block North, 1 block West, then 1 block South.

### Answers

Enter the avenue of the intersection. (ie. Avenue F & 3rd st, enter F)

```
J
```

Enter the street of the intersection (ie. Avenue F & 3rd st, enter 3)

```
14
```

## Task 5 - Where Has the Drone Been? - (Reverse Engineering, Cryptography) (1300 points)

### Description

A rescue team was deployed to the criminal safehouse identified by your efforts. The team encountered resistance but was able to seize the location without causalities. Unfortunately, all of the kidnappers escaped and the hostage was not found. The team did find two important pieces of technology left behind: the journalist's Stepinator device, and a damaged surveillance drone. An analyst retrieved some encrypted logs as well as part of the drone's GPS software. Your goal for this task is to identify the location of the criminal organization's base of operations.

### Solution

The given binary is an ARM64 binary written in Go:

```
$ file gpslogger
gpslogger: ELF 64-bit LSB executable, ARM aarch64, version 1 (SYSV), dynamically linked, interpreter /lib/ld-musl-aarch64.so.1, Go BuildID=kuiME-kEtOrjYr0NtuSC/F9nT4PAOBdJwNF6rCoCc/7l0D-CmOuZoubD_SqInN/JtxNTIAPCIxt045aNaaC, not stripped
```

Strings in Go are stored differently, so a simple `strings` command will not extract them. Using https://github.com/CarveSystems/gostringsr2, some interesting information is learned:

```
$ gostringsr2 gpslogger
crypto/aes
*cipher.cbc
main.generate_key
main.generate_iv
main.setup_cipher
!!!   IV REQUIRES LONGITUDE COORDINATE   !!!
!!!   KEY REQUIRES LATITUDE COORDINATE   !!!
!!!   EXPECTED TO FIND NMEA $GNGGA HEADER   !!!
```

The logs must be encrypted using AES CBC, the IV must be derived from a latitude, and the key from a longitude. Researching `NMEA $GNGGA` results in the following document: http://navspark.mybigcommerce.com/content/NMEA_Format_v0.1.pdf. The drone must receive data from a GPS device in the following format, where `llll.lll` is the latitude and `yyyyy.yyy` is the longitude:

```
$GNGGA,hhmmss.ss,llll.lll,a,yyyyy.yyy,a,x,uu,v.v,w.w,M,x.x,M,,zzzz*hh<CR><LF>
```

Attempts to analyze the binary dynamically failed because it is compiled for ARM. Even if emulated, it relies on other shared libraries and reading from the GPS device. Thus, Ghidra was used for further static analysis. It should be noted that, while ARM typically follows a calling convention that uses registers for arguments and return values, the Go calling convention uses the stack to pass these values between functions (https://dr-knz.net/go-calling-convention-x86-64.html). For example, for a function with two arguments and one return value, the first argument would be at `SP + 0x8`, the second at `SP + 0x10`, and the return value at `SP + 0x18`. Ghidra does not account for this calling convention, so function signatures were altered to assist with decompilation. Also in Go, strings are stored as a struct with an 8-byte pointer to the characters and an 8-byte integer for its length. Checking the box `Custom Storage` for the function signature allows Ghidra to use the stack for arguments and the return value. Here is an example:\
![](images/task5_ghidra.png)\
After altering function signatures and walking through the assembly instructions, the following pseudocode for `main.setup_cipher` was produced:

```python
def setup_cipher(gps_data):
    parts = gps_data.split(",")
    if parts[0] != "$GNGGA":
        return
    latitude = parts[2].split(".")[0]
    longitude = parts[4].split(".")[0]
    key = latitude * 4
    iv = longitude * 3 + "0"
```

This results in a 128-bit AES key and a 128-bit IV. However, the logs can be decrypted without the IV if the first block is used as the IV and the ciphertext starts at the second block. The first block of plaintext will be lost. Since a possible latitude ranges from 0 to 9,000, the key space is significantly reduced, allowing for a bruteforce attack. `task5/solve.py` creates a key for every possible latitude and attempts to decrypt each log file:

```
$ python3 solve.py
Found key b'0534053405340534' for file 20200628_153027.log
Found key b'0534053405340534' for file 20200630_161219.log
Found key b'0513051305130513' for file 20200723_183021.log
Found key b'0513051305130513' for file 20200920_154004.log
```

Each decrypted log file contains various NMEA GPS communications:

```
$GNGGA,153338.00,0534.075426,N,02540.697046,W,1,12,0.7,58.0,M,-42.0,M,,*74
```

The challenge only requires the 4 most signicant digits for the latitude/longitude. From the log files, there are only two possibilities, and the correct answer is the location in the earlier logs.

### Answers

Enter the approximate location of the organization's base in the format: ##°##'N ##°##'W

```
05°34'N 25°40'W
```

## Task 6 - Proof of Life - (Signals Analysis) (1300 points)

### Description

Satellite imaging of the location you identified shows a camouflaged building within the jungle. The recon team spotted multiple armed individuals as well as drones being used for surveillance. Due to this heightened security presence, the team was unable to determine whether or not the journalist is being held inside the compound. Leadership is reluctant to raid the compound without proof that the journalist is there.

The recon team has brought back a signal collected near the compound. They suspect it is a security camera video feed, likely encoded with a systematic Hamming code. The code may be extended and/or padded as well. We've used BPSK demodulation on the raw signal to generate a sequence of half precision floating point values. The floats are stored as IEEE 754 binary16 values in little-endian byte order within the attached file. Each float is a sample of the signal with 1 sample per encoded bit. You should be able to interpret this to recover the encoded bit stream, then determine the Hamming code used. Your goal for this task is to help us reproduce the original video to provide proof that the journalist is alive and being held at this compound.

### Solution

Python is used to convert `signal.ham` to a list of bits. `struct.iter_unpack` is used with a format string of `<e`. `<` is for little-endian and `e` is for half precision float (2 bytes).

To determine the length of the codeword, the percentage of ones in the data is counted for each bit position in different codeword lengths. Normal data should have a percentage close to 0.5. However, the last bit in a 17 bit codeword is almost always 0:

```
codeword len = 17, bit_pos = 16, ones % = 0.0
```

Therefore, the codeword must have a length of 17 bits with the last bit being a padding of 0.

The percentages of ones for every bit position with this codeword length are found:

```
bit pos = 0, ones % = 54.26589390788833
bit pos = 1, ones % = 54.39030217227801
bit pos = 2, ones % = 54.38003804804672
bit pos = 3, ones % = 54.37048179445206
bit pos = 4, ones % = 54.42675751006504
bit pos = 5, ones % = 54.52143520771578
bit pos = 6, ones % = 54.37048179445206
bit pos = 7, ones % = 54.51488740432686
bit pos = 8, ones % = 54.41684732115206
bit pos = 9, ones % = 54.372605406361984
bit pos = 10, ones % = 54.353846834490994
bit pos = 11, ones % = 50.58195814714861
bit pos = 12, ones % = 51.80144228642216
bit pos = 13, ones % = 51.3868070610096
bit pos = 14, ones % = 49.69464230411892
bit pos = 15, ones % = 49.384241029951774
bit pos = 16, ones % = 0.12263858779807989
```

The description states that this is a systematic hamming code. Systematic means that the first bits of the codeword are data bits, and the codeword ends in parity check bits. This data shows that bits 0 through 10 are consistently 54% ones, while bits 11 through 15 are lower and have less consistent values. It can be assumed that bits 0 through 10 are the data bits, bits 11 through 15 are the parity check bits, and bit 16 is the 0 padding bit.

To determine the parity check matrix, each possible combination of data bits for each parity check bit is iterated through. The parity of each combination of data bits is compared to the parity check bit. Combinations with nearly 100% correct are found:

```
check bit pos 11, # data bits = 7, data bits = (1, 2, 3, 4, 5, 9, 10), % correct 100.0
check bit pos 12, # data bits = 7, data bits = (0, 3, 4, 6, 7, 9, 10), % correct 100.0
check bit pos 13, # data bits = 7, data bits = (0, 1, 2, 3, 4, 6, 8), % correct 100.0
check bit pos 14, # data bits = 7, data bits = (1, 4, 5, 6, 7, 8, 10), % correct 100.0
check bit pos 15, # data bits = 7, data bits = (0, 2, 4, 5, 7, 8, 9), % correct 100.0
```

This shows the data bits each parity check bit is for. The parity check matrix can then be constructed:

```
[0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0]
[1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0]
[1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0]
[0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0]
[1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1]
```

Each row represents a parity check and each column represents a bit position in the codeword. A bit is set for the data bits being checked and the parity check bit.

To decode the original bits, they are separated into codewords with the 17th bit being removed because it is padding. The syndrome is calculated by multiplying the parity check matrix and the codeword modulus 2. If the syndrome is non-zero, the column in the parity check matrix with equal bits is found. The index of this column is equal to the bit position in the codeword that has its bit flipped. The first 11 bytes of the codeword (the data bytes) are appended to the decoded bits, which are written to a file.

The output file appears to be a video file:

```
$ file output.avi
output.avi: RIFF (little-endian) data, AVI, 640 x 360, ~30 fps, video: FFMpeg MPEG-4
```

The video has an RFC3339 timestamp in the top left corner.

### Answers

Enter the parity check matrix used to decode the data stream. Enter in JSON format, as an array of arrays ie. a 3x2 matrix, on a single line (optional), might be: [[1,0],[0,0],[1,1]]

```
[[0,1,1,1,1,1,0,0,0,1,1,1,0,0,0,0],[1,0,0,1,1,0,1,1,0,1,1,0,1,0,0,0],[1,1,1,1,1,0,1,0,1,0,0,0,0,1,0,0],[0,1,0,0,1,1,1,1,1,0,1,0,0,0,1,0],[1,0,1,0,1,1,0,1,1,1,0,0,0,0,0,1]]
```

Enter the RFC3339 timestamp in the surveillance video stream that proves the hostage was alive recently inside the compound.

```
2020-10-01T09:15:53Z
```

## Task 7 - Plan for Rescue - (Reverse Engineering) (500 points)

### Description

With proof the journalist is alive and held inside the compound, the team will make a plan to infiltrate without being discovered. But, they see a surveillance drone and expect more are in the area. It is too risky without knowing how many there are and where they might be. To help the team avoid detection, we need you to enumerate all of the drones around the compound. Luckily we've made some discoveries at the safehouse which should help you. You can find the details in the attached README file. Get to work and give us the list of drone hostnames you discover. This will tell us how many to look for, and hopefully indicate where they might be.

### Solution

### Answers

Enter the list of hostnames (case sensitive, one per line)

```

```

## Task 8 - Rescue & Escape (Part 1) - (Reverse Engineering, Network Protocol Analysis) (1700 points)

### Description

The team is ready to go in to rescue the hostage. With your help they will be able to escape safely. There is no doubt the team will be detected once they find the hostage, so they will need help reaching the evacuation site. We need you to destroy all of the drones. Physically crashing the drone(s) at just the right moment will both disable any surveillance and distract the guards. This should give the team just enough time for escape to the evacuation site.

This will complicated...

We've done some more analysis looking at the strings and symbols in the drone binaries, and our technical team thinks the best approach is to send a 'restart' or 'poweroff' command to the power module in each of the drones. If the command is executed, the drone will lose power and drop out of the sky (and likely be destroyed).

But, it looks like the commands may not be executed when the drone is in-flight. Solving that will be the next step, but for now, focus on figuring out how to send a command to the power module, even if the command is rejected because the drone is in flight.

In this case, it would be best if you can determine a single message which can be sent to the controller so it can be be broadcast to all of the drones at exactly the same time. We dont know what other monitoring or safety mechanisms are in place if a drone malfunction is detected, and we cannot affort to disable only some of the drones.

Once you've determined the buffer that needs to be sent, upload it here We will use the './hello.py send_packet ' functionality to attempt to send the message to verify it

### Solution

### Answers

Upload the packet (not frame) contents we should send when we're ready to disable the drones. (It should not have the 2 byte length prefix)

```

```

## Task 9 - Rescue & Escape (Part 2) - (Reverse Engineering, Cryptography, Vulnerability Analysis) (2500 points)

### Description

Now that you have a working power command, you need to make changes to the drones so the command will be accepted. This string in the power module gives us some hope: 'New Flight Monitor Available. Allowing forced power command'

Find a vulnerability in the updater module that allows you to update the flightmonitor to a new version. Exploit the vulnerability on all of the drones to prepare them. Then, send your power command validated in the previous task. Once all the drones are unresponsive, let us know by submitting the word 'READY' in the field below. We will confirm and then send in the team to rescue the hostage!

Additional Notes: This will require a better understanding of the netsvc module than you needed for the power command. The update process looks like it requires a long running 'session' and the netsvc may have some protection mechanisms to guard against bruteforce attempts which you will need to abide by.

### Solution

### Answers

Let us know when you are 'READY'

```

```
