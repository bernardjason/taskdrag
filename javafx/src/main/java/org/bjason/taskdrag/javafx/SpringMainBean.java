package org.bjason.taskdrag.javafx;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.bjason.taskdrag.common.CallBackend;
import org.bjason.taskdrag.model.FreeText;
import org.bjason.taskdrag.model.TaskStates;
import org.bjason.taskdrag.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class SpringMainBean {
    private static final Logger log = LoggerFactory.getLogger(SpringMainBean.class);

    private static final String REMOVE = "remove";

    @Autowired
    CallBackend callBackend;

    Stage stage;
    MainSceneController msc;

    ArrayList<MainSceneController.WorkTableData> tableData = new ArrayList<>();
    boolean isReady;

    public void initialize(Stage stage, MainSceneController msc) {
        this.stage = stage;
        this.msc = msc;
    }

    public void updateGui() throws Exception {
        getBackendData();
        Platform.runLater(() -> {
                    try {
                        msc.clearPanels();
                        msc.updateCreateInProgressDonePanes(tableData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }


    public void guiReady() throws Exception {
        getBackendData();
        TaskStates[] taskStates = callBackend.getTaskStats();

        if ( taskStates.length == 0 ) {
            throw new RuntimeException("Empty database, no task_states");
        }
        Platform.runLater(() -> {
                    try {
                        msc.addColumnsNow(taskStates);
                        msc.clearPanels();
                        msc.updateCreateInProgressDonePanes(tableData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public ArrayList<MainSceneController.WorkTableData> getBackendData() throws Exception {
        Work[] allWork = callBackend.getAllWork();
        HashMap<Long, ArrayList<FreeText>> freeTextMap = callBackend.getFreeTextMap();

        tableData.clear();
        for (Work work : allWork) {
            if (!REMOVE.equals(work.getStatus())) {

                ArrayList<FreeText> freeText = freeTextMap.get(work.getId());
                MainSceneController.WorkTableData wtd = new MainSceneController.WorkTableData(work, freeText);
                tableData.add(wtd);
            }
        }
        log.debug("getBackendData " + allWork.length);

        return tableData;
    }

    public void moveWork(String work_id, String work_status) throws Exception {
        callBackend.setStatus(work_id, work_status);
    }

    public void createNew(Work work) throws Exception {
        callBackend.createNew( work);
    }

    public void addNote(String text, MainSceneController.WorkTableData data) {
        FreeText freeText = new FreeText(text, data.getWork());
        callBackend.addNote(freeText);

    }

    public void setDisplayOrder(long displayOrder, Work work) {
        work.setDisplayOrder(displayOrder);
        callBackend.putWork(work);
    }

    public void setWorkTitle(String text, MainSceneController.WorkTableData row) {
        row.getWork().setTitle(text);
        callBackend.putWork(row.getWork());
    }

    public void login(String userName, String passWord) throws Exception {
        callBackend.login(userName,passWord);
    }

    public void setReady() {
        this.isReady=true;
    }
}
