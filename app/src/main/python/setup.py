import spacy
import os

MODEL_PATH = "model"


def setup() -> None:
    if not os.path.exists(MODEL_PATH):
        nlp = spacy.load("ru_core_news_md")
        nlp.to_disk(MODEL_PATH)
