import os
from dotenv import load_dotenv
from src.repository import DatabaseConnection
from tests.end_to_end_test.drivers.browser_manager import create_driver

SEED_SQL_PATH = os.path.abspath(
    os.path.join(os.path.dirname(__file__), "../../sql/seed.sql")
)

#implement multi browser functionality?

def before_all(context):
    # Read DB path from environment
    load_dotenv()
    db_path = os.getenv("DATABASE_PATH")
    if not db_path:
        raise RuntimeError(
            "DATABASE_PATH is not set. "
            "Start the server with DATABASE_PATH pointing to a test database."
        )

    if not os.path.exists(db_path):
        raise RuntimeError(f"Database file not found at {db_path}")

    # Use the SAME database as the running server
    context.db = DatabaseConnection(db_path)


def after_all(context):
    pass

def before_scenario(context, scenario):
    # --- Reset & reseed database ---
    with context.db.get_connection() as conn:
        conn.execute("DELETE FROM approvals")
        conn.execute("DELETE FROM expenses")
        conn.execute("DELETE FROM users")

        with open(SEED_SQL_PATH, "r") as f:
            conn.executescript(f.read())

        conn.commit()

    # --- Browser setup ---
    browser = os.getenv("BROWSER", "chrome").lower()
    headless = os.getenv("headless", "false").lower() == "true"

    context.driver = create_driver(browser, headless)
    context.driver.maximize_window()


def after_scenario(context, scenario):
    if context.driver:
        context.driver.quit()