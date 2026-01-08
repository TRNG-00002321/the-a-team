INSERT INTO users (id, username, password, role) VALUES
    (1, 'employee1', 'password123', 'Employee'),
    (2, 'employee2', 'password123', 'Employee'),
    (3, 'manager1',  'password123', 'Manager');

INSERT INTO expenses (id, user_id, amount, description, date) VALUES
    (1, 1, 50.00,  'Client lunch',    '2025-01-05'),
    (2, 1, 200.00, 'Hotel stay',      '2025-01-06'),
    (3, 1, 30.00,  'Parking fee',     '2025-01-07'),
    (4, 2, 75.00,  'Office supplies', '2025-01-05'),
    (5, 2, 450.00, 'Flight ticket',   '2025-01-08');

INSERT INTO approvals (id, expense_id, status, reviewer, comment, review_date) VALUES
    (1, 1, 'pending',  NULL, NULL, NULL),
    (2, 2, 'approved', 3, 'Approved', '2025-01-07'),
    (3, 3, 'denied', 3, 'Not reimbursable', '2025-01-07'),
    (4, 4, 'pending',  NULL, NULL, NULL),
    (5, 5, 'approved', 3, 'Business travel', '2025-01-09');
