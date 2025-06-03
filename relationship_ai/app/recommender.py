from app.preference_store import load_user_preferences


def generate_recommendation(user_id, rec_type):
    prefs = load_user_preferences(user_id)

    if rec_type == "подарок":
        return {
            "recommendation": f"Рекомендуется персональный подарок на основе предпочтений: {prefs.get('gifts', 'книга, романтический ужин')}"
        }
    elif rec_type == "свидание":
        return {
            "recommendation": f"Предлагаем сходить в {prefs.get('places', 'уютное кафе')} — неподалёку от вас. Учитываем погоду и сезон."
        }
    elif rec_type == "фильм":
        return {
            "recommendation": "Посмотрите вместе фильм '1+1' или 'Стажёр' — отличный выбор для пар."
        }
    elif rec_type == "еженедельный_совет":
        return {
            "recommendation": "На этой неделе попробуйте задавать друг другу 3 вопроса каждый вечер, чтобы укрепить связь."
        }
    else:
        return {
            "recommendation": f"Совет по теме '{rec_type}' ещё не реализован."
        }
