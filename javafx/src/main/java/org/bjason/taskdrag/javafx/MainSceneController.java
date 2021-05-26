package org.bjason.taskdrag.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bjason.taskdrag.model.FreeText;
import org.bjason.taskdrag.model.TaskStates;
import org.bjason.taskdrag.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainSceneController implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(MainSceneController.class);

    public static String[] WORK_STATES = null;
    public static final String REMOVE = "remove";
    public ImageView binImage;
    public Button binButton;
    public ContextMenu binContextMenu;
    public HBox toplevelId;
    public GridPane mainGridPain;
    public Label loading;
    public VBox mainVbox;
    HashMap<String,DisplayColumn> panelIds = new HashMap<>();
    public static HashMap<String,TaskStates> taskStates = new HashMap<String,TaskStates>();

    private Main main;
    Stage stage;

    ArrayList<Work> updated = new ArrayList<>();

    SpringMainBean springMainBean;
    Parent root;

    @Override
    public void handle(Event event) {
        try {
            switch (event.getEventType().getName()) {
            case "DRAG_OVER": this.doDragOver((DragEvent) event);break;
            case "DRAG_DONE": this.doDragDone((DragEvent) event);break;
            case "DRAG_DETECTED": this.doDragDetected((MouseEvent) event);break;
            case "DRAG_DROPPED": this.doDragDropped((DragEvent) event); break;
            case "MOUSE_CLICKED" : this.minimizeColumn((MouseEvent) event); break;
            default: throw new RuntimeException("What is "+event.getEventType()) ;
            }
        } catch (Exception e) {
            log.error("Failed doing drag drop "+e.getMessage());
            log.debug("Failed doing drag drop "+e.getMessage(),e);
            Main.handleBackendError(e,stage);
        }
    }

    public void close() {
        this.stage.close();
    }


    class DisplayColumn {
        VBox panel;
        ColumnConstraints columnConstraints;
        ScrollPane scrollPane;
        HBox hboxLabel;
        Label title;

        public DisplayColumn(String status, String colour, MainSceneController mainSceneController) {
            scrollPane = new ScrollPane();
            scrollPane.setId(status);
            scrollPane.setOnDragDetected(mainSceneController);
            scrollPane.setOnDragDone(mainSceneController);
            scrollPane.setOnDragDropped(mainSceneController);
            scrollPane.setOnDragOver(mainSceneController);

            panel = new VBox();
            panel.setOnDragDetected(mainSceneController);
            panel.setOnDragDropped(mainSceneController);
            columnConstraints = new ColumnConstraints();
            scrollPane.setContent(panel);
            hboxLabel = new HBox();
            hboxLabel.setId(status);
            hboxLabel.setOnMouseClicked(mainSceneController);
            title = new Label(status);
            title.setUnderline(true);
            hboxLabel.setAlignment(Pos.CENTER);
            hboxLabel.getChildren().add(title);
            hboxLabel.setFillHeight(true);
            hboxLabel.setBackground(new Background(new BackgroundFill(Color.valueOf(colour), CornerRadii.EMPTY, Insets.EMPTY)));

        }
    }


    public void addColumnsNow(TaskStates[] taskStates) {

        mainVbox.getChildren().remove(loading);
        ArrayList<String> states = new ArrayList();
        for(TaskStates ts : taskStates) {
            MainSceneController.taskStates.put(ts.getStatus(),ts);
            states.add(ts.getStatus());
            log.debug("Add columns "+ts);
            DisplayColumn dc = new DisplayColumn(ts.getStatus(),ts.getColour(),this);
            panelIds.put(ts.getStatus(),dc);
            dc.scrollPane.setFitToHeight(true);
            dc.scrollPane.setFitToWidth(true);
            dc.columnConstraints.setPercentWidth(100/taskStates.length);
            dc.columnConstraints.setHgrow(Priority.ALWAYS);

            mainGridPain.getColumnConstraints().add(dc.columnConstraints);
            mainGridPain.add(dc.hboxLabel,(int) (ts.getDisplayOrder()-1),0);
            mainGridPain.add(dc.scrollPane,(int) (ts.getDisplayOrder()-1),1);
            GridPane.setHgrow(dc.scrollPane, Priority.ALWAYS);
            GridPane.setRowIndex(dc.scrollPane,1);

        }
        states.add(REMOVE);
        MainSceneController.WORK_STATES = states.toArray(new String[states.size()]);
    }

    @FXML
    public void initialize() {
    }

    public void setWorkStyle() {
        binButton.setGraphic(binImage);
        resetBinContentMenu();

    }


    private Parent createWorkCard(String id, WorkTableData row) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/workItem.fxml");
        loader.setLocation(xmlUrl);

        Parent root = loader.load();
        ((IndividualWorkItemController) loader.getController()).initialiseWork(row, springMainBean);
        root.setId(id);
        return root;
    }


    public void refreshButtonPressed(ActionEvent actionEvent)  {
        try {
            this.springMainBean.getBackendData();
            clearPanels();
            this.updateCreateInProgressDonePanes(this.springMainBean.tableData);
        } catch(Exception e) {
            Main.handleBackendError(e,stage);
        }
    }

    void clearPanels() {
        for(DisplayColumn dc : panelIds.values()) {
            dc.panel.getChildren().clear();
        }
    }

    public void setMain(Main main, Parent root,Stage stage) {
        this.main = main;
        this.root = root;
        this.stage = stage;
    }

    public void setSpringMainBean(SpringMainBean smb) {
        this.springMainBean = smb;
    }


    public void updateCreateInProgressDonePanes(ArrayList<WorkTableData> tableData) throws IOException {
        int i = 0;
        for (WorkTableData row : tableData) {
            log.debug("Row "+row.summaryString());
            Parent card = createWorkCard("id+" + i++, row);
            card.setUserData(row.workData);
            DisplayColumn dc = panelIds.get( row.workData.getStatus()) ;
            dc.panel.setSpacing(10);
            dc.panel.getChildren().add(card);
        }
        runResizeInFuture();
    }

    public void doDragDetected(MouseEvent mouseEvent) {
        DragDrop.doDragDetected(mouseEvent);
    }

    public void doDragDone(DragEvent dragEvent) {
        DragDrop.doDragDone(dragEvent);
    }

    public void doDragOver(DragEvent dragEvent) {
        DragDrop.doDragOver(dragEvent);
    }

    public void doDragDropped(DragEvent dragEvent) {
        try {
            Work moved = DragDrop.doDragDropped(dragEvent, springMainBean);
            handleBinDropForRollbackHandling(dragEvent, moved);
        } catch(Exception e  ) {
            Main.handleBackendError(e,stage);
        }
    }

    private void handleBinDropForRollbackHandling(DragEvent dragEvent, Work moved) {
        if (dragEvent.getTarget().equals(binButton)) {
            updated.add(moved);
            String title = moved.getTitle();
            if (title.length() >= 16) {
                title = title.substring(0, 15) + "..";
            }
            MenuItem menuItem = new MenuItem(moved.getId() + ":" + title);
            menuItem.setDisable(true);
            binContextMenu.getItems().add(menuItem);
        }
        refresh();
    }

    private void refresh() {
        try {
            this.springMainBean.getBackendData();
            clearPanels();
            this.updateCreateInProgressDonePanes(this.springMainBean.tableData);
        } catch (Exception e) {
            Main.handleBackendError(e,stage);
        }
    }


    public void openNewWorkDialog(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/newwork.fxml");
        loader.setLocation(xmlUrl);

        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());
        CreateNewWorkController cnwc = loader.getController();
        cnwc.setStageAndSpring(stage, springMainBean);
        stage.show();
    }


    public void dragOverBin(DragEvent dragEvent) {
        DragDrop.doDragOverBin(dragEvent);
    }

    public void undoBinRemove(ActionEvent actionEvent) {
        ArrayList<Work> reversed = new ArrayList<>(updated);
        Collections.reverse(reversed);
        try {
            for (Work w : reversed) {
                log.info("Roll back " + w);
                springMainBean.moveWork("" + w.getId(), w.getStatus());
            }
            updated.clear();
            resetBinContentMenu();
            refresh();
        } catch (Exception e) {
            Main.handleBackendError(e,stage);
        }
    }


    private void resetBinContentMenu() {
        binContextMenu.getItems().clear();
        MenuItem undo = new MenuItem("undo all");
        undo.setOnAction(event -> undoBinRemove(event));
        binContextMenu.getItems().add(undo);
    }

    public void minimizeColumn(MouseEvent mouseEvent) {
        String what = "";

        if (mouseEvent.getSource() instanceof HBox) {
            what = ((HBox) mouseEvent.getSource()).getId();
        }
        log.debug("Minimize " + mouseEvent.getSource()+"  what=["+what+"]");
        assert(what != null);

        flipColumn( panelIds.get( what).columnConstraints) ;
        ArrayList<ColumnConstraints> allColumns = new ArrayList();
        for(DisplayColumn dc : panelIds.values()){
            allColumns.add(dc.columnConstraints);
            log.debug("current set "+dc.columnConstraints.getPercentWidth() +"  for "+dc);
        }
        List<ColumnConstraints> expandedColumns = allColumns.stream().filter(c -> c.getPercentWidth() > 11).collect(Collectors.toList());
        expandedColumns.stream().forEach(c -> {
            c.setPercentWidth(100 / expandedColumns.size());
            log.debug("Set "+c.getPercentWidth() +"  for "+c);
        });

        runResizeInFuture();
    }


    void runResizeInFuture() {
        Runnable runnable =
                () -> {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                    Platform.runLater(() -> {
                        resizeFreeText();
                    });
                };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void resizeFreeText() {
        log.debug("start resizeFreeText");
        for (DisplayColumn dc : panelIds.values()) {
            VBox column = dc.panel;
            double width = column.getWidth();
            ArrayList<Node> listviews = new ArrayList<>();
            findAllChildren(listviews, column, ListView.class);
            log.debug(column + "  " + "WIDTH " + width);
            for (Node node : listviews) {
                ListView list = (ListView) node;
                for (Object l : list.getItems()) {
                    ((Label) l).setMaxWidth(width);
                }
            }
        }
    }

    private void findAllChildren(ArrayList<Node> result, Pane start, Class ofType) {
        for (Node n : start.getChildren()) {
            if (n.getClass() == ofType) {
                result.add(n);
            }
            if (n instanceof Pane) {
                findAllChildren(result, (Pane) n, ofType);
            }
        }
    }

    private void flipColumn(ColumnConstraints col) {
        log.debug("flipColumn " + col.getPercentWidth());
        if (col.getPercentWidth() > 11) {
            col.setPercentWidth(10);
        } else {
            col.setPercentWidth(33);
        }
    }


    public static class WorkTableData {
        private Work workData;
        private ArrayList<FreeText> freetextData = new ArrayList<>();

        public WorkTableData(Work work, List<FreeText> freetext) {
            this.workData = work;
            if (freetext != null && freetext.size() > 0) {
                freetextData.addAll(freetext);
            }
        }

        public List<FreeText> getFreeText() {
            return freetextData;
        }

        @Override
        public String toString() {
            return "WorkTableData{" +
                    "workData=" + workData +
                    ", freetextData=" + freetextData +
                    '}';
        }
        public String summaryString() {
            return "WorkTableData{" +
                    "workData=" + workData +
                    '}';
        }

        public Work getWork() {
            return workData;
        }
    }
}
