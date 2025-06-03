import os
import openai
from dotenv import load_dotenv
from app.preference_store import load_user_preferences

load_dotenv()
openai.api_key = os.getenv('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')

SYSTEM_PROMPT = (
    "Ты — виртуальный психолог-консультант по отношениям. Отвечай только на русском языке. "
    "Ты даёшь советы по улучшению романтических отношений, разрешению конфликтов, "
    "предлагаешь идеи для свиданий, подарков и эмоциональной связи между партнёрами."
)

def analyze_preferences(message):
    prompt = f"Извлеки предпочтения пользователя из следующего сообщения: '{message}'"
    response = openai.Completion.create(
        engine="text-davinci-003",
        prompt=prompt,
        max_tokens=150,
        temperature=0.7,
        language="ru"
    )
    return response.choices[0].text.strip()



def get_chat_response(user_id, message, context=""):
    prefs = load_user_preferences(user_id)

    prompt = (
        f"{SYSTEM_PROMPT}\n\n"
        f"Контекст: {context}\n"
        f"Предпочтения партнёра: {prefs}\n"
        f"Сообщение пользователя: {message}"
    )

    response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": prompt}
        ],
        temperature=0.7,
        max_tokens=500
    )

    return response.choices[0].message["content"]
