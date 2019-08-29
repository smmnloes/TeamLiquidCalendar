import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class TLCalendarParserMain {

    /**
     * Method is called by TILCalendarGUI to retrieve new Data
     *
     * @return Array of LinkedLists with all events sorted by weekday
     */

    @SuppressWarnings("unchecked")
    static List<Event>[] getNewEvents(ZonedDateTime startDate) throws IOException {
        String params = "&year=" + startDate.getYear() + "&month=" + startDate.getMonth().getValue() + "&day=" + startDate.getDayOfMonth();
        String query = "http://www.teamliquid.net/calendar/?view=week&game=1" + params;


        HtmlPage page;
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            initWebClient(webClient);

            page = webClient.getPage(query);
            while (page.getElementsById("calendar").size() < 2) {
                webClient.waitForBackgroundJavaScript(500);
            }

        }
        DomNodeList<DomNode> weekDayColumns = page.querySelectorAll(".evc-l");

        if (weekDayColumns.size() == 0) {
            throw new IOException("Could not get events from page");
        }
        List<Event>[] eventListArray = new ArrayList[7];

        for (int i = 0; i < 7; i++) {
            eventListArray[i] = extractEventsFromSection(weekDayColumns.get(i));
        }
        return eventListArray;
    }

    private static void initWebClient(WebClient webClient) {
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);

        webClient.setWebConnection(new FalsifyingWebConnection(webClient) {
            @Override
            public WebResponse getResponse(WebRequest request) throws IOException {

                if (request.getUrl().toString().contains("google")) {
                    return createWebResponse(request, "", "application/javascript");
                }

                return super.getResponse(request);
            }
        });
    }


    /**
     * Extracts all events of a given weekday from an Element
     *
     * @param element Weekday as Element
     * @return Linked List of all Events of that day
     */
    private static List<Event> extractEventsFromSection(DomNode element) {
        List<Event> events = new ArrayList<>();

        DomNodeList<DomNode> allEventsOfDay = element.querySelectorAll(".ev-block");

        for (DomNode x : allEventsOfDay) {
            String eventName = x.querySelector(".ev-ctrl span").getTextContent();
            String eventTime = x.querySelector(".ev-timer").getTextContent().trim();
            String eventStage = x.querySelector(".ev-stage").getTextContent();
            events.add(new Event(eventName, eventTime, eventStage));
        }

        return events;
    }

}


