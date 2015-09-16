package de.beyondjava.bootsfaces.examples;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Sobean implements Serializable{
	private String page="page0";
	public void page0() {
		page="page0";
	}
	public void page1() {
		page="page1";
	}
	public void page2() {
		page="page2";
	}
	public void page3() {
		page="page3";
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}

}
