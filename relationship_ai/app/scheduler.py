from flask_apscheduler import APScheduler
from app import create_app
from app.utils.recommendations import send_weekly_recommendations

scheduler = APScheduler()

def start_scheduler():
    app = create_app()
    scheduler.init_app(app)
    scheduler.start()

    @scheduler.task('cron', id='weekly_recommendations', day_of_week='sun', hour=9)
    def weekly_task():
        with app.app_context():
            send_weekly_recommendations()
