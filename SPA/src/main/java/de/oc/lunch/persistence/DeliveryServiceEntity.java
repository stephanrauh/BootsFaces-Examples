package de.oc.lunch.persistence;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import javax.validation.constraints.Size;

import de.oc.lunch.database.example.DefaultDatabase;

@Entity
public class DeliveryServiceEntity implements Serializable, PersistentObject<DeliveryServiceEntity> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id; // = UUID.randomUUID().getLeastSignificantBits();

	@Size(max=30)
	private String name;

	@Size(max=100)
	private String website;

	public DeliveryServiceEntity() {
	}

	public DeliveryServiceEntity(String name, String website) {
		this.name = name;
		this.website = website;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
}
