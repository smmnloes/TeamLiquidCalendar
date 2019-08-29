import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

class TLCalendarParserMain {
    private static WebClient webClient;
    private static boolean firstLoad = true;
    private static String javaScript;

    /**
     * Method is called by TILCalendarGUI to retrieve new Data
     *
     * @return Array of LinkedLists with all events sorted by weekday
     */

    @SuppressWarnings("unchecked")
    static List<Event>[] getNewEvents(ZonedDateTime startDate) throws IOException, URISyntaxException {
        initSingletons();

        String params = "&year=" + startDate.getYear() + "&month=" + startDate.getMonth().getValue() + "&day=" + startDate.getDayOfMonth();
        String query = "http://www.teamliquid.net/calendar/?view=week&game=1" + params;

        HtmlPage page = webClient.getPage(query);

        // On the first load, the javascript for timezones gets executed correctly, from then on we have to execute it
        // manually
        if (firstLoad) {
            firstLoad = false;
        } else {
            page.executeJavaScript(javaScript);
        }

        // The timezones script duplicates the div with id "calendar". This is how we know it has completed.
        while (page.getElementsById("calendar").size() < 2) {
            webClient.waitForBackgroundJavaScript(500);
        }

        DomNodeList<DomNode> weekDayColumns = page.querySelectorAll(".evc-l");

        if (weekDayColumns.size() == 0) {
            throw new IOException("Could not get events from page!");
        }
        List<Event>[] eventListArray = new ArrayList[7];

        for (int i = 0; i < 7; i++) {
            eventListArray[i] = extractEventsFromSection(weekDayColumns.get(i));
        }

        return eventListArray;
    }

    private static void initSingletons() throws URISyntaxException, IOException {
        if (webClient == null) {
            initWebClient();
        }

        if (javaScript == null) {
            javaScript = new String(Files.readAllBytes(Paths.get(TLCalendarParserMain.class.getResource("timezones.js").toURI())));
        }
    }

    private static void initWebClient() {
        webClient = new WebClient(BrowserVersion.CHROME);
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


    private static String javascript = "function getTimezoneName() {\n" +
            "    tmSummer = new Date(Date.UTC(2005, 6, 30, 0, 0, 0, 0));\n" +
            "    so = -1 * tmSummer.getTimezoneOffset();\n" +
            "    tmWinter = new Date(Date.UTC(2005, 12, 30, 0, 0, 0, 0));\n" +
            "    wo = -1 * tmWinter.getTimezoneOffset();\n" +
            "\n" +
            "    if (-660 == so && -660 == wo) return 'Pacific/Midway';\n" +
            "    if (-600 == so && -600 == wo) return 'Pacific/Tahiti';\n" +
            "    if (-570 == so && -570 == wo) return 'Pacific/Marquesas';\n" +
            "    if (-540 == so && -600 == wo) return 'America/Adak';\n" +
            "    if (-540 == so && -540 == wo) return 'Pacific/Gambier';\n" +
            "    if (-480 == so && -540 == wo) return 'America/Juneau';\n" +
            "    if (-480 == so && -480 == wo) return 'Pacific/Pitcairn';\n" +
            "    if (-420 == so && -480 == wo) return 'America/Los_Angeles';\n" +
            "    if (-420 == so && -420 == wo) return 'America/Phoenix';\n" +
            "    if (-360 == so && -420 == wo) return 'America/Boise';\n" +
            "    if (-360 == so && -360 == wo) return 'America/Guatemala';\n" +
            "    if (-360 == so && -300 == wo) return 'Pacific/Easter';\n" +
            "    if (-300 == so && -360 == wo) return 'America/Chicago';\n" +
            "    if (-300 == so && -300 == wo) return 'America/Bogota';\n" +
            "    if (-240 == so && -300 == wo) return 'America/New_York';\n" +
            "    if (-240 == so && -240 == wo) return 'America/Caracas';\n" +
            "    if (-240 == so && -180 == wo) return 'America/Santiago';\n" +
            "    if (-180 == so && -240 == wo) return 'Canada/Atlantic';\n" +
            "    if (-180 == so && -180 == wo) return 'America/Montevideo';\n" +
            "    if (-180 == so && -120 == wo) return 'America/Sao_Paulo';\n" +
            "    if (-150 == so && -210 == wo) return 'America/St_Johns';\n" +
            "    if (-120 == so && -180 == wo) return 'America/Godthab';\n" +
            "    if (-120 == so && -120 == wo) return 'America/Noronha';\n" +
            "    if (-60 == so && -60 == wo) return 'Atlantic/Cape_Verde';\n" +
            "    if (0 == so && -60 == wo) return 'Atlantic/Azores';\n" +
            "    if (0 == so && 0 == wo) return 'Africa/Casablanca';\n" +
            "    if (60 == so && 0 == wo) return 'Europe/London';\n" +
            "    if (60 == so && 60 == wo) return 'Africa/Algiers';\n" +
            "    if (60 == so && 120 == wo) return 'Africa/Windhoek';\n" +
            "    if (120 == so && 60 == wo) return 'Europe/Amsterdam';\n" +
            "    if (120 == so && 120 == wo) return 'Africa/Harare';\n" +
            "    if (180 == so && 120 == wo) return 'Europe/Athens';\n" +
            "    if (180 == so && 180 == wo) return 'Africa/Nairobi';\n" +
            "    if (240 == so && 180 == wo) return 'Europe/Moscow';\n" +
            "    if (240 == so && 240 == wo) return 'Asia/Dubai';\n" +
            "    if (270 == so && 210 == wo) return 'Asia/Tehran';\n" +
            "    if (270 == so && 270 == wo) return 'Asia/Kabul';\n" +
            "    if (300 == so && 240 == wo) return 'Asia/Baku';\n" +
            "    if (300 == so && 300 == wo) return 'Asia/Karachi';\n" +
            "    if (330 == so && 330 == wo) return 'Asia/Calcutta';\n" +
            "    if (345 == so && 345 == wo) return 'Asia/Katmandu';\n" +
            "    if (360 == so && 300 == wo) return 'Asia/Yekaterinburg';\n" +
            "    if (360 == so && 360 == wo) return 'Asia/Colombo';\n" +
            "    if (390 == so && 390 == wo) return 'Asia/Rangoon';\n" +
            "    if (420 == so && 360 == wo) return 'Asia/Almaty';\n" +
            "    if (420 == so && 420 == wo) return 'Asia/Bangkok';\n" +
            "    if (480 == so && 420 == wo) return 'Asia/Krasnoyarsk';\n" +
            "    if (480 == so && 480 == wo) return 'Australia/Perth';\n" +
            "    if (540 == so && 480 == wo) return 'Asia/Irkutsk';\n" +
            "    if (540 == so && 540 == wo) return 'Asia/Tokyo';\n" +
            "    if (570 == so && 570 == wo) return 'Australia/Darwin';\n" +
            "    if (570 == so && 630 == wo) return 'Australia/Adelaide';\n" +
            "    if (600 == so && 540 == wo) return 'Asia/Yakutsk';\n" +
            "    if (600 == so && 600 == wo) return 'Australia/Brisbane';\n" +
            "    if (600 == so && 660 == wo) return 'Australia/Sydney';\n" +
            "    if (630 == so && 660 == wo) return 'Australia/Lord_Howe';\n" +
            "    if (660 == so && 600 == wo) return 'Asia/Vladivostok';\n" +
            "    if (660 == so && 660 == wo) return 'Pacific/Guadalcanal';\n" +
            "    if (690 == so && 690 == wo) return 'Pacific/Norfolk';\n" +
            "    if (720 == so && 660 == wo) return 'Asia/Magadan';\n" +
            "    if (720 == so && 720 == wo) return 'Pacific/Fiji';\n" +
            "    if (720 == so && 780 == wo) return 'Pacific/Auckland';\n" +
            "    if (765 == so && 825 == wo) return 'Pacific/Chatham';\n" +
            "    if (780 == so && 780 == wo) return 'Pacific/Enderbury';\n" +
            "    if (840 == so && 840 == wo) return 'Pacific/Kiritimati';\n" +
            "    return undefined;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "var name = getTimezoneName();\n" +
            "if (name != undefined) {\n" +
            "    var token = $('meta[name=csrf-token]').attr('content');\n" +
            "    $.post('/mytlnet/set_time_zone.php5', {token: token, timezone: name, page: window.location.href}, function (data) {\n" +
            "        if (data.live_html) {\n" +
            "            $('#live_events_block').html(data.live_html);\n" +
            "            $('#upcoming_events_block').html(data.upcoming_html);\n" +
            "            setupEventCalendarBindings();\n" +
            "        }\n" +
            "        if (data.calendar_page) {\n" +
            "            refreshCalendarDiv();\n" +
            "        }\n" +
            "    });\n" +
            "}\n" +
            "\n";

}


