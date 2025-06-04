Hotel Management System (Java)
A Java-based Hotel Management System designed to streamline hotel operations such as room bookings, guest information management, and administrative tasks.

Features
Room Booking: Facilitates booking of various room types (Single, Double, Triple) with different categories (VIP, Normal, Economy).

Guest Management: Stores and manages guest information efficiently.

Administrative Access: Provides functionalities for administrators to manage hotel data.

Data Persistence: Utilizes text files (admininfo.txt, guestsinfo.txt) to persist data across sessions.
github.com

Technologies Used
Java: Core programming language for application logic.

Maven: Build automation tool for managing project dependencies.

File Handling: For reading and writing data to text files.
github.com

Project Structure
css
Copy
Edit
HotelManagementJava/
├── data/
├── src/
│   └── main/
├── admininfo.txt
├── guestsinfo.txt
├── pom.xml
├── mvnw
├── mvnw.cmd
└── .gitignore
data/: Contains data-related resources.

src/main/: Holds the main Java source code.

admininfo.txt: Stores administrator credentials or information.

guestsinfo.txt: Stores guest-related information.

pom.xml: Maven configuration file.
github.com
+9
github.com
+9
github.com
+9
github.com
+7
docs.github.com
+7
github.com
+7
gist.github.com
+1
github.com
+1

Getting Started
Prerequisites
Java Development Kit (JDK) installed.

Maven installed.
docs.github.com
github.com
+1
github.com
+1

Installation
Clone the repository:

bash
Copy
Edit
git clone https://github.com/eyumazi/HotelManagementJava.git
cd HotelManagementJava
Build the project using Maven:

bash
Copy
Edit
mvn clean install
Run the application:

Navigate to the src/main directory and execute the main class.

Usage
Upon running the application, follow the on-screen prompts to navigate through the hotel management functionalities.

Ensure that admininfo.txt and guestsinfo.txt are present in the root directory for data persistence.
github.com

Contributing
Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

License
This project is open-source and available under the MIT License.
