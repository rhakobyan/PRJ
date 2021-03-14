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

INSERT INTO problem (problem_file, lesson_id) VALUES
    ("problems/Problem1.java", 1),
    ("problems/problem2.java", 2),
    ("problems/problem3.java", 3),
    ("problems/problem4.java", 4),
    ("problems/problem5.java", 5),
    ("problems/problem6.java", 6),
    ("problems/problem7.java", 7);

INSERT INTO solution (solution_file, problem_id) VALUES
    ("solutions/problem2-solution1.java", 2);