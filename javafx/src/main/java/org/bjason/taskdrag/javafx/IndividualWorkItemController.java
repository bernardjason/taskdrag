package org.bjason.taskdrag.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bjason.taskdrag.model.FreeText;
import org.bjason.taskdrag.model.Work;

import java.io.IOException;
import java.net.URL;

import static org.bjason.taskdrag.javafx.AddFreeTextController.formatCreated;

public class IndividualWorkItemController implements UpdateData {
    public TextArea titleFieldId;
    public ListView freetextListViewId;
    public Label workTitleBar;
    public Polygon expandId;
    public VBox workVbox;
    public HBox workHbox;
    public HBox templateWorkPaneId;
    private SpringMainBean springMainBean;
    private MainSceneController.WorkTableData row;
    private long lastClicked = 0;
    private Stage stage;


    public void doDrag(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof Node) {
            Node c = (Node) mouseEvent.getSource();
            Parent p = null;
            if (c instanceof TextField || c instanceof ListView) {
                p = c.getParent();
            } else {
                p = (Parent) c;
            }

            c.startDragAndDrop(TransferMode.MOVE);
            if (c.getUserData() instanceof Work) {
                Work w = (Work) c.getUserData();
                Dragboard db = c.startDragAndDrop(TransferMode.MOVE);
                WritableImage image = p.snapshot(new SnapshotParameters(), null);
                db.setDragView(image);

                ClipboardContent content = new ClipboardContent();
                content.putString(DragDrop.workToString(w));
                db.setContent(content);

                mouseEvent.consume();
            }
        }
    }


    void initialiseWork(MainSceneController.WorkTableData row, SpringMainBean springMainBean) {
        workTitleBar.setText(row.getWork().getId() + " " + row.getWork().getStatus() + " " + row.getWork().getTitle());
        // to get reorder working
        workTitleBar.setUserData(row.getWork());
        workVbox.setUserData(row.getWork());
        workHbox.setUserData(row.getWork());
        templateWorkPaneId.setUserData(row.getWork());

        titleFieldId.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                String text = titleFieldId.getText();
                springMainBean.setWorkTitle(text, row);
                try {
                    springMainBean.updateGui();
                } catch (Exception e) {
                    Main.handleBackendError(e,stage);
                }

            }
        });
        titleFieldId.setWrapText(true);

        if(row.getFreeText().size() == 0 ) {
            Label doubleClick = new Label("Double click to add free text");
            doubleClick.setOnMouseClicked(event ->{
                doubleClickFreeText(event);
            });
            workVbox.getChildren().add(2,doubleClick);
        }

        this.springMainBean = springMainBean;
        this.row = row;
        setTitle(row, titleFieldId);
        setFreeText(row, freetextListViewId);
        String colour = MainSceneController.taskStates.get(row.getWork().getStatus()).getColour();
        assert(colour != null ) ;
        expandId.setFill(Paint.valueOf(colour ));
    }


    static void setFreeText(MainSceneController.WorkTableData row, ListView listView) {
        listView.getItems().clear();
        for (FreeText entry : row.getFreeText()) {
            Label label = new Label(formatCreated(entry) + " " + entry.getText());
            label.setWrapText(true);
            listView.getItems().add(label);
            listView.setUserData(row);
        }
        listView.setUserData(row.getWork());
    }

    static void setTitle(MainSceneController.WorkTableData row, TextArea textField) {
        textField.setText(row.getWork().getTitle());
        textField.setUserData(row.getWork());
    }

    private void setStageAndSpring(Stage stage, SpringMainBean springMainBean) {
        this.springMainBean = springMainBean;
    }

    public void expandIt(MouseEvent event) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = IndividualWorkItemController.class.getResource("/template.fxml");
        loader.setLocation(xmlUrl);

        try {
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(
                    ((Node) event.getSource()).getScene().getWindow());
            IndividualWorkItemController controller = loader.getController();
            controller.setStageAndSpring(stage, springMainBean);
            controller.initialiseWork(row, springMainBean);
            controller.setStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }
/*
    public void updateTextField(ActionEvent event) {
        TextField tf = (TextField) event.getSource();
        springMainBean.setWorkTitle(tf.getText(), row);
        try {
            springMainBean.updateGui();
        } catch (Exception e) {
            Main.handleBackendError(e,stage);
        }
    }

 */

    public void doubleClickFreeText(MouseEvent mouseEvent) {
        long now = System.currentTimeMillis();
        if (now - lastClicked < 500) {
            try {
                addFreeText(mouseEvent, row);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lastClicked = now;
    }

    public void addFreeText(MouseEvent event, MainSceneController.WorkTableData row) throws IOException {
        Stage freeTextStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/newfreetext.fxml");
        loader.setLocation(xmlUrl);

        freeTextStage.setScene(new Scene(loader.load()));
        freeTextStage.initModality(Modality.WINDOW_MODAL);
        freeTextStage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());
        AddFreeTextController addFreeTextController = loader.getController();
        addFreeTextController.setData(row);
        addFreeTextController.setStageAndSpring(freeTextStage, springMainBean);
        addFreeTextController.setCallBack(this);
        freeTextStage.show();
    }

    @Override
    public void stale() {
        if ( this.stage != null ) {
            this.stage.close();
        }
    }
}
