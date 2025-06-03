import os

class Config:
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URL', 'postgresql://postgres:12345678@localhost:5432/heatkeeper')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
