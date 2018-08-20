from pwn import *

def gcd(a, b):
    while b:
        a, b=b, a%b
    return a
def phi(a):
    b=a-1
    c=0
    while b:
        if not gcd(a,b)-1:
            c+=1
        b-=1
    return c

r = remote('misc04.grandprix.whitehatvn.com', 1337)
while 1==1:
	data = r.recvrepeat(1)
	print(data)

	lines = data.splitlines()
	for i in range(len(lines)):
		if lines[i].startswith("Face_index"):
			face_index = int(lines[i].split(' ')[1])
			print("Face index: " + str(face_index))
			break
	faces = []

	for y in range(i + 2, len(lines)):
		if lines[y].startswith("So, "):
			break
		split = lines[y].split(' ')
		points = []
		for thing in split:
			if len(thing) > 0:
				if thing.isdigit():
					points.append(int(thing))
				else:
					points.append(thing)
		if len(points) != 5:
			print("ERROR")
			print(points)
			exit(1)
		faces.append(points)

	best_face = "IDK"
	best_score = 0
	p = phi(face_index)
	print("Phi : " + str(p))
	for face in faces:
			x = pow(face[1], face[2], face_index)
			y = pow(face[3], face[4], p)
			score = pow(x, y, face_index)
			if score > best_score:
				best_score = score
				best_face = face[0]

	print("Sending " + best_face)
	r.sendline(best_face)
	data = r.recvrepeat(1)
	print(data)
	print("Sending " + str(best_score))
	r.sendline(str(best_score))
