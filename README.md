# QRreport

Nowadays, offices, services and education infrastructures have more and more points of common use, and in these places there are more and more devices, such as taps, flushing systems, switches, doors, among many others. 
    
With the great diversity and high number of devices, when a specific or more general device breaks down or is inoperable, such as an electrical problem, the procedure to report it is not only very laborious and time expensive, but also very confusing, because, usually, a person never knows the procedure to report the problem, from whom to talk about it, to the place to report the anomaly detected. When already knows what to do, eventually, it will be necessary to describe on a paper or in an e-mail the localization, the device and the anomaly, without knowing afterwards if the repair is already done.
    
With these problems in mind, we have developed a system, in which all these difficulties will be **centralized**, with the objective of **reducing not only the time to report existing malfunctions**, but also the **time associated with their arrangement**, implementing an infrastructure for the reporting of problems in buildings and their distribution by employees qualified to solve the problems encountered, **making this whole process much faster for everyone involved**. The system is composed of a database handled through an API server, which receives requests via HTTP from client applications.
    
The **entry point** to start the report of a malfunction is through a `QR Code`, which will be near a reportable device and will redirect the mobile device to a form in order to submit the detected malfunction.

## How to run the project

For now the only way to test our project is to download the zip file and run the files in your own computer.

#### Backend
To run the backend files, i.e. the API server and PostgreSQL database you need to perform the following steps:

##### PostgreSQL Database
To set up the database, you need to create two databases, available on the **port 5432**, one called `production` and another called `test` for execution of the tests in the server API. And then you need to execute the SQL scripts **create_tables** and all the **functionalities scripts** that are inside the functionalities folder. If you want some data inside the database run the script **isel_study_case_data**.

An easier approach would be to execute the **Docker Compose** file that is on the path `code/docker`, to execute it run the following command: 
* **docker-compose up -d**

Don't forget to set the environment variables of the database with the keys: **DB_QRREPORT_USER**, with the user name used in the database and the key **DB_QRREPORT_PASSWORD**, with the database password. If you wish you can change these values defined in the docker compose file, otherwise you must use the values that are described in the file.

##### API Server
In the root of the jvm project execute the commands:
* **./gradlew build**
* **java -jar -cp build/libs project-0.0.1-SNAPSHOT.jar**

Or just run it in the IntelliJ IDEA or in a similar IDE.

#### Frontend
In case of the frontend code, developed with React, to run it in the root of the project, you must execute the following commands:
* **npm install**
* **npm start**

(Make sure you have the NPM installed in your computer)
