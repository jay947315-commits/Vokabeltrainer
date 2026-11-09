#!/usr/bin/env python3
"""Build VocaPro's offline English-German SQLite dictionary from the CC BY-SA release."""
import gzip, hashlib, html, io, os, re, sqlite3, struct, urllib.request, zipfile
from pathlib import Path

URL = "https://github.com/DanielGregorini/koreader-dicts/releases/download/v1.3.0/en-de.zip"
SHA256 = "61ac5f8a481f1a5d067555d886f1e1f530a5bd7b6d28abec65f0ca44a0ffed8e"
ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/src/main/assets"
ASSETS.mkdir(parents=True, exist_ok=True)

print("Downloading open English-German dictionary…")
with urllib.request.urlopen(URL, timeout=90) as response:
    archive = response.read()
digest = hashlib.sha256(archive).hexdigest()
if digest != SHA256:
    raise SystemExit(f"Dictionary checksum mismatch: {digest}")
zf = zipfile.ZipFile(io.BytesIO(archive))
names = zf.namelist()

def find_file(suffix):
    matches = [n for n in names if n.lower().endswith(suffix.lower())]
    if not matches:
        raise RuntimeError(f"Missing {suffix} in dictionary archive")
    return matches[0]

idx = zf.read(find_file(".idx"))
dict_data = gzip.decompress(zf.read(find_file(".dict.dz")))
records = []
pos = 0
while pos < len(idx):
    end = idx.find(b"\0", pos)
    if end < 0 or end + 9 > len(idx):
        raise RuntimeError("Malformed StarDict index")
    word = idx[pos:end].decode("utf-8", "replace").strip()
    offset, size = struct.unpack(">II", idx[end+1:end+9])
    raw = dict_data[offset:offset+size].decode("utf-8", "replace")
    raw = re.sub(r"<br\s*/?>", " · ", raw, flags=re.I)
    raw = re.sub(r"</(p|div|li)>", " · ", raw, flags=re.I)
    meaning = html.unescape(re.sub(r"<[^>]+>", " ", raw))
    meaning = re.sub(r"\s+", " ", meaning).strip(" ·;")
    if word and meaning:
        records.append((word, meaning))
    pos = end + 9

db_path = ASSETS / "en_de_dictionary.db"
if db_path.exists():
    db_path.unlink()
db = sqlite3.connect(db_path)
db.execute("PRAGMA journal_mode=OFF")
db.execute("PRAGMA synchronous=OFF")
db.execute("CREATE TABLE entries (id INTEGER PRIMARY KEY, word TEXT NOT NULL COLLATE NOCASE UNIQUE, meaning TEXT NOT NULL)")
db.execute("CREATE INDEX idx_entries_word ON entries(word COLLATE NOCASE)")
db.executemany("INSERT OR IGNORE INTO entries(word,meaning) VALUES (?,?)", records)
# StarDict synonym file points to the zero-based entry number in .idx.
syn_suffix = ".syn"
syn_names = [n for n in names if n.lower().endswith(syn_suffix)]
if syn_names:
    syn = zf.read(syn_names[0])
    pos = 0
    while pos < len(syn):
        end = syn.find(b"\0", pos)
        if end < 0 or end + 5 > len(syn): break
        alias = syn[pos:end].decode("utf-8", "replace").strip()
        entry_no = struct.unpack(">I", syn[end+1:end+5])[0]
        if alias and entry_no < len(records):
            db.execute("INSERT INTO entries(word,meaning) VALUES (?,?) ON CONFLICT(word) DO NOTHING",
                       (alias, records[entry_no][1]))
        pos = end + 5
db.commit()
count = db.execute("SELECT COUNT(*) FROM entries").fetchone()[0]
db.close()

# Keep the original upstream notices bundled in the app.
notice_files = [n for n in names if any(k in n.lower() for k in ("attribution", "license", "readme"))]
notice = ["VocaPro offline English-German dictionary", "Source: Daniel Gregorini, koreader-dicts, release v1.3.0", "Dictionary source: English Wiktionary via Kaikki/Wiktextract and Princeton WordNet 3.0.", "Dictionary compilation license: CC BY-SA 4.0. See the bundled source notices below.", "Project: https://github.com/DanielGregorini/koreader-dicts", "Data page: https://danielgregorini.github.io/koreader-dicts/en/en-de/", "License: https://creativecommons.org/licenses/by-sa/4.0/", ""]
for name in notice_files:
    notice.append(f"===== {name} =====")
    notice.append(zf.read(name).decode("utf-8", "replace"))
(ASSETS / "DICTIONARY_ATTRIBUTION.txt").write_text("\n".join(notice), encoding="utf-8")
print(f"Built {count:,} offline entries; database size: {db_path.stat().st_size:,} bytes")
