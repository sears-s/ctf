# Read the file
with open("50k-users.txt", "r") as f:
    data = f.read()

# Find username with specifications
for user in data.split("\n"):
    if len(user) < 1:
        continue
    if not user[2] == "x":
        continue
    if not user[3] in ["2", "3", "4", "5", "6"]:
        continue
    if not "Z" in user:
        continue
    if not user[-1] == "S":
        continue
    print(user)
