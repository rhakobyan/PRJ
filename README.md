# Intelligent Tutoring System to teach Java Programming for Year-1 students

The project developed an Intelligent Tutoring System website that provides adaptive hints and other features.

## How to run the code

#### !Please view the Some common errors section if you encounter any errors!

### Prerequisites
Before being able to run the code there, an appropriate environment needs to be set up.

1. Make sure that you have Java JDK 8 or higher installed. You can check of your java version by running ```java -version``` in your terminal or CLI.
2. Ensure that you have a MySQL server installed on your machine.
3. Install Gradle. You can follow the Gradle installation guide from here: https://gradle.org/install/.
4. Make sure your Gradle installation was successful by running ```gradle -v```.
5. Ascertain that your MySQL server is running on port 3306. You can check this by entering into the MySQL interface and running ```SHOW GLOBAL VARIABLES LIKE 'PORT';```. Alternatively, enter the src/main/resources directory, open the application.properties file and set up the ```spring.datasource.url``` field to your MySQL server port.
6. Create a new database in MySQL called 'prj'. Alternatively, if you do not want to name your database 'prj', name it anything you want then open the src/main/resources/application.properties file and change the database name in ```spring.datasource.url``` from 'prj' to the name of your selection.
7. Ensure that the database credentials ```spring.datasource.username``` and ```spring.datasource.password``` are set up correctly with your database credentials. The default MySQL credentials are pre-written here.


### Running
After all the prerequisites are complete the project can be started through Gradle.


1. Make sure that you are in the root directory.
2. Run ```gradle build``` in your terminal or CLI to build and compile the project.
3. Run ```gradle bootRun``` to start the application.
4. Visit http://localhost:8080/ in a browser of your choice to start working with the application.

## Some common errors
1. When building/running your application for the first time, you may see an error similar to ```java.sql.SQLSyntaxErrorException: Table 'prj.lesson' doesn't exist```. Simply ignore this error message and try building/running your application again.
2. When you unexpectedly get errors, such as ```java.lang.ClassNotFoundException``` or ```Unable to find a single main class from the following candidates```, when building or running the application, try deleting the build directory and building/running again.

# Deployed application
If you are unable to run the source code for some reason, you can visit the deployed version of the application on https://prj-its.herokuapp.com/
Please note, that this deployed version has not been well tested to ensure everything is working. The deployed version is much slower as it is deployed on a free hosting service.
## How to run tests

### Unit Testing

1. Make sure you are in the root directory of the project.
2. Unit tests can be executed by running ```gradle test``` or ```./gradlew test```.

### MVC Integration Testing
1. Make sure you are in the root directory of the project.
2. Make sure that your MySQL server is running.
3. MVC Integration tests can be executed by running ```gradle integrationTest```.

### UI Integration Testing
Unlike the two cases above, running UI tests takes a little bit more work.

1. Ensure you have a Google Chrome browser installed in your machine. Install one if you do not.
2. Check the version of your browser and note it down. You can check the version by clicking the three dots menu button on the top right corner, clicking Settings and then clicking on the "About Chrome" button on bottom left.
3. Visit https://chromedriver.chromium.org/downloads and download the ChromeDriver matching your Google Chrome version and your operating system.
4. Extract the zip folder and move the chromedriver executable to the root directory of the project (/prj).
5. Open the src/uiIntegrationTest/java/ui/ChromeDriverLocation.java file and make sure that the ```location``` field correctly reflects the name of your chromedriver. Rename it appropriately.
6. UI integration tests can now be executed by running ```gradle uiIntegrationTest``` from the root directory of the project.
