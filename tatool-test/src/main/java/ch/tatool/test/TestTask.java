package ch.tatool.test;

import ch.tatool.core.data.Level;
import ch.tatool.core.data.Points;
import ch.tatool.core.executable.BlockingExecutable;
import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionOutcome;

/**
 * Test task implementation that simply returns a pre-configured outcome.
 * 
 * This task does not implement a UI.
 * 
 * @author Michael Ruflin
 */
public class TestTask extends BlockingExecutable{

	private String outcome = ExecutionOutcome.STOPPED;

	private int counter = 0;
	private static final int FEEDBACK_MODULO = 1;
	
	public TestTask() {
	    super("test-task");
	}
	
	public void startExecution() {
		counter++;
		if ((counter % FEEDBACK_MODULO) == 0) {
			System.out.println("TestTask " + getId() + " executed for " + counter + " times.");
		}
		
		insertData(getExecutionContext());
		
		if (getFinishExecutionLock()) {
			finishExecution();
		}
	}
	
	private void insertData(ExecutionContext context) {
		Points.getMinPointsProperty().setValue(this, 0);
		Points.getPointsProperty().setValue(this, 0);
		Points.getMaxPointsProperty().setValue(this, 0);
		Level.getLevelProperty().setValue(this, 1);
	}

}
