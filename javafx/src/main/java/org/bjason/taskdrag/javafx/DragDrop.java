package org.bjason.taskdrag.javafx;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.bjason.taskdrag.model.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DragDrop {

    private static final Logger log = LoggerFactory.getLogger(DragDrop.class);

    public static void doDragDetected(MouseEvent mouseEvent) {
        log.debug("doDrag STARTED " + mouseEvent.getSource());
        if (mouseEvent.getSource() instanceof Control) {
            Control c = (Control) mouseEvent.getSource();
            c.startDragAndDrop(TransferMode.MOVE);
            log.debug("Drag userData=[" + c.getUserData()+"]");
            mouseEvent.consume();
        }

    }


    public static void doDragDone(DragEvent dragEvent) {
        log.debug("dragDone STARTED " + dragEvent.getSource());
        log.debug("content "+dragEvent.getDragboard().getContent(DataFormat.PLAIN_TEXT));
    }

    public static Work doDragDropped(DragEvent dragEvent, SpringMainBean springMainBean) throws Exception {
        log.debug("doDragDropped "+dragEvent.getDragboard().getContent(DataFormat.PLAIN_TEXT));
        String content = (String) dragEvent.getDragboard().getContent(DataFormat.PLAIN_TEXT);
        String work_id = DragDrop.getWorkId(content);
        String work_status = DragDrop.getWorkStatus(content);
        String work_title = DragDrop.getWorkTitle(content);
        long work_displayOrder = DragDrop.getDisplayOrder(content);
        Work moved = null;
        EventTarget target = dragEvent.getTarget();
        log.debug("doDragDroped " + content+"  target "+target);
        if (target instanceof Node) {
            Node t = ((Node) target);
            Node found = travelUpParentsSearchForId(t, MainSceneController.WORK_STATES);
            Work ontoWork  = tavelUpParentsSearchForWork(t);
            if (found != null) {
                String id = found.getId();
                String newStatus = id.replace("PanelId", "");
                log.debug("Found dropped " + found+"  old status "+work_status+"  new "+newStatus);
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                dragEvent.consume();
                springMainBean.moveWork(work_id, newStatus);
                moved = new Work();
                moved.setId(Long.parseLong(work_id));
                if ( MainSceneController.REMOVE.equals(newStatus)) {
                    moved.setStatus(work_status);
                } else {
                    moved.setStatus(newStatus);
                }
                moved.setTitle(work_title);
                moved.setDisplayOrder(work_displayOrder);
                if (ontoWork != null ) {
                   if (ontoWork.getDisplayOrder() >= moved.getDisplayOrder() )  {
                       log.info("REORDER!!!!! " +ontoWork.getDisplayOrder() +" moved "+moved.getDisplayOrder());
                       springMainBean.setDisplayOrder(ontoWork.getDisplayOrder() +1, moved);
                   } else {
                       log.debug("BELOW REORDER!!!!! " );
                       springMainBean.setDisplayOrder(ontoWork.getDisplayOrder() -1, moved);
                   }
                }
            }
        } else {
            log.error("Over what?? " + dragEvent.getTarget().getClass());
        }

        return moved;
    }




    public static void doDragOver(DragEvent dragEvent) {
        String content = (String) dragEvent.getDragboard().getContent(DataFormat.PLAIN_TEXT);
        EventTarget target = dragEvent.getTarget();
        if (target instanceof Node) {
            Node t = ((Node) target);
            Node found = travelUpParentsSearchForId(t, MainSceneController.WORK_STATES);
            if (found != null) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
                dragEvent.consume();
            } else {
                log.debug("Over " + dragEvent.getTarget().getClass());
            }
        }
    }

    public static void doDragOverBin(DragEvent dragEvent) {
        dragEvent.getTarget();
        dragEvent.acceptTransferModes(TransferMode.MOVE);
        dragEvent.consume();
    }

    public static <T extends Node> Parent travelUpParentsSearchForId(T c, String[] startsWith) {
        Parent found = null;
        T current = c;
        while (current != null) {
            for (String s : startsWith) {
                String id = current.getId();
                if (id != null && id.startsWith(s)) {
                    found = (Parent) current;
                    break;
                }
            }
            current = (T) current.getParent();
        }
        return found;
    }
    public static Work tavelUpParentsSearchForWork(Node c) {
        Parent found = null;
        Node current = c;
        while (current != null) {
            if ( current.getUserData() != null && current.getUserData() instanceof  Work) {
               return (Work) current.getUserData();
            }
            current = current.getParent();
        }
        return null;
    }


    public static String workToString(Work w) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.appendField("work_id", w.getId());
        jsonObject.appendField("work_status", w.getStatus());
        jsonObject.appendField("work_displayorder", w.getDisplayOrder());
        jsonObject.appendField("work_title", w.getTitle());
        return jsonObject.toString();
    }

    public static String getWorkId(String w) {
        String id = null;
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(w);
            id = json.getAsString("work_id");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static String getWorkStatus(String w) {
        String status = null;
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(w);
            status = json.getAsString("work_status");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }

    public static String getWorkTitle(String w) {
        String status = null;
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(w);
            status = json.getAsString("work_title");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return status;
    }

    private static long getDisplayOrder(String content) {
        long order = 0;
        JSONParser parser = new JSONParser();
        try {
            JSONObject json = (JSONObject) parser.parse(content);
            order = Long.parseLong(json.getAsString("work_displayorder"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return order;
    }
}