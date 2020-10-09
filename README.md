# NSA Codebreaker Challenge 2020 Solutions

## Task 1
### Description
In accordance with USSID18, a collection on an American citizen is permitted in cases where the person is reasonably believed to be held captive by a group engaged in international terrorism. As a result, we have obtained a copy of the home directory from the journalist's laptop and are hoping it will contain information that will help us to locate and rescue the hostage. Your first task is to analyze the data and files available in the journalist's home directory.
### Solution
Extract `home.zip`. It contains a directory named `JakeOtters385`, so this must be the username. Within this directory, running `file pwfile` outputs `GPG symmetrically encrypted data (AES256 cipher)`, so `pwfile` must be the encrypted file
### Answers
What is the journalist's username on their computer?\
`JakeOtters385`\
Enter the file name for the encrypted file on the journalist's computer.\
`pwfile`