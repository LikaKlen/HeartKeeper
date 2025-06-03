import os
import requests

API_KEY = os.getenv('GEO_LOC')

def get_geolocation(ip_address):
    url = f"https://ipgeolocation.abstractapi.com/v1/?api_key={API_KEY}&ip_address={ip_address}"
    response = requests.get(url)
    if response.status_code == 200:
        return response.json()
    return None
