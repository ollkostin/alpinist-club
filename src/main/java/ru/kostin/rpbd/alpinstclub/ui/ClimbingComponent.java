package ru.kostin.rpbd.alpinstclub.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;
import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.kostin.rpbd.alpinstclub.persistence.model.ClimbingStatus;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;
import ru.kostin.rpbd.alpinstclub.service.ClimbingService;
import ru.kostin.rpbd.alpinstclub.service.MountainService;
import ru.kostin.rpbd.alpinstclub.service.PersonService;
import ru.kostin.rpbd.alpinstclub.service.dto.ClimbingDTO;
import ru.kostin.rpbd.alpinstclub.service.dto.MountainDTO;
import ru.kostin.rpbd.alpinstclub.service.dto.PersonDTO;
import ru.kostin.rpbd.alpinstclub.service.dto.RouteDTO;
import ru.kostin.rpbd.alpinstclub.util.Util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kostin.rpbd.alpinstclub.util.Constant.*;
import static ru.kostin.rpbd.alpinstclub.util.Util.*;

@Component
public class ClimbingComponent {
    private HashMap<MountainDTO, List<RouteDTO>> mountainRouteMap = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClimbingComponent.class);
    private ViewService viewService;
    private ClimbingService climbingService;
    private MountainService mountainService;
    private PersonService personService;
    private TableView<ClimbingDTO> table;
    private Scene scene;
    private PersonDTO personDTO;
    private Alert alert;
    private Date start, end;
    private List<String> statuses = Arrays.asList(
            ClimbingStatus.NEW.name(), ClimbingStatus.SUCCESS.name(),
            ClimbingStatus.CANCELED.name(), ClimbingStatus.FAIL.name()
    );

    private void buildTable() {
        table = new TableView<>();
        table.setPrefWidth(ViewService.WIDTH);
        table.setPrefHeight(ViewService.HEIGHT);
        TableColumn dateColumn = new TableColumn("Даты восхождения");
        TableColumn<ClimbingDTO, Date> startDateColumn = new TableColumn<>(START_DATE);
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
        TableColumn<ClimbingDTO, Date> endDateColumn = new TableColumn<>(END_DATE);
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
        dateColumn.getColumns().addAll(startDateColumn, endDateColumn);
        TableColumn<ClimbingDTO, String> mountainColumn = new TableColumn<>(MOUNTAIN);
        mountainColumn.setCellValueFactory(new PropertyValueFactory<>("mountain"));
        TableColumn<ClimbingDTO, String> statusColumn = new TableColumn<>(STATUS);
        statusColumn.setCellValueFactory(param ->
                new SimpleStringProperty(getStatus(param.getValue().getStatus())));
        TableColumn<ClimbingDTO, String> routeColumn = new TableColumn<>(ROUTE);
        routeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getRoute().getName())
        );
        TableColumn<ClimbingDTO, String> minLevelColumn = new TableColumn<>(MIN_LEVEL);
        TableColumn<ClimbingDTO, String> personLimitColumn = new TableColumn<>(PERSON_LIMIT);
        personLimitColumn.setCellValueFactory(new PropertyValueFactory<>("personLimit"));
        minLevelColumn.setCellValueFactory(param -> new SimpleStringProperty(getLevel(param.getValue().getMinLevel())));
        TableColumn showMembersColumn = new TableColumn<>();
        showMembersColumn.setCellFactory(showMembersColumnCellFactory());
        TableColumn<ClimbingDTO, Boolean> personIsMemberColumn = new TableColumn<>("Принимаю участие");
        personIsMemberColumn.setCellValueFactory(param -> {
            ClimbingDTO val = param.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(val.getPersonIsMember());
            booleanProp.addListener((observable, oldValue, newValue) -> {
                if (!oldValue.equals(newValue)) {
                    if (newValue && val.getMembers().size() + 1 > val.getPersonLimit()) {
                        viewService.showError("Невозможно принять участие,превышено количество участников");
                        val.setPersonIsMember(oldValue);
                    } else {
                        if (newValue) {
                            Set<PersonDTO> members = val.getMembers();
                            members.add(personDTO);
                            System.out.println(members.contains(personDTO));
                            val.setMembers(members);
                        } else {
                            Set<PersonDTO> members = val.getMembers();
                            members.remove(personDTO);
                            System.out.println(members.contains(personDTO));
                            val.setMembers(members);
                        }
                        climbingService.save(val);
                        val.setPersonIsMember(newValue);
                        table.refresh();
                    }
                }
            });
            return booleanProp;
        });
        personIsMemberColumn.setCellFactory(param -> {
            CheckBoxTableCell<ClimbingDTO, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        table.getColumns().addAll(dateColumn, mountainColumn, routeColumn, personLimitColumn, statusColumn,
                minLevelColumn, personIsMemberColumn, showMembersColumn);
        table.setEditable(true);
        personIsMemberColumn.setEditable(true);
    }

    public void configure(Person person) {
        getStatuses();
        this.personDTO = new PersonDTO(person);
        buildTable();
        List<ClimbingDTO> climbingDTOList =
                climbingService
                        .fetch(person.getLevel(), null, null, null)
                        .stream()
                        .peek(climbingDTO ->
                                climbingDTO
                                        .setPersonIsMember(
                                                climbingDTO.getMembers().contains(personDTO)
                                        )
                        )
                        .collect(Collectors.toList());
        ObservableList<ClimbingDTO> climbings = FXCollections.observableList(climbingDTOList);
        table.setItems(climbings);
        table.setPrefWidth(ViewService.WIDTH);
        Button goToPreviousSceneButton = new Button(BACK);
        goToPreviousSceneButton.setOnAction(viewService::showPreviousScene);
        ButtonBar bar = new ButtonBar();
        bar.getButtons().add(goToPreviousSceneButton);
        VBox vBox = new VBox();
        HBox hBox = new HBox();
        AnchorPane anchorPane = new AnchorPane();
        vBox.setPrefWidth(ViewService.WIDTH);
        vBox.setPrefHeight(ViewService.HEIGHT);
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        hBox.setAlignment(Pos.CENTER);
        scene = new Scene(vBox);
        GridPane pane = new GridPane();
        pane.add(buildDateFilterBox(), 0, 0);
        pane.add(buildStatusFilterBox(), 0, 1);
        pane.add(table, 0, 2, 2, 1);
        anchorPane.getChildren().add(pane);
        hBox.getChildren().addAll(bar);
        vBox.getChildren().addAll(anchorPane, hBox);
        if (person.getLevel().equals(PersonLevel.LEAD)) {
            Button addNewClimbingButton = new Button(ADD);
            Button deleteClimbingButton = new Button(DELETE);
            Button changeClimbingButton = new Button(CHANGE);
            bar.getButtons().addAll(addNewClimbingButton, changeClimbingButton, deleteClimbingButton);
            addNewClimbingButton.setOnAction(e -> alert = viewService.showPaneDialog(ADD, buildClimbingVBox(null)));
            changeClimbingButton.setOnAction(e -> {
                ClimbingDTO dto = table.getSelectionModel().getSelectedItem();
                if (dto == null) {
                    viewService.showError(NO_ELEMENT);
                } else {
                    alert = viewService.showPaneDialog(CHANGE, buildClimbingVBox(dto));
                }
            });
            deleteClimbingButton.setOnAction(e -> {
                ClimbingDTO dto = table.getSelectionModel().getSelectedItem();
                if (dto == null) {
                    viewService.showError(NO_ELEMENT);
                } else {
                    climbingService.delete(dto.getId());
                    table.getItems().remove(dto);
                }
            });
        }
    }

    private VBox buildClimbingVBox(ClimbingDTO climbingDTO) {
        VBox climbingVBox = new VBox();
        GridPane pane = new GridPane();
        Label startDateLabel = new Label(START_DATE);
        DatePicker startDatePicker = new DatePicker(climbingDTO == null ? LocalDate.now() :
                climbingDTO.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        pane.add(startDateLabel, 0, 0);
        pane.add(startDatePicker, 1, 0);
        Label endDateLabel = new Label(END_DATE);
        DatePicker endDatePicker = new DatePicker(climbingDTO == null ? LocalDate.now() :
                climbingDTO.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        pane.add(endDateLabel, 0, 1);
        pane.add(endDatePicker, 1, 1);
        Label mountainLabel = new Label(MOUNTAIN);
        List<String> mountainDTOS = mountainService.fetchMountains()
                .stream()
                .map(MountainDTO::getName)
                .collect(Collectors.toList());
        ObservableList<String> mountains = FXCollections.observableList(mountainDTOS);
        ComboBox<String> mountainComboBox = new ComboBox<>(mountains);
        pane.add(mountainLabel, 0, 2);
        pane.add(mountainComboBox, 1, 2);
        Label routeLabel = new Label(ROUTE);
        ComboBox<String> routeComboBox = new ComboBox<>();
        pane.add(routeLabel, 0, 3);
        pane.add(routeComboBox, 1, 3);
        Label levelLabel = new Label(LEVEL);
        ObservableList<String> person = FXCollections.observableArrayList(NEWBIE, SKILLED, LEAD);
        ComboBox<String> levelComboBox = new ComboBox<>(person);
        pane.add(levelLabel, 0, 4);
        pane.add(levelComboBox, 1, 4);
        ObservableList<String> statusesComboBoxValues = FXCollections.observableArrayList(getStatusesValues());
        ComboBox<String> statusComboBox = new ComboBox<>(statusesComboBoxValues);
        Label personLimitLabel = new Label(PERSON_LIMIT);
        TextField personLimitTextField = new TextField(climbingDTO == null ?
                "" : climbingDTO.getPersonLimit().toString());
        pane.add(personLimitLabel, 0, 5);
        pane.add(personLimitTextField, 1, 5);

        if (climbingDTO == null) {
            mountainComboBox.getSelectionModel().selectFirst();
            statusComboBox.getSelectionModel().select(NEW);
            levelComboBox.getSelectionModel().selectFirst();
            routeComboBox.setItems(
                    FXCollections.observableArrayList(
                            getRoutesByMountain(mountainComboBox.getSelectionModel().getSelectedItem())
                    )
            );
            routeComboBox.getSelectionModel().selectFirst();
        } else {
            mountainComboBox.getSelectionModel().select(climbingDTO.getMountain());
            statusComboBox.getSelectionModel().select(getStatus(climbingDTO.getStatus()));
            levelComboBox.getSelectionModel().select(getLevel(climbingDTO.getMinLevel()));
            Label statusLabel = new Label(STATUS);
            pane.add(statusLabel, 0, 6);
            pane.add(statusComboBox, 1, 6);
            routeComboBox.setItems(
                    FXCollections.observableArrayList(
                            getRoutesByMountain(mountainComboBox.getSelectionModel().getSelectedItem())
                    )
            );
            routeComboBox.getSelectionModel().select(climbingDTO.getRoute().getName());
        }
        List<PersonDTO> personList = personService.fetch()
                .stream()
                .filter(p -> filterByLevel(levelComboBox.getSelectionModel().getSelectedItem(), p))
                .peek(p -> {
                    if (climbingDTO != null) {
                        p.setInTeam(climbingDTO.getMembers().contains(p));
                    }
                })
                .collect(Collectors.toList());
        ObservableList<PersonDTO> people = FXCollections.observableList(personList);
        TableView<PersonDTO> personTable = buildPersonTableForClimbingVBox();
        mountainComboBox.setOnAction(event -> {
            String mountainName = mountainComboBox.getSelectionModel().getSelectedItem();
            routeComboBox.setItems(FXCollections.observableArrayList(getRoutesByMountain(mountainName)));
            routeComboBox.getSelectionModel().selectFirst();
        });
        levelComboBox.setOnAction(event -> {
            List<PersonDTO> pList =
                    personService
                            .fetch()
                            .stream()
                            .filter(p ->
                                    filterByLevel(levelComboBox.getSelectionModel().getSelectedItem(), p))
                            .peek(p -> {
                                if (climbingDTO != null) {
                                    p.setInTeam(climbingDTO.getMembers().contains(p));
                                }
                            })
                            .collect(Collectors.toList());
            ObservableList<PersonDTO> observablePersonList = FXCollections.observableList(pList);
            people.setAll(observablePersonList);
        });
        personTable.setItems(people);
        Button saveButton = new Button(SAVE);
        pane.add(personTable, 2, 0, 3, 8);
        pane.add(saveButton, 1, 8);
        climbingVBox.getChildren().addAll(pane);
        saveButton.setOnAction(e -> {
            try {
                LocalDate localStartDate = startDatePicker.getValue();
                Date startDate = Date.from(Instant.from(localStartDate.atStartOfDay(ZoneId.systemDefault())));
                LocalDate localEndDate = endDatePicker.getValue();
                Date endDate = Date.from(Instant.from(localEndDate.atStartOfDay(ZoneId.systemDefault())));
                Set<PersonDTO> members = people.stream().filter(PersonDTO::isInTeam).collect(Collectors.toSet());
                Integer personLimit = Integer.parseUnsignedInt(personLimitTextField.getText());
                String mountainName = mountainComboBox.getSelectionModel().getSelectedItem();
                String status = getClimbingStatusName(statusComboBox.getSelectionModel().getSelectedItem());
                String minLevel = getPersonLevelName(levelComboBox.getSelectionModel().getSelectedItem());
                String route = routeComboBox.getSelectionModel().getSelectedItem();
                if (startDate.after(endDate) || personLimit < 2 || personLimit > 10
                        || members.size() > personLimit) {
                    viewService.showError(CLIMBING_ERROR);
                } else {
                    ClimbingDTO dto = Optional.ofNullable(climbingDTO).orElse(new ClimbingDTO());
                    dto.setMountain(mountainName);
                    dto.setStatus(status);
                    dto.setMinLevel(minLevel);
                    dto.setStartDate(startDate);
                    dto.setEndDate(endDate);
                    dto.setMembers(members);
                    dto.setPersonLimit(personLimit);
                    RouteDTO routeDto = mountainService.findAllRoutesByMountain(dto.getMountain()).stream()
                            .filter(r -> r.getName().equals(route)).collect(Collectors.toList()).get(0);
                    dto.setRoute(routeDto);
                    dto.setId(climbingService.save(dto).getId());
                    if (climbingDTO == null) {
                        table.getItems().add(dto);
                    }
                    dto.setPersonIsMember(dto.getMembers().contains(personDTO));
                    table.refresh();
                    alert.close();
                }
            } catch (NumberFormatException ex) {
                viewService.showError(CLIMBING_ERROR);
            } catch (DataIntegrityViolationException ex) {
                viewService.showError(ALREADY_EXISTS);
            }
        });
        return climbingVBox;
    }

    private TableView<PersonDTO> buildPersonTableForClimbingVBox() {
        TableView<PersonDTO> personTable = buildPersonTable();
        personTable.setEditable(true);
        TableColumn<PersonDTO, Boolean> cbPersonColumn = new TableColumn<>();
        cbPersonColumn.setCellValueFactory(param -> param.getValue().inTeamProperty());
        cbPersonColumn.setCellFactory(CheckBoxTableCell.forTableColumn(cbPersonColumn));
        personTable.getColumns().add(cbPersonColumn);
        return personTable;
    }

    private HBox buildDateFilterBox() {
        DatePicker startDatePicker = new DatePicker();
        Label startDateLabel = new Label(START_DATE);
        Label endDateLabel = new Label(END_DATE);
        DatePicker endDatePicker = new DatePicker();
        Button filterButton = new Button(SEARCH);
        Button clearStartDateButton = new Button(CLEAR);
        Button clearEndDateButton = new Button(CLEAR);
        Button clearButton = new Button("Очистить поиск");
        HBox box = new HBox(startDateLabel, startDatePicker, clearStartDateButton, endDateLabel, endDatePicker, clearEndDateButton, filterButton, clearButton);
        box.setSpacing(5);
        filterButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            start = startDate != null ? Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            end = endDate != null ? Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
            List<ClimbingDTO> climbingDTOList =
                    climbingService
                            .fetch(
                                    PersonLevel.valueOf(this.personDTO.getLevel()), start, end, getStatuses())
                            .stream()
                            .peek(climbingDTO ->
                                    climbingDTO
                                            .setPersonIsMember(climbingDTO.getMembers().contains(personDTO)))
                            .collect(Collectors.toList());
            table.setItems(FXCollections.observableList(climbingDTOList));
        });
        clearStartDateButton.setOnAction(event -> {
            start = null;
            startDatePicker.setValue(null);
        });
        clearEndDateButton.setOnAction(event -> {
            end = null;
            endDatePicker.setValue(null);
        });
        clearButton.setOnAction(event -> {
            start = null;
            end = null;
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            statuses = Arrays.asList(ClimbingStatus.NEW.name(), ClimbingStatus.SUCCESS.name(), ClimbingStatus.CANCELED.name(), ClimbingStatus.FAIL.name());
            List<ClimbingDTO> climbingDTOList =
                    climbingService
                            .fetch(PersonLevel.valueOf(this.personDTO.getLevel()), start, end, getStatuses())
                            .stream()
                            .peek(climbingDTO ->
                                    climbingDTO
                                            .setPersonIsMember(climbingDTO.getMembers().contains(personDTO)))
                            .collect(Collectors.toList());
            table.setItems(FXCollections.observableList(climbingDTOList));
        });
        return box;
    }

    private List<String> getStatusesValues() {
        return statuses.stream().map(Util::getStatus).collect(Collectors.toList());
    }

    private HBox buildStatusFilterBox() {
        Label filterLabel = new Label(STATUS);
        ObservableList<String> statusComboboxValues = FXCollections.observableArrayList(getStatusesValues());
        CheckComboBox<String> checkComboBox = new CheckComboBox(statusComboboxValues);
        checkComboBox.getCheckModel().checkAll();
        checkComboBox.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            if (checkComboBox.getCheckModel().getCheckedItems().size() != 0) {
                statuses = new ArrayList<>();
                checkComboBox.getCheckModel().getCheckedItems()
                        .stream()
                        .forEach(el -> statuses.add(getClimbingStatusName(el)));
                List<ClimbingDTO> climbingDTOList =
                        climbingService
                                .fetch(
                                        PersonLevel.valueOf(personDTO.getLevel()), start, end, getStatuses())
                                .stream()
                                .peek(climbingDTO ->
                                        climbingDTO
                                                .setPersonIsMember(climbingDTO.getMembers().contains(personDTO)))
                                .collect(Collectors.toList());
                ObservableList<ClimbingDTO> climbings = FXCollections.observableList(climbingDTOList);
                table.setItems(climbings);
            } else {
                viewService.showError("Должен быть выбран хотя бы один статус для фильтрации!");
            }
        });
        HBox filterBox = new HBox(filterLabel, checkComboBox);
        filterBox.setSpacing(2);
        filterBox.setAlignment(Pos.CENTER);
        return filterBox;
    }

    private TableView<PersonDTO> buildPersonTable() {
        TableView<PersonDTO> tableView = new TableView<>();
        TableColumn<PersonDTO, String> fullNameColumn = new TableColumn<>(FIO);
        TableColumn<PersonDTO, String> levelColumn = new TableColumn<>(LEVEL);
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        levelColumn.setCellValueFactory(param -> new SimpleStringProperty(getLevel(param.getValue().getLevel())));
        tableView.getColumns().addAll(fullNameColumn, levelColumn);
        return tableView;
    }

    private boolean filterByLevel(String level, PersonDTO personDTO) {
        boolean b = true;
        switch (level) {
            case LEAD:
                b = personDTO.getLevel().equals(PersonLevel.LEAD.name());
                break;
            case SKILLED:
                b = personDTO.getLevel().equals(PersonLevel.LEAD.name()) || personDTO.getLevel().equals(PersonLevel.SKILLED.name());
                break;
            case NEWBIE:
                break;
        }
        return b;
    }

    public Scene getScene() {
        return scene;
    }

    private List<String> getRoutesByMountain(String mountainName) {
        return mountainService.findAllRoutesByMountain(mountainName)
                .stream()
                .map(RouteDTO::getName)
                .collect(Collectors.toList());
    }

    private List<ClimbingStatus> getStatuses() {
        List<ClimbingStatus> statusList = new ArrayList<>();
        if (statuses != null && !statuses.isEmpty()) {
            statusList =
                    statuses.stream()
                            .map(ClimbingStatus::valueOf)
                            .collect(Collectors.toList());
        }
        return statusList;
    }

    @Autowired
    public ClimbingComponent(ViewService viewService, ClimbingService climbingService,
                             MountainService mountainService, PersonService personService) {
        this.climbingService = climbingService;
        this.viewService = viewService;
        this.mountainService = mountainService;
        this.personService = personService;
    }

    private Callback<TableColumn<ClimbingDTO, Void>, TableCell<ClimbingDTO, Void>> showMembersColumnCellFactory() {
        return new Callback<TableColumn<ClimbingDTO, Void>, TableCell<ClimbingDTO, Void>>() {
            @Override
            public TableCell<ClimbingDTO, Void> call(TableColumn<ClimbingDTO, Void> param) {
                return new TableCell<ClimbingDTO, Void>() {
                    private String title = "Участники";
                    private Button btn = new Button(title);

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ClimbingDTO climbingDTO = getTableView().getItems().get(getIndex());
                            VBox vBox = new VBox();
                            TableView<PersonDTO> membersTable = buildPersonTable();
                            membersTable.getItems().addAll(FXCollections.observableSet(climbingDTO.getMembers()));
                            Stage secondStage = new Stage();
                            secondStage.setTitle(title);
                            vBox.getChildren().add(membersTable);
                            Scene membersScene = new Scene(vBox);
                            secondStage.setScene(membersScene);
                            secondStage.show();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
    }

}