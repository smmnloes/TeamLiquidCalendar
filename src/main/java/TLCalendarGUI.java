
import org.jetbrains.annotations.Nullable;

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
        contentPaneContainer.add(lastUpdatedLabel, getGridbagConstraintsFor(5, 2, 2));

        Button refreshButton = new Button("Refresh");
        refreshButton.addActionListener(__ -> updateCalendar(contentPaneContainer));
        contentPaneContainer.add(refreshButton, getGridbagConstraintsFor(0, 2, null));


        Button lastWeekButton = new Button("prev. Week");
        lastWeekButton.addActionListener(__ -> {
            startDate = startDate.minus(1, ChronoUnit.WEEKS);
            updateCalendar(contentPaneContainer);
        });
        contentPaneContainer.add(lastWeekButton, getGridbagConstraintsFor(1, 2, null));


        Button nextWeekButton = new Button("next Week");
        nextWeekButton.addActionListener(__ -> {
            startDate = startDate.plus(1, ChronoUnit.WEEKS);
            updateCalendar(contentPaneContainer);
        });
        contentPaneContainer.add(nextWeekButton, getGridbagConstraintsFor(2, 2, null));
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

        textAreas[i] = eventsTextArea;
        contentPaneContainer.add(eventsTextArea, getGridbagConstraintsFor(i, 1, null));
    }

    private void updateWeekdayDatePanel(Container contentPaneContainer, int i, ZonedDateTime columnDate) {
        JPanel weekdayDatePanel = createWeekDayDatePanel(columnDate);

        Component oldWeekdayDatePanel = weekdayDatePanels[i];
        if (oldWeekdayDatePanel != null) {
            contentPaneContainer.remove(oldWeekdayDatePanel);
        }
        weekdayDatePanels[i] = weekdayDatePanel;
        contentPaneContainer.add(weekdayDatePanel, getGridbagConstraintsFor(i, 0, null));
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

    private static GridBagConstraints getGridbagConstraintsFor(int gridx, int gridy, @Nullable Integer gridwidth) {
        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        if (gridwidth != null) {
            gridBagConstraints.gridwidth = gridwidth;
        }
        return gridBagConstraints;
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
