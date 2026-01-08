CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE expenses (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    amount REAL NOT NULL,
    description TEXT NOT NULL,
    date TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE approvals (
    id INTEGER PRIMARY KEY,
    expense_id INTEGER NOT NULL,
    status TEXT NOT NULL,
    reviewer INTEGER,
    comment TEXT,
    review_date TEXT,
    FOREIGN KEY (expense_id) REFERENCES expenses (id)
);
