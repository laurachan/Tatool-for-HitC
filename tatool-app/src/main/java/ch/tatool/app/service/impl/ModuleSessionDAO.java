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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ch.tatool.app.data.ModuleImpl;
import ch.tatool.data.ModuleSession;

/**
 * Property access object for module objects.
 * 
 * @author Michael Ruflin
 */
public class ModuleSessionDAO extends HibernateDaoSupport {

    /** Get a list of all sessions. */
    @SuppressWarnings("unchecked")
    public List<ModuleSession> getSessions(final ModuleImpl module) {
        return (List<ModuleSession>) getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    String queryString = "select session from ModuleSessionImpl session where session.module = :module order by session.id";
                    Query query = session.createQuery(queryString);
                    query.setParameter("module", module);
                    List<ModuleSession> result = (List<ModuleSession>) query.list();
                    // make sure we property set the module object
                    for (ModuleSession s : result) {
                    	s.setModule(module);
                    }
                    return result;
                }
            }
        );
    }
    
    private Integer findLastSessionIndex(final ModuleImpl module) {
    	return (Integer) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        String queryString = "select max(session.index) from ModuleSessionImpl session where session.module = :module";
                        Query query = session.createQuery(queryString);
                        query.setParameter("module", module);
                        Integer result = (Integer) query.uniqueResult();
                        
                        if (result != null) {
                        	return result;
                        } else {
                        	return 0;
                        }
                        
                    }
                }
            );
    }
    
    /** Saves a session object. */
    public void saveSession(ModuleImpl module, ModuleSession session) {
        if (session.getId() == null) {
        	session.setModule(module);
        	session.setIndex(findLastSessionIndex(module) + 1);
        	getHibernateTemplate().save(session);
        } else {
            getHibernateTemplate().update(session);
        }
    }
    
    /** Deletes a session object. */
    public void deleteSession(ModuleImpl module, ModuleSession session) {
        getHibernateTemplate().delete(session);
    }
    
    /** Finds the last session for a given module. */
    public ModuleSession findLastSession(final ModuleImpl module) {
        return (ModuleSession) getHibernateTemplate().execute(
                new HibernateCallback() {
                    @SuppressWarnings("unchecked")
                    public Object doInHibernate(Session session) {
                        Criteria criteria = session.createCriteria(ModuleSession.class);
                        criteria.add(Restrictions.eq("module", module));
                        criteria.setFetchSize(1);
                        criteria.addOrder(Order.desc("id"));
                        List<ModuleSession> sessions = (List<ModuleSession>) criteria.list();
                        if (sessions.size() > 0) {
                            return sessions.get(0);
                        } else {
                            return null;
                        }
                    }
                }
            );
    }
    
    /** Finds the number of session for the given module. */
    public Long getSessionCount(final ModuleImpl module, final boolean includeUnfinished) {
        return (Long) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                        String query = "select count(session) from ModuleSessionImpl session where session.module = :module";
                        if (!includeUnfinished) {
                            query += " and session.endTime != null";
                        }
                        return (Long) session.createQuery(query)
                                .setParameter("module", module)
                                .uniqueResult();
                    }
                }
            );
    }
    
    /**
     * Get all distinct session property names for a module.
     * 
     * @return An array with [0] containing the item name, [1] the property name
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findDistinctSessionPropertyNames(final ModuleImpl module) {
        return (List<Object[]>) getHibernateTemplate().execute(
            new HibernateCallback() {
                //@SuppressWarnings("unchecked")
                public Object doInHibernate(Session session) {
                    StringBuilder queryString = new StringBuilder();
                    queryString
                    	.append("Select distinct(entry.nodeId), entry.name")
                    	.append(" from ModuleSessionImpl session left join session.entriesImpl entry")
                    	.append(" where session.module = :module")
                    	.append(" and entry.nodeId is not NULL")
                    	.append(" order by entry.nodeId, entry.name");
                    
                    // put together the complete query
                    Query query = session.createQuery(queryString.toString());
                    query.setParameter("module", module);
                    
                    // and get the results
                    return (List<Object[]>) query.list();
                }
            }
        );
    }
}
