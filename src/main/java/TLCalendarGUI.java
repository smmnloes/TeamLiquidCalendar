
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

    public void start() {
        JFrame jFrame = new JFrame("Team Liquid Starcraft 2 Calendar");
        lastUpdatedLabel = new Label();

        Container contentPane = jFrame.getContentPane();

        updateCalendar(contentPane);
        jFrame.pack();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setMinimumSize(new Dimension(1800, 900));
        jFrame.setVisible(true);
    }

    private void createScaffoldingComponents(Container contentPaneContainer) {
        GridBagConstraints lastUpdatedConstraints = new GridBagConstraints();
        lastUpdatedConstraints.gridx = 5;
        lastUpdatedConstraints.gridy = 3;
        lastUpdatedConstraints.gridwidth = 2;
        contentPaneContainer.add(lastUpdatedLabel, lastUpdatedConstraints);

        Button refreshButton = new Button("Refresh");
        GridBagConstraints refreshButtonConstraints = new GridBagConstraints();
        refreshButtonConstraints.gridx = 0;
        refreshButtonConstraints.gridy = 3;

        refreshButton.addActionListener(__ -> updateCalendar(contentPaneContainer));
        contentPaneContainer.add(refreshButton, refreshButtonConstraints);


        Button lastWeekButton = new Button("prev. Week");
        lastWeekButton.addActionListener(__ -> {
            startDate = startDate.minus(1, ChronoUnit.WEEKS);
            updateCalendar(contentPaneContainer);
        });
        GridBagConstraints lastWeekButtonConstraints = new GridBagConstraints();
        lastWeekButtonConstraints.gridx = 1;
        lastWeekButtonConstraints.gridy = 3;
        contentPaneContainer.add(lastWeekButton, lastWeekButtonConstraints);


        Button nextWeekButton = new Button("next Week");
        nextWeekButton.addActionListener(__ -> {
            startDate = startDate.plus(1, ChronoUnit.WEEKS);
            updateCalendar(contentPaneContainer);
        });
        GridBagConstraints nextWeekButtonConstraints = new GridBagConstraints();
        nextWeekButtonConstraints.gridx = 2;
        nextWeekButtonConstraints.gridy = 3;
        contentPaneContainer.add(nextWeekButton, nextWeekButtonConstraints);
    }

    private void resetContainer(Container contentPaneContainer) {
        contentPaneContainer.removeAll();
        GridBagLayout grid = new GridBagLayout();
        contentPaneContainer.setLayout(grid);
    }

    private void updateCalendar(Container contentPaneContainer) {
        try {
            List<Event>[] newEvents = TLCalendarParserMain.getNewEvents(startDate);
            resetContainer(contentPaneContainer);
            createScaffoldingComponents(contentPaneContainer);
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

            Label weekdayLabel = new Label(columnDate.getDayOfWeek().toString() + "\n\n");
            Font weekDayFont = new Font(Font.SANS_SERIF, columnDate.getDayOfYear() == ZonedDateTime.now().getDayOfYear() ? Font.BOLD : Font.PLAIN, 15);
            weekdayLabel.setFont(weekDayFont);
            GridBagConstraints weekdayLabelConstraints = new GridBagConstraints();
            weekdayLabelConstraints.gridx = i;
            weekdayLabelConstraints.gridy = 0;
            contentPaneContainer.add(weekdayLabel, weekdayLabelConstraints);

            Label dateLabel = new Label(columnDate.format(DateTimeFormatter.ofPattern("dd.MM")));
            dateLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            GridBagConstraints dateLabelConstraints = new GridBagConstraints();
            dateLabelConstraints.gridx = i;
            dateLabelConstraints.gridy = 1;

            contentPaneContainer.add(dateLabel, dateLabelConstraints);

            TextArea eventsTextArea = new TextArea(concatEventsOfWeekday(events[i]));
            eventsTextArea.setEditable(false);
            eventsTextArea.setPreferredSize(new Dimension(250, 700));
            GridBagConstraints eventsAreaConstraints = new GridBagConstraints();
            eventsAreaConstraints.gridx = i;
            eventsAreaConstraints.gridy = 2;

            contentPaneContainer.add(eventsTextArea, eventsAreaConstraints);
        }
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
