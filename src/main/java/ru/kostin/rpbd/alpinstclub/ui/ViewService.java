package ru.kostin.rpbd.alpinstclub.ui;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;

@Service
public class ViewService {
    public final static int WIDTH = 1024, HEIGHT = 600;
    private static Stage primaryStage;
    private Scene previousScene;
    @Autowired
    private PersonInfoComponent personInfoComponent;
    @Autowired
    private PersonComponent personComponent;
    @Autowired
    private MenuComponent menuComponent;
    @Autowired
    private MountainComponent mountainComponent;
    @Autowired
    private ClimbingComponent climbingComponent;
    @Autowired
    private LoginComponent loginComponent;
    @Autowired
    private RegistrationComponent registrationComponent;

    public void showMenu(Person person) {
        previousScene = primaryStage.getScene();
        menuComponent.setPerson(person);
        primaryStage.setScene(menuComponent.getScene());
    }

    public void showLogin(ActionEvent event) {
        loginComponent.configure();
        previousScene = primaryStage.getScene();
        primaryStage.setScene(loginComponent.getScene());
    }

    public void showPerson(Person person) {
        previousScene = primaryStage.getScene();
        personInfoComponent.configure(person);
        primaryStage.setScene(personInfoComponent.getScene());
    }

    public void showPeople(Person person) {
        previousScene = primaryStage.getScene();
        personComponent.configure(person);
        primaryStage.setScene(personComponent.getScene());
    }

    public void showMountains(Person person) {
        previousScene = primaryStage.getScene();
        mountainComponent.configure(person);
        primaryStage.setScene(mountainComponent.getScene());
    }

    public void showClimbings(Person person) {
        previousScene = primaryStage.getScene();
        climbingComponent.configure(person);
        primaryStage.setScene(climbingComponent.getScene());
    }

    public Alert showPaneDialog(String title, VBox pane) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(title);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.CLOSE);
        alert.getDialogPane().setContent(pane);
        alert.show();
        return alert;
    }

    public void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setResizable(true);
        alert.setTitle("");
        alert.setHeaderText("");
        Label label = new Label("Сообщение:");
        TextArea textArea = new TextArea(error);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public void showPreviousScene(ActionEvent event) {
        primaryStage.setScene(previousScene);
        previousScene = null;
    }

    public void showRegistration(ActionEvent event) {
        registrationComponent.configure();
        primaryStage.setScene(registrationComponent.getScene());
    }

    public void setPrimaryStage(Stage primaryStage) {
        ViewService.primaryStage = primaryStage;
    }


}
