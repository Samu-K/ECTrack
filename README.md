# ECTrack 

## Functionality
ECTrack is a desktop app that allows you to see trends in electricity usage and pricing.

## Instructions for development

### Style
Developers should use checkstyle, with the [google_checks.xml](https://google.github.io/styleguide/javaguide.html) configuration, so that all code is uniform.

### Workflow
**Don't push to main**

Instead for each new feature always create an issue, then create a new branch and merge request relating to that issue.
Make you changes to the new branch. When done ask someone to look over your code and merge it.

For each new feature create an issue, a merge request and a new branch relating to that issue.
Develop on the new branch and merge to main.

*Note: If we have time, I'd prefer we do code review before merging*

### CI / CD
Still a work-in-progress.

Main pipeline is that each merge request gets checked with checkstyle, if that passes the code gets built and tested. 
If everything is green, the merge is accepted.
