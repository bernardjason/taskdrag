package org.bjason.taskdrag.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class Main extends Application implements MainAppInterface {


    boolean isLoggedIn = false;


    private ConfigurableApplicationContext springApplicationContext;

    SpringMainBean smb;
    MainSceneController msc;

    public static void handleBackendError(Exception e,Stage parentStage) {
        final Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);


        stage.initOwner(parentStage);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.pannableProperty().set(true);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.fitToHeightProperty().set(true);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.ALWAYS);

        TextFlow dialogVbox = new TextFlow();
        scrollPane.setContent(dialogVbox);


        Text l =new Text("Error "+e.getMessage()+" "+e.getCause().getMessage()+"\n\n");
        dialogVbox.getChildren().add(l);
        if ( e.getCause() != null && e.getCause().getMessage() != null ) {
            l =new Text("Cause "+e.getCause().getMessage()+"\n\n") ;
            dialogVbox.getChildren().add(l);
        }
        dialogVbox.getChildren().add(new Text("-----------------------------\n"));
        for(StackTraceElement s : e.getStackTrace() ) {
            Label t = new Label(s.toString());
            t.setWrapText(true);
            dialogVbox.getChildren().add(t);
        }
        Scene dialogScene = new Scene(scrollPane, 600, 400);
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(dialogScene);
        stage.show();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/main.fxml");
        loader.setLocation(xmlUrl);

        Parent root = loader.load();

        msc = loader.getController();
        msc.setMain(this,root,primaryStage);

        if ( isLoggedIn == false ) {
            LoginController.displayLogin(primaryStage,this);
        }

        Parameters parameters = getParameters();
        String[] args = new String[0];
        if ( parameters != null && parameters.getRaw() != null ) {
            args = parameters.getRaw().toArray(args);
        }
        setupSpring(primaryStage, msc,args);
    }

    void startTheMainScene(Stage primaryStage) throws Exception {

        isLoggedIn = true;
        primaryStage.setScene(new Scene(msc.root));
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.show();
        primaryStage.getScene().getStylesheets().add("fxstyle.css");

        InvalidationListener stageSizeListener = (observable) -> {
            msc.runResizeInFuture();
        };

        // without this the invalidation listen doesnt fire.
        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            }
        });

        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);

        msc.setWorkStyle();
        smb.guiReady();
    }



    private void setupSpring(Stage primaryStage, MainSceneController msc,String[] args) {
        CompletableFuture<ConfigurableApplicationContext> completableFuture
                = CompletableFuture.supplyAsync(() -> new SpringApplicationBuilder(TheManagerApplication.class).run(args));

        CompletableFuture<ConfigurableApplicationContext> future = completableFuture
                .thenApply( spring -> {
                    springApplicationContext = spring;
                    smb = spring.getBean(SpringMainBean.class);
                    smb.initialize(primaryStage, msc);
                    msc.setSpringMainBean(smb);
                    smb.setReady();
                    return spring ;
                    }
                );
    }

    @Override
    public void stop() {
        springApplicationContext.close();
        Platform.exit();
    }

    public void loggedIn(Stage primaryStage) throws Exception {
        primaryStage.close();
        startTheMainScene(primaryStage);

    }

    public void doLogin(String userName, String passWord) throws Exception {
        smb.login(userName,passWord);
    }

    public void close() {
        msc.close();
    }
}

