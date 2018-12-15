package application;

public enum NutrientsEnum {
	CALORIES,
	FAT,
	CARBOHYDRATE,
	FIBER,
	PROTEIN;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
