package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
					//e.printStackTrace();
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
        List<FoodItem> ret = new ArrayList<FoodItem>();
        if(substring == null) {return ret;}
        
        // iterate through all foods in foodItemList
        for(FoodItem food : foodItemList) {
        	if(food == null) {continue;}
        	
        	if(food.getName().toLowerCase().contains(substring.toLowerCase())) {
        		ret.add(food);
        	}
        }
        
        return ret;
    }
 
    /*
     * (non-Javadoc)
     * @see skeleton.FoodDataADT#filterByNutrients(java.util.List)
     */
    @Override
    public List<FoodItem> filterByNutrients(List<String> rules) {
    	List<FoodItem> retList = new ArrayList<FoodItem>();
    	if(rules == null) {return retList;}
    	
    	HashSet<FoodItem> rulePassFoodItemsSet = new HashSet<FoodItem>(getAllFoodItems());
    	
    	for(String currentRule : rules) {
    		if(currentRule == null) {continue;}
    		
    		String nutrientName = null;
    		String comparator = null;
    		Double nutrientVal = null;
    		
    		String del = " ";
			String[] currentRulePcs = currentRule.split(del);
			
			// rule format: <nutrient> <comparator> <value>
	    	// 0 - <nutrient> - name of one of the 5 nutrients (calories,carbs,fat,protein,fiber) [CASE-INSENSITIVE]
	    	// 1 - <comparator> - One of the following comparison operators: <=, >=, ==
	    	// 2 - <value> - a double value
			
			// rule must follow above format
			if (currentRulePcs.length != 3) {continue;}
    		
			// rule must be for valid nutrient
    		boolean isValidNutrient = false;
	        for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
	    		if(nutrient.toString().equals(currentRulePcs[0].toLowerCase())) {
	    			isValidNutrient = true;
	    			nutrientName = currentRulePcs[0].toLowerCase();
	    			break;
	    		}
	        }
	        if(!isValidNutrient) {continue;}
	        
	        // comparator must be valid
	        String [] comparators = {"==","<=",">="};
	        boolean isValidComparator = false;
	        for(String currentComp : comparators) {
	        	if(currentComp.equals(currentRulePcs[1])) {
	        		comparator = currentRulePcs[1];
	        		isValidComparator = true;
	        		break;
	        	}
	        }
	        if(!isValidComparator) {continue;}
	        
	        // value must be valid
	        try {
	        	nutrientVal = Double.parseDouble(currentRulePcs[2]);
	        	if(nutrientVal < 0) {continue;}
	        } catch(Exception e) {continue;}
	        
	        // all values must exist
	        if((nutrientName == null) || (comparator == null) || (nutrientVal == null)) {
	        	continue;
	        }
	        
	        
	        // at this point, all pieces of the rule are valid
	        // get list of foodItems that qualify for the filter
	        rulePassFoodItemsSet.retainAll(indexes.get(nutrientName).rangeSearch(nutrientVal, comparator));
	        
	        // short circuit
	        if(rulePassFoodItemsSet.isEmpty()) {return retList;}
    	}
    	
    	if(rulePassFoodItemsSet.isEmpty()) {return retList;}
    	
    	retList.addAll(rulePassFoodItemsSet);
    	List<FoodItem> retListSorted = retList.stream()
    			.sorted((food1, food2) -> food1.getName().compareTo(food2.getName()))
				.collect(Collectors.toList());
        return retListSorted;
    }
	
	
	
	
	
	// for testing only
	public static void main(String[] args) {
		FoodData testFoodDataObj = new FoodData();
		
		String filePath = new String("foodItemsShort.csv");
		testFoodDataObj.loadFoodItems(filePath);
	}
}
