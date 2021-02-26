INSERT INTO users (email, first_name, last_name, password) VALUES
    ("ruben.hakobyan@kcl.ac.uk", "Ruben", "Hakobyan", "$2a$10$M7pSodU7jpIjfffioigIuO.abhDBT1CNmca3wrXCla2nf9U/YbeEK");

INSERT INTO topic (title, description) VALUES
    ("Statements", "Learning how to define statements in Java."),
    ("Conditional Statements", "Learning how to change the flow of the program using conditional statements."),
    ("Loops", "Learning how to use loops and iterations in Java.");

INSERT INTO lesson (title, explanation, topic_id) VALUES
    ("Assignment Statements", "Assignment Statements Explained", 1),
    ("If Statement", "If Statement Explained", 2),
    ("If-Else Statement", "If-Else Statement Explained", 2),
    ("Switch Statement", "Switch Statement Explained", 2),
    ("While Loop", "While Loop Explained", 3),
    ("For Loop", "For Loop Explained", 3),
    ("For-Each Loop", "For-Each Loop Explained", 3);