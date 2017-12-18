package ru.kostin.rpbd.alpinstclub.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;
import ru.kostin.rpbd.alpinstclub.service.PersonService;
import ru.kostin.rpbd.alpinstclub.util.Search;
import ru.kostin.rpbd.alpinstclub.service.dto.PersonDTO;
import ru.kostin.rpbd.alpinstclub.util.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static ru.kostin.rpbd.alpinstclub.util.Constant.*;
import static ru.kostin.rpbd.alpinstclub.util.Util.getLevel;
import static ru.kostin.rpbd.alpinstclub.util.Util.getPersonLevelName;

@Component
public class PersonComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonComponent.class);
    private PersonService personService;
    private ViewService viewService;
    private Scene scene;
    private Alert alert;
    private TableView<PersonDTO> table;
    private Person person;
    private HashMap<String, Search> searchMap = new HashMap<>();
    private HashMap<String, PersonLevel> personLevelsMap = new HashMap<>();
    private String query = null, attr = null;
    private List<PersonLevel> levels = new ArrayList<>();

    {
        searchMap.put("по имени пользователя", Search.USERNAME);
        searchMap.put("по ФИО", Search.NAME);
        personLevelsMap.put(Constant.NEWBIE, PersonLevel.NEWBIE);
        personLevelsMap.put(Constant.SKILLED, PersonLevel.SKILLED);
        personLevelsMap.put(Constant.LEAD, PersonLevel.LEAD);
        initLevelsAndQuery();
    }

    public void configure(Person person) {
        this.person = person;
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        AnchorPane anchorPane = new AnchorPane();
        vBox.setPrefWidth(ViewService.WIDTH);
        vBox.setPrefHeight(ViewService.HEIGHT);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        hBox.setAlignment(Pos.CENTER);
        scene = new Scene(vBox);
        Button goToPreviousSceneButton = new Button(BACK);
        goToPreviousSceneButton.setOnAction(viewService::showPreviousScene);
        ButtonBar bar = new ButtonBar();
        bar.getButtons().addAll(goToPreviousSceneButton);
        buildTable();
        ObservableList<PersonDTO> people = FXCollections.observableArrayList(personService.filteredFetch(levels, query, attr));
        table.setItems(people);
        anchorPane.getChildren().add(table);
        hBox.getChildren().addAll(bar);
        HBox searchBox = buildSearchAndSortBox();
        HBox filterBox = buildFilterBox();
        vBox.getChildren().addAll(searchBox, filterBox, anchorPane, hBox);
        if (person.getLevel().equals(PersonLevel.LEAD)) {
            Button deleteButton = new Button(DELETE);
            Button changeLevelButton = new Button(CHANGE + " " + LEVEL.toLowerCase());
            bar.getButtons().addAll(changeLevelButton, deleteButton);
            deleteButton.setOnAction(e -> {
                PersonDTO personDTO = table.getSelectionModel().getSelectedItem();
                if (personDTO == null) {
                    viewService.showError(NO_ELEMENT);
                } else if (personDTO.getId().equals(person.getId())) {
                    viewService.showError(NOT_ALLOWED_DELETE_YOURSELF);
                } else {
                    personService.deletePerson(personDTO.getId());
                    table.getItems().remove(personDTO);
                }
            });
            changeLevelButton.setOnAction(e -> {
                PersonDTO personDTO = table.getSelectionModel().getSelectedItem();
                if (personDTO == null) {
                    viewService.showError(NO_ELEMENT);
                } else if (personDTO.getId().equals(person.getId())) {
                    viewService.showError(NOT_ALLOWED_CHANGE_LEVEL);
                } else {
                    alert = viewService.showPaneDialog(CHANGE + LEVEL.toLowerCase(), buildChangeLevelBox(personDTO));
                }
            });
        }
    }

    private VBox buildChangeLevelBox(PersonDTO personDTO) {
        VBox box = new VBox();
        Text headerText = new Text(CHANGE + " " + LEVEL.toLowerCase() + " : " + personDTO.getUsername());
        Label levelLabel = new Label(LEVEL);
        ComboBox<String> levelComboBox =
                new ComboBox<>(FXCollections.observableArrayList(personLevelsMap.keySet()));
        levelComboBox.getSelectionModel().select(getLevel(personDTO.getLevel()));
        Button saveButton = new Button(SAVE);
        box.getChildren().addAll(headerText, levelLabel, levelComboBox, saveButton);
        saveButton.setOnAction(event -> {
            personDTO.setLevel(getPersonLevelName(levelComboBox.getSelectionModel().getSelectedItem()));
            personService.edit(personDTO);
            table.refresh();
            alert.close();
        });
        return box;
    }

    private void buildTable() {
        table = new TableView<>();
        table.setPrefWidth(ViewService.WIDTH);
        table.setPrefHeight(ViewService.HEIGHT);
        TableColumn<PersonDTO, String> usernameColumn = new TableColumn<>(USERNAME);
        TableColumn<PersonDTO, String> fullNameColumn = new TableColumn<>(FIO);
        TableColumn<PersonDTO, String> levelColumn = new TableColumn<>(LEVEL);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        levelColumn.setCellValueFactory(param -> new SimpleStringProperty(getLevel(param.getValue().getLevel())));
        table.getColumns().addAll(usernameColumn, fullNameColumn, levelColumn);
    }

    private HBox buildSearchAndSortBox() {
        Label searchLabel = new Label(SEARCH);
        TextField searchField = new TextField();
        searchField.setPromptText("по ФИО или имени пользователя");
        Button searchButton = new Button(SEARCH);
        Button clearButton = new Button(CLEAR);
        ButtonBar bar = new ButtonBar();
        bar.getButtons().setAll(searchButton, clearButton);
        ComboBox searchComboBox =
                new ComboBox(FXCollections.observableArrayList(searchMap.keySet()));
        searchComboBox.getSelectionModel().selectFirst();
        HBox searchBox = new HBox(searchLabel, searchComboBox, searchField, bar);
        searchBox.setSpacing(2);
        searchBox.setAlignment(Pos.CENTER);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            query = newValue;
            System.out.println("search field changed from " + oldValue + " to " + newValue);
        });
        searchButton.setOnAction(event -> {
            query = query.trim()
                    .replaceAll("%", "")
                    .replaceAll("'", "")
                    .replaceAll(";", "");
            if (!query.isEmpty()) {
                String key = String.valueOf(searchComboBox.getSelectionModel().getSelectedItem());
                Search search = searchMap.get(key);
                switch (search) {
                    case USERNAME:
                        attr = "username";
                        break;
                    case NAME:
                        attr = "fullName";
                        break;
                }
                table.getItems().setAll(personService.filteredFetch(levels, query, attr));
            }
        });
        clearButton.setOnAction(event -> {
            initLevelsAndQuery();
            table.setItems(FXCollections.observableArrayList(personService.filteredFetch(levels, query, attr)));
            searchField.setText("");
        });
        return searchBox;
    }

    private HBox buildFilterBox() {
        Label filterLabel = new Label("Фильтр по уровню");
        CheckComboBox<String> checkComboBox = new CheckComboBox(
                FXCollections.observableArrayList(personLevelsMap.keySet())
        );
        checkComboBox.getCheckModel().checkAll();
        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            public void onChanged(ListChangeListener.Change<? extends String> c) {
                if (checkComboBox.getCheckModel().getCheckedItems().size() != 0) {
                    levels = new ArrayList<>();
                    checkComboBox.getCheckModel().getCheckedItems()
                            .stream()
                            .forEach(el -> {
                                levels.add(personLevelsMap.get(el));
                            });
                    table.setItems(FXCollections.observableArrayList(personService.filteredFetch(levels, query, attr)));
                } else {
                    viewService.showError("Должен быть выбран хотя бы один уровень для фильтрации!");
                }
            }
        });
        HBox filterBox = new HBox(filterLabel, checkComboBox);
        filterBox.setSpacing(2);
        filterBox.setAlignment(Pos.CENTER);
        return filterBox;
    }

    private void initLevelsAndQuery() {
        attr = null;
        query = null;
        levels = Arrays.asList(PersonLevel.NEWBIE, PersonLevel.SKILLED, PersonLevel.LEAD);
    }

    public Scene getScene() {
        return scene;
    }

    @Autowired
    public PersonComponent(ViewService viewService, PersonService personService) {
        this.viewService = viewService;
        this.personService = personService;
    }
}
