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
		if (year > 50) {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Thank you!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
		else if (year > 30) {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Hey, you're in your best years!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
		else if (year > 0) {
			FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Enjoy your youth!");
			FacesContext.getCurrentInstance().addMessage(null, success);
		}
	}
}
