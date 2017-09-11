package de.oc.lunch.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import de.oc.lunch.database.DataBase;
import de.oc.lunch.database.example.DefaultDatabase;

public interface PersistentObject<T> {
	
	public default void persist(EntityManager entityManager) {
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			entityManager.persist(this);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}

	}

	public default List<T> findAll(EntityManager entityManager) {
		String entityName = this.getClass().getSimpleName();
		TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery("from " + entityName, getClass());
		List<T> resultList = query.getResultList();
		entityManager.close();
		return resultList;
	}

	public default List<T> findBy(EntityManager entityManager) {
//		EntityManager em = DefaultDatabase.emf.createEntityManager();
		String entityName = this.getClass().getSimpleName();
		Map<String, Method> fields = findFields();
		String queryString = "from " + entityName;
		boolean firstParameter = true;
		for (String s : fields.keySet()) {
			try {
				Object value = fields.get(s).invoke(this);
				if (null != value) {
					if (firstParameter) {
						firstParameter=false;
						queryString += " where";
					}
					queryString += " " + s + " like :" + s;
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		TypedQuery<T> query = (TypedQuery<T>) entityManager.createQuery(queryString, getClass());

		for (String s : fields.keySet()) {
			try {
				Object value = fields.get(s).invoke(this);
				if (null != value) {
					query.setParameter(s, value);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		List<T> resultList = query.getResultList();
		return resultList;
	}

	public default Map<String, Method> findFields() {
		Map<String, Method> fields = new HashMap<>();
		Map<String, String> setters = new HashMap<>();
		Method[] methods = this.getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().startsWith("set"))
				setters.put(m.getName(), m.getName());
		}
		for (Method m : methods) {
			if (m.getName().startsWith("get")) {
				String setter = "s" + m.getName().substring(1);
				if (setters.containsKey(setter)) {
					if (!"getId".equals(m.getName()))
						fields.put(m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4), m);
				}
			}
		}

		return fields;
	}

}
