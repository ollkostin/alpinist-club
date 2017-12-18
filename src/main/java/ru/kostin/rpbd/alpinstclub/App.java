package ru.kostin.rpbd.alpinstclub;

import javafx.application.Preloader;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kostin.rpbd.alpinstclub.ui.ViewService;
import ru.kostin.rpbd.alpinstclub.ui.LoginComponent;

@SpringBootApplication
@SuppressWarnings("restriction")
public class App extends AbstractJavaFxApplicationSupport {
    @Value("${app.ui.title}")
    private String windowTitle;
    @Autowired
    private LoginComponent loginComponent;
    @Autowired
    private ViewService viewService;

    @Override
    public void start(Stage stage) throws Exception {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
        stage.setTitle(windowTitle);
        loginComponent.configure();
        stage.setScene(loginComponent.getScene());
        stage.setResizable(true);
        stage.centerOnScreen();
        viewService.setPrimaryStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(App.class, args);
    }

}