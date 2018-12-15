package application;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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


public class Main extends Application {
	private FoodData food;
	private String oldNameFilter;

	@Override
	public void start(Stage primaryStage) {
		food = new FoodData();
		food.loadFoodItems("foodItems.csv");

		try {

			BorderPane rootContainer = new BorderPane();
			Scene scene = new Scene(rootContainer,1500,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			String windowTitle = "Meal Planner";
			primaryStage.setTitle(windowTitle);

			// top left pane
			String inputFilePathLabelText = "Load Foods From a File";
			String inputFilePathInputBoxText = "Enter file path";
			String loadFoodButtonText = "Load Food";

			TextField inputFilePathInputBox = new TextField();
			inputFilePathInputBox.setPromptText(inputFilePathInputBoxText);

			Label inputFilePathLabel = new Label(inputFilePathLabelText); 
			inputFilePathLabel.setFont(Font.font(inputFilePathLabel.getFont().toString(),FontWeight.BOLD,14));
			inputFilePathLabel.setUnderline(true);

			String filePathInputBoxInputtedText = inputFilePathInputBox.getText();
			Button loadFoodButton = new Button(loadFoodButtonText);

			//middle left
			String saveFoodListText = "Save Foods to File";
			TextField filePathSaveBox = new TextField();
			filePathSaveBox.setPromptText(inputFilePathInputBoxText);
			Button saveFoodFileButton = new Button(saveFoodListText);
			String filePathSaveInputPath = filePathSaveBox.getText();
			saveFoodFileButton.setOnAction(e -> {food.saveFoodItems(filePathSaveInputPath);});

			Label saveFoodFileLabel = new Label(saveFoodListText); 

			final FileChooser fileChooser = new FileChooser();


			//String filePathInputBoxText = new String;
			saveFoodFileLabel.setFont(Font.font(saveFoodFileLabel.getFont().toString(),FontWeight.BOLD,14));
			saveFoodFileLabel.setUnderline(true);


			//labels and text fields for adding food
			//bottom left
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

			String addFoodString = "Add Food to List";
			Label addFoodLabel = new Label(addFoodString);
			addFoodLabel.setFont(Font.font(addFoodLabel.getFont().toString(),FontWeight.BOLD,14));
			addFoodLabel.setUnderline(true);

			addFoodButton.setOnAction(a -> {createFoodItem(idField.getText(),nameField.getText(),
					calField.getText(),fatField.getText(),carbField.getText(),proteinField.getText(),
					fiberField.getText());});

			//grid for adding food
			GridPane foodPane = new GridPane();
			foodPane.setGridLinesVisible(false);
			foodPane.setAlignment(Pos.CENTER);
			foodPane.add(idLabel, 0, 0);
			foodPane.add(idField, 1, 0);
			foodPane.add(nameLabel, 0, 1);  
			foodPane.add(nameField, 1, 1);  
			foodPane.add(calLabel, 0, 2);  
			foodPane.add(calField, 1, 2);
			foodPane.add(fatLabel, 0, 3); 
			foodPane.add(fatField, 1, 3);
			foodPane.add(carbLabel, 0, 4); 
			foodPane.add(carbField, 1, 4);
			foodPane.add(proteinLabel, 0, 5);  
			foodPane.add(proteinField, 1, 5);
			foodPane.add(fiberLabel, 0, 6);  
			foodPane.add(fiberField, 1, 6);
			foodPane.setHgap(5);
			foodPane.setVgap(5);
			foodPane.setPadding(new Insets(10));

			//top left pane combined into Vbox
			VBox topLeftPane = new VBox(10,inputFilePathLabel, inputFilePathInputBox, loadFoodButton);
			VBox middleLeftPane = new VBox(10,saveFoodFileLabel, filePathSaveBox, saveFoodFileButton);
			//bottom left pane combined into Vbox
			VBox bottomLeftPane = new VBox(10,addFoodLabel,foodPane,addFoodButton);

			//combing separate VBox for left side
			VBox leftPane = new VBox(110,topLeftPane,middleLeftPane,bottomLeftPane);
			leftPane.setAlignment(Pos.TOP_LEFT);
			leftPane.setPadding(new Insets(10));
			rootContainer.setLeft(leftPane);


			// center pane
			VBox vBox = new VBox();

			//add text labels
			String availableFoodsString = "All Available Foods";
			Label availableFoodsLabel = new Label(availableFoodsString);
			availableFoodsLabel.setFont(Font.font(availableFoodsLabel.getFont().toString(),FontWeight.BOLD,14));
			availableFoodsLabel.setUnderline(true);

			String mealString = "Meal to Analyze";
			Label mealLabel = new Label(mealString);
			mealLabel.setFont(Font.font(mealLabel.getFont().toString(),FontWeight.BOLD,14));
			mealLabel.setUnderline(true);

			// food list
			TableView foodTable = new TableView();

			TableColumn nameColumn = new TableColumn("Name");
			TableColumn caloriesColumn = new TableColumn("Calories");
			TableColumn fatColumn = new TableColumn("Fat");
			TableColumn carbohydratesColumn = new TableColumn("Carbohydrates");
			TableColumn proteinColumn = new TableColumn("Protein");
			TableColumn fiberColumn = new TableColumn("Fiber");

			foodTable.getColumns().addAll(nameColumn, caloriesColumn, fatColumn,
					carbohydratesColumn, proteinColumn, fiberColumn);
			foodTable.setColumnResizePolicy(foodTable.CONSTRAINED_RESIZE_POLICY);

			// add and remove buttons
			GridPane buttonPane = new GridPane();
			buttonPane.setGridLinesVisible(false);
			buttonPane.setAlignment(Pos.CENTER);

			Image addFoodToMealImage = new Image(new File("addFoodToMealIcon.png").toURI().toString(), 40, 40, true, true);
			Image removeFoodFromMealImage = new Image(new File("removeFoodFromMealIcon.png").toURI().toString(), 40, 40, true, true);

			Button addFoodToMeal = new Button();
			addFoodToMeal.setTooltip(new Tooltip("Add Food from Food List to Meal List"));
			addFoodToMeal.setMinWidth(40);
			addFoodToMeal.setGraphic(new ImageView(addFoodToMealImage));

			Button removeFoodFromMeal = new Button();
			removeFoodFromMeal.setTooltip(new Tooltip("Remove Food from Meal List"));
			removeFoodFromMeal.setMinWidth(40);
			removeFoodFromMeal.setGraphic(new ImageView(removeFoodFromMealImage));

			buttonPane.add(addFoodToMeal, 0, 0);
			buttonPane.add(removeFoodFromMeal, 1, 0);
			buttonPane.setHgap(20);

			// meal list
			TableView mealTable = new TableView();
			mealTable.getColumns().addAll(nameColumn, caloriesColumn, fatColumn,
					carbohydratesColumn, proteinColumn, fiberColumn);

			mealTable.setColumnResizePolicy(foodTable.CONSTRAINED_RESIZE_POLICY);
			
			
			addFoodToMeal.setOnAction(a -> {
				int selectedIdx = foodTable.getSelectionModel().getSelectedIndex();
				mealTable.getItems().add(selectedIdx);
			});
			
			
			removeFoodFromMeal.setOnAction(a -> {
				int selectedIdx = foodTable.getSelectionModel().getSelectedIndex();
				mealTable.getItems().remove(selectedIdx);
			});
			
			
			

			vBox.getChildren().addAll(availableFoodsLabel,foodTable, buttonPane, mealLabel, mealTable);
			vBox.setPadding(new Insets(10, 0, 0, 10));
			vBox.setSpacing(5);
			rootContainer.setCenter(vBox);


			// right pane
			// initialize strings
			String filterLabelText = "Filters";
			String nameFilterLabelText = "Name Filter";
			String nameFilterInputBoxText = "Enter name filter";
			String nutrientFilterLabelText = "Nutrient Filter";
			String nutrientFilterInputBoxText = "Enter nutrient filter";

			String activeFiltersText = "Active Filters";
			String removeFilterButtonText = "Remove Filter";
			String analyzeMealButtonText = "Analyze Meal";

			// create control objects
			// filters label
			Label filterLabel = new Label(filterLabelText);
			filterLabel.setFont(Font.font(filterLabel.getFont().toString(),FontWeight.BOLD,14));
			filterLabel.setUnderline(true);

			Label activeFiltersLabel = new Label(activeFiltersText);
			activeFiltersLabel.setFont(Font.font(activeFiltersLabel.getFont().toString(),FontWeight.BOLD,14));
			activeFiltersLabel.setUnderline(true);

			// Name filter
			Label nameFilterLabel = new Label(nameFilterLabelText);
			nameFilterLabel.setPrefWidth(100);
			TextField nameFilterInputBox = new TextField();
			nameFilterInputBox.setPromptText(nameFilterInputBoxText);
			//String nameFilter = nameFilterInputBox.getText();
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

			// Analyze Meal Button
			Button analyzeMealButton = new Button(analyzeMealButtonText);

			// filters list view
			ListView<String> filtersList  = new ListView<String>();
			filtersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

			addNameFilterButton.setOnAction(a -> {
				if (!nameFilterInputBox.getText().equals("")){
					String nameFilterText=nameFilterInputBox.getText();
					//System.out.println(oldNameFilter.isEmpty());
					if (oldNameFilter!=null){
						System.out.println(oldNameFilter);
						filtersList.getItems().remove("Name Filter: "+oldNameFilter);
					}
					filtersList.getItems().add("Name Filter: "+nameFilterText);
					food.filterByName(nameFilterText);	
					nameFilterInputBox.clear();
					oldNameFilter=nameFilterText;
				}
			});

			ArrayList<String> nutrientFilterList = new ArrayList<String>(); 

			addNutrientFilterButton.setOnAction(a -> {
				if (isValidNutrientFilter(nutrientFilterInputBox.getText())){
					String nutrientFilter = nutrientFilterInputBox.getText();
					filtersList.getItems().add(nutrientFilter);
					nutrientFilterList.add(nutrientFilter);
					food.filterByNutrients(nutrientFilterList);
					nutrientFilterInputBox.clear();
				}
			});

			filtersList.setFixedCellSize(30);

			removeFilterButton.setOnAction(a -> {
				ObservableList<String> selectedList = filtersList.getSelectionModel().getSelectedItems();
				filtersList.getItems().removeAll(selectedList);
			}
					);

			// create child layout containers
			HBox nameFilterHbox = new HBox(10, nameFilterLabel, nameFilterInputBox);
			nameFilterHbox.setAlignment(Pos.CENTER_LEFT);

			HBox nutFilterHbox = new HBox(10, nutrientFilterLabel, nutrientFilterInputBox);
			nutFilterHbox.setAlignment(Pos.CENTER_LEFT);


			// Create parent layout container
			VBox rightTopVbox = new VBox(10, filterLabel, nameFilterHbox,addNameFilterButton, nutFilterHbox, addNutrientFilterButton);
			VBox rightBottomVbox = new VBox(10,activeFiltersLabel, filtersList, removeFilterButton, analyzeMealButton);
			VBox rightPane = new VBox(75,rightTopVbox,rightBottomVbox);
			rightPane.setAlignment(Pos.TOP_RIGHT);
			rightPane.setPadding(new Insets(10));
			rootContainer.setRight(rightPane);


			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}//start
	
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
		String nutrientName = FilterPcs[0];
		boolean isValidNutrient = false;
        for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
    		if(nutrient.toString().equals(FilterPcs[0].toLowerCase())) {
    			isValidNutrient = true;
    			nutrientName = FilterPcs[0].toLowerCase();
    			break;
    		}
        }
        if(!isValidNutrient) {return false;}
		
		//check that comparator is valid
		String comparator = FilterPcs[1];
		String [] comparators = {"==","<=",">="};
        boolean isValidComparator = false;
        for(String currentComp : comparators) {
        	if(currentComp.equals(FilterPcs[1])) {
        		comparator = FilterPcs[1];
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

	private void createFoodItem(String ID, String name, String calories,
			String fat, String carbs,String protein,String fiber) {

		Double doubleCalories = null;
		Double doubleFat = null;
		Double doubleCarbs = null;
		Double doubleProtein = null;
		Double doubleFiber = null;

		try {
			doubleCalories=Double.parseDouble(calories);
			doubleFat=Double.parseDouble(fat);
			doubleCarbs=Double.parseDouble(carbs);
			doubleProtein=Double.parseDouble(protein);
			doubleFiber=Double.parseDouble(fiber);
		}
		catch(Exception e) {
			Alert badFood = new Alert(AlertType.WARNING, "Please review your food");
			badFood.showAndWait().filter(response -> response == ButtonType.OK);
		}

		if (doubleCalories!=null && doubleFat!=null && doubleCarbs!=null 
				&& doubleProtein!=null && doubleFiber!=null) {
			FoodItem foodItemObj = new FoodItem(ID, name);
			foodItemObj.addNutrient("calories",doubleCalories);
			foodItemObj.addNutrient("fat",doubleFat);
			foodItemObj.addNutrient("carbohydrate",doubleCarbs);
			foodItemObj.addNutrient("protein",doubleProtein);
			foodItemObj.addNutrient("fiber",doubleFiber);
			//System.out.println(ID);
			//System.out.println(name);
			//System.out.println(food);
			//System.out.println(foodItemObj);
			food.addFoodItem(foodItemObj);
			//System.out.println(foodItemObj);

		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

