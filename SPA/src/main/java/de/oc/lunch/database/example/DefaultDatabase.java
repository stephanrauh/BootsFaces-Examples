package de.oc.lunch.database.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import de.oc.lunch.database.DataBase;
import de.oc.lunch.persistence.DeliveryServiceEntity;
import de.oc.lunch.persistence.UserEntity;

@WebListener
public class DefaultDatabase implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(DefaultDatabase.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		DataBase.getEmf().close();
		try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:lunch", "robina", "kuh")) {
			Statement st = conn.createStatement();
			st.execute("SHUTDOWN");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		EntityManager entityManager = new DataBase().createEntityManager();
		populateUserTable(entityManager);
		readUserTable(entityManager);
		populateDeliveryServiceTable(entityManager);
		entityManager.close();
	}

	public void populateUserTable(EntityManager entityManager) {
		
		EntityTransaction transaction = entityManager.getTransaction();
		try {
			transaction.begin();
			createUser(entityManager, "Robina Kuh", "robina.kuh@example.com");
			createUser(entityManager, "Wayne Interessierts", "wayne.interessierts@example.com");
			createUser(entityManager, "John Doe", "john.doe@example.com");
			createUser(entityManager, "Jane Dull", "jane.dull@example.com");
			List<String> names = Names.getNames();
			for (String name:names) {
				createUser(entityManager, name, name.replace(" ", ".")+"@example.com");
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		}
		
	}

	public void populateDeliveryServiceTable(EntityManager entityManager) {
		DeliveryServiceEntity service = new DeliveryServiceEntity("Lieferheld", "www.lieferheld.de");
		service.persist(entityManager);
		service = new DeliveryServiceEntity("BeyondJava", "www.beyondjava.net");
		service.persist(entityManager);
		if (false) {
			List<DeliveryServiceEntity> services = new DeliveryServiceEntity().findAll(entityManager);
			LOGGER.info(services.size());
		}
	}

	public void readUserTable(EntityManager entityManager) {
		TypedQuery<UserEntity> query = entityManager.createQuery("from UserEntity", UserEntity.class);
		List<UserEntity> resultList = query.getResultList();
		if (false) {
			resultList.stream()
					.forEach(user -> LOGGER.info(user.getId() + " " + user.getName() + " " + user.getEmail()));
			new UserEntity().findAll(entityManager).stream()
					.forEach(user -> LOGGER.info(user.getId() + " " + user.getName() + " " + user.getEmail()));
		}
	}

	private void createUser(EntityManager entityManager, String name, String email) {
		UserEntity jane = new UserEntity();
		jane.setName(name);
		jane.setEmail(email);
		entityManager.persist(jane);
	}
}
