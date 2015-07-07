package de.beyondjava.bootsfaces.examples.f5recognizer;

import java.awt.event.ActionEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean
public class F5Recognizer {
	private String previousPage = null;
	
	public void checkF5() {
		String msg="";
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		if (null != viewRoot) {
			String id = viewRoot.getViewId();
			if (previousPage==null) {
				msg="First page ever";
			}
			else if (previousPage.equals(id)) {
				msg="F5 or reload";
			}
			else if (FacesContext.getCurrentInstance().isPostback()) {
				msg="It's a postback";
			}
			else msg="It's a navigation";
			previousPage=id;
			FacesMessage fm = new FacesMessage(msg);
			FacesContext.getCurrentInstance().addMessage(null,  fm);
		}
	}
}
