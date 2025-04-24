import os, json, re, argparse
import requests

parser = argparse.ArgumentParser()
parser.add_argument(
    "--pr-numbers", default="", help="Comma-separated PR numbers to process"
)
args = parser.parse_args()

repo = os.environ["GITHUB_REPOSITORY"]
token = os.environ["BOT_TOKEN"]
headers = {"Authorization": f"Bearer {token}", "Accept": "application/vnd.github.v3+json"}

def fetch_pr(pr_number):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}"
    r = requests.get(url, headers=headers)
    r.raise_for_status()
    return r.json()

def extract_entries(title: str, body: str) -> list[str]:
    """
    1. Strip all HTML comments.
    2. Find all blocks after each :cl: or 🆑 marker up to ---, -->, or end-of-string.
    3. Return lines from the LAST such block (negative index [-1]).
    4. If no block found, return [title].
    """
    # 1) Remove ALL HTML comments (<!-- ... -->), DOTALL so it spans lines
    cleaned = re.sub(r"<!--.*?-->", "", body or "", flags=re.S)

    # 2) Find all ":cl:" or "🆑" blocks up to the next ---, -->, or end-of-string
    pattern = r"(?:\:cl\:|🆑)(.*?)(?=(?:\n---|\n-->|$))"
    blocks = re.findall(pattern, cleaned, flags=re.S)  # returns list of inner captures

    if blocks:
        # 3) Only use the LAST block via negative indexing
        last = blocks[-1]
        # split on lines, strip whitespace, drop empty lines
        return [ln.strip() for ln in last.splitlines() if ln.strip()]

    # 4) Fallback when no live marker found
    return [title]

def process_pr(pr):
    num = pr["number"]
    title = pr["title"].strip()
    body = pr.get("body") or ""
    entries = extract_entries(title, body)
    link = f"https://github.com/{repo}/pull/{num}"
    lines = []
    for e in entries:
        # strip any existing leading dashes and spaces
        clean = e.lstrip('- ').strip()
        lines.append(f"- {clean} ([#{num}]({link}))")
    return lines

all_lines = []

if args.pr_numbers:
    for n in args.pr_numbers.split(","):
        pr = fetch_pr(n.strip())
        all_lines.extend(process_pr(pr))
else:
    with open(os.environ["GITHUB_EVENT_PATH"]) as f:
        ev = json.load(f)
    pr = ev["pull_request"]
    all_lines = process_pr(pr)

with open("CHANGELOG.md", "a") as fh:
    fh.write("\n".join(all_lines) + "\n")
