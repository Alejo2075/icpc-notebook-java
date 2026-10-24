import tempfile
import unittest
from pathlib import Path
import sys

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from export_main import export_main, resolve_ids, catalog_entries


class ExporterTest(unittest.TestCase):
    def test_transitive_dependencies(self):
        self.assertEqual(resolve_ids(["advanced-graphs"], catalog_entries()),
                         ["structures", "graphs", "advanced-graphs"])

    def test_single_public_class_and_solution(self):
        with tempfile.TemporaryDirectory() as folder:
            output = Path(folder) / "Main.java"
            solution = Path(folder) / "solve.txt"
            solution.write_text('out.println("ready");\n', encoding="utf-8")
            export_main(["graphs"], output, solution)
            text = output.read_text(encoding="utf-8")
            self.assertEqual(text.count("public class Main"), 1)
            self.assertNotIn("package notebook;", text)
            self.assertIn("class DataStructures", text)
            self.assertIn("class Graphs", text)
            self.assertIn('out.println("ready");', text)

    def test_reject_unknown_entry(self):
        with tempfile.TemporaryDirectory() as folder:
            with self.assertRaises(ValueError):
                export_main(["unknown"], Path(folder) / "Main.java")


if __name__ == "__main__":
    unittest.main()
