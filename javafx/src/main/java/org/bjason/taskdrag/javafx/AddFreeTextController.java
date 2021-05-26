package org.bjason.taskdrag.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bjason.taskdrag.model.FreeText;

import java.text.SimpleDateFormat;

public class AddFreeTextController {
    public static final String DISPLAY_FREE_TEXT_DATETIME = "yyyy-MM-dd";
    private Stage stage;
    private SpringMainBean springMainBean;

    @FXML
    TextField freetextFieldId;

    @FXML
    ListView<String> currentFreeTextId;
    private MainSceneController.WorkTableData data;
    private UpdateData callBackOnUpdate;


    public void setStageAndSpring(Stage stage, SpringMainBean springMainBean) {
        this.stage = stage;
        this.springMainBean = springMainBean;
        this.freetextFieldId.requestFocus();
    }

    public void okPressed(ActionEvent actionEvent) {
        try {
            if (freetextFieldId.getText().length() > 0) {
                springMainBean.addNote(freetextFieldId.getText(), data);
                springMainBean.updateGui();
                this.callBackOnUpdate.stale();
            }
            stage.close();
        } catch (Exception e) {
            Main.handleBackendError(e,stage);
        }
    }

    public void cancelPressed(ActionEvent actionEvent) {
        stage.close();
    }

    public void setData(MainSceneController.WorkTableData row) {
        this.data = row;
        for (FreeText r : data.getFreeText()) {
            this.currentFreeTextId.getItems().add(formatCreated(r) + " " + r.getText());
        }
    }

    public void setCallBack(UpdateData update) {
        this.callBackOnUpdate = update;
    }
    public static String formatCreated(FreeText ft) {
        SimpleDateFormat createdDateFormat = new SimpleDateFormat(DISPLAY_FREE_TEXT_DATETIME);
        return  createdDateFormat.format(ft.getCreated());
    }
}
