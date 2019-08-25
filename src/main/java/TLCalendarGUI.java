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
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TLCalendarGUI extends Application {


    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        GridPane grid = initGridPane();

        Button updateButton = new Button("Update");

        Label lastUpdatedLabel = new Label();
        grid.add(lastUpdatedLabel, 1, 2, 6, 1);

        updateButton.setOnAction(click -> updateCalendar(grid, lastUpdatedLabel));

        grid.add(updateButton, 0, 2);

        root.getChildren().add(grid);

        Scene scene = new Scene(root, 1800, 700);

        primaryStage.setTitle("Team Liquid Starcraft 2 Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateCalendar(grid, lastUpdatedLabel);

    }

    private GridPane initGridPane() {
        GridPane gridPane = new GridPane();

        RowConstraints rcFirstRow = new RowConstraints();                       //First row doesnt resize
        rcFirstRow.setVgrow(Priority.NEVER);
        gridPane.getRowConstraints().add(rcFirstRow);

        RowConstraints rcSecondRow = new RowConstraints();                       //Second does
        rcSecondRow.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().add(rcSecondRow);

        RowConstraints rcThirdRow = new RowConstraints();                       //Third row doesnt resize
        rcThirdRow.setVgrow(Priority.NEVER);
        gridPane.getRowConstraints().add(rcThirdRow);

        return gridPane;
    }


    private void updateCalendar(GridPane grid, Label lastUpdatedLabel) {
        try {
            populateCalendar(TLCalendarParserMain.getNewEvents(), grid);
            lastUpdatedLabel.setText("Last Updated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - hh:mm:ss a")));
        } catch (IOException e) {
            lastUpdatedLabel.setText("Error while trying to update!");
        }
    }

    private void populateCalendar(List<Event>[] events, GridPane grid) {
        DayOfWeek dow = LocalDateTime.now().getDayOfWeek();

        for (int i = 0; i < 7; i++) {
            Label header = new Label(dow.plus(i).toString() + "\n\n");                  //Add weekday header
            header.setFont(Font.font("Verdana", FontWeight.BOLD, 15));           //Make Weekday-Label bold
            GridPane.setHalignment(header, HPos.CENTER);                                    //Center Weekday-Label
            grid.add(header, i, 0);

            TextArea currentColumnText = new TextArea(concatEventsOfWeekday(events[i]));           //Add events
            currentColumnText.home();                                                                  //Scroll to top
            currentColumnText.setEditable(false);                                                      //Make uneditable
            grid.add(currentColumnText, i, 1);
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
