package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents the backend for managing all 
 * the operations associated with FoodItems
 * 
 * @author sapan (sapan@cs.wisc.edu)
 */
public class FoodData implements FoodDataADT<FoodItem> {
    
    // List of all the food items.
    private List<FoodItem> foodItemList;

    // Map of nutrients and their corresponding index
    private HashMap<String, BPTree<Double, FoodItem>> indexes;
    
    // BPTree for food ID index
    private BPTree<String, FoodItem> foodIDIx;
    
    /**
     * Public constructor
     */
    public FoodData() {
    	foodItemList = new ArrayList<FoodItem>();
    	    	
    	int BPTreeBranchFactor = 11;
    	foodIDIx = new BPTree<String, FoodItem>(BPTreeBranchFactor);
    	
    	indexes = new HashMap<String,BPTree<Double, FoodItem>>();
    	for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
    		indexes.put(nutrient.toString(), new BPTree<Double, FoodItem>(BPTreeBranchFactor));
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#loadFoodItems(java.lang.String)
     */
    @Override
    public void loadFoodItems(String filePath) {
    	if(filePath.equals(null) || filePath.equals("")) {return;}
    	
    	Stream<String> fileStream = null;
    	
		try {
			List<String> fileLineList = new ArrayList<String>();
			fileStream = Files.lines(Paths.get(filePath));
			fileLineList = fileStream.collect(Collectors.toList());
			
			for(String dataLine : fileLineList) {
				try {
					// parse data line
					/*
					 * <id>,<food_name>,<calories>,<calorie_count>,<fat>,<fat_grams>,<carbohydrate>,<carbohydrate_grams>,<fiber>,<fiber_grams>,<protein>,<protein_grams>,
					 * 0 - id
					 * 1 - name
					 * 2 - "calories"
					 * 3 - calories
					 * 4 - "fat"
					 * 5 - fat
					 * 6 - "carbohydrate"
					 * 7 - carbohydrate
					 * 8 - "fiber"
					 * 9 - fiber
					 * 10 - "protein"
					 * 11 - protein
					 */
					String del = ",";
					String[] dataLinePcs = dataLine.split(del);
					
					String id = new String (dataLinePcs[0]);
					String name = new String(dataLinePcs[1]);
					
					// create food item
					FoodItem foodItemObj = new FoodItem(id, name);
										
					// add info about each nutrient
					for(int strIx = 2; strIx < dataLinePcs.length; strIx = strIx + 2) {
						// only add valid nutrients
						boolean isValidNutrient = false;
				        for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
				    		if(nutrient.toString().equals(dataLinePcs[strIx])) {
				    			isValidNutrient = true;
				    			break;
				    		}
				        }
				        if(!isValidNutrient) {continue;}
				        
				        // at this point we have a valid nutrient
				        foodItemObj.addNutrient(dataLinePcs[strIx], new Double(dataLinePcs[strIx + 1]));
					}
					
					// add food item
					addFoodItem(foodItemObj);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(fileStream != null) {
				fileStream.close();
			}
		}
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#addFoodItem(skeleton.FoodItem)
     */
    @Override
    public void addFoodItem(FoodItem foodItem) {
    	if(foodItem == null) {return;}
    	
    	// need to add foodItem to all of the relevant BPTrees (name, ID, nutrient indexes)
    	foodIDIx.insert(foodItem.getID(), foodItem);
    	
    	HashMap<String, Double> foodNutrients = foodItem.getNutrients();
    	
    	// only add valid nutrients
        for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
    		if(foodNutrients.containsKey(nutrient.toString())) {
    			indexes.get(nutrient.toString()).insert(foodNutrients.get(nutrient.toString()), foodItem);
    		}
        }
        
        foodItemList.add(foodItem);
    }

	@Override
	public void saveFoodItems(String filename) {
		// attempts to save the current layout to file
		File outputFile = null;
		PrintStream writer = null;
		
		// attempt to create file
		try {
			outputFile = new File(filename);
			writer = new PrintStream(outputFile);
			
			// loop through list of foodItems and add each to output file
			for(FoodItem foodItemObj : foodItemList) {
				if(foodItemObj == null) {continue;}
				
				String[] dataLinePcs = new String[(NutrientsEnum.values().length * 2) + 2];
				// data line
				/*
				 * <id>,<food_name>,<calories>,<calorie_count>,<fat>,<fat_grams>,<carbohydrate>,<carbohydrate_grams>,<fiber>,<fiber_grams>,<protein>,<protein_grams>,
				 * 0 - id
				 * 1 - name
				 * 2 - "calories"
				 * 3 - calories
				 * 4 - "fat"
				 * 5 - fat
				 * 6 - "carbohydrate"
				 * 7 - carbohydrate
				 * 8 - "fiber"
				 * 9 - fiber
				 * 10 - "protein"
				 * 11 - protein
				 */
				
				// create array of string pieces
				dataLinePcs[0] = foodItemObj.getID();
				dataLinePcs[1] = foodItemObj.getName();
				int strIx = 2;
				for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
		    		dataLinePcs[strIx] = nutrient.toString();
		    		dataLinePcs[strIx + 1] = new Double(foodItemObj.getNutrientValue(nutrient.toString())).toString();
		    		strIx = strIx + 2;
		        }
				
				// concatenate array into single string
				String del = ",";
				String dataLine = new String();
				for(int i = 0; i < dataLinePcs.length; i++) {
					if(i == 0) {
						dataLine = dataLinePcs[i];
					} else {
						dataLine = dataLine + del + dataLinePcs[i];
					}
				}
				writer.println(dataLine); // push line of data to printwriter
			}
		} catch(IOException e) {
			// use catch block to catch IO exceptions from try block
			System.out.println("WARNING: Could not save to file " + filename);
			e.printStackTrace();
		} finally {
			// use finally to close file even if we ran into an exception
			// if statement checks for null pointer
			if (writer != null) {
				// close the file
				writer.close();
			}
		}
	}
	
	/*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#getAllFoodItems()
     */
    @Override
    public List<FoodItem> getAllFoodItems() {
    	List<FoodItem> retFoods = foodItemList.stream()
    			.sorted((food1, food2) -> food1.getName().compareTo(food2.getName()))
				.collect(Collectors.toList());
    	foodItemList = retFoods;
        return foodItemList;
    }
	
	/*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByName(java.lang.String)
     */
    @Override
    public List<FoodItem> filterByName(String substring) {
        // TODO : Complete
        return null;
    }

    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
        // TODO : Complete
        return null;
    }
	
	
	
	
	
	
	// for testing only
	public static void main(String[] args) {
		FoodData testFoodDataObj = new FoodData();
		
		String filePath = new String("foodItemsShort.csv");
		testFoodDataObj.loadFoodItems(filePath);
	}
}
