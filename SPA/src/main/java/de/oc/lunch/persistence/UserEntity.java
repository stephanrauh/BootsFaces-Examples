package de.oc.lunch.persistence;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import de.oc.lunch.database.LunchDB;

@Entity
@Named
@SessionScoped
public class UserEntity implements Serializable, PersistentObject<UserEntity> {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id; // = UUID.randomUUID().getLeastSignificantBits();

	@Size(max = 30)
	private String name;

	@Size(max = 100)
	private String email;

	public UserEntity() {
	}

	public UserEntity(String name, String email) {
		this.name = name;
		this.email = email;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
