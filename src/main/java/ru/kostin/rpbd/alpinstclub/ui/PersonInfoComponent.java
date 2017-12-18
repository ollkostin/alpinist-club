package ru.kostin.rpbd.alpinstclub.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kostin.rpbd.alpinstclub.exception.UsernameAlreadyExistsException;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.service.PersonService;
import ru.kostin.rpbd.alpinstclub.service.dto.PersonDTO;
import ru.kostin.rpbd.alpinstclub.util.Util;


@Component
public class PersonInfoComponent {
    private PersonService personService;
    private ViewService viewService;
    private Scene scene;
    private Person person;

    public void configure(Person person) {
        this.person = person;
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 0, 0));
        scene = new Scene(grid, ViewService.WIDTH, ViewService.HEIGHT);
        Button goToPreviousSceneButton = new Button("Назад");
        goToPreviousSceneButton.setOnAction(viewService::showPreviousScene);
        grid.add(goToPreviousSceneButton, 0, 0);
        Text title = new Text("Пользователь");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(title, 1, 0, 2, 1);
        Label usernameLabel = new Label("Имя пользователя:");
        grid.add(usernameLabel, 0, 1);
        TextField usernameTextField = new TextField();
        grid.add(usernameTextField, 1, 1);
        Label nameLabel = new Label("Полное имя:");
        grid.add(nameLabel, 0, 2);
        TextField nameTextField = new TextField();
        grid.add(nameTextField, 1, 2);
        Label levelLabel = new Label("Уровень");
        grid.add(levelLabel, 0, 3);
        Label levelValue = new Label();
        grid.add(levelValue, 1, 3);
        Button saveChangesButton = new Button("Сохранить");
        saveChangesButton.setOnAction(event -> {
            String name = nameTextField.getText();
            String username = usernameTextField.getText();
            if (name.length() > 255 || name.length() < 2
                    || username.length() < 5 || username.length() > 255) {
                viewService.showError("Количество символов в поле имени пользователя от 5 до 255,\n в поле с полным именем - от 1 до 255");
            } else {
                try {
                    PersonDTO p = new PersonDTO();
                    p.setId(person.getId());
                    p.setFullName(name);
                    p.setUsername(username);
                    p.setLevel(person.getLevel().name());
                    Person person1 = personService.edit(p);
                    person.setUsername(p.getUsername());
                    person.setFullName(p.getFullName());
                    viewService.showError("Сохранено");
                } catch (UsernameAlreadyExistsException e) {
                    viewService.showError(e.getMessage());
                } finally {
                    usernameTextField.setText(person.getUsername());
                    nameTextField.setText(person.getFullName());
                }
            }
        });
        grid.add(saveChangesButton, 2, 4);
        nameTextField.setText(person.getFullName());
        usernameTextField.setText(person.getUsername());
        levelValue.setText(Util.getLevel(person.getLevel().name()));
    }

    public Scene getScene() {
        return scene;
    }

    @Autowired
    public PersonInfoComponent(ViewService viewService, PersonService personService) {
        this.personService = personService;
        this.viewService = viewService;
    }

}
