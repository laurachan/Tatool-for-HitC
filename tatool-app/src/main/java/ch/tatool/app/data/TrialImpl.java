/*******************************************************************************
 * Copyright (c) 2011 Michael Ruflin, André Locher, Claudia von Bastian.
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
package ch.tatool.app.data;

import java.util.Date;

import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;

/**
 * Represents a trial of a session.
 * 
 * @author Michael Ruflin
 */
public class TrialImpl extends DataContainerImpl implements Trial {
    
    /** Session this trial belongs to. */
    private ModuleSession session;
    
    // Task information
    private Long id;
    
    /** Index of the trial within the session. */
    private int index;
    
    /** Id of the executed element this trial is based on. */
    private String parentId;
    
    /** Level this trial was based on. */
    private int level;
    
    /** Min points this tasks attributes. */
    private int minPoints;
    
    /** Max points this task attributes. */
    private int maxPoints;
    
    /** Trial data.
     * More data might be stored in the trial properties
     */
    private String question;

    /** Correct response value. */
    private String correctResponse;
    
    /** Given response. */
    private String givenResponse;
    
    /** Actual outcome. */
    private String outcome;
    
    /** Points attributed. */
    private int points;

    /** Task start time. */
    private Date startTime;
    
    /** Task end time. */
    private Date endTime;
    
    public TrialImpl() {
        super();
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMinPoints() {
        return minPoints;
    }

    public void setMinPoints(int minPoints) {
        this.minPoints = minPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(String correctResponse) {
        this.correctResponse = correctResponse;
    }

    public String getGivenResponse() {
        return givenResponse;
    }

    public void setGivenResponse(String givenResponse) {
        this.givenResponse = givenResponse;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ModuleSession getSession() {
        return session;
    }

    public void setSession(ModuleSession session) {
        this.session = session;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
