package application;

import java.util.HashMap;

/**
 * This class represents a food item with all its properties.
 * 
 * @author aka
 */
public class FoodItem {
    // The name of the food item.
    private String name;

    // The id of the food item.
    private String id;

    // Map of nutrients and value.
    private HashMap<String, Double> nutrients;
    
    /**
     * Constructor
     * @param name name of the food item
     * @param id unique id of the food item 
     */
    public FoodItem(String id, String name) {
        this.id = id;
        this.name = name;
        nutrients = new HashMap<String, Double>();
    }
    
    /**
     * Gets the name of the food item
     * @return name of the food item
     */
    public String getName() {return name;}

    /**
     * Gets the unique id of the food item
     * @return id of the food item
     */
    public String getID() {return id;}
    
    /**
     * Gets the nutrients of the food item
     * @return nutrients of the food item
     */
    public HashMap<String, Double> getNutrients() {return nutrients;}

    /**
     * Adds a nutrient and its value to this food. 
     * If nutrient already exists, updates its value.
     */
    public void addNutrient(String name, double value) {
    	if(name == null) {return;} // don't allow null nutrients
    	if(value < 0) {return;} // also don't allow adding negative nutrient values
    	
    	// check if nutrient is valid
    	if(!isValidNutrient(name)) {return;}
        
        // at this point, we have a valid nutrient since it exists in the NutrientsEnum
        nutrients.put(name, value);
    }

    /**
     * Returns the value of the given nutrient for this food item. 
     * If not present, then returns 0.
     */
    public double getNutrientValue(String name) {
        double ret = 0;
        if(name == null) {return ret;}
        
        // check if nutrient is valid
        if(!isValidNutrient(name)) {return ret;}
        
        // at this point, we have a valid nutrient since it exists in the NutrientsEnum
        ret = nutrients.get(name);
        
        return ret;
    }
    
    /**
     * helper function to determine if passed in string is a nutrient in the
     * NutrientsEnum
     * @param nutrientName - String to examine
     * @return boolean - true if nutrientName is an entry in NutrientsEnum,
     * false otherwise
     */
    private boolean isValidNutrient(String nutrientName) {
        for (NutrientsEnum nutrient : NutrientsEnum.values()) { 
    		if(nutrient.toString().equals(nutrientName)) {
    			return true;
    		}
        }
        return false;
    }
}
