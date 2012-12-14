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
package ch.tatool.app.service.impl;

import ch.tatool.app.data.UserAccountImpl;
import ch.tatool.data.Module;

/**
 * Module info object
 */
public class ModuleInfoImpl implements Module.Info, Comparable<ModuleInfoImpl> {
    private Long id;
    private String name;
    private UserAccountImpl account;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccountImpl getAccount() {
        return account;
    }

    public void setAccount(UserAccountImpl account) {
        this.account = account;
    }
    
    public String toString() {
        return name != null ? name : super.toString();
    }

    public int compareTo(ModuleInfoImpl o) {
        return toString().compareTo(o.toString());
    }
    
}
