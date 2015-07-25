package de.beyondjava.bootsfaces.examples;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ViewScoped
@ManagedBean
public class User {
	
	private int year;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
	
	public void validate() {
		if (year > 0) {
			FacesMessage success = new FacesMessage("Thank you!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
	}
}
