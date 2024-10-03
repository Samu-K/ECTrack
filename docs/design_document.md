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

![High-level diagram of MVC architecture](docs/images/proto_arch_diagram.png)
The View sends user inputs to the Controller. The Controller processes the inputs and communicates with the Model. The Model retrieves or updates the data and notifies the View when changes occur.

## Design Patterns

### Model-View-Controller (MVC)

This design pattern is key for separating concerns within the app. By decoupling the user interface from the business logic and data model, it allows us to implement changes independently in each area.

### Singleton Pattern (Tentative)

We are considering the Singleton pattern for handling API requests and ensuring that only one instance of the data-fetching service exists throughout the application.

### Observer Pattern (Tentative)

The View can observe changes in the Model using the Observer pattern. This would allow the GUI to react in real-time to changes in the data.

## APIs and Data Sources

We have not yet fully committed to any particular APIs, but we are planning to use the following APIs:

-   **Electricity Price API**: This API provides real-time electricity prices. We are testing the porssisahko.net to pull data for electricity pricing, but we are exploring other options as well to get data from more than one country or region.
-   **Energy Usage Data API**: To track electricity consumption, we are planning on using the data set from Electricity Maps. They also have an API, but it only provides real-time data, so we plan on using their free data set to visualize previous data. For prototyping, we are using a mock “data set”.

### Data Handling

Data will be fetched from external APIs in JSON format, parsed using libraries such as Gson or Jackson, and then stored in the Model. The app will allow the user to visualize historical data as well as view real-time trends.

## User Interface

### Wireframes

The initial wireframe focuses on simplicity:

-   **Dashboard**: Displays a graph showing electricity usage over time and pricing trends.
-   **Controls**: Users can filter data by time (e.g., day, week, month) and region.

#### Prototype Day View

![Wireframe](docs/images/proto_d_view.jpg)

## Future Plans and Considerations

-   **Real-time Data Updates**: Once the initial prototype is completed, we plan to implement real-time data updates.
-   **Authentication**: If we integrate personal smart meters, users will need to authenticate to fetch their data.
-   **Cross-Platform Compatibility**: JavaFX allows for compatibility across platforms, and we will explore packaging options for Windows, macOS, and Linux.
