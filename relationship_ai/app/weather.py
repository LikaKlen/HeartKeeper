import os
import requests

API_KEY = os.getenv('WEATHER_API_KEY')

def get_weather(city):
    url = f"http://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}&lang=ru&units=metric"
    response = requests.get(url)
    if response.status_code == 200:
        return response.json()
    return None
