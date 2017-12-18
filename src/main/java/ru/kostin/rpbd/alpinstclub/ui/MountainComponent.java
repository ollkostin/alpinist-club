package ru.kostin.rpbd.alpinstclub.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;
import ru.kostin.rpbd.alpinstclub.service.MountainService;
import ru.kostin.rpbd.alpinstclub.service.dto.MountainDTO;
import ru.kostin.rpbd.alpinstclub.service.dto.RouteDTO;
import ru.kostin.rpbd.alpinstclub.util.Search;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static ru.kostin.rpbd.alpinstclub.util.Constant.*;

@Component
public class MountainComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MountainComponent.class);
    private MountainService mountainService;
    private ViewService viewService;
    private Person person;
    private Scene scene;
    private TableView<MountainDTO> table;
    private Alert alert;
    private HashMap<String, Search> searchMap = new HashMap();

    {
        searchMap.put("название", Search.NAME);
        searchMap.put("высота меньше", Search.LESS);
        searchMap.put("высота больше", Search.GREATER);
        searchMap.put("высота равна", Search.EQUALS);
    }

    private void buildTable() {
        table = new TableView<>();
        table.setPrefWidth(ViewService.WIDTH);
        table.setPrefHeight(ViewService.HEIGHT);
        TableColumn<MountainDTO, String> nameColumn = new TableColumn<>(NAME);
        TableColumn<MountainDTO, Float> heightColumn = new TableColumn<>(HEIGHT);
        TableColumn<MountainDTO, Float> latColumn = new TableColumn<>(LAT);
        TableColumn<MountainDTO, Float> lonColumn = new TableColumn<>("Долгота");
        TableColumn<MountainDTO, Void> routesColumn = new TableColumn<>();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));
        latColumn.setCellValueFactory(new PropertyValueFactory<>("lat"));
        lonColumn.setCellValueFactory(new PropertyValueFactory<>("lon"));
        routesColumn.setCellFactory(showRoutesColumnFactory());
        table.getColumns().addAll(nameColumn, heightColumn, latColumn, lonColumn, routesColumn);
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
        buildTable();
        Button goToPreviousSceneButton = new Button("Назад");
        ButtonBar bar = new ButtonBar();
        bar.getButtons().add(goToPreviousSceneButton);
        HBox searchBox = buildSearchBox();
        anchorPane.getChildren().add(table);
        hBox.getChildren().addAll(bar);
        vBox.getChildren().addAll(searchBox, anchorPane, hBox);
        goToPreviousSceneButton.setOnAction(viewService::showPreviousScene);
        ObservableList<MountainDTO> mountains =
                FXCollections.observableList(mountainService.fetchMountains());
        table.setItems(mountains);
        if (person.getLevel().equals(PersonLevel.LEAD)) {
            Button addNewMountainButton = new Button(ADD);
            Button deleteMountainButton = new Button("Удалить");
            Button changeMountainButton = new Button(CHANGE);
            addNewMountainButton.setOnAction(event -> {
                this.alert = viewService.showPaneDialog(ADD, buildMountainVBox(null));
            });
            deleteMountainButton.setOnAction(event -> {
                MountainDTO dto = table.getSelectionModel().getSelectedItem();
                if (dto != null) {
                    mountainService.deleteMountain(dto.getId());
                    table.getItems().remove(dto);
                } else {
                    viewService.showError(NO_ELEMENT);
                }
            });
            changeMountainButton.setOnAction(event -> {
                MountainDTO dto = table.getSelectionModel().getSelectedItem();
                if (dto != null) {
                    this.alert = viewService.showPaneDialog(CHANGE, buildMountainVBox(dto));
                } else {
                    viewService.showError(NO_ELEMENT);
                }
            });
            bar.getButtons().addAll(addNewMountainButton, changeMountainButton, deleteMountainButton);
        }
    }

    private VBox buildMountainVBox(MountainDTO dto) {
        VBox mountainVBox = new VBox();
        GridPane pane = new GridPane();
        Label nameLabel = new Label(NAME);
        TextField nameField = dto == null ? new TextField() : new TextField(dto.getName());
        nameField.setPromptText(NAME);
        pane.add(nameLabel, 0, 0);
        pane.add(nameField, 1, 0);
        Label heightLabel = new Label(HEIGHT);
        TextField heightField = dto == null ? new TextField() : new TextField(dto.getHeight().toString());
        heightField.setPromptText(HEIGHT);
        pane.add(heightLabel, 0, 1);
        pane.add(heightField, 1, 1);
        Label latLabel = new Label(LAT);
        TextField latField = dto == null ? new TextField() : new TextField(dto.getLat().toString());
        latField.setPromptText(LAT);
        pane.add(latLabel, 0, 2);
        pane.add(latField, 1, 2);
        Label lonLabel = new Label(LON);
        TextField lonField = dto == null ? new TextField() : new TextField(dto.getLon().toString());
        lonField.setPromptText(LON);
        pane.add(lonLabel, 0, 3);
        pane.add(lonField, 1, 3);
        Button saveButton = new Button("Сохранить");
        pane.add(saveButton, 1, 4);
        mountainVBox.getChildren().addAll(pane);
        saveButton.setOnAction(event -> {
            try {
                String name = nameField.getText();
                Float height = Float.parseFloat(heightField.getText());
                Float lat = Float.parseFloat(latField.getText());
                Float lon = Float.parseFloat(lonField.getText());
                if (name.trim().isEmpty() || name.length() > 255
                        || height > 9000f || lat > 180f || lon > 180f
                        || height < 0f || lat < 0f || lon < 0f) {
                    viewService.showError(MOUNTAIN_ERROR);
                } else {
                    MountainDTO m = Optional.ofNullable(dto).orElse(new MountainDTO());
                    m.setName(name);
                    m.setHeight(height);
                    m.setLat(lat);
                    m.setLon(lon);
                    m.setId(mountainService.saveMountain(m).getId());
                    if (dto == null) {
                        table.getItems().add(m);
                    }
                    table.refresh();
                    this.alert.close();
                }
            } catch (NumberFormatException ex) {
                viewService.showError(MOUNTAIN_ERROR);
            } finally {
                nameField.setText("");
                heightField.setText("");
                latField.setText("");
                lonField.setText("");
            }
        });
        return mountainVBox;
    }

    private HBox buildSearchBox() {
        Label searchLabel = new Label(SEARCH);
        TextField searchField = new TextField();
        searchField.setPromptText(SEARCH);
        Button searchButton = new Button(SEARCH);
        Button clearButton = new Button(CLEAR);
        ButtonBar bar = new ButtonBar();
        bar.getButtons().setAll(searchButton, clearButton);
        ComboBox searchComboBox =
                new ComboBox(FXCollections.observableArrayList(searchMap.keySet()));
        searchComboBox.getSelectionModel().selectFirst();
        HBox searchBox = new HBox(searchLabel, searchComboBox, searchField, bar);
        searchBox.setAlignment(Pos.CENTER);
        searchButton.setOnAction(event -> {
            String comp = String.valueOf(searchComboBox.getSelectionModel().getSelectedItem());
            Search c = searchMap.get(comp);
            String query = searchField.getText()
                    .trim()
                    .replaceAll("%", "")
                    .replaceAll("'", "")
                    .replaceAll(";", "");
            if (!query.isEmpty()) {
                switch (c) {
                    case LESS:
                    case EQUALS:
                    case GREATER:
                        try {
                            Float height = Float.parseFloat(query);
                            if (height < 0) {
                                throw new NumberFormatException();
                            }
                            List<MountainDTO> mountainDTOList =
                                    mountainService.findAllByHeightCompare(height, c);
                            table.getItems().setAll(mountainDTOList);
                        } catch (NumberFormatException ex) {
                            viewService.showError(HEIGHT_ERROR);
                        }
                        break;
                    case NAME:
                        String nameLike = query;
                        List<MountainDTO> mountainDTOList = mountainService.findAllByNameLike(nameLike);
                        table.getItems().setAll(mountainDTOList);
                        break;
                }
            }
        });
        clearButton.setOnAction(event -> {
            table.getItems().setAll(FXCollections.observableList(mountainService.fetchMountains()));
            searchField.setText("");
        });
        return searchBox;
    }

    public Scene getScene() {
        return scene;
    }

    @Autowired
    public MountainComponent(MountainService mountainService, ViewService viewService) {
        this.mountainService = mountainService;
        this.viewService = viewService;
    }

    private Callback<TableColumn<MountainDTO, Void>, TableCell<MountainDTO, Void>> showRoutesColumnFactory() {
        return new Callback<TableColumn<MountainDTO, Void>, TableCell<MountainDTO, Void>>() {
            @Override
            public TableCell<MountainDTO, Void> call(TableColumn<MountainDTO, Void> param) {
                return new TableCell<MountainDTO, Void>() {
                    private String title = "Маршруты";
                    private Button btn = new Button(title);

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            MountainDTO mountainDTO = getTableView().getItems().get(getIndex());
                            VBox vBox = new VBox();
                            TableView<RouteDTO> routesTable = new TableView<>();
                            TableColumn<RouteDTO, String> nameColumn = new TableColumn<>("Название");
                            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                            routesTable.getColumns().add(nameColumn);
                            routesTable.getItems().addAll(FXCollections.observableList(mountainDTO.getRoutes()));
                            Stage secondStage = new Stage();
                            secondStage.setTitle(title);
                            vBox.getChildren().add(routesTable);
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

    private Callback<TableColumn<RouteDTO, Void>, TableCell<RouteDTO, Void>> deleteRouteColumnCellFactory() {
        return new Callback<TableColumn<RouteDTO, Void>, TableCell<RouteDTO, Void>>() {
            @Override
            public TableCell<RouteDTO, Void> call(TableColumn<RouteDTO, Void> param) {
                return new TableCell<RouteDTO, Void>() {
                    private String title = "Удалить";
                    private Button btn = new Button(title);

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            RouteDTO r = getTableView().getItems().get(getIndex());
                            try {
                                mountainService.deleteMountain(r.getId());
                                table.getItems().remove(r);
                            } catch (Exception e) {
                                viewService.showError(e.getMessage());
                            }
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


//    private TableView buildRouteTable() {
//        TableView<RouteDTO> routeTable = new TableView<>();
//        routeTable.setEditable(this.person.getLevel().equals(PersonLevel.LEAD));
//        TableColumn<RouteDTO, String> nameColumn = new TableColumn<>("Название");
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        nameColumn.setOnEditCommit(event -> {
//            RouteDTO dto = routeTable.getSelectionModel().getSelectedItem();
//            try {
//                if(dto.getName().trim().isEmpty()){
//                    throw new Exception("Не может быть пустым");
//                }
//                if (dto.getMountainId() == null){
////                    dto.setMountainId();
//                }
//                mountainService.saveRoute(dto);
//            } catch (Exception ex) {
//                viewService.showError(ex.getMessage());
//            }
//        });
//        TableColumn deleteColumn = new TableColumn();
//        deleteColumn.setCellFactory(deleteRouteColumnCellFactory());
//        return routeTable;
//    }