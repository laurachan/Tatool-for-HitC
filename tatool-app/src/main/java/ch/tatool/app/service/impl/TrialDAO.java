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

import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ch.tatool.app.data.ModuleImpl;
import ch.tatool.app.data.ModuleSessionImpl;
import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;
import ch.tatool.element.Node;

/**
 * Property access object for module objects.
 * 
 * @author Michael Ruflin
 */
public class TrialDAO extends HibernateDaoSupport {

    Logger logger = LoggerFactory.getLogger(TrialDAO.class);

    /** Get a list of all sessions. */
    @SuppressWarnings("unchecked")
    public List<Trial> loadAllTrials(final ModuleImpl module) {
        return (List<Trial>) getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                    String queryString = "select trial from TrialImpl trial where trial.session.module = :module";
                    Query query = session.createQuery(queryString);
                    query.setParameter("module", module);
                    return (List<Trial>) query.list();
                }
            }
        );
    }
    
    /** Get all trials for a given session. */
    @SuppressWarnings("unchecked")
    public List<Trial> getTrials(final ModuleSession moduleSession) {
    	return (List<Trial>) getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                	getHibernateTemplate().update(moduleSession);
                	Query query = session.createQuery("select trial from TrialImpl trial where trial.session = :session order by trial.id");
                	query.setParameter("session", moduleSession);
                	List<Trial> trials = (List<Trial>) query.list();
                	// make sure we set the moduleSession correctly
                	for (Trial t : trials) {
                		t.setSession(moduleSession);
                	}
                	
                	return trials;
                }
            }
        );
    }
    
    /** Get all trials for a given session. */
    @SuppressWarnings("unchecked")
    public List<Trial> getTrials(final ModuleImpl module, final ModuleSession moduleSession, final String elementNameLike, final String propertyNameLike, final int offset, final int maxResults) {
    	if (elementNameLike == null && propertyNameLike == null) {
    		throw new RuntimeException("Either elementName or propertyName needs to be non-null");
    	}
    	return (List<Trial>) getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) {
                	
                	getHibernateTemplate().update(module);
                	if (moduleSession != null) {
                		getHibernateTemplate().update(moduleSession);
                	}
                	
                	StringBuilder q = new StringBuilder();
                	q.append("select distinct(trial) from TrialImpl trial ");
                	q.append(" join trial.entriesImpl as entry where ");
                	if (moduleSession != null) {
                		q.append(" trial.session = :session ");
                	} else {
                		q.append(" trial.session.module = :module ");
                	}
                    if (elementNameLike != null) {
                        q.append(" and entry.nodeId like :elementName");
                    }
                    if (propertyNameLike != null) {
                        q.append(" and entry.name like :propName");
                    }
                	
                    // sort reverse
                    q.append(" order by trial.id DESC");
                    
                    // create the query and set the various parameters
                	Query query = session.createQuery(q.toString());
                	if (moduleSession != null) {
                		query.setParameter("session", moduleSession);
                	} else {
                		query.setParameter("module", module);
                	}
                	if (elementNameLike != null) {
                		query.setParameter("elementName", elementNameLike);
                	}
                	if (propertyNameLike != null) {
                		query.setParameter("propName", propertyNameLike);
                	}
                	// limit results if requested
                	if (offset > 0) {
                		query.setFirstResult(offset);
                	}
                	if (maxResults > -1) {
                		query.setMaxResults(maxResults);
                	}
                	List<Trial> trials = (List<Trial>) query.list();
                	
                	// make sure we set the moduleSession correctly
                	if (moduleSession != null) {
                		for (Trial t : trials) {
                			t.setSession(moduleSession);
                		}
                	}
                	
                	return trials;
                }
            }
        );
    }
    
    
    /** Get the last trial index for a given session. */
    @SuppressWarnings("unchecked")
    public int getLastTrialIndex(final ModuleSession moduleSession) {
    	List<Integer> index = (List<Integer>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) {
                    	Query query = session.createQuery("select max(trial.index) from TrialImpl trial where trial.session = :session");
                    	query.setParameter("session", moduleSession);
                    	return (List<Trial>) query.list();
                    }
                }
            );
    	if (index != null && index.isEmpty()) {
    		return 0;
    	} else {
    		return index.get(0);
    	}
    }
    
    /** Persist a trial. */
    public void saveTrial(Module module, ModuleSession session, Trial trial) {
    	ModuleSessionImpl sessionImpl = (ModuleSessionImpl) session; 
        if (trial.getId() == null) {
            getHibernateTemplate().update(session);
            trial.setSession(session);
            sessionImpl.getTrials().add(trial);
            trial.setIndex(sessionImpl.getTrials().indexOf(trial));
            getHibernateTemplate().save(trial);
            //getHibernateTemplate().update(session);
            //session.getTrials().add(trial);
            //getHibernateTemplate().save(trial);
        } else {
            getHibernateTemplate().update(trial);
        }
    }
    
    /** Delete a trial object. */
    public void deleteTrial(Module module, ModuleSession session, Trial trial) {
        Iterator<Trial> it = ((ModuleSessionImpl) session).getTrials().iterator();
        while (it.hasNext()) {
            Trial t = it.next();
            if (t.getId().equals(trial.getId())) {
                it.remove();
            }
        }
        getHibernateTemplate().delete(trial);
    }
    
    @SuppressWarnings("unchecked")
    public List<Trial> getTrials(ModuleSession session, Node node, int maxResults) { 
        List<Trial> trials = null;

        DetachedCriteria criteria = DetachedCriteria.forClass(Trial.class);
        // limit the session
        if (session != null) {
            criteria.add(Property.forName("session").eq(session));
        }
        // limit the element
        if (node != null) {
            criteria.add(Property.forName("nodeId").eq(node.getId()));
        }
        // get the last trial as first
        criteria.addOrder(Order.desc("id"));
        
        // find the trials
        trials = (List<Trial>) getHibernateTemplate().findByCriteria(criteria, 0, maxResults);
        
        return trials;
    }

    /**
     * Get all distinct trial property names for a module.
     * 
     * @return An array with [0] containing the item name, [1] the property name
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findDistinctTrialPropertyNames(final ModuleImpl module) {
        return (List<Object[]>) getHibernateTemplate().execute(
            new HibernateCallback() {
                //@SuppressWarnings("unchecked")
                public Object doInHibernate(Session session) {
                    StringBuilder queryString = new StringBuilder();
                    queryString
                    	.append("Select distinct(entry.nodeId), entry.name")
                    	.append(" from TrialImpl trial left join trial.entriesImpl entry")
                    	.append(" where trial.session.module = :module")
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
