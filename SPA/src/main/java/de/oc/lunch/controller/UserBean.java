package de.oc.lunch.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.EntityManager;

import de.oc.lunch.database.DataBase;
import de.oc.lunch.database.LunchDB;
import de.oc.lunch.persistence.UserEntity;

@Named
@SessionScoped
public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject @LunchDB
	private EntityManager entityManager = null;

	private UserEntity filter = new UserEntity();
	
	private List<UserEntity> users;
		
	public UserBean() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void init() {
		filter();
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public UserEntity getFilter() {
		return filter;
	}

	public void filter() {
		users = filter.findBy(entityManager);
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
}
