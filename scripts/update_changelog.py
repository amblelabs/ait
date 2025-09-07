import argparse
import json
import os
import re
import requests

parser = argparse.ArgumentParser()
parser.add_argument(
    "--pr-numbers", default="", help="Comma-separated PR numbers to process"
)
args = parser.parse_args()

repo = os.environ["GITHUB_REPOSITORY"]
token = os.environ["BOT_TOKEN"]
headers = {"Authorization": f"Bearer {token}", "Accept": "application/vnd.github.v3+json"}

# Fetch PR metadata
def fetch_pr(pr_number):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}"
    r = requests.get(url, headers=headers)
    r.raise_for_status()
    return r.json()

# Fetch all commit authors on a PR
# Uses the List commits on a pull request endpoint
# └─ GET /repos/:owner/:repo/pulls/{pull_number}/commits ([docs.github.com](https://docs.github.com/rest/reference/pulls?utm_source=chatgpt.com))
def fetch_commit_authors(pr_number):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/commits"
    r = requests.get(url, headers=headers)
    r.raise_for_status()
    commits = r.json()
    authors = []
    for c in commits:
        author = c.get("author")
        if author and author.get("login"):
            login = author["login"]
            if login not in authors:
                authors.append(login)
    return authors

# Extract CL entries from title/body
def extract_entries(title: str, body: str) -> list[str]:
    cleaned = re.sub(r"<!--.*?-->", "", body or "", flags=re.S)
    pattern = r"(?:\:cl\:|🆑)(.*?)(?=(?:\n---|\n-->|$))"
    blocks = re.findall(pattern, cleaned, flags=re.S)
    if blocks:
        last = blocks[-1]
        return [ln.strip() for ln in last.splitlines() if ln.strip()]
    return [title]


# Determine if a PR should be ignored based on body content
def should_ignore(body: str) -> bool:
    cleaned = re.sub(r"<!--.*?-->", "", body or "", flags=re.S)
    return bool(re.search(r"(?:\:cl-ignore\:|🆑-ignore|:nocl:|no🆑)", cleaned))

# Process one PR into changelog lines, including authors hyperlinked
def process_pr(pr):
    num = pr["number"]
    title = pr["title"].strip()
    body = pr.get("body") or ""

    if should_ignore(body):
        return []

    entries = extract_entries(title, body)
    # Build markdown link to PR
    link = f"https://github.com/{repo}/pull/{num}"
    # Fetch all contributors/authors on this PR
    authors = fetch_commit_authors(num)
    # Format authors as markdown hyperlinks
    if authors:
        authors_md = ", ".join(f"[@{login}](https://github.com/{login})" for login in authors)
    else:
        authors_md = ""
    lines = []
    for e in entries:
        clean = e.lstrip('- ').strip()
        # Append "by authors" only if authors list not empty
        suffix = f" by {authors_md}" if authors_md else ""
        lines.append(f"- {clean} |{suffix} ([#{num}]({link}))")
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

# check if any lines were generated
if not all_lines:
    print("No changelog entries found.")
else:
    with open("CHANGELOG.md", "a") as fh:
        fh.write("\n".join(all_lines) + "\n")
