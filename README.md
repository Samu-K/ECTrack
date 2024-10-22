# ECTrack 

## Functionality
ECTrack is a desktop app that allows you to see trends in electricity usage and pricing.

## How to run software
Software runs on java 22 and is built using maven. In order to run the software simply clone the repository and run mvn package javafx:run

## Instructions for development

### What is being used
We are using java version 22. CI / CD used openjdk, so it's recommended to use the same. \
Project management is done using Maven. \
Graphics are done with JavaFX

### Style
Developers should use checkstyle, with the [google_checks.xml](https://google.github.io/styleguide/javaguide.html) configuration, so that all code is uniform.

### Workflow
**Don't push to main**

Instead for each new feature always create an issue, then create a new branch and merge request relating to that issue.
Make you changes to the new branch. When done ask someone to look over your code and merge it.

*Note: If we have time, I'd prefer we do code review before merging*

### CI / CD
Still a work-in-progress.

Main pipeline is that each merge request gets checked with checkstyle, if that passes the code gets built and tested. 
If everything is green, the merge is accepted.

### Testing
If you know how to and have the time, it's good to write tests for each piece on functionality we make.
Regular unit tests are fine, but if you have experience in testing feel free to make more complicated tests.
