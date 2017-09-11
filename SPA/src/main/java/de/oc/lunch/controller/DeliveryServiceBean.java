package de.oc.lunch.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import de.oc.lunch.database.LunchDB;
import de.oc.lunch.persistence.DeliveryServiceEntity;

@SessionScoped
//@ViewScoped
@Named
public class DeliveryServiceBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private List<DeliveryServiceEntity> deliveryServices;

	@Inject @LunchDB
	private EntityManager entityManager = null;
	
	private DeliveryServiceEntity filter = new DeliveryServiceEntity();

	@PostConstruct
	public void init() {
		deliveryServices = new DeliveryServiceEntity().findAll(entityManager);
	}

	public List<DeliveryServiceEntity> getDeliveryServices() {
		return deliveryServices;
	}

	public void setDeliveryServices(List<DeliveryServiceEntity> deliveryServices) {
		this.deliveryServices = deliveryServices;
	}
	
	public void filter() {
		deliveryServices = getFilter().findBy(getEntityManager());
	}

	public DeliveryServiceEntity getFilter() {
		return filter;
	}

	public void setFilter(DeliveryServiceEntity filter) {
		this.filter = filter;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}



}
