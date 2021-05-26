package org.bjason.taskdrag.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bjason.taskdrag.model.Work;

public class CreateNewWorkController {
    private Stage stage;
    private SpringMainBean springMainBean;

    @FXML
    TextField titleFieldId;

    public void okPressed(ActionEvent actionEvent) {
        Work work = new Work(0,"created",titleFieldId.getText());
        try {
            springMainBean.createNew(work);
            springMainBean.updateGui();
            stage.close();
        } catch(Exception e) {
            Main.handleBackendError(e,stage);
        }
    }

    public void cancelPressed(ActionEvent actionEvent) {
        stage.close();
    }


    public void setStageAndSpring(Stage stage, SpringMainBean springMainBean) {
        this.stage = stage;
        this.springMainBean = springMainBean;
        this.titleFieldId.requestFocus();
    }
}
