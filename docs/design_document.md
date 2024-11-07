# ECTrack Design Document

## Introduction

ECTrack is a desktop application that visualizes trends in electricity usage and pricing. The goal is to provide users with insights into their electricity consumption, allowing them to track usage patterns and view associated pricing trends. This document outlines the structural components, interfaces, design patterns, and APIs that will be used to build the application.

## System Architecture

### Overview

ECTrack is designed using the Model-View-Controller (MVC) architecture, which separates the data (Model), the presentation (View), and the control logic (Controller). This structure facilitates maintenance, scalability, and flexibility.

#### Key Components:

-   **Model**: Contains the logic for handling and processing electricity consumption data.
-   **View**: Implements the graphical user interface (GUI) using JavaFX to display data to the user.
-   **Controller**: Connects the Model and View, handling user input and updating the View based on data changes in the Model.

### High-Level Diagram

![High-level diagram of MVC architecture](images/proto_arch_diagram.png) \
The View sends user inputs to the Controller. The Controller processes the inputs and communicates with the Model. The Model retrieves or updates the data and notifies the View when changes occur.

## Design Patterns

### Model-View-Controller (MVC)

This design pattern is key for separating concerns within the app. By decoupling the user interface from the business logic and data model, it allows us to implement changes independently in each area.

### Singleton Pattern (Tentative)

We are considering the Singleton pattern for handling API requests and ensuring that only one instance of the data-fetching service exists throughout the application.

### Observer Pattern (Tentative)

The View can observe changes in the Model using the Observer pattern. This would allow the GUI to react in real-time to changes in the data.

## APIs and Data Sources

We switched from the intial API choice to the ENTSO-E Transparency Platform API, which provides extensive data on electricity prices and consumption across Europe. This makes it better suited for our purposes. The API alllows us to fetch pricing and usage data for multiple countries and supports various time intervals, enabling ECTrack to provide both realtime and historical insights.
* Pricing data: Day-ahead prices can be fetched hourly, allowing users to view electricity pricing trends in their region of interest.*
* Usage data: Electricity usage data, retrieved at intervals as frequent as every quarter hour, allows users to monitor consumption patterns over time.*
(*There are some kinks with the API requests to straighten out for now.)

### Data Handling

Data is fetched in XML format from the ENTSO-E API, parsed using Java's XML parsing libraries, and stored in the Model. The app allows the user to visulize historical data (e.g., hourly, daily, and weekly views) as well as current trends by region.

## User Interface

### Wireframes

The initial wireframe focuses on simplicity:

-   **Dashboard**: Displays two separate graphs for both electricity usage and electricity pricing.
-   **Controls**: Users can filter data by time (e.g., day, week, month) and region.

#### Prototype Day View

![Wireframe](images/updated_wireframe.png)

## Future Plans and Considerations

-   **Real-time Data Updates**: Once the initial prototype is completed, we plan to implement real-time data updates.
-   **Authentication**: If we integrate personal smart meters, users will need to authenticate to fetch their data.
-   **Cross-Platform Compatibility**: JavaFX allows for compatibility across platforms, and we will explore packaging options for Windows, macOS, and Linux.

---

## Midterm self-evaluation
So far the ECTrack project is approximately 60%-70% complete. We have made good progress in developing the core functionalities of the application, including the inegration of the ENTSO-E Transparency Platform API for retrieving both electricity pricing and usage data. We decided to switch to using this API since it allows us to access comprehensive data from multiple countries in Europe, enhancing the application's capabilities and relevance.

### Progress overview
* **API integration:** The transition to the ENTSO-E API has been a pivotal development in the project. Initially, we planned to use the porssisahko.net API, which limited us to Finland. The new API not only provides real-time pricing but also historical usage data, allowing us to implement various time views for the users. This change has greatly enriched our data handling and user experience.
* **Data handling and visualization:** We have established a method for fetching and parsing data from the API. While this process is functional, we are still learning about the most effective way to make calls to the API and retrieve the precise data we need. Due to the API's complexity and the range of available options, some aspects of data collection and filtering remain in progress as we refine our approach to align with the app's requirements.
* **Graphical user interface:** The GUI implementation is well underway and currently meets (at least very nearly) all minimum requirements, providing a clear and intuitive interfaec for users. The GUI displays pricing and usage trends and includes filter options that allow users to view data for different time periods and countries. The visual design aligns well with the project's goals and provides a good foundation for further enhancement.
* **Testing:** Unit testing has been started, focusing on core functionality and ensuring that data handling and API requests function as expected. Early tests indicate a stable foundation, and we plan to continue expanding test coverage as the application evolves.

### Challenges faced
Our main challenge in the beggining was finding a suitable API for our app. The current challenge we face is understanding the API documentation and ensuring that we correctly handle data formats (XML) and structure. We are exploring different methods to optimize data retrievaland improve the efficiency of our API requests.

### Use of AI tools
We have mainly utilized various AI tools to assist in documentation and debugging. The use of AI tools at this point has been minimal. However, we believe that these tools could help us streamline our work during the final phase of development.

### Future work
Moving forward, we aim to complete the remaining features, including enhancing the user interface and implementing real-time data updates. We also plan to continue refining our API call as we gain a better understanding of the ENTSO-E platform.

### Conclusion
At this midway point we have made good progress and have built a strong foundation for our application. There is still work to be done for sure, but we remain confident that we will be able to deliver a good product that meets the requirements.

---