# create databases
CREATE DATABASE IF NOT EXISTS `team15`;
CREATE DATABASE IF NOT EXISTS `testDB`;

-- # create root user and grant rights
ALTER USER 'root'@'localhost' IDENTIFIED BY '1q2w3e4r!@';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';