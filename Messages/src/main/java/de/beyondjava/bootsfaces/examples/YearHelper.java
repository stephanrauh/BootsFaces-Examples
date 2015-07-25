package de.beyondjava.bootsfaces.examples;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class YearHelper {
	private List<Integer> allYears;
	
	public YearHelper(){
		allYears = new ArrayList<>();
		for (int i = 18; i < 68; i++)
		{
			getAllYears().add(i);
		}
	}

	public List<Integer> getAllYears() {
		return allYears;
	}

	public void setAllYears(List<Integer> allYears) {
		this.allYears = allYears;
	}
}    
