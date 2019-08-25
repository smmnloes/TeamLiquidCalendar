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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class TLCalendarGUI extends Application {

    private ZonedDateTime startDate = ZonedDateTime.now();

    private GridPane grid;
    private StackPane root;
    private Label lastUpdatedLabel;

    public void start(Stage primaryStage) {
        root = new StackPane();
        initGridPane();

        root.getChildren().add(grid);

        Scene scene = new Scene(root, 1800, 700);

        primaryStage.setTitle("Team Liquid Starcraft 2 Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCalendar();
    }

    private void initGridPane() {
        grid = new GridPane();

        RowConstraints rcFirstRow = new RowConstraints();                       //First row doesnt resize
        rcFirstRow.setVgrow(Priority.NEVER);
        grid.getRowConstraints().add(rcFirstRow);

        RowConstraints rcSecondRow = new RowConstraints();                       //Second does
        rcSecondRow.setVgrow(Priority.NEVER);
        grid.getRowConstraints().add(rcSecondRow);

        RowConstraints rcThirdRow = new RowConstraints();                       //Third row doesnt resize
        rcThirdRow.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().add(rcThirdRow);

        RowConstraints rcFourthRow = new RowConstraints();                       //Third row doesnt resize
        rcFourthRow.setVgrow(Priority.NEVER);
        grid.getRowConstraints().add(rcFourthRow);

        Button updateButton = new Button("Update");

        lastUpdatedLabel = new Label();
        this.grid.add(lastUpdatedLabel, 3, 3, 3, 1);

        updateButton.setOnAction(click -> updateCalendar());

        this.grid.add(updateButton, 2, 3);

        Button lastWeekButton = new Button("prev. Week");
        lastWeekButton.setOnAction(click -> {
            resetGrid();
            startDate = startDate.minus(1, ChronoUnit.WEEKS);
            updateCalendar();
        });
        this.grid.add(lastWeekButton, 0, 3);

        Button nextWeekButton = new Button("next Week");
        nextWeekButton.setOnAction(click -> {
            resetGrid();
            startDate = startDate.plus(1, ChronoUnit.WEEKS);
            updateCalendar();
        });
        this.grid.add(nextWeekButton, 1, 3);

    }

    private void resetGrid() {
        root.getChildren().remove(grid);
        initGridPane();
        root.getChildren().add(grid);
    }

    private void updateCalendar() {
        try {
            populateGrid(TLCalendarParserMain.getNewEvents(startDate));
            lastUpdatedLabel.setText("Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - hh:mm:ss a")));
        } catch (IOException e) {
            lastUpdatedLabel.setText("Error while trying to update!");
        }
    }

    private void populateGrid(List<Event>[] events) {
        DayOfWeek dow = startDate.getDayOfWeek();

        for (int i = 0; i < 7; i++) {
            Label header = new Label(dow.plus(i).toString() + "\n\n");                  //Add weekday header
            header.setFont(Font.font("Verdana", FontWeight.BOLD, 15));           //Make Weekday-Label bold
            GridPane.setHalignment(header, HPos.CENTER);                                    //Center Weekday-Label
            grid.add(header, i, 0);

            Label date = new Label(startDate.plus(i, ChronoUnit.DAYS).format(DateTimeFormatter.ofPattern("dd.MM")));
            header.setFont(Font.font("Verdana",15));
            GridPane.setHalignment(date, HPos.CENTER);
            grid.add(date, i, 1);

            TextArea currentColumnText = new TextArea(concatEventsOfWeekday(events[i]));           //Add events
            currentColumnText.home();                                                                  //Scroll to top
            currentColumnText.setEditable(false);                                                      //Make uneditable
            grid.add(currentColumnText, i, 2);
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
