import os
import requests  # Add this line
from flask import Flask, request, jsonify
from app.openai_client import get_chat_response
from app.recommender import generate_recommendation
from app.preference_store import update_user_preferences

from flask import Flask, request, jsonify
import random

app = Flask(__name__)
WEATHER_API_KEY = os.getenv('WEATHER_API_KEY', '311438e22dfd86b464b6d389a96a20a2')
GEOLOCATION_API_KEY = os.getenv('GEOLOCATION_API_KEY', 'd0af87be-bf06-4701-b857-420c1a5ad347')
OPENAI_API_KEY = os.getenv('OPENAI_API_KEY')

# Enhanced response database with Russian translations
RESPONSE_DB = {

  "greeting": {
    "en": [
      "Hello! How can I help your relationship today?",
      "Hi there! What relationship topic concerns you?",
      "Hey! Ready to strengthen your bond today?",
      "Welcome back! What’s on your heart today?",
      "Good to see you! Let’s work on love together.",
      "Hi! Need support with anything in your relationship?"
    ],
    "ru": [
      "Привет! Как я могу помочь вашим отношениям сегодня?",
      "Здравствуйте! Какая тема отношений вас волнует?",
      "Привет! Готовы поработать над вашими отношениями?",
      "Рада вас видеть! Что у вас на сердце сегодня?",
      "Добро пожаловать! Давайте вместе поработаем над любовью.",
      "Здравствуйте! Нужна поддержка в отношениях?"
    ]
  },
  "relationship_tips": {
    "en": [
      "Practice active listening - focus fully when your partner speaks",
      "Schedule regular 'us time' without distractions",
      "Express appreciation daily for small things",
      "Celebrate each other's achievements, big or small",
      "Check in emotionally with each other regularly",
      "Keep flirting – surprise them with a sweet message or smile",
      "Create shared rituals like morning coffee or evening walks"
    ],
    "ru": [
      "Практикуйте активное слушание - полностью сосредоточьтесь, когда говорит партнер",
      "Запланируйте регулярное время для вас двоих без отвлечений",
      "Выражайте благодарность за мелочи каждый день",
      "Отмечайте достижения друг друга, даже самые маленькие",
      "Регулярно проверяйте эмоциональное состояние друг друга",
      "Флиртуйте – удивляйте партнёра милым сообщением или улыбкой",
      "Создавайте совместные ритуалы, как утренний кофе или вечерние прогулки"
    ]
  },
  "conflict_resolution": {
    "en": [
      "Take a 20-minute break if emotions run high before continuing discussion",
      "Use 'I feel' statements instead of 'You always' accusations",
      "Find compromise where both get some of what they want",
      "Focus on the problem, not the person",
      "Agree to disagree when needed – not every battle needs a winner",
      "Apologize sincerely and ask how to make things right",
      "Use calm tones and open body language"
    ],
    "ru": [
      "Сделайте 20-минутный перерыв, если эмоции накаляются, прежде чем продолжать обсуждение",
      "Используйте фразы «Я чувствую» вместо обвинений «Ты всегда»",
      "Найдите компромисс, где каждый получит что-то желаемое",
      "Сосредоточьтесь на проблеме, а не на личности",
      "Иногда можно согласиться не соглашаться – не каждая ссора требует победителя",
      "Извиняйтесь искренне и спрашивайте, как всё исправить",
      "Используйте спокойный тон и открытую позу тела"
    ]
  },
        "compliments": {
    "en": [
        "You're lucky to have each other!",
        "You two make a beautiful couple!",
        "Your bond is truly inspiring.",
        "Your relationship radiates love and respect.",
        "You bring out the best in each other.",
        "You seem to understand each other so well.",
        "You're a team, and a great one at that.",
        "Your love is something special.",
        "You make love look easy.",
        "Your connection is really heartwarming."
    ],
    "ru": [
        "Вы такая прекрасная пара!",
        "Ваша связь вдохновляет.",
        "Вы словно созданы друг для друга.",
        "Вы — отличный тандем!",
        "Ваши отношения наполнены любовью и заботой.",
        "Вы дополняете друг друга.",
        "Вас приятно видеть вместе — столько тепла между вами.",
        "Вы — настоящая команда.",
        "Вы делаете любовь видимой.",
        "Ваша искренность и нежность восхищают."
    ]
},"weekend_ideas": {
    "en": [
        "Go hiking together!",
        "Plan a board game night",
        "Try a new restaurant or cuisine",
        "Have a movie marathon at home",
        "Take a spontaneous road trip",
        "Visit a nearby town or city",
        "Attend a local event or fair",
        "Cook a new recipe together",
        "Visit a botanical garden or zoo",
        "Have a technology-free day in nature"
    ],
    "ru": [
        "Сходите в поход",
        "Устройте вечер настольных игр",
        "Попробуйте новую кухню в ресторане",
        "Устроьте домашний кинотеатр на весь день",
        "Совершите спонтанную поездку на машине",
        "Посетите соседний город",
        "Сходите на местный фестиваль или ярмарку",
        "Приготовьте что-то новое вместе",
        "Посетите ботанический сад или зоопарк",
        "Устройте день без гаджетов на природе"
    ]
},
    "trust_issues": {
        "en": [
            "Trust is built through consistent actions over time",
            "Open communication is key to rebuilding trust",
            "Consider small steps to gradually rebuild confidence",
            "Be patient - trust takes time to develop",
            "Focus on being reliable in small things first"
        ],
        "ru": [
            "Доверие строится через последовательные действия",
            "Открытое общение - ключ к восстановлению доверия",
            "Делайте маленькие шаги для восстановления доверия",
            "Будьте терпеливы - доверие требует времени",
            "Начните с надежности в мелочах"
        ]
    },
    "intimacy": {
        "en": [
            "Emotional intimacy is the foundation for physical intimacy",
            "Try to express your needs openly but gently",
            "Small daily gestures of affection build connection",
            "Quality time together helps maintain intimacy",
            "Remember that intimacy ebbs and flows naturally"
        ],
        "ru": [
            "Эмоциональная близость - основа физической",
            "Попробуйте открыто, но мягко выражать свои желания",
            "Маленькие ежедневные знаки внимания укрепляют связь",
            "Качественное время вместе поддерживает близость",
            "Помните, что близость естественным образом меняется"
        ]
    },
    "anniversary_ideas": {
        "en": [
            "Create a memory book of your time together",
            "Recreate your first date",
            "Write love letters to each other",
            "Take a trip down memory lane visiting special places",
            "Plan a surprise activity your partner would love"
        ],
        "ru": [
            "Создайте альбом воспоминаний",
            "Повторите ваше первое свидание",
            "Напишите друг другу любовные письма",
            "Посетите места, важные для ваших отношений",
            "Запланируйте сюрприз, который понравится партнеру"
        ]
    },
    "emotion_recognition": {
        "en": [
            "Try naming the emotion you're noticing in your partner",
            "Ask open-ended questions about how they're feeling",
            "Validate their emotions before problem-solving",
            "Pay attention to body language cues",
            "Create a safe space for emotional expression"
        ],
        "ru": [
            "Попробуйте назвать эмоцию, которую вы замечаете у партнера",
            "Задавайте открытые вопросы об их чувствах",
            "Признайте их чувства прежде чем предлагать решения",
            "Обращайте внимание на язык тела",
            "Создайте безопасное пространство для выражения эмоций"
        ]
    }
}



# User preference database (simulated)
user_preferences = {}


def get_user_language(user_id):
    """Determine user's preferred language"""
    return user_preferences.get(user_id, {}).get("language", "en")


def get_relationship_duration(user_id):
    """Get how long the user has been in relationship"""
    return user_preferences.get(user_id, {}).get("relationship_duration", 0)


def get_partner_preferences(user_id):
    """Get partner's stored preferences"""
    return user_preferences.get(user_id, {}).get("partner_prefs", {})


def get_user_location(user_id):
    """Get user's location from preferences or IP"""
    # First try to get from preferences
    if user_id in user_preferences and "location" in user_preferences[user_id]:
        return user_preferences[user_id]["location"]

    # Fallback to IP geolocation
    ip_address = request.remote_addr
    location = get_geolocation(ip_address)
    if location:
        return {
            "city": location.get("city"),
            "country": location.get("country")
        }
    return None


def get_geolocation(ip_address):
    """Get location from IP address"""
    if ip_address == '127.0.0.1':
        ip_address = ''  # Use API's automatic IP detection for localhost

    url = f"https://ipgeolocation.abstractapi.com/v1/?api_key={GEOLOCATION_API_KEY}&ip_address={ip_address}"
    try:
        response = requests.get(url, timeout=3)
        if response.status_code == 200:
            return response.json()
    except requests.exceptions.RequestException:
        pass
    return None


def get_current_weather(location):
    """Get current weather for a location"""
    if not location or not location.get("city"):
        return None

    city = location["city"]
    country = location.get("country", "")

    url = f"http://api.openweathermap.org/data/2.5/weather?q={city},{country}&appid={WEATHER_API_KEY}&units=metric"
    try:
        response = requests.get(url, timeout=3)
        if response.status_code == 200:
            weather_data = response.json()
            return {
                "condition": weather_data["weather"][0]["main"].lower(),
                "temp": weather_data["main"]["temp"],
                "description": weather_data["weather"][0]["description"]
            }
    except (requests.exceptions.RequestException, KeyError):
        pass
    return None


def get_weather_condition(weather_data):
    """Determine general weather condition from weather data"""
    if not weather_data:
        return "sunny"  # Default to sunny if no weather data

    condition = weather_data["condition"]
    temp = weather_data["temp"]

    if "rain" in condition:
        return "rainy"
    elif "snow" in condition:
        return "snowy"
    elif temp > 30:  # Very hot
        return "sunny"
    elif temp < 5:  # Very cold
        return "snowy"
    else:
        return "sunny"


def get_weather_based_recommendation(user_id):
    """Get recommendation based on actual weather"""
    lang = get_user_language(user_id)
    location = get_user_location(user_id)
    weather_data = get_current_weather(location)
    weather_condition = get_weather_condition(weather_data) if weather_data else "sunny"

    recommendations = {
        "sunny": {
            "en": [
                "How about a picnic in the park?",
                "Rent bikes and explore a new neighborhood",
                "Have lunch on a sunny patio",
                "Visit an outdoor farmers market",
                "Go for a swim or beach day"
            ],
            "ru": [
                "Как насчет пикника в парке?",
                "Арендуйте велосипеды и прокатитесь по новому району",
                "Пообедайте на летней террасе",
                "Сходите на фермерский рынок",
                "Сходите на пляж или поплавайте"
            ]
        },
        "rainy": {
            "en": [
                "Perfect day for a museum visit and cozy café",
                "Watch a movie at home under a blanket",
                "Bake something together",
                "Visit an indoor botanical garden",
                "Try a board game café"
            ],
            "ru": [
                "Идеальный день для посещения музея и уютного кафе",
                "Посмотрите фильм дома под пледом",
                "Испеките что-нибудь вместе",
                "Посетите зимний сад или оранжерею",
                "Попробуйте кафе с настольными играми"
            ]
        },
        "snowy": {
            "en": [
                "Go ice skating together",
                "Warm up with hot chocolate and a shared book",
                "Build a snowman and act like kids again",
                "Visit a cozy winter market",
                "Try a fondue restaurant"
            ],
            "ru": [
                "Сходите вместе на каток",
                "Горячий шоколад и совместное чтение книги",
                "Слепите снеговика и повеселитесь по-детски",
                "Посетите уютную зимнюю ярмарку",
                "Попробуйте ресторан с фондю"
            ]
        }
    }

    return {
        "recommendation": random.choice(recommendations[weather_condition][lang]),
        "weather": weather_data["description"] if weather_data else "sunny weather"
    }


def generate_gift_recommendations(user_id):
    prefs = get_partner_preferences(user_id)
    lang = get_user_language(user_id)

    gifts = {
        "flowers": {"en": "a bouquet of their favorite flowers", "ru": "букет их любимых цветов"},
        "book": {"en": "a personalized book based on their interests", "ru": "персонализированная книга по их интересам"},
        "spa": {"en": "a couple's spa day", "ru": "день в спа для пар"},
        "jewelry": {"en": "a piece of elegant jewelry", "ru": "украшение со вкусом"},
        "custom_mug": {"en": "a custom mug with a shared photo", "ru": "чашка с вашей общей фотографией"},
        "experience": {"en": "a surprise experience like a dance class", "ru": "неожиданное событие, например урок танцев"},
        "subscription": {"en": "a subscription to something they love (e.g., audiobooks, flowers)", "ru": "подписка на то, что им нравится (аудиокниги, цветы и т.д.)"},
        "memory_box": {"en": "a box with memories and messages", "ru": "коробка с воспоминаниями и записками"},
        "photo_album": {"en": "a printed photo album of your moments together", "ru": "фотоальбом с вашими совместными моментами"}
    }

    if prefs.get("likes_flowers", False):
        return {"gift": gifts["flowers"][lang], "reason": "because they love flowers"}
    elif prefs.get("favorite_author"):
        return {"gift": gifts["book"][lang], "reason": f"they enjoy {prefs['favorite_author']}'s works"}
    elif prefs.get("loves_spa"):
        return {"gift": gifts["spa"][lang], "reason": "to help them relax and recharge"}
    elif prefs.get("likes_jewelry"):
        return {"gift": gifts["jewelry"][lang], "reason": "because they enjoy elegant accessories"}
    else:
        return {"gift": random.choice(list(gifts.values()))[lang], "reason": "to surprise them with something thoughtful"}


def generate_date_ideas(user_id):
    lang = get_user_language(user_id)
    duration = get_relationship_duration(user_id)

    ideas = {
        "new": {
            "en": [
                "Try a cooking class together",
                "Visit a local museum",
                "Go to an escape room",
                "Have a coffee date at a cozy new café",
                "Go on a mini photo walk in the city"
            ],
            "ru": [
                "Мастер-класс по кулинарии",
                "Посещение местного музея",
                "Квест-комната",
                "Свидание в уютном кафе",
                "Фотопрогулка по городу"
            ]
        },
        "established": {
            "en": [
                "Recreate your first date",
                "Weekend getaway",
                "Cook dinner together at home with candles",
                "Plan a surprise staycation",
                "Take a sunset walk and talk"
            ],
            "ru": [
                "Повторите ваше первое свидание",
                "Выходные в новом месте",
                "Приготовьте ужин дома со свечами",
                "Устройте романтический вечер дома",
                "Прогулка на закате с душевным разговором"
            ]
        }
    }

    category = "new" if duration < 6 else "established"
    return random.choice(ideas[category][lang])

def generate_weekly_challenge(user_id):
    lang = get_user_language(user_id)
    challenges = {
        "en": [
            "Leave surprise love notes for your partner this week",
            "Try a new activity together that you've never done before",
            "Each day, share one thing you appreciate about your partner",
            "Do one unexpected kind thing for your partner daily",
            "Write a letter expressing what you love about your relationship",
            "Unplug from screens during dinner and talk about your day",
            "Plan a surprise mini date one evening this week"
        ],
        "ru": [
            "Оставляйте партнеру сюрприз-записки с признаниями на этой неделе",
            "Попробуйте новое совместное занятие",
            "Каждый день говорите партнеру, за что вы его цените",
            "Каждый день делайте что-то неожиданное и приятное для партнера",
            "Напишите письмо о том, за что вы любите ваши отношения",
            "Во время ужина отложите телефоны и поговорите о дне",
            "Устройте неожиданный мини-свидание вечером на этой неделе"
        ]
    }
    return random.choice(challenges[lang])

@app.route("/api/chat", methods=["POST"])
def chat():
    data = request.get_json()
    user_id = str(data.get("user_id"))
    message = data.get("message")
    context = data.get("context", "")

    response = {
        "response": "",
        "suggestions": [],
        "follow_up": None,
        "metadata": {}
    }

    lang = get_user_language(user_id)
    message_lower = message.lower()

    # Relationship tips
    def matches(keywords):
        return any(word in message_lower for word in keywords)

    if matches(["tip", "advice", "совет", "рекомендац"]):
        response["response"] = random.choice(RESPONSE_DB["relationship_tips"][lang])

    elif matches(["fight", "argument", "conflict", "ссор", "конфликт", "разноглас"]):
        response["response"] = random.choice(RESPONSE_DB["conflict_resolution"][lang])

    elif matches(["gift", "present", "подарок", "сюрпр", "удивить"]):
        gift_info = generate_gift_recommendations(user_id)
        response["response"] = f"{gift_info['gift']} ({gift_info['reason']})"

    elif matches(["date", "свидан", "встретиться", "поужинать", "выйти вдвоем"]):
        weather_rec = get_weather_based_recommendation(user_id)
        date_idea = generate_date_ideas(user_id)
        response["response"] = f"{weather_rec['recommendation']} {date_idea}"
        response["metadata"]["weather"] = weather_rec["weather"]

    elif matches(["challenge", "задани", "испытан", "вызов"]):
        response["response"] = generate_weekly_challenge(user_id)

    elif matches(["angry", "sad", "depressed", "зл", "груст", "депресс", "плохо", "тяжело", "одиноч"]):
        response["response"] = "I understand this is difficult. Would you like specific coping strategies?"
        response["follow_up"] = {
            "en": ["Yes, help me cope", "Not right now"],
            "ru": ["Да, помогите справиться", "Не сейчас"]
        }

    elif matches(["compliment", "комплимент", "приятное слово", "похвал"]):
        response["response"] = random.choice(RESPONSE_DB["compliments"][lang])

    elif matches(["weekend", "выходные", "что сделать в выходные", "планы на субботу", "куда сходить"]):
        response["response"] = random.choice(RESPONSE_DB["weekend_ideas"][lang])

    elif matches(["trust", "jealous", "ревность", "доверие", "подозрен"]):
        response["response"] = random.choice(RESPONSE_DB["trust_issues"][lang])

    elif matches(["sex", "интим", "близость", "страсть"]):
        response["response"] = random.choice(RESPONSE_DB["intimacy"][lang])

    elif matches(["anniversary", "дата", "годовщина", "праздник", "событие"]):
        response["response"] = random.choice(RESPONSE_DB["anniversary_ideas"][lang])

    elif matches(["emotion", "эмоции", "чувства", "настроение", "как он/она себя чувству"]):
        response["response"] = random.choice(RESPONSE_DB["emotion_recognition"][lang])

    else:
        response["response"] = random.choice(RESPONSE_DB["greeting"][lang])

    return jsonify(response)


@app.route("/api/update_preferences", methods=["POST"])
def update_prefs():
    data = request.get_json()
    user_id = str(data.get("user_id"))

    if user_id not in user_preferences:
        user_preferences[user_id] = {}

    # Update language preference
    if "language" in data:
        user_preferences[user_id]["language"] = data["language"]

    # Update location if provided
    if "location" in data:
        user_preferences[user_id]["location"] = {
            "city": data["location"].get("city"),
            "country": data["location"].get("country")
        }
    # Update partner preferences from quiz
    if "partner_quiz" in data:
        user_preferences[user_id]["partner_prefs"] = data["partner_quiz"]

    # Update relationship duration if provided
    if "relationship_duration" in data:
        user_preferences[user_id]["relationship_duration"] = data["relationship_duration"]

    return jsonify({"status": "preferences updated"})

@app.route("/api/weekly_report", methods=["POST"])
def weekly_report():
    data = request.get_json()
    user_id = str(data.get("user_id"))

    report = {
        "challenge": generate_weekly_challenge(user_id),
        "date_idea": generate_date_ideas(user_id),
        "tip": random.choice(RESPONSE_DB["relationship_tips"][get_user_language(user_id)])
    }
    return jsonify(report)

if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=5000)