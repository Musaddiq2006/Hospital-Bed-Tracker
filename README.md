🏥 Hospital Bed Tracker
Greeting! This is my Hospital Bed Management System (HBMS). I built this to solve the headache of not knowing if a hospital actually has a bed free before you show up. It’s a Java Swing app that talks to a MySQL database to keep everything in sync.

🚀 What it actually does:
For Patients: You can hop on, check the live bed count for the ICU or General Ward, and "Claim" a bed. No more calling the front desk just to ask "is there space?"

For Admins: I added a password-protected portal (try 1234 ). Admins can add new wings to the hospital, import patient data from CSV files, and see a visual bar graph of which departments are getting full.

🛠 The "Under the Hood" Stuff:
Java Swing: For the windows and buttons.

MySQL: Where the actual room and patient data lives.

JDBC: The "bridge" I used to connect the Java code to the database.

📂 How to get it running:
Make sure you have MySQL installed.

Run the SQL script (I'll add a db.sql file soon) to set up the tables.

Change the password in HBMSmain.java to your own MySQL password.

Run it and start booking
