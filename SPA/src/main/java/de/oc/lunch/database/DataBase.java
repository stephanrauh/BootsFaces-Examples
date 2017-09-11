package de.oc.lunch.database;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Transient;

@SessionScoped
@Named
public class DataBase implements Serializable {
	private static final long serialVersionUID = 1L;

	private static EntityManagerFactory emf = null;

	@Transient
	private EntityManager entityManager = null;

	public EntityManagerFactory createEntityManagerFactory() {
		if (null == getEmf()) {
			setEmf(Persistence.createEntityManagerFactory("lunch"));
		}
		return getEmf();
	}

	@Produces @LunchDB
	public EntityManager createEntityManager() {
		if (null == getEmf()) {
			setEmf(Persistence.createEntityManagerFactory("lunch"));
		}
		if (null == entityManager) {
			entityManager = getEmf().createEntityManager();
		} else if (!entityManager.isOpen()) {
			entityManager = getEmf().createEntityManager();
		}
		return entityManager;
	}

	public static EntityManagerFactory getEmf() {
		return emf;
	}

	public static void setEmf(EntityManagerFactory emf) {
		DataBase.emf = emf;
	}

}
