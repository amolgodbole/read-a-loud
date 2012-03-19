package readaloud.app;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;

import readaloud.models.User;
import readaloud.util.HibernateUtil;

/**
 * Sample application to demonstrate basic hibernate persistence - this is
 * effectively a DAO or SOA-storage. Note this is a very naive approach in that
 * it accounts for cases where we have multiple DAOs working together under one
 * transaction but does not have full support for:
 * 
 * <ol>
 * <li>Use of JTA for transactions (when running in a application server - need
 * jndi)
 * <li>Validation
 * </ol>
 * 
 * @author gash
 * 
 */
public class People {

	public User find(String id) {
		if (id == null)
			return null;

		User r = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			r = (User) session.load(User.class, id);
			System.out.println("Name: "+r.getFirstName());
			// forcing the proxy to load contacts - this not good as it costs us
			// another trip to the database
	/*		if (r != null)
				r.getContacts().size();
*/
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	/**
	 * here we use a class to act as a template for what we are searching for.
	 * Demonstrates the Criteria type of searching. The nice feature of Criteria
	 * is the ability for some compile-time checking whereas, HQL is all runtime
	 * checking
	 * 
	 * @param template
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> find(User template) {
		ArrayList<User> r = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();

			// since the data is being detached we must fetch eagerly
			Criteria c = session.createCriteria(User.class);
			c.setFetchMode("contacts", FetchMode.JOIN);

			if (template.getId() != null) {
				c.add(Restrictions.idEq(template.getId()));
				c.setMaxResults(1);
			} else {
				if (template.getLastName() != null)
					c.add(Restrictions.like("firstname", template.getFirstName()));

				if (template.getLastName() != null)
					c.add(Restrictions.like("lastname", template.getLastName()));

			}

			//System.out.println("find() " + c.toString());

			// the join creates duplicate records - this will remove them
			Set<User> set = new LinkedHashSet<User>(c.list());
			r = new ArrayList<User>(set);

			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
		return r;
	}

	/**
	 * demonstration of HQL and setting the fetch depth of the graph
	 * 
	 * @param template
	 * @return
	 */


	public void createUser(User p) {
		if (!validate(p))
			throw new RuntimeException("Invalid User");

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			System.out.println(p.getFirstName());
			session.save(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}
	
	
	
	
	
	

	public void updateUser(User p) {
		if (!validate(p) || p.getId() == null)
			throw new RuntimeException("Invalid User");

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.saveOrUpdate(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}

	
	
	
	
	
	
	public void removeUser(Long id) {
		if (id == null)
			return;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			User p = (User) session.get(User.class, id);
			if (p != null)
				session.delete(p);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			session.getTransaction().rollback();
			throw e;
		}
	}
	
	
	
	
	
	

	protected boolean validate(User p) {
		if (p == null)
			return false;

		// TODO validate values
		return true;
	}

	
	
	
	
	
	
	public void showStats() {
		try {
			Statistics stats = HibernateUtil.getSessionFactory().getStatistics();
			double queryCacheHitCount = stats.getQueryCacheHitCount();
			double queryCacheMissCount = stats.getQueryCacheMissCount();
			double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount);
			System.out.println("--> Query Hit ratio: " + queryCacheHitRatio);

			System.out.println("--> TX count: " + stats.getTransactionCount());

			EntityStatistics entityStats = stats.getEntityStatistics(User.class.getName());
			long changes = entityStats.getInsertCount() + entityStats.getUpdateCount() + entityStats.getDeleteCount();
			System.out.println("--> " + User.class.getName() + " changed " + changes + " times");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
