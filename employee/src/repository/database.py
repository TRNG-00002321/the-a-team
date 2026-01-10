"""
Database connection and initialization.
"""
import sqlite3
import os
from typing import Optional
from dotenv import load_dotenv


class DatabaseConnection:
    """Handles SQLite database connections and initialization."""
    
    def __init__(self, db_path: Optional[str] = None):
        load_dotenv()
        test_mode = os.getenv("TEST_MODE", "false").lower() == "true"

        if db_path:
            self.db_path = db_path
        elif test_mode:
            self.db_path = os.getenv("TEST_DATABASE_PATH")
        else:
            self.db_path = os.getenv('DATABASE_PATH', '/expense_manager.db')

        if not self.db_path:
            raise ValueError("Database path is not configured")
    
    def get_connection(self) -> sqlite3.Connection:
        """Get a database connection."""
        conn = sqlite3.connect(self.db_path)
        conn.row_factory = sqlite3.Row  # Enable dict-like access to rows
        return conn
    
    def initialize_database(self):
        """Create database tables if they don't exist."""
        with self.get_connection() as conn:
            # Create users table
            conn.execute('''
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                )
            ''')
            
            # Create expenses table
            conn.execute('''
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY,
                    user_id INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    description TEXT NOT NULL,
                    date TEXT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
            ''')
            
            # Create approvals table
            conn.execute('''
                CREATE TABLE IF NOT EXISTS approvals (
                    id INTEGER PRIMARY KEY,
                    expense_id INTEGER NOT NULL,
                    status TEXT NOT NULL DEFAULT 'pending',
                    reviewer INTEGER,
                    comment TEXT,
                    review_date TEXT,
                    FOREIGN KEY (expense_id) REFERENCES expenses (id),
                    FOREIGN KEY (reviewer) REFERENCES users (id)
                )
            ''')
            
            conn.commit()