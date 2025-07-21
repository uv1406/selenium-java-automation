# SeleniumDemo

A sample Java automation framework using Selenium WebDriver, TestNG, and Log4j2.

## Features
- Selenium WebDriver for browser automation
- TestNG for test management and execution
- Log4j2 for logging
- Maven for build and dependency management
- Thread-safe WebDriver management
- Modular project structure (Page Object Model)

## Project Structure
```
src/
  main/java/com/automation/demo/         # Application code
  test/java/com/automation/demo/         # Test code
    data/                                # Test data providers
    pageobjects/                         # Page Object classes
    tests/                               # Test classes
    utils/                               # Utilities (DriverManager, ConfigReader, etc.)
  test/resources/                        # Test resources (config, log4j2, testng suites)
reports/                                 # Automation logs and reports
pom.xml                                  # Maven configuration
```

## Prerequisites
- Java 17 or higher
- Maven 3.6+

## How to Run Tests
1. Install dependencies:
   ```sh
   mvn clean install
   ```
2. Run all tests using the master TestNG suite:
   ```sh
   mvn test
   ```
   or specify a suite:
   ```sh
   mvn test -DsuiteXmlFile=src/test/resources/testng/suite_master.xml
   ```

## Configuration
- Update `src/test/resources/config.properties` for environment-specific settings.
- Logging is configured via `src/test/resources/log4j2.xml`.
- TestNG suite files are in `src/test/resources/testng/`.

## Reports
- Test reports are generated in the `target/surefire-reports/` directory after test execution.
- Additional logs can be found in the `reports/` directory.

## Dependencies
- Selenium Java `${selenium.version}`
- TestNG `${testng.version}`
- Log4j2 Core & API

See `pom.xml` for full dependency details.

## License
This project is for demo and educational purposes.
