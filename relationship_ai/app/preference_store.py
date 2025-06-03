import json
import os

PREF_PATH = "train/user_preferences.json"

def load_user_preferences(user_id):
    if not os.path.exists(PREF_PATH):
        return {}

    with open(PREF_PATH, "r", encoding="utf-8") as f:
        data = json.load(f)
    return data.get(user_id, {})

def update_user_preferences(user_id, message):
    if not os.path.exists(PREF_PATH):
        data = {}
    else:
        with open(PREF_PATH, "r", encoding="utf-8") as f:
            data = json.load(f)

    # Примитивный парсинг предпочтений на основе ключевых слов (можно улучшить)
    prefs = data.get(user_id, {})
    if "подар" in message:
        prefs["gifts"] = "украшения, путешествия"  # здесь можно извлекать предпочтения из текста
    if "свидание" in message:
        prefs["places"] = "парк, ресторан, кинотеатр"
    data[user_id] = prefs

    with open(PREF_PATH, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
