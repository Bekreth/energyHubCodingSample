# Thermostat Delta CLI
## About
This is a simple a Scala application designed for reading in thermostat deltas and search for events.  I will do my best
to explain in detail all of my assumptions and decisions, so please excuse the verbosity.

## Core Assumptions
Given the open ended nature of this assignment, I made a handle of key assumptions to better focus what I should try
to accomplish in the brief time that I've worked on this.  They are as follows:

1. This is an application intended to be run in the cloud on an infinite stream of data.  While a CLI has been designed
for this and an implementation to pull data from a local file system, I've aimed to make the heart of this application
for to be a forever on data stream.  With just a few minor updates, it would easily transition to acting as an event 
filter as opposed to an event search.
    1. Having built this as a stream is the assumption of "warmed" data.  This application assumes that data flow has 
    been long running and there is an accurate representation of the world in memory.
    2. Having been built for the cloud, my main focus was on composite searches instead on field filtering. e.g, find 
    events that are above temperature _x_, before time _y_, and while the set point for the heater was _z_.

2. I assumed that this application received "clean data", i.e. no missing events, all data was accurate, everything was
in order. 
    1. There is very little in validation given that I am assuming the friendly case.
    2. It is assumed that each JSON is separated by a new line character and that there are no new lines within the body

## On TODOs
Throughout this application, I had several rather fun ideas for things I wanted/intended to add that ended up being
out of scope of the project. I've marked these items as `TODO` statements

## How To Operate
Compiling this application should be fairly simple, from the root directory run `./gradlew shadowJar`. Use `relay -h`
for the basic CLI instructions. The `relay` file is just a bash shell that starts the JVM with the appropriate JAR file.
On my machine, I am running openjdk version 1.8.0_242.  Newer versions of Java _should_ work, but I have not personally
verified them.

### Testing
To run the testing, a small preparation is required  `./gradlew testPrep`;  this function only needs to be called once
to build up the directory structure that the `LocalFileReaderTest` uses. After running this once, use `./gradlew test`
