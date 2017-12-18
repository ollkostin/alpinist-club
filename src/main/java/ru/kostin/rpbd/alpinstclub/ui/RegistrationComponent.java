package ru.kostin.rpbd.alpinstclub.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.service.LoginService;

@Component
public class RegistrationComponent {
    @Autowired
    private ViewService viewService;
    @Autowired
    private LoginService loginService;
    private Scene scene;

    public void configure() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        scene = new Scene(grid, ViewService.WIDTH, ViewService.HEIGHT);
        Text title = new Text("Добро пожаловать");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        Label usernameLabel = new Label("Имя пользователя:");
        TextField usernameTextField = new TextField();
        Label nameLabel = new Label("Полное имя:");
        TextField nameTextField = new TextField();
        Label passwordLabel = new Label("Пароль:");
        Label confirmPasswordLabel = new Label("Подтвердите пароль:");
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        Button registerButton = new Button("Зарегистрироваться");
        Label loginLabel = new Label("Уже есть аккаунт?");
        Button loginButton = new Button("Войти");
        grid.add(title, 0, 0, 2, 1);
        grid.add(nameLabel, 0, 1);
        grid.add(nameTextField, 1, 1);
        grid.add(usernameLabel, 0, 2);
        grid.add(usernameTextField, 1, 2);
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(confirmPasswordLabel, 0, 4);
        grid.add(confirmPasswordField, 1, 4);
        grid.add(registerButton, 1, 5);
        grid.add(loginLabel, 0, 6);
        grid.add(loginButton, 1, 6);
        loginButton.setOnAction(viewService::showLogin);
        registerButton.setOnAction(event -> {
            try {
                String name = nameTextField.getCharacters().toString();
                String user = usernameTextField.getCharacters().toString();
                String password = passwordField.getCharacters().toString();
                String confirmPassword = confirmPasswordField.getCharacters().toString();
                if (user.isEmpty() || password.isEmpty() || name.isEmpty() || confirmPassword.isEmpty() || user.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                    throw new IllegalArgumentException("Поле не может быть пустым");
                }
                if (user.length() > 255 || name.length() > 255
                        || password.length() > 255 || confirmPassword.length() > 255) {
                    throw new IllegalArgumentException("Значение в текстовом поле не может быть длиннее 255 символов");
                }
                if (user.length() < 5) {
                    throw new IllegalArgumentException("Имя пользователя должно быть длиннее 5 символов");
                }
                if (!password.trim().equals(confirmPassword.trim())) {
                    throw new IllegalArgumentException("Пароли не совпадают");
                }
                Person person = loginService.register(user, name, password);
                viewService.showMenu(person);
            } catch (Exception ex) {
                viewService.showError(ex.getMessage());
            }
        });
    }

    public Scene getScene() {
        return scene;
    }
}
