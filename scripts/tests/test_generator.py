import json
import tempfile
import unittest
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from generate_notebook import ROOT, generate


class GeneratorTest(unittest.TestCase):
    def test_catalog_sources_and_profiles(self):
        catalog = json.loads((ROOT / "notebook.json").read_text(encoding="utf-8"))
        ids = []
        files = []
        for chapter in catalog["chapters"]:
            self.assertTrue(chapter["intro"])
            for entry in chapter["entries"]:
                ids.append(entry["id"])
                self.assertTrue((ROOT / entry["file"]).is_file())
                self.assertTrue(entry["profiles"])
                for key in ("title", "level", "when", "complexity", "caution"):
                    self.assertTrue(entry[key])
                for key in ("idea", "example", "practice"):
                    self.assertTrue(entry["study"][key])
                if entry["id"] != "template":
                    files.append(entry["file"])
        self.assertEqual(len(ids), len(set(ids)))
        self.assertEqual(len(files), len(set(files)))
        self.assertEqual(set(files), {
            str(path.relative_to(ROOT)) for path in (ROOT / "src/main/java/notebook").rglob("*.java")
        })
        by_profile = {
            profile: {entry["id"] for chapter in catalog["chapters"]
                      for entry in chapter["entries"] if profile in entry["profiles"]}
            for profile in ("beginner", "intermediate", "advanced")
        }
        self.assertTrue(by_profile["beginner"] < by_profile["intermediate"] < by_profile["advanced"])
        self.assertEqual(by_profile["advanced"], set(ids))
        self.assertEqual(set(catalog["profileGuides"]), set(by_profile))
        self.assertEqual(set(catalog["learningNotes"]), set(by_profile))
        self.assertEqual(set(catalog["guidedProblems"]), set(by_profile))
        for chapter in catalog["chapters"]:
            for entry in chapter["entries"]:
                self.assertTrue(set(entry.get("requires", [])) <= set(ids))
                for profile in entry["profiles"]:
                    self.assertTrue(set(entry.get("requires", [])) <= by_profile[profile])

    def test_selected_export_embeds_source_and_logo(self):
        with tempfile.TemporaryDirectory() as folder:
            output = Path(folder) / "notebook.tex"
            self.assertEqual(generate("advanced", {"games"}, output), 1)
            tex = output.read_text(encoding="utf-8")
            self.assertIn("firstWinsNim", tex)
            self.assertNotIn("class Geometry", tex)
            self.assertIn("How to use this reference", tex)
            self.assertIn("Learning path: Advanced", tex)
            self.assertTrue((Path(folder) / "logo.png").is_file())

    def test_beginner_has_teaching_content(self):
        with tempfile.TemporaryDirectory() as folder:
            output = Path(folder) / "beginner.tex"
            self.assertGreaterEqual(generate("beginner", set(), output), 11)
            tex = output.read_text(encoding="utf-8")
            self.assertIn("Core idea.", tex)
            self.assertIn("Worked example.", tex)
            self.assertIn("Problem-solving foundations", tex)
            self.assertIn("Guided problems", tex)
            self.assertIn("class BeginnerArrays", tex)
            self.assertNotIn("class MinCostFlow", tex)


if __name__ == "__main__":
    unittest.main()
