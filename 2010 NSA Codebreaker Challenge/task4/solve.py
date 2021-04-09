#!/usr/bin/env python3

import json

# Read the JSON file
with open("stepinator.json", "r") as f:
    data = json.load(f)

# Print information at each time
cur_speed = 0
distance = 0
time = 0
for acceleration in data:
    print(
        f"t = {time}\ta = {acceleration}\tv = {round(cur_speed, 2)}\td = {round(distance, 2)}"
    )
    cur_speed += acceleration
    distance += cur_speed
    time += 1
