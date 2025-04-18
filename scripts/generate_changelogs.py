import requests
import re
import os

SEP = '\n\n'
PREFIX = 'Last update: #'
STOP = ['---', '-->']
COMMENTS = re.compile(r'<!--(.*?)-->', re.MULTILINE | re.DOTALL)

headers = {
    'Accept': 'application/vnd.github+json',
    'X-GitHub-Api-Version': '2022-11-28'
}

token = os.getenv('GITHUB_TOKEN')

if token:
    headers['Authorization'] = f'Bearer {token}'

changelog: str | None = '\n\n'
last_update = 0

with open('CHANGELOG.md', 'r') as f:
    s = f.read()
    if len(s.strip()) != 0:
        changelog = s

changelog_lines = changelog.splitlines()
clines = changelog_lines[len(SEP):]

if len(clines) != 0 and changelog_lines[0].startswith(PREFIX):
    last_update = int(changelog_lines[0][len(PREFIX):])

clines = []

r = requests.get('https://api.github.com/repos/amblelabs/ait/pulls?state=closed', headers=headers)
j = r.json()

if r.status_code in [403, 429]:
    t = input('Too many requests. Input your GitHub token in GITHUB_TOKEN env variable.')

max_pr = 0

for e in j:
    if not isinstance(e, dict):
        continue

    body = e['body']

    if not body:
        continue

    if not e['merged_at']:
        continue

    if e['base']['ref'] != 'main':
        continue

    body = COMMENTS.sub('', body)
    num = e['number']

    if num <= last_update:
        continue
    print(num, '>', last_update)

    max_pr = max(num, max_pr)

    if ':cl:' not in body:
        print(f'#{num}: changelog not found, using title')
        clines.append("- " + e['title'] + f" ([#{num}]({e['html_url']}))")
        continue

    print(f'#{num} changelog found')

    ls = body.splitlines()
    i = ls.index(':cl:') + 1

    for n in range(i, len(ls)):
        if len(ls[n].strip()) == 0:
            continue

        if ls[n] in STOP:
            break

        clines.append(ls[n] + f" ([#{num}]({e['html_url']}))")

with open('CHANGELOG.md', 'w') as f:
    f.write(PREFIX + str(last_update))

    f.write(SEP)
    f.write('\n'.join(clines))
