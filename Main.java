package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.scene.Node;
import javafx.scene.Parent;

public class Main extends Application {
  
  private FoodData food;
  private String oldNameFilter;
  private List<FoodItem> mealList;
  private TableView<FoodItem> foodTable;
  private TableView<FoodItem> mealTable;
  private ObservableList<FoodItem> obsFoodList;
  private ObservableList<FoodItem> obsMealList;
  private List<FoodItem> foodList;
  private Stage primaryStage;
  private Label availableFoodsLabel;

  @Override
  public void start(Stage primaryStage) {
    food = new FoodData();
    food.loadFoodItems("foodItems.csv");
    mealList = new ArrayList<FoodItem>();
    mealTable = new TableView<FoodItem>();
    foodTable = new TableView<FoodItem>();
    
    this.primaryStage = primaryStage;

    try {
      BorderPane rootContainer = new BorderPane();
      Scene scene = new Scene(rootContainer,1200,600);
      scene.getStylesheets().add(getClass().getResource("application.css").
          toExternalForm());
      String windowTitle = "Meal Planner";
      primaryStage.setTitle(windowTitle);

      //left pane
      rootContainer.setLeft(createLeftPane());

      // center pane
      rootContainer.setCenter(createCenterPane());

      // right pane
      rootContainer.setRight(createRightPane());

      primaryStage.setScene(scene);
      primaryStage.show();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }//start

  /**
   * creates Node for left pane
   * @return - Node of left pane
   */
  private Node createLeftPane() {
    VBox leftPane = new VBox();
    leftPane.setSpacing(110);
    leftPane.setAlignment(Pos.TOP_LEFT);
    leftPane.setPadding(new Insets(10));

    // top left pane
    leftPane.getChildren().addAll(createLoadFoodPane(), createSaveFoodPane(), 
        createAddFoodPane());

    return leftPane;
  }

  /**
   * creates Node for top left pane for loading food data from file
   * @return - Node of top left pane for loading food data from file
   */
  private Node createLoadFoodPane() {
    VBox loadFoodPane = new VBox();
    loadFoodPane.setSpacing(10);

    String inputFilePathLabelText = "Load Foods From a File";
    String loadFoodButtonText = "Load Food";

    Label inputFilePathLabel = new Label(inputFilePathLabelText); 
    inputFilePathLabel.setFont(Font.font(inputFilePathLabel.getFont().toString(),
        FontWeight.BOLD,14));
    inputFilePathLabel.setUnderline(true);

    Button loadFoodButton = new Button(loadFoodButtonText);
    loadFoodButton.setOnAction(a -> {
      FileChooser chooseFile = new FileChooser();
      chooseFile.setTitle("Open Food File");
      chooseFile.getExtensionFilters().addAll(
          new ExtensionFilter("CSV Files", "*.csv"),
          new ExtensionFilter("Text Files", "*.txt"),
          new ExtensionFilter("All Files","*.*"));
      File selectedFile = chooseFile.showOpenDialog(primaryStage);
      if (selectedFile != null) {
        obsFoodList.removeAll(obsFoodList);
        obsMealList.removeAll(obsMealList);
        mealList.clear();
        mealList = new ArrayList<FoodItem>();
        food = new FoodData(); 
        food.loadFoodItems(selectedFile.getAbsolutePath());
        foodList = food.getAllFoodItems();
        obsFoodList = FXCollections.observableArrayList(foodList);
        foodTable.setItems(obsFoodList);
      }
    });

    // combine into Vbox
    loadFoodPane.getChildren().addAll(inputFilePathLabel, loadFoodButton);

    return loadFoodPane;
  }

  /**
   * creates Node for middle left pane for saving food data to file
   * @return - Node of middle left pane for saving food data to file
   */
  private Node createSaveFoodPane() {
    VBox saveFoodPane = new VBox();
    saveFoodPane.setSpacing(10);

    String saveFoodListText = "Save Food";

    Button saveFoodFileButton = new Button(saveFoodListText);

    saveFoodFileButton.setOnAction(e -> {
      FileChooser saveFile = new FileChooser();
      saveFile.setTitle("Save Food File");
      saveFile.getExtensionFilters().addAll(
          new ExtensionFilter("CSV Files", "*.csv"),
          new ExtensionFilter("Text Files", "*.txt"),
          new ExtensionFilter("All Files","*.*"));
      File selectedFile = saveFile.showSaveDialog(primaryStage);
      if (selectedFile != null) {
        food.saveFoodItems(selectedFile.getAbsolutePath());
      }
    });

    Label saveFoodFileLabel = new Label(saveFoodListText); 
    saveFoodFileLabel.setFont(Font.font(saveFoodFileLabel.getFont().toString(),
        FontWeight.BOLD,14));
    saveFoodFileLabel.setUnderline(true);

    // combine into Vbox
    saveFoodPane.getChildren().addAll(saveFoodFileLabel, saveFoodFileButton);

    return saveFoodPane;
  }

  /**
   * creates Node for bottom left pane for manually adding a food item
   * @return - Node of middle left pane for manually adding a food item
   */
  private Node createAddFoodPane() {
    VBox addFoodPane = new VBox();
    addFoodPane.setSpacing(10);

    String addFoodString = "Add Food to List";
    Label addFoodLabel = new Label(addFoodString);
    addFoodLabel.setFont(Font.font(addFoodLabel.getFont().toString(),FontWeight.BOLD,
        14));
    addFoodLabel.setUnderline(true);

    //labels and text fields for adding food
    Label idLabel = new Label("ID");
    TextField idField = new TextField();
    Label nameLabel = new Label("Name");
    TextField nameField = new TextField();
    Label calLabel = new Label("Calories");
    TextField calField = new TextField();
    Label fatLabel = new Label("Fat");
    TextField fatField = new TextField();
    Label carbLabel = new Label("Carbohydrates");
    TextField carbField = new TextField();
    Label proteinLabel = new Label("Protein");
    TextField proteinField = new TextField();
    Label fiberLabel = new Label("Fiber");
    TextField fiberField = new TextField();
    Button addFoodButton = new Button("Add Food");

    //grid for adding food
    GridPane foodPane = new GridPane();
    foodPane.setGridLinesVisible(false);
    foodPane.setAlignment(Pos.CENTER);
    foodPane.setHgap(5);
    foodPane.setVgap(5);
    foodPane.setPadding(new Insets(10));

    // add labels and fields to gridpane
    int row = 0; int col = 0;
    foodPane.add(idLabel, col, row); col++;
    foodPane.add(idField, col, row); row++; col = 0;
    foodPane.add(nameLabel, col, row); col++;
    foodPane.add(nameField, col, row); row++; col = 0;
    foodPane.add(calLabel, col, row); col++;
    foodPane.add(calField, col, row); row++; col = 0;
    foodPane.add(fatLabel, col, row); col++;
    foodPane.add(fatField, col, row); row++; col = 0;
    foodPane.add(carbLabel, col, row); col++;
    foodPane.add(carbField, col, row); row++; col = 0;
    foodPane.add(fiberLabel, col, row); col++;
    foodPane.add(fiberField, col, row); row++; col = 0;
    foodPane.add(proteinLabel, col, row); col++;
    foodPane.add(proteinField, col, row); row++; col = 0;

    addFoodButton.setOnAction(a -> {
      boolean success = createFoodItem(idField.getText(),nameField.getText(),
          calField.getText(),fatField.getText(),carbField.getText(),proteinField.getText(),
          fiberField.getText());
      foodList = food.getAllFoodItems();
      obsFoodList = FXCollections.observableArrayList(foodList);
      foodTable.setItems(obsFoodList);
      foodTable.refresh();
      if (success) {
        idField.clear();
        nameField.clear();
        calField.clear();
        fatField.clear();
        carbField.clear();
        proteinField.clear();
        fiberField.clear();
      }

    });



    //combing separate VBox for left side
    addFoodPane.getChildren().addAll(addFoodLabel, foodPane, addFoodButton);

    return addFoodPane;
  }

  /**
   * creates Node for middle left pane
   * @return - Node of middle left pane
   */
  private Node createCenterPane() {
    VBox centerPane = new VBox();
    centerPane.setPadding(new Insets(10, 0, 0, 10));
    centerPane.setSpacing(5);
    
    foodList = food.getAllFoodItems();
    obsFoodList = FXCollections.observableArrayList(foodList);
    foodTable.setItems(obsFoodList);
  
    String availableFoodsString = "All Available Foods (" + obsFoodList.size() + ")";

    availableFoodsLabel = new Label(availableFoodsString);
    availableFoodsLabel.setFont(Font.font(availableFoodsLabel.getFont().toString(),
        FontWeight.BOLD,14));
    availableFoodsLabel.setUnderline(true);

    String mealString = "Meal to Analyze";
    Label mealLabel = new Label(mealString);
    mealLabel.setFont(Font.font(mealLabel.getFont().toString(),FontWeight.BOLD,14));
    mealLabel.setUnderline(true);

    // food list
    TableColumn<FoodItem,String> nameColumn = new TableColumn<FoodItem,String>("Name");
    nameColumn.setCellValueFactory(nameData -> new ReadOnlyStringWrapper(
        nameData.getValue().getName()));

    TableColumn<FoodItem,String> caloriesColumn = new TableColumn<FoodItem,String> ("Calories");
    caloriesColumn.setCellValueFactory(calData -> new ReadOnlyStringWrapper(
        (new Double(calData.getValue().getNutrientValue(NutrientsEnum.CALORIES.toString()))).toString()));

    TableColumn<FoodItem,String> fatColumn = new TableColumn<FoodItem,String> ("Fat");
    fatColumn.setCellValueFactory(fatData -> new ReadOnlyStringWrapper(
        (new Double(fatData.getValue().getNutrientValue(NutrientsEnum.FAT.toString()))).toString()));

    TableColumn<FoodItem,String> carbohydratesColumn = new TableColumn<FoodItem,String> ("Carbohydrates");
    carbohydratesColumn.setCellValueFactory(carbData -> new ReadOnlyStringWrapper(
        (new Double(carbData.getValue().getNutrientValue(NutrientsEnum.CARBOHYDRATE.toString()))).toString()));

    TableColumn<FoodItem,String> fiberColumn = new TableColumn<FoodItem,String> ("Fiber");
    fiberColumn.setCellValueFactory(fiberData -> new ReadOnlyStringWrapper(
        (new Double(fiberData.getValue().getNutrientValue(NutrientsEnum.FIBER.toString()))).toString()));
    
    TableColumn<FoodItem,String> proteinColumn = new TableColumn<FoodItem,String> ("Protein");
    proteinColumn.setCellValueFactory(proData -> new ReadOnlyStringWrapper(
        (new Double(proData.getValue().getNutrientValue(NutrientsEnum.PROTEIN.toString()))).toString()));

    foodTable.getColumns().setAll(nameColumn, caloriesColumn, fatColumn, 
        carbohydratesColumn, fiberColumn, proteinColumn);
    foodTable.setColumnResizePolicy(foodTable.CONSTRAINED_RESIZE_POLICY);

    // add and remove buttons
    GridPane buttonPane = new GridPane();
    buttonPane.setGridLinesVisible(false);
    buttonPane.setAlignment(Pos.CENTER);
    buttonPane.setHgap(20);

    Image addFoodToMealImage = new Image(new File("addFoodToMealIcon.png").
        toURI().toString(), 40, 40, true, true);
    Image removeFoodFromMealImage = new Image(new File("removeFoodFromMealIcon.png").
        toURI().toString(), 40, 40, true, true);

    // Analyze Meal Button
    String analyzeMealButtonText = "Analyze Meal";
    Button analyzeMealButton = new Button(analyzeMealButtonText);

    analyzeMealButton.setOnAction(e -> { 
      Stage popupStage = new Stage();
      popupStage.initModality(Modality.APPLICATION_MODAL);
      popupStage.initOwner(primaryStage);
      popupStage.setTitle("Meal Analysis");
      Node mealPopup = createMealPopup(mealList);
      Scene popupScene = new Scene((Parent) mealPopup, 300, 200);
      popupStage.setScene(popupScene);
      popupStage.show();
    });

    // Add Food to Meal Button
    Button addFoodToMeal = new Button();
    addFoodToMeal.setTooltip(new Tooltip("Add Food from Food List to Meal List"));
    addFoodToMeal.setMinWidth(40);
    addFoodToMeal.setGraphic(new ImageView(addFoodToMealImage));

    addFoodToMeal.setOnAction(a -> {
      FoodItem selectedFood = foodTable.getSelectionModel().getSelectedItem();
      if (selectedFood != null) {
        mealTable.getItems().add(selectedFood);
        mealList.add(selectedFood);
        mealList = mealList.stream()
            .sorted((food1, food2) -> food1.getName().toLowerCase().compareTo(food2.getName().toLowerCase()))
            .collect(Collectors.toList());
        obsMealList = FXCollections.observableArrayList(mealList);
        mealTable.setItems(obsMealList);
        mealTable.refresh();
      }
    });

    // Remove Food from Meal Button
    Button removeFoodFromMeal = new Button();
    removeFoodFromMeal.setTooltip(new Tooltip("Remove Food from Meal List"));
    removeFoodFromMeal.setMinWidth(40);
    removeFoodFromMeal.setGraphic(new ImageView(removeFoodFromMealImage));

    removeFoodFromMeal.setOnAction(a -> {
      FoodItem selectedFood = mealTable.getSelectionModel().getSelectedItem();
      if (selectedFood != null) {
        mealTable.getItems().remove(selectedFood);
        mealList.remove(selectedFood);
        mealTable.refresh();
      }
    });

    // Clear Food from Meal
    Button clearFoodFromMeal = new Button("Clear Meal");
    clearFoodFromMeal.setTooltip(new Tooltip("Clear All Food from Meal List"));
    clearFoodFromMeal.setMinWidth(80);

    clearFoodFromMeal.setOnAction(a -> {
      mealList.clear();
      mealList = new ArrayList<FoodItem>();
      obsMealList = FXCollections.observableArrayList(mealList);
      mealTable.setItems(obsMealList);
    });

    buttonPane.add(analyzeMealButton, 0, 0);
    buttonPane.add(addFoodToMeal, 1, 0);
    buttonPane.add(removeFoodFromMeal, 2, 0);
    buttonPane.add(clearFoodFromMeal, 3, 0);

    // meal list
    obsMealList = FXCollections.observableArrayList(mealList);
    mealTable.setItems(obsMealList);

    TableColumn<FoodItem,String> mealNameColumn = new TableColumn<FoodItem,String>("Name");
    mealNameColumn.setCellValueFactory(nameData -> new ReadOnlyStringWrapper(
        nameData.getValue().getName()));

    TableColumn<FoodItem,String> mealCaloriesColumn = new TableColumn<FoodItem,String> ("Calories");
    mealCaloriesColumn.setCellValueFactory(calData -> new ReadOnlyStringWrapper(
        (new Double(calData.getValue().getNutrientValue(NutrientsEnum.CALORIES.toString()))).toString()));

    TableColumn<FoodItem,String> mealFatColumn = new TableColumn<FoodItem,String> ("Fat");
    mealFatColumn.setCellValueFactory(fatData -> new ReadOnlyStringWrapper(
        (new Double(fatData.getValue().getNutrientValue(NutrientsEnum.FAT.toString()))).toString()));

    TableColumn<FoodItem,String> mealCarbohydratesColumn = new TableColumn<FoodItem,String> ("Carbohydrates");
    mealCarbohydratesColumn.setCellValueFactory(carbData -> new ReadOnlyStringWrapper(
        (new Double(carbData.getValue().getNutrientValue(NutrientsEnum.CARBOHYDRATE.toString()))).toString()));

    TableColumn<FoodItem,String> mealFiberColumn = new TableColumn<FoodItem,String> ("Fiber");
    mealFiberColumn.setCellValueFactory(fiberData -> new ReadOnlyStringWrapper(
        (new Double(fiberData.getValue().getNutrientValue(NutrientsEnum.FIBER.toString()))).toString()));
    
    TableColumn<FoodItem,String> mealProteinColumn = new TableColumn<FoodItem,String> ("Protein");
    mealProteinColumn.setCellValueFactory(proData -> new ReadOnlyStringWrapper(
        (new Double(proData.getValue().getNutrientValue(NutrientsEnum.PROTEIN.toString()))).toString()));

    mealTable.getColumns().addAll(mealNameColumn, mealCaloriesColumn, mealFatColumn,
        mealCarbohydratesColumn, mealFiberColumn, mealProteinColumn);
    mealTable.setColumnResizePolicy(foodTable.CONSTRAINED_RESIZE_POLICY);

    centerPane.getChildren().addAll(availableFoodsLabel,foodTable, buttonPane, 
        mealLabel, mealTable);		

    return centerPane;
  }

  /**
   * creates VBox for right pane
   * @return - VBox of right pane
   */
  private Node createRightPane() {
    VBox rightPane = new VBox();
    rightPane.setAlignment(Pos.TOP_RIGHT);
    rightPane.setPadding(new Insets(10));
    rightPane.setSpacing(75);

    // initialize strings
    String filterLabelText = "Filters";
    String nameFilterLabelText = "Name Filter";
    String nameFilterInputBoxText = "Enter name filter";
    String nutrientFilterLabelText = "Nutrient Filter";
    String nutrientFilterInputBoxText = "Enter nutrient filter";
    String activeFiltersText = "Active Filters";
    String removeFilterButtonText = "Remove Filter";


    // create control objects
    // filters label
    Label filterLabel = new Label(filterLabelText);
    filterLabel.setFont(Font.font(filterLabel.getFont().toString(),FontWeight.BOLD,14));
    filterLabel.setUnderline(true);

    Label activeFiltersLabel = new Label(activeFiltersText);
    activeFiltersLabel.setFont(Font.font(activeFiltersLabel.getFont().toString(),
        FontWeight.BOLD,14));
    activeFiltersLabel.setUnderline(true);

    // Name filter
    Label nameFilterLabel = new Label(nameFilterLabelText);
    nameFilterLabel.setPrefWidth(100);
    TextField nameFilterInputBox = new TextField();
    nameFilterInputBox.setPromptText(nameFilterInputBoxText);
    Button addNameFilterButton = new Button("Add Name Filter");

    // Nutrient filter
    Label nutrientFilterLabel = new Label(nutrientFilterLabelText);
    nutrientFilterLabel.setPrefWidth(100);
    TextField nutrientFilterInputBox = new TextField();
    nutrientFilterInputBox.setPromptText(nutrientFilterInputBoxText);

    // Add Filter Button
    Button addNutrientFilterButton = new Button("Add Nutrient Filter");

    //Remove Filter Button
    Button removeFilterButton = new Button(removeFilterButtonText);

    // filters list view
    ListView<String> filtersList  = new ListView<String>();
    filtersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    filtersList.setFixedCellSize(30);

    ArrayList<String> nutrientFilterList = new ArrayList<String>();

    addNameFilterButton.setOnAction(a -> {
      if (!nameFilterInputBox.getText().trim().equals("")){
        String nameFilterText=nameFilterInputBox.getText();
        if (oldNameFilter != null){
          filtersList.getItems().remove("Name Filter: " + oldNameFilter);
          obsFoodList = FXCollections.observableArrayList(foodList);
          obsFoodList.retainAll(food.filterByNutrients(nutrientFilterList));
        }
        filtersList.getItems().add("Name Filter: "+nameFilterText);

        obsFoodList.retainAll(food.filterByName(nameFilterText));
        foodTable.setItems(obsFoodList);
        foodTable.refresh();
        nameFilterInputBox.clear();
        oldNameFilter = nameFilterText;
        
        availableFoodsLabel.setText("All Available Foods (" + obsFoodList.size() + ")");
      }
      
      else {
        nameFilterInputBox.clear();
      }
    });

    addNutrientFilterButton.setOnAction(a -> {
      if (isValidNutrientFilter(nutrientFilterInputBox.getText())){
        String nutrientFilter = nutrientFilterInputBox.getText();
        filtersList.getItems().add(nutrientFilter);
        nutrientFilterList.add(nutrientFilter);
        obsFoodList.retainAll(food.filterByNutrients(nutrientFilterList));
        nutrientFilterInputBox.clear();
        foodTable.setItems(obsFoodList);
        foodTable.refresh();
        
        availableFoodsLabel.setText("All Available Foods (" + obsFoodList.size() + ")");
      }
      
      else {
        nutrientFilterInputBox.clear();
        Alert negative = new Alert(AlertType.WARNING, "Filter format must be "
            + "[nutrient] [comparator] [value].");
        negative.showAndWait().filter(response -> response == ButtonType.OK);
      }
    });

    removeFilterButton.setOnAction(a -> {
      ObservableList<String> selectedList = filtersList.getSelectionModel().getSelectedItems();
      filtersList.getItems().removeAll(selectedList);

      oldNameFilter = null;
      nutrientFilterList.clear();

      obsFoodList = FXCollections.observableArrayList(foodList);

      for (String row : filtersList.getItems()) {
        if (row.contains("Name Filter:")){
          String[] FilterPcs = row.split(" ");
          oldNameFilter = FilterPcs[2];
        }
        else {
          nutrientFilterList.add(row);
        }
      }

      if (oldNameFilter!= null) obsFoodList.retainAll(food.filterByName(oldNameFilter));
      obsFoodList.retainAll(food.filterByNutrients(nutrientFilterList));        
      
      availableFoodsLabel.setText("All Available Foods (" + obsFoodList.size() + ")");

      foodTable.setItems(obsFoodList);
      foodTable.refresh();
    }
        );

    // create child layout containers
    HBox nameFilterHbox = new HBox(10, nameFilterLabel, nameFilterInputBox);
    nameFilterHbox.setAlignment(Pos.CENTER_LEFT);

    HBox nutFilterHbox = new HBox(10, nutrientFilterLabel, nutrientFilterInputBox);
    nutFilterHbox.setAlignment(Pos.CENTER_LEFT);

    // Create parent layout container
    VBox rightTopVbox = new VBox(10, filterLabel, nameFilterHbox,addNameFilterButton,
        nutFilterHbox, addNutrientFilterButton);
    VBox rightBottomVbox = new VBox(10,activeFiltersLabel, filtersList, 
        removeFilterButton);

    rightPane.getChildren().addAll(rightTopVbox, rightBottomVbox);

    return rightPane;
  }

  /**
   * Evaluates one nutrient filter string to determine if it is valid
   * @param S - string representing the nutrient filter
   * @return true if filter string is valid, false otherwise
   */
  private boolean isValidNutrientFilter(String S) {
    if (S == null) return false;

    String del = " ";

    String[] FilterPcs = S.split(del);
    if (FilterPcs.length != 3) return false;

    //check that nutrient name is valid
    String nutrientName = FilterPcs[0].toLowerCase();
    boolean isValidNutrient = false;
    for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
      if(nutrient.toString().equals(nutrientName)) {
        isValidNutrient = true;
        break;
      }
    }
    if(!isValidNutrient) {return false;}

    //check that comparator is valid
    String comparator = FilterPcs[1];
    String [] comparators = {"==","<=",">="};
    boolean isValidComparator = false;
    for(String currentComp : comparators) {
      if(currentComp.equals(comparator)) {
        isValidComparator = true;
        break;
      }
    }
    if(!isValidComparator) {return false;}

    //check that value is valid
    try {
      Double nutrientVal = Double.parseDouble(FilterPcs[2]);
      if (nutrientVal < 0.0d) return false;
    } catch (Exception E) {
      return false;
    }

    // all checks passed
    return true;
  }


  private boolean createFoodItem(String ID, String name, String calories,
      String fat, String carbs,String protein,String fiber) {

    Double doubleCalories = null;
    Double doubleFat = null;
    Double doubleCarbs = null;
    Double doubleProtein = null;
    Double doubleFiber = null;
    if (ID.equals("") || name.equals("")) {
      Alert negative = new Alert(AlertType.WARNING, "ID and Name fields must be populated.");
      negative.showAndWait().filter(response -> response == ButtonType.OK);
      return false;
    }
    
    try {
      doubleCalories=Double.parseDouble(calories);
      doubleFat=Double.parseDouble(fat);
      doubleCarbs=Double.parseDouble(carbs);
      doubleProtein=Double.parseDouble(protein);
      doubleFiber=Double.parseDouble(fiber);
      if (doubleCalories < 0 || doubleFat < 0 || doubleCarbs < 0 || doubleProtein < 0 || doubleFiber < 0) {
        Alert negative = new Alert(AlertType.WARNING, "All nutrient values must be positive.");
        negative.showAndWait().filter(response -> response == ButtonType.OK);
        return false;
      }
    }
    catch(Exception e) {
      Alert badFood = new Alert(AlertType.WARNING, "Invalid nutrient information entered.");
      badFood.showAndWait().filter(response -> response == ButtonType.OK);
      return false; 
    }

    if (doubleCalories!=null && doubleFat!=null && doubleCarbs!=null 
        && doubleProtein!=null && doubleFiber!=null) {
      FoodItem foodItemObj = new FoodItem(ID, name);
      foodItemObj.addNutrient("calories",doubleCalories);
      foodItemObj.addNutrient("fat",doubleFat);
      foodItemObj.addNutrient("carbohydrate",doubleCarbs);
      foodItemObj.addNutrient("protein",doubleProtein);
      foodItemObj.addNutrient("fiber",doubleFiber);
      food.addFoodItem(foodItemObj);
      return true;

    }
    return false; 

  }


  private Node createMealPopup(List<FoodItem> meal) {
    VBox mealPopup = new VBox();

    //grid for analyzing meal
    GridPane analyzeMealPane = new GridPane();
    analyzeMealPane.setGridLinesVisible(false);
    analyzeMealPane.setAlignment(Pos.CENTER);
    analyzeMealPane.setHgap(5);
    analyzeMealPane.setVgap(5);
    analyzeMealPane.setPadding(new Insets(10));

    Label totalsLabel = new Label("Totals");
    totalsLabel.setFont(Font.font(totalsLabel.getFont().toString(),FontWeight.BOLD,14));
    totalsLabel.setUnderline(true);
    mealPopup.getChildren().add(totalsLabel);

    // total values for each food in meal, and add to grid pane
    int row = 0;
    for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
      Double nutrientTotal = new Double(0);
      for(FoodItem foodItemObj : meal) {
        nutrientTotal = nutrientTotal + foodItemObj.getNutrientValue(
            nutrient.toString());
      }

      Label nutrientLabel = new Label(nutrient.toString());
      Label nutrientVal = new Label(nutrientTotal.toString());
      analyzeMealPane.add(nutrientLabel, 0, row);
      analyzeMealPane.add(nutrientVal, 1, row);
      row++;
    }

    mealPopup.getChildren().add(analyzeMealPane);

    return mealPopup;
  }


  public static void main(String[] args) {
    launch(args);
  }
}

