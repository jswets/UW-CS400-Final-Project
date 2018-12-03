package application;
	
import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane rootContainer = new BorderPane();
			Scene scene = new Scene(rootContainer,1500,900);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			String windowTitle = "Meal Planner";
			primaryStage.setTitle(windowTitle);
			
			rootContainer.setLeft(createLeftPane());
            rootContainer.setCenter(createCenterPane());
            rootContainer.setRight(createRightPane());
            
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private Pane createLeftPane() {
		// top left pane
        String filePathLabelText = "Load Foods From a File";
        String filePathInputBoxText = "Enter file path";
        String loadFoodButtonText = "Load Food";
        String addFoodString = "Add Food to List";
        Label filePathLabel = new Label(filePathLabelText);
        filePathLabel.setFont(Font.font(filePathLabel.getFont().toString(),FontWeight.BOLD,14));
        filePathLabel.setUnderline(true);
        
        
        TextField filePathInputBox = new TextField();
        
        filePathInputBox.setPromptText(filePathInputBoxText);
        Button loadFoodButton = new Button(loadFoodButtonText);
        Label addFoodLabel = new Label(addFoodString);
         
        addFoodLabel.setFont(Font.font(addFoodLabel.getFont().toString(),FontWeight.BOLD,14));
        addFoodLabel.setUnderline(true);
         
        //labels and text fields for adding food 
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
        foodPane.add(nameLabel, 0, 0);  
        foodPane.add(nameField, 1, 0);  
        foodPane.add(calLabel, 0, 1);  
        foodPane.add(calField, 1, 1);
        foodPane.add(fatLabel, 0, 2); 
        foodPane.add(fatField, 1, 2);
        foodPane.add(carbLabel, 0, 3); 
        foodPane.add(carbField, 1, 3);
        foodPane.add(proteinLabel, 0, 4);  
        foodPane.add(proteinField, 1, 4);
        foodPane.add(fiberLabel, 0, 5);  
        foodPane.add(fiberField, 1, 5);
        foodPane.setHgap(5);
        foodPane.setVgap(5);
        foodPane.setPadding(new Insets(10));
        
        //top left pane combined into Vbox
        VBox topLeftPane = new VBox(10,filePathLabel, filePathInputBox, loadFoodButton);
        //bottom left pane combined into Vbox
        VBox bottomLeftPane = new VBox(10,addFoodLabel,foodPane,addFoodButton);
        
        //combing separate VBox for left side
        VBox leftPane = new VBox(110,topLeftPane,bottomLeftPane);
        leftPane.setAlignment(Pos.TOP_LEFT);
        leftPane.setPadding(new Insets(10));
        
        return leftPane;
	}
	
	private Pane createCenterPane() {
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
        
        vBox.getChildren().addAll(availableFoodsLabel,foodTable, buttonPane, mealLabel, mealTable);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.setSpacing(5);
        
        return vBox;
	}
	
	private Pane createRightPane() {
		// initialize strings
		String filterLabelText = "Filters";
		String nameFilterLabelText = "Name Filter";
		String nameFilterInputBoxText = "Enter name filter";
		String nutrientFilterLabelText = "Nutrient Filter";
		String nutrientFilterInputBoxText = "Enter nutrient filter";
		String addFilterButtonText = "Add Filter";
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
		
		// Nutrient filter
		Label nutrientFilterLabel = new Label(nutrientFilterLabelText);
		nutrientFilterLabel.setPrefWidth(100);
		TextField nutrientFilterInputBox = new TextField();
		nutrientFilterInputBox.setPromptText(nutrientFilterInputBoxText);
		
		// Add Filter Button
		Button addFilterButton = new Button(addFilterButtonText);
		
		//Remove Filter Button
        Button removeFilterButton = new Button(removeFilterButtonText);
        
        // Analyze Meal Button
        Button analyzeMealButton = new Button(analyzeMealButtonText);
		
		// filters list view
        ListView<String> filtersList  = new ListView();
        filtersList.setFixedCellSize(30);
        filtersList.getItems().addAll("First Filter", "Second Filter", "Third Filter");
        
		
        // create child layout containers
        HBox nameFilterHbox = new HBox(10, nameFilterLabel, nameFilterInputBox);
        nameFilterHbox.setAlignment(Pos.CENTER_LEFT);
        
        HBox nutFilterHbox = new HBox(10, nutrientFilterLabel, nutrientFilterInputBox);
        nutFilterHbox.setAlignment(Pos.CENTER_LEFT);
        
        
        // Create parent layout container
        VBox rightTopVbox = new VBox(10, filterLabel, nameFilterHbox, nutFilterHbox, addFilterButton);
        VBox rightBottomVbox = new VBox(10,activeFiltersLabel, filtersList, removeFilterButton, analyzeMealButton);
        //rightVbox.setAlignment(Pos.CENTER);
        //rightVbox.setPadding(new Insets(30));
        VBox rightPane = new VBox(75,rightTopVbox,rightBottomVbox);
        rightPane.setAlignment(Pos.TOP_RIGHT);
        rightPane.setPadding(new Insets(10));
        
        return rightPane;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}