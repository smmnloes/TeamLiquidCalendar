
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class TLCalendarGUI {
    private ZonedDateTime startDate = ZonedDateTime.now();
    private Label lastUpdatedLabel;
    private final Component[] textAreas = new Component[7];
    private final Component[] weekdayDatePanels = new Component[7];

    public void start() {
        JFrame jFrame = new JFrame("Team Liquid Starcraft 2 Calendar");
        lastUpdatedLabel = new Label();

        Container contentPane = jFrame.getContentPane();
        contentPane.setBackground(Color.WHITE);

        GridBagLayout grid = new GridBagLayout();
        contentPane.setLayout(grid);

        createButtons(contentPane);
        updateCalendar(contentPane);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setMinimumSize(new Dimension(1800, 900));
        jFrame.setVisible(true);
    }

    private void createButtons(Container contentPaneContainer) {
        GridBagConstraints lastUpdatedConstraints = new GridBagConstraints();
        lastUpdatedConstraints.gridx = 5;
        lastUpdatedConstraints.gridy = 2;
        lastUpdatedConstraints.gridwidth = 2;
        contentPaneContainer.add(lastUpdatedLabel, lastUpdatedConstraints);

        Button refreshButton = new Button("Refresh");
        GridBagConstraints refreshButtonConstraints = new GridBagConstraints();
        refreshButtonConstraints.gridx = 0;
        refreshButtonConstraints.gridy = 2;

        refreshButton.addActionListener(__ -> updateCalendar(contentPaneContainer));
        contentPaneContainer.add(refreshButton, refreshButtonConstraints);


        Button lastWeekButton = new Button("prev. Week");
        lastWeekButton.addActionListener(__ -> {
            startDate = startDate.minus(1, ChronoUnit.WEEKS);
            updateCalendar(contentPaneContainer);
        });
        GridBagConstraints lastWeekButtonConstraints = new GridBagConstraints();
        lastWeekButtonConstraints.gridx = 1;
        lastWeekButtonConstraints.gridy = 2;
        contentPaneContainer.add(lastWeekButton, lastWeekButtonConstraints);


        Button nextWeekButton = new Button("next Week");
        nextWeekButton.addActionListener(__ -> {
            startDate = startDate.plus(1, ChronoUnit.WEEKS);
            updateCalendar(contentPaneContainer);
        });
        GridBagConstraints nextWeekButtonConstraints = new GridBagConstraints();
        nextWeekButtonConstraints.gridx = 2;
        nextWeekButtonConstraints.gridy = 2;
        contentPaneContainer.add(nextWeekButton, nextWeekButtonConstraints);
    }

    private void updateCalendar(Container contentPaneContainer) {
        try {
            List<Event>[] newEvents = TLCalendarParserMain.getNewEvents(startDate);
            populateGrid(newEvents, contentPaneContainer);
            repaintContainer(contentPaneContainer);
            lastUpdatedLabel.setText("Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - hh:mm:ss a")));
        } catch (IOException e) {
            lastUpdatedLabel.setText("Error while trying to update!");
            e.printStackTrace();
        }
    }

    private void repaintContainer(Container contentPaneContainer) {
        contentPaneContainer.revalidate();
        contentPaneContainer.repaint();
    }

    private void populateGrid(List<Event>[] events, Container contentPaneContainer) {
        for (int i = 0; i < 7; i++) {
            ZonedDateTime columnDate = startDate.plus(i, ChronoUnit.DAYS);
            updateWeekdayDatePanel(contentPaneContainer, i, columnDate);
            updateTextArea(contentPaneContainer, i, events[i]);
        }
    }

    private void updateTextArea(Container contentPaneContainer, int i, List<Event> event) {
        Component oldTextArea = textAreas[i];
        if (oldTextArea != null) {
            contentPaneContainer.remove(oldTextArea);
        }

        TextArea eventsTextArea = new TextArea(concatEventsOfWeekday(event));
        eventsTextArea.setBackground(Color.WHITE);
        eventsTextArea.setEditable(false);
        eventsTextArea.setPreferredSize(new Dimension(250, 700));
        GridBagConstraints eventsAreaConstraints = new GridBagConstraints();
        eventsAreaConstraints.gridx = i;
        eventsAreaConstraints.gridy = 1;

        textAreas[i] = eventsTextArea;
        contentPaneContainer.add(eventsTextArea, eventsAreaConstraints);
    }

    private void updateWeekdayDatePanel(Container contentPaneContainer, int i, ZonedDateTime columnDate) {
        JPanel weekdayDatePanel = createWeekDayDatePanel(columnDate);

        GridBagConstraints weekdayDatePanelConstraints = new GridBagConstraints();
        weekdayDatePanelConstraints.gridx = i;
        weekdayDatePanelConstraints.gridy = 0;

        Component oldWeekdayDatePanel = weekdayDatePanels[i];
        if (oldWeekdayDatePanel != null) {
            contentPaneContainer.remove(oldWeekdayDatePanel);
        }
        weekdayDatePanels[i] = weekdayDatePanel;
        contentPaneContainer.add(weekdayDatePanel, weekdayDatePanelConstraints);
    }

    private JPanel createWeekDayDatePanel(ZonedDateTime columnDate) {
        JPanel weekdayDatePanel = new JPanel();
        weekdayDatePanel.setBackground(Color.WHITE);
        weekdayDatePanel.setLayout(new BoxLayout(weekdayDatePanel, BoxLayout.PAGE_AXIS));

        Label weekdayLabel = new Label(columnDate.getDayOfWeek().toString() + "\n\n");
        Font weekDayFont = new Font(Font.SANS_SERIF, columnDate.getDayOfYear() == ZonedDateTime.now().getDayOfYear() ? Font.BOLD : Font.PLAIN, 15);
        weekdayLabel.setFont(weekDayFont);
        weekdayDatePanel.add(weekdayLabel);

        Label dateLabel = new Label(columnDate.format(DateTimeFormatter.ofPattern("dd.MM")));
        dateLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        weekdayDatePanel.add(dateLabel);
        return weekdayDatePanel;
    }

    private String concatEventsOfWeekday(List<Event> events) {
        StringBuilder sb = new StringBuilder();

        for (Event e : events) {
            sb.append(e).append("\n");
        }

        return sb.toString();
    }


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new TLCalendarGUI().start());
    }

}
