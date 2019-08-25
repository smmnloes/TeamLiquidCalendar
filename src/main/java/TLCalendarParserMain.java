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
    static List<Event>[] getNewEvents() throws IOException {

        Document wholePage = Jsoup.connect("http://www.teamliquid.net/calendar/?game=1").get();

        Elements weekDayColumns = wholePage.getElementsByClass("evc-l");
        List<Event>[] eventListArray = new ArrayList[7];

        for (int i = 0; i < 7; i ++) {
            eventListArray[i] = extractEventsFromSection(weekDayColumns.get(i), i);
        }

        return eventListArray;
    }


    /**
     * Extracts all events of a given weekday from an Element
     *
     * @param element Weekday as Element
     * @return Linked List of all Events of that day
     */
    private static List<Event> extractEventsFromSection(Element element, int daysFromNow) {
        List<Event> events = new ArrayList<>();

        Elements allEventsOfDay = element.getElementsByClass("ev-block");

        for (Element x : allEventsOfDay) {
            String eventName = x.select("[^data-event-id]").text();
            String eventTime = adjustTimeZone(x.select(".ev-timer").text().trim(), daysFromNow);
            String eventStage = x.select(".ev-stage").text();
            events.add(new Event(eventName, eventTime, eventStage));
        }

        return events;
    }

    /**
     * Converts the time to the local time zone (teamliquid calendar gives times in GMT Zone)
     * 
     * @param time - Time in String-form
     * @return Time adjusted by local Time-Zone
     */
    private static String adjustTimeZone(String time, int daysFromNow) {
        ZonedDateTime originalEventTime = ZonedDateTime.of(LocalDate.now().plusDays(daysFromNow), LocalTime.parse(time), ZoneId.of("GMT"));
        ZonedDateTime localEventTime = originalEventTime.withZoneSameInstant(ZoneId.systemDefault());
        return localEventTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}


