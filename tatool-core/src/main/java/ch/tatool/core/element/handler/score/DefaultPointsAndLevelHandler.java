/*******************************************************************************
 * Copyright (c) 2011 Michael Ruflin, Andr� Locher, Claudia von Bastian.
 * 
 * This file is part of Tatool.
 * 
 * Tatool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * Tatool is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Tatool. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ch.tatool.core.element.handler.score;

import java.util.HashMap;
import java.util.List;

import ch.tatool.core.data.DoubleProperty;
import ch.tatool.core.data.IntegerProperty;
import ch.tatool.core.data.Misc;
import ch.tatool.core.data.Points;
import ch.tatool.core.element.CompoundElement;
import ch.tatool.core.element.CompoundSelector;
import ch.tatool.core.element.handler.score.AbstractPointsAndLevelHandler;
import ch.tatool.data.Module;
import ch.tatool.data.Trial;
import ch.tatool.element.Executable;
import ch.tatool.element.Node;
import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionOutcome;

/**
 * Default implementation of a score and level handler. Checks for the
 * performance after all n trials
 * 
 * @author Andre Locher
 */
public class DefaultPointsAndLevelHandler extends AbstractPointsAndLevelHandler {

	private int sampleSize = 3;

	private double maxThreshold = 80;

	private double minThreshold = 60;

	private double performance = 0;

	public static final String PROPERTY_LEVEL_COUNTER = "levelCounter";

	public static final String PROPERTY_LEVEL_PERFORMANCE = "levelPerformance";
	public static final String PROPERTY_LEVEL_TOTALPOINTS = "levelTotalPoints";
	public static final String PROPERTY_LEVEL_MAXPOINTS = "levelMaxPoints";

	private static IntegerProperty trialCounterProperty = new IntegerProperty(
			PROPERTY_LEVEL_COUNTER);
	private static DoubleProperty performanceProperty = new DoubleProperty(
			PROPERTY_LEVEL_PERFORMANCE);

	private double totalScore = 0;
	private double maxScore = 0;
	private int trialCounter;

	// statistics data
	private HashMap<Integer, Integer> performanceData;
	private HashMap<Integer, Integer> levelData;

	public DefaultPointsAndLevelHandler() {
		super("level-handler");
	}

	/**
	 * Initializes the algorithm with the values of the DB at session start
	 */
	protected void initializeHandler(ExecutionContext context) {
		totalScore = 0;
		maxScore = 0;

		// session data
		performanceData = new HashMap<Integer, Integer>();
		levelData = new HashMap<Integer, Integer>();
		trialCounter = 0;
	}

	/**
	 * Initializes the algorithm with the values of the DB.
	 */
	public void initializeAlgorithm(ExecutionContext event) {
		Module module = event.getExecutionData().getModule();

		// get the counters from the module
		trialCounter = trialCounterProperty.getValue(module, this, 0);
	}

	@Override
	protected int checkLevelChange(ExecutionContext context, int currentLevel) {
		List<Trial> trials = context.getExecutionData().getTrials();
		int oldLevel = currentLevel;
		int newLevel = oldLevel;

		Executable executable = context.getActiveExecutable();

		// loop through all trials
		for (int i = 0; i < trials.size(); i++) {
			Trial trial = trials.get(i);

			totalScore += Points.getPointsProperty().getValue(trial,
					trial.getParentId(), 0);
			maxScore += Points.getMaxPointsProperty().getValue(trial,
					trial.getParentId(), 0);

			if (trials.isEmpty())
				return currentLevel;

			String trialOutcome = Misc.getOutcomeProperty().getValue(trial,
					executable);

			// only do calculation if trial is finished and compound element is
			// done
			if (trialOutcome != null
					&& trialOutcome.equals(ExecutionOutcome.FINISHED)
					&& isCompoundDone(context, trial)) {

				initializeAlgorithm(context);

				if (trialCounter >= (sampleSize - 1)) {
					performance = (totalScore / maxScore) * 100;
					// level up counter
					if (performance >= maxThreshold) {
						newLevel = changeLevel(context, oldLevel, 1);
						// level down counter
					} else if (performance <= minThreshold && oldLevel > 1) {
						newLevel = changeLevel(context, oldLevel, -1);
					}

					// save data to trial
					performanceProperty.setValue(trial, this, performance);

					performanceData.put(trialCounter, (int) performance);
					levelData.put(trialCounter, newLevel);

					totalScore = 0;
					maxScore = 0;
					performance = 0;
					trialCounter = 0;
					trialCounterProperty.setValue(trial, this, trialCounter);
					trialCounterProperty.setValue(context.getExecutionData()
							.getModule(), this, trialCounter);
				} else {
					trialCounter++;
				}

				trialCounterProperty.setValue(trial, this, trialCounter);
				trialCounterProperty.setValue(context.getExecutionData()
						.getModule(), this, trialCounter);

			}
		}

		return newLevel;
	}

	/**
	 * Changes level and adapts all parameters of the adaptive algorithm.
	 * 
	 * @return the new level
	 */
	private int changeLevel(ExecutionContext event, int oldLevel, int addition) {
		int newLevel;

		// level up and reset benchmark
		newLevel = oldLevel + addition;

		return newLevel;
	}

	/**
	 * Checks whether the current trial is complete. The algorithm only gets
	 * triggered if a compound element is finished
	 * 
	 * @return whether the trial is complete
	 */
	private boolean isCompoundDone(ExecutionContext context, Trial trial) {

        Node currElement = context.getActiveElement();
        
        boolean isDone = true;
        while (this.getParent() != null
                && this.getParent() != currElement) {

            // CompoundElement
            if (currElement instanceof CompoundElement) {
                CompoundElement comp = (CompoundElement) currElement;

                for (Object handler : comp.getHandlers()) {
                    if (handler instanceof CompoundSelector) {
                        CompoundSelector selector = (CompoundSelector) handler;
                        isDone = selector.isDone();
                    }
                }

            }
            if (currElement.getParent() != null) {
                currElement = currElement.getParent();
            } else {
                return true;
            }

        }

        return isDone;
    }

	public HashMap<Integer, Integer> getPerformanceData() {
		return performanceData;
	}

	public HashMap<Integer, Integer> getLevelData() {
		return levelData;
	}

	public double getMaxThreshold() {
		return maxThreshold;
	}

	public void setMaxThreshold(double maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	public double getMinThreshold() {
		return minThreshold;
	}

	public void setMinThreshold(double minThreshold) {
		this.minThreshold = minThreshold;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
}
