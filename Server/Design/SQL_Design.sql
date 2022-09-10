BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "Users" (
	"Username"	TEXT NOT NULL UNIQUE,
	"Password"	TEXT NOT NULL,
	"Email"	TEXT NOT NULL UNIQUE,
	"First_Name"	TEXT NOT NULL,
	"Last_Name"	TEXT NOT NULL,
	"Gender"	TEXT NOT NULL,
	"Person_ID"	TEXT NOT NULL,
	"Index"	INTEGER,
	PRIMARY KEY("Index" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Events" (
	"personID"	INTEGER UNIQUE,
	"User"	TEXT,
	"Person_ID"	TEXT NOT NULL,
	"Latitude"	REAL,
	"Longitude"	REAL,
	"Country"	TEXT,
	"City"	TEXT,
	"EventType"	TEXT,
	"Year"	INTEGER,
	PRIMARY KEY("personID" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Persons" (
	"personID"	INTEGER UNIQUE,
	"User"	TEXT NOT NULL,
	"First_Name"	TEXT NOT NULL,
	"Last_Name"	TEXT NOT NULL,
	"Gender"	TEXT NOT NULL,
	"Father"	TEXT,
	"Mother"	TEXT,
	"Spouse"	TEXT,
	PRIMARY KEY("personID" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "Authorization_Token" (
	"User"	TEXT NOT NULL,
	"Token"	TEXT NOT NULL UNIQUE,
	"TimeStamp"	INTEGER,
	PRIMARY KEY("Token")
);
COMMIT;
