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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ch.tatool.app.data.ModuleImpl;
import ch.tatool.app.data.ModuleSessionImpl;
import ch.tatool.app.data.TrialImpl;
import ch.tatool.app.data.UserAccountImpl;
import ch.tatool.data.Module;

/**
 * Property access object for module objects.
 * 
 * @author Michael Ruflin
 */
public class ModuleDAO extends HibernateDaoSupport {

    /**
     * Get info objects of all available module instances for an account.
     * 
     * @param account
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<Module.Info> getModules(final UserAccountImpl account) {
        return (Set<Module.Info>) getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    // query the names and ids of all modules inside an account
                    Query query = session.createQuery("select module.id, module.name FROM ModuleImpl as module where module.accountId = :accountId");
                    query.setParameter("accountId", account.getId());
                    List<Object[]> results = (List<Object[]>) query.list();
                    Set<Module.Info> infos = new TreeSet<Module.Info>();
                    for (Object[] result : results) {
                        ModuleInfoImpl info = new ModuleInfoImpl();
                        info.setAccount(account);
                        info.setId((Long) result[0]);
                        info.setName((String) result[1]);
                        infos.add(info);
                    }
                    return infos;
                }
            }
        );
    }
    
    
    /**
     * Loads a module object from the database, using the id specified in the module.
     * 
     * If no module with given id exists in the database, a new one is created
     * 
     * @param moduleInfo the module to load
     */
    public ModuleImpl loadModule(ModuleInfoImpl moduleInfo) {
        ModuleImpl module = (ModuleImpl) getHibernateTemplate().get(ModuleImpl.class, moduleInfo.getId());

        // set the account object manually 
        if (! moduleInfo.getAccount().getId().equals(module.getAccountId())) {
            throw new RuntimeException("ModuleInfo and Module object don't belong to each other! Account id mismatch");
        }
        module.setAccount(moduleInfo.getAccount());
        
        return module;
    }
    
    /**
     * Deletes a module
     * 
     * @param module the module to load
     */
    public void deleteModule(final ModuleInfoImpl moduleInfo) {
        getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                	// PENDING: can't hibernate figure this out on its own?
                	// First delete all trials
                	List<?> trialIds = session.createQuery("select id from TrialImpl trial where trial.session.module.id = :id").setParameter("id", moduleInfo.getId()).list();
                	for (Object o : trialIds) {
                		TrialImpl trial = new TrialImpl();
                		session.load(trial, (Long)o);
                		session.delete(trial);
                		//session.createQuery("delete TrialImpl trial where trial.id = :id").setParameter("id", o).executeUpdate();
                	}
                	session.flush();
                	
                	// then delete the sessions
                	List<?> sessionIds = session.createQuery("select id from ModuleSessionImpl session where session.module.id = :id").setParameter("id", moduleInfo.getId()).list();
                	for (Object o : sessionIds) {
                		ModuleSessionImpl s = new ModuleSessionImpl();
                		session.load(s, (Long)o);
                		session.delete(s);
                	}
                	
                	// finally delete the module
                    // Does not work because for some reason the module properties are not deleted in cascade...
                    /*
                    Query query = session.createQuery("delete from ModuleImpl module where module-id = :moduleId");
                    query.setLong("moduleId", moduleInfo.getId());
                    query.executeUpdate();
                    */
                	
                	// and finally the module itself
                	ModuleImpl module = loadModule(moduleInfo);
                    session.delete(module);
                    return null;
                }
            }
        );
    }
    
    public void addModuleProperties(final ModuleImpl impl, final Map<String, String> properties) {
        getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    session.update(impl);
                    Map<String, String> props = impl.getModuleProperties();
                    for (String key : props.keySet()) {
                        logger.debug(key + ": " + props.get(key));
                    }
                    for (String key : properties.keySet()) {
                        String value = properties.get(key);
                        props.put(key, value);
                    }
                    session.save(impl);
                    return null; 
                }
            }
        );
    }
    
    /**
     * Stores the module object.
     */
    public void saveModule(ModuleImpl module) {
        getHibernateTemplate().saveOrUpdate(module);
    }
    
    /**
     * Deletes a module object.
     */
    public void deleteModule(Module module) {
        getHibernateTemplate().delete(module);
    }
}
