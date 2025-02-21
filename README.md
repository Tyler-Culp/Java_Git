# Java Git Overview
This is a personal project of mine where I attempt to answer the question "What if git was written in a much slower programming language?" :). In actuality my goal for this project was just to learn more about the inner workings of git and practice my Java skills.

This is not a one-to-one clone of git however, as I made it a point to learn about the high level workings of git and try to replicate it using my own design (which is most certainly flawed). I also don't think the code I have written is perfect (I have certainly repeated myself in a few areas). Still it has been a good project allowing me to learn more of the inner workings of git, filesystems, and Java.

## Pitfalls to be aware of
I would **NOT** recommend using jit to track your projects. This is something I worked on for only 2 months and certainly has bugs.

Known issues:
1. Handling deleted files is still an open issue
    - Something about the way I chose to implement the index file and staging area is making this a non trivial problem (Non trivial in general I think as well)
    - Currently have plans for a specific function in Status command to detect when a previously tracked file has been deleted

## How to use
1. Download the [app.jar](gradle/app/build/libs/app.jar) file from the folder `gradle/app/build/libs/app.jar`.
2. Run the jar file with `java -jar /path/to/jarFile Main <option>`
    - To see different command options just run `java -jar /path/to/jarFile Main <option>`

## Future Work
1. Currently working on Checkout command to make the VCS complete. Should let users checkout a specific commit hash and rebuild their directory from it.
    - Future goals include adding a branching feature like git as well
2. Implement Log and Blame Commands to make it easier to review commit history

