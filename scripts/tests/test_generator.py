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
        for chapter in catalog["chapters"]:
            for entry in chapter["entries"]:
                ids.append(entry["id"])
                self.assertTrue((ROOT / entry["file"]).is_file())
                self.assertTrue(entry["profiles"])
        self.assertEqual(len(ids), len(set(ids)))

    def test_selected_export_embeds_source_and_logo(self):
        with tempfile.TemporaryDirectory() as folder:
            output = Path(folder) / "notebook.tex"
            self.assertEqual(generate("full", {"games"}, output), 1)
            tex = output.read_text(encoding="utf-8")
            self.assertIn("firstWinsNim", tex)
            self.assertNotIn("class Geometry", tex)
            self.assertTrue((Path(folder) / "logo.png").is_file())


if __name__ == "__main__":
    unittest.main()
