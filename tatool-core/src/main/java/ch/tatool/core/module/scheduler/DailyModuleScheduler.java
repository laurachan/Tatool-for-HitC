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
package ch.tatool.core.module.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.module.ModuleSchedulerMessage;

/**
 * Allows one session a day and doesn't allow the user to terminate a session.
 * 
 * @author Michael Ruflin
 */
public class DailyModuleScheduler extends AbstractModuleScheduler {

    /** Give the scheduler a chance to initialize. */
    public void initialize() {

    }

    public String getName() {
        return "DailyExecutionScheduler";
    }

    public boolean canUserTerminateSession(ModuleSession moduleSession) {
        // user can not terminate session at will
        return false;
    }

    public ModuleSchedulerMessage isSessionStartAllowed(Module module) {
        Calendar todayDate = new GregorianCalendar();
        todayDate.set(Calendar.HOUR_OF_DAY, 0);
        todayDate.set(Calendar.MINUTE, 0);
        todayDate.set(Calendar.SECOND, 0);
        todayDate.set(Calendar.MILLISECOND, 0);
        Calendar lastSessionDate = new GregorianCalendar();

        long numSessions = getDataService().getSessionCount(module, false);
        ModuleSession lastSession = getDataService()
                .getLastSession(module);
        if (lastSession != null && lastSession.getEndTime() != null) {
            lastSessionDate.setTime(lastSession.getStartTime());
        } else {
            lastSessionDate.set(Calendar.YEAR, 1900);
        }
        
        
        ModuleSchedulerMessage message = new ModuleSchedulerMessageImpl();

        if (lastSessionDate.before(todayDate)) {
        	message.setSessionStartAllowed(true);
            return message;
        } else if (!lastSessionDate.before(todayDate)) {
        	message.setSessionStartAllowed(false);
        	message.setMessageTitle("Tatool");
        	int hours = (23 - lastSessionDate.get(Calendar.HOUR_OF_DAY));
        	int minutes = (60 - lastSessionDate.get(Calendar.MINUTE));
        	StringBuilder mb = new StringBuilder();
        	mb.append("Sie haben heute bereits eine Einheit absolviert.\n");
        	mb.append("Dieses Modul erlaubt nur eine Einheit pro Tag.\n");
        	mb.append("Sie können die nächste Einheit in " + hours + " Stunden und " + minutes + " Minuten starten.");
        	message.setMessageText(mb.toString());
            return message;
        } else if (numSessions < 26) {
        	message.setSessionStartAllowed(false);
        	message.setMessageTitle("Tatool");
        	message.setMessageText("Sie dürfen nur 26 Einheiten von diesem Modul absolvieren.");
        	return message;
        }
        
        // Allow a total of 26 session and only one session a day (before and after midnight)
        if (lastSessionDate.before(todayDate) && numSessions < 26) {
            return message;
        } else {
            return message;
        }
    }
 
}
