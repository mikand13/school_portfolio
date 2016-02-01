INSERT INTO User (id, email, password, usertype) VALUES (1, 'mari@test.com', '123ABc', 'STUDENT');
INSERT INTO User (id, email, password, usertype) VALUES (2, 'ole@test.com', 'abC123', 'TEACHER');
INSERT INTO User (id, email, password, usertype) VALUES (3, 'kari@test.com', '1234Abc', 'STUDENT');
INSERT INTO User (id, email, password, usertype) VALUES (4, 'marianne@test.com', '1234Abc', 'STUDENT');

INSERT INTO Location (id, building, room) VALUES (1, 'Galleriet', '82');
INSERT INTO Location (id, building, room) VALUES (2, 'Galleriet', '81');

INSERT INTO Subject (id, name, FK_LOCATION) VALUES (1, 'PG5100', 1);
INSERT INTO Subject (id, name, FK_LOCATION) VALUES (2, 'PG6100', 2);

INSERT INTO Event (id, FK_SUBJECT, eventType, title, description) VALUES (1, 1, 'LECTURE', 'Lecture 1', 'First lecture on Java EE');
INSERT INTO Event (id, FK_SUBJECT, eventType, title, description) VALUES (2, 1, 'EXERCISE', 'Exercise 1', 'First exercise in Java EE');

INSERT INTO Event_Details (id, startTime, endTime) VALUES (1, parsedatetime('01-01-2015 12:00', 'dd-MM-yyyy HH:mm'), parsedatetime('02-01-2015 12:00', 'dd-MM-yyyy HH:mm'));
INSERT INTO Event_Details (id, startTime, endTime) VALUES (2, parsedatetime('01-01-2015 12:00', 'dd-MM-yyyy HH:mm'), parsedatetime('02-02-2015 12:00', 'dd-MM-yyyy HH:mm'));

INSERT INTO USR_SUB (SUBJECTS_ID, USERS_ID) VALUES (1, 1);
INSERT INTO USR_SUB (SUBJECTS_ID, USERS_ID) VALUES (2, 1);
INSERT INTO USR_SUB (SUBJECTS_ID, USERS_ID) VALUES (1, 2);
INSERT INTO USR_SUB (SUBJECTS_ID, USERS_ID) VALUES (2, 3);