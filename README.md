# Team Liquid Calendar 
A tool to scrape Information about competitive eSports events for the game 'Starcraft 2' from https://www.tl.net/calendar 
while avoiding spoilers (don't include individual matches).  

Uses Swing for GUI as well as HtmlUnit for getting the page content. HtmlUnit is necessary because Javascript needs to be executed on the page for the times of the events to be correct regarding the time zone.  

## Usage:
Download the latest release jar file from [here](https://github.com/smmnloes/TeamLiquidCalendar/raw/master/dist/TeamLiquidCalendar-all.jar) and run using  
```java -jar TeamLiquidCalendar-all.jar```  
in the directory you downloaded to.

## Requirements
Java runtime version 14 or greater.
