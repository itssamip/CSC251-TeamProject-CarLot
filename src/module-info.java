module CSC251_JavaFX_Example {
	
	// JavaFX
	requires javafx.controls;
	requires javafx.graphics;

	opens csc251.team.project to javafx.controls, javafx.graphics, javafx.base;
}