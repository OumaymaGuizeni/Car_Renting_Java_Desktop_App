Manager Renting Car Agency

Created on 30 jan 11:04
Project Overview

Manager Renting Car Agency is a Java-based application designed to efficiently manage car rental agencies. The system allows for the management of rental vehicles, customer information, reservations, and billing processes, aiming to streamline agency operations and enhance user experience.
Features

    Car Management: Add, remove, and update vehicle details.
    Customer Management: Manage customer profiles and rental history.
    Reservation System: Book, modify, and cancel vehicle rentals.
    Billing System: Generate and manage invoices and payment tracking.
    Reporting: Detailed reports on rental statistics, revenue, and vehicle usage.

Technologies Used

    Programming Language: Java
    Development Tools: VSCode or your preferred Java IDE
    Version Control: Git
    CI/CD Pipeline: Integrated for testing and deployment
    Testing Framework: (You can specify the testing framework, such as JUnit or TestNG)
    Build Tool: Maven/Gradle (Specify based on your choice)

CI/CD Pipeline

We have integrated a continuous integration and continuous deployment (CI/CD) pipeline into the project for:

    Automated Testing: Each code push triggers automated unit and integration tests to ensure the application is stable.
    Automated Deployment: Once all tests pass, the application is automatically deployed to the target environment.

Installation

    Clone the repository:

    bash

git clone https://github.com/OumaymaGuizeni/Car_Renting_Java_Desktop_App.git
cd manager-renting-car-agency

Build the project:

    If using Maven:

    bash

mvn clean install

If using Gradle:

bash

    gradle build

Run the project:

    If using Maven:

    bash

mvn exec:java

If using Gradle:

bash

        gradle run

Testing

We use (Testing Framework) to write and run unit and integration tests. To run the tests, use the following commands:

    Maven:

    bash

mvn test

Gradle:

bash

    gradle test

License

This project is licensed under a private license. Unauthorized copying, distribution, or modification is prohibited.