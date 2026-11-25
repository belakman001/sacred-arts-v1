#!/usr/bin/env python3
"""Watch the GitHub Actions build for a pushed commit SHA.

Usage: python3 watch_build.py <commit-sha> [timeout_minutes]

Polls GET /repos/belakman001/sacred-arts-v1/actions/runs?per_page=5 every 60s
until the run for the given SHA completes (up to timeout, default 25 min).
On success, prints the artifact id for the sacredarts-jar artifact so the
caller can download it. Unauthenticated API calls (shared egress IP can
rate-limit — a 403 just waits for the next poll).
"""
import json
import sys
import time
import urllib.request
import urllib.error

REPO = "belakman001/sacred-arts-v1"
API = f"https://api.github.com/repos/{REPO}"


def get(url):
    req = urllib.request.Request(url, headers={"Accept": "application/vnd.github+json"})
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            return json.load(resp)
    except urllib.error.HTTPError as e:
        print(f"[watch] HTTP {e.code} on {url} — retrying next poll", flush=True)
        return None
    except Exception as e:
        print(f"[watch] error: {e} — retrying next poll", flush=True)
        return None


def main():
    sha = sys.argv[1]
    timeout_min = int(sys.argv[2]) if len(sys.argv) > 2 else 25
    deadline = time.time() + timeout_min * 60
    print(f"[watch] waiting for build of {sha}", flush=True)
    run_id = None
    while time.time() < deadline:
        data = get(f"{API}/actions/runs?per_page=5")
        if data:
            for run in data.get("workflow_runs", []):
                if run.get("head_sha") == sha:
                    run_id = run["id"]
                    status = run["status"]
                    print(f"[watch] run {run_id}: {status} (conclusion: {run.get('conclusion')})", flush=True)
                    if status == "completed":
                        conclusion = run.get("conclusion")
                        if conclusion == "success":
                            arts = get(f"{API}/actions/runs/{run_id}/artifacts")
                            if arts:
                                for a in arts.get("artifacts", []):
                                    print(f"[watch] artifact: {a['name']} id={a['id']}", flush=True)
                            print("[watch] BUILD GREEN", flush=True)
                            return 0
                        print(f"[watch] BUILD FAILED: {conclusion}", flush=True)
                        return 1
                    break
            else:
                print("[watch] no run for this SHA yet", flush=True)
        time.sleep(60)
    print("[watch] timed out", flush=True)
    return 2


if __name__ == "__main__":
    sys.exit(main())
