# NSA Codebreaker Challenge 2020 Solutions

## Task 1
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

## Task 2
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