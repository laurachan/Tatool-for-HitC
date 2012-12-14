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

import java.util.Random;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ch.tatool.app.data.UserAccountImpl;

/**
 * Property access object for user account objects.
 * 
 * @author Michael Ruflin
 */
public class UserAccountDAO extends HibernateDaoSupport {

	// Account management

	/**
	 * Loads an account object from the database, using the id specified in the
	 * account.
	 * 
	 * If no account with given id exists, a new one is created.
	 * 
	 * @param module
	 *            the module to load
	 */
	public void loadAccount(UserAccountImpl userAccount) {
		if (userAccount.getId() != null) {
			getHibernateTemplate().load(userAccount, userAccount.getId());
		} else {
			// create a random id
			Long id = new Random().nextLong();
			id = Math.abs(id);
			userAccount.setId(id);
			getHibernateTemplate().save(userAccount);
			
			// change the script type
			setScriptType(userAccount);
		}
	}

	/**
	 * Stores a user account.
	 */
	public void saveAccount(UserAccountImpl account) {
		getHibernateTemplate().saveOrUpdate(account);
	}

	/**
	 * Deletes a user account.
	 */
	public void deleteAccount(UserAccountImpl account) {
		getHibernateTemplate().delete(account);
	}

	public void setAccountPassword(final UserAccountImpl account,
			final String password) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				// find the username
				BasicDataSource dataSource = (BasicDataSource) account
						.getBeanFactory().getBean("userAccountDataSource");
				String username = dataSource.getUsername();
				String newPassword = password != null ? password : "";
				//String driver = dataSource.getDriverClassName();

				// update the password using an alter user sql query (every db except mysql)
				try {
					StringBuilder sql = new StringBuilder();
					sql.append("ALTER USER '").append(username).append(
							"' SET PASSWORD '").append(newPassword).append("'");

					SQLQuery query = session.createSQLQuery(sql.toString());
					query.executeUpdate();

					// update the current account object
					dataSource.setPassword(newPassword);
					account.setPassword(newPassword);
					account.setPasswordProtected(!newPassword.isEmpty());
				} catch(HibernateException hb) {
					//TODO: Add something
				}

				return null;
			}
		});
	}

	/** Set the scripttype used in the database to compressed.
	 */
	private void setScriptType(final UserAccountImpl account) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				try {
					// SET FILES SCRIPT FORMAT {TEXT | COMPRESSED};
					// http://hsqldb.org/doc/2.0/guide/deployment-chapt.html#deploymen_modes-sect
					// COMPRESSED leads to problems with Test, therefore we go with TEXT format
					String sql = "SET FILES SCRIPT FORMAT TEXT";
					SQLQuery query = session.createSQLQuery(sql);
					query.executeUpdate();
				} catch(HibernateException hb) {
					
				}
				return null;
			}
		});
	}

	public void shutdown() {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				try {
					String sql = "SHUTDOWN";
					SQLQuery query = session.createSQLQuery(sql);
					query.executeUpdate();
				} catch(HibernateException hb) {
				}
				return null;
			}
		});
	}
}
