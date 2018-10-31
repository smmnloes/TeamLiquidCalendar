package TLCalendarParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class TLCalendarParserMain {

    /**
     * Method is called by TILCalendarGUI to retrieve new Data
     *
     * @return Array of LinkedLists with all events sorted by weekday
     */

    @SuppressWarnings("unchecked")
    static List<Event>[] getUpdatedEvents() throws IOException {

        Document wholePage = Jsoup.connect("http://www.teamliquid.net/calendar/?game=1").get();

        Elements weekDayColumns = wholePage.getElementsByClass("evc-l");
        List<Event>[] eventListArray = new ArrayList[7];

        int i = 0;
        for (Element element : weekDayColumns) {
            eventListArray[i++] = extractEventsFromSection(element);
        }

        return eventListArray;
    }


    /**
     * Extracts all events of a given weekday from an Element
     *
     * @param element Weekday as Element
     * @return Linked List of all Events of that day
     */
    private static List<Event> extractEventsFromSection(Element element) {
        List<Event> events = new ArrayList<>();

        Elements allEventsOfDay = element.getElementsByClass("ev-block");

        for (Element x : allEventsOfDay) {
            String eventName = x.select("[^data-event-id]").text();
            String eventTime = adjustTimeZone(x.select(".ev-timer").text().trim());
            String eventStage = x.select(".ev-stage").text();
            events.add(new Event(eventName, eventTime, eventStage));
        }

        return events;
    }

    /**
     * Converts the time to the local time zone (teamliquid calendar gives times in GMT Zone)
     * <p>
     * Daylight savings may be inaccurate during transition days
     *
     * @param time - Time in String-form
     * @return Time adjusted by local Time-Zone
     */
    private static String adjustTimeZone(String time) {
        ZonedDateTime eventTime = ZonedDateTime.of(LocalDate.now(), LocalTime.parse(time), ZoneId.of("GMT"));
        return eventTime.withZoneSameInstant(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}


