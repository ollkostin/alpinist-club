package ru.kostin.rpbd.alpinstclub.ui;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;

@Component
public class MenuComponent {
    private ViewService viewService;
    private Person person;
    private Scene scene;

    @Autowired
    public MenuComponent(ViewService viewService) {
        this.viewService = viewService;
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        vBox.setPrefWidth(ViewService.WIDTH);
        vBox.setPrefHeight(ViewService.HEIGHT);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        scene = new Scene(vBox);
        Text title = new Text("Меню");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        Button logoutButton = new Button("Выйти");
        Button personInfoButton = new Button("Информация о пользователе");
        Button mountainsInfoButton = new Button("Вершины");
        Button personListButton = new Button("Пользователи");
        Button climbingsButton = new Button("Восхождения");
        logoutButton.setOnAction(this::showLoginScene);
        personInfoButton.setOnAction(this::showPersonInfo);
        personListButton.setOnAction(this::showPersonList);
        mountainsInfoButton.setOnAction(this::showMountainList);
        climbingsButton.setOnAction(this::showClimbings);
        vBox.getChildren().addAll(logoutButton, personInfoButton, mountainsInfoButton, personListButton, climbingsButton);
    }

    private void showLoginScene(ActionEvent event) {
        person = null;
        viewService.showLogin(event);
    }

    private void showPersonList(ActionEvent event) {
        viewService.showPeople(person);
    }

    private void showMountainList(ActionEvent event) {
        viewService.showMountains(person);
    }

    private void showPersonInfo(ActionEvent event) {
        viewService.showPerson(person);
    }

    private void showClimbings(ActionEvent event) {
        viewService.showClimbings(person);
    }

    public Scene getScene() {
        return scene;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
