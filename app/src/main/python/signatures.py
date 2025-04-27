import json
from typing import List, Dict

JSON_PATH = "signatures.json"


def read_file() -> dict:
    with open(JSON_PATH, "r", encoding="utf") as file:
        return json.load(file)


data = read_file()


def get_base_signatures() -> List[str]:
    return data["Просьбы о переводе денег"]


def get_reason_signatures() -> Dict[str, Dict[str, List[str],]]:
    return data["Причины"]
