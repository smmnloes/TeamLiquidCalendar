import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class TLCalendarGUI extends Application {
    private ZonedDateTime startDate = ZonedDateTime.now();
    private Label lastUpdatedLabel;

    public void start(Stage primaryStage) {
        lastUpdatedLabel = new Label();

        StackPane root = new StackPane();
        GridPane grid = initGridPane(root);

        root.getChildren().add(grid);

        Scene scene = new Scene(root, 1800, 700);

        primaryStage.setTitle("Team Liquid Starcraft 2 Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
        updateCalendar(grid, root);
    }

    private GridPane initGridPane(StackPane root) {
        GridPane grid = new GridPane();

        RowConstraints rcFirstRow = new RowConstraints();
        rcFirstRow.setVgrow(Priority.NEVER);
        grid.getRowConstraints().add(rcFirstRow);

        RowConstraints rcSecondRow = new RowConstraints();
        rcSecondRow.setVgrow(Priority.NEVER);
        grid.getRowConstraints().add(rcSecondRow);

        RowConstraints rcThirdRow = new RowConstraints();
        rcThirdRow.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().add(rcThirdRow);

        RowConstraints rcFourthRow = new RowConstraints();
        rcFourthRow.setVgrow(Priority.NEVER);
        grid.getRowConstraints().add(rcFourthRow);

        Button refreshButton = new Button("Refresh");

        grid.add(lastUpdatedLabel, 3, 3, 3, 1);

        refreshButton.setOnAction(click -> updateCalendar(grid, root));

        grid.add(refreshButton, 2, 3);

        Button lastWeekButton = new Button("prev. Week");
        lastWeekButton.setOnAction(click -> {
            startDate = startDate.minus(1, ChronoUnit.WEEKS);
            updateCalendar(grid, root);
        });
        grid.add(lastWeekButton, 0, 3);

        Button nextWeekButton = new Button("next Week");
        nextWeekButton.setOnAction(click -> {
            startDate = startDate.plus(1, ChronoUnit.WEEKS);
            updateCalendar(grid, root);
        });
        grid.add(nextWeekButton, 1, 3);
        return grid;
    }

    private GridPane resetGrid(StackPane root, GridPane oldGrid) {
        root.getChildren().remove(oldGrid);
        GridPane newGrid = initGridPane(root);
        root.getChildren().add(newGrid);
        return newGrid;
    }

    private void updateCalendar(GridPane grid, StackPane root) {
        GridPane newGrid = resetGrid(root, grid);
        try {
            populateGrid(TLCalendarParserMain.getNewEvents(startDate), newGrid);
            lastUpdatedLabel.setText("Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - hh:mm:ss a")));
        } catch (IOException | URISyntaxException e) {
            lastUpdatedLabel.setText("Error while trying to update!");
            e.printStackTrace();
        }
    }

    private void populateGrid(List<Event>[] events, GridPane grid) {
        for (int i = 0; i < 7; i++) {
            ZonedDateTime columnDate = startDate.plus(i, ChronoUnit.DAYS);

            Label weekdayLabel = new Label(columnDate.getDayOfWeek().toString() + "\n\n");
            FontWeight fontWeight = columnDate.getDayOfYear() == ZonedDateTime.now().getDayOfYear() ? FontWeight.BOLD : FontWeight.NORMAL;
            weekdayLabel.setFont(Font.font("Verdana", fontWeight, 15));
            GridPane.setHalignment(weekdayLabel, HPos.CENTER);
            grid.add(weekdayLabel, i, 0);

            Label dateLabel = new Label(columnDate.format(DateTimeFormatter.ofPattern("dd.MM")));
            dateLabel.setFont(Font.font("Verdana", fontWeight, 15));
            GridPane.setHalignment(dateLabel, HPos.CENTER);
            grid.add(dateLabel, i, 1);

            TextArea eventsTextArea = new TextArea(concatEventsOfWeekday(events[i]));
            eventsTextArea.home();
            eventsTextArea.setEditable(false);

            grid.add(eventsTextArea, i, 2);
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
        launch(args);
    }

}
