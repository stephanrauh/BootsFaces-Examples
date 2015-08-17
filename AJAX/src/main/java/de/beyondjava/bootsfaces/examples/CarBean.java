package de.beyondjava.bootsfaces.examples;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class CarBean {
	
	private List<String> messages = new ArrayList<>();
	
	private boolean bool;
	
	private String input = "Ignore the text. It's not important.";
	
	public CarBean() {
		getMessages().add("No message yet.");
		getMessages().add("Play with the combobox to add messages.");
	}
	private int brand=1;

	public int getBrand() {
		return brand;
	}
	
	private String now() {
		return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
	}

	public void setBrand(int brand) {
		this.brand = brand;
		messages.add(0, now() + " Setter has been called: " + brand);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public String onBlur() {
		messages.add(0, now() + " blur");
		return "Hallo";
	}

	public String onChange() {
		messages.add(0, now() + " onChange");
		return "Hallo";
	}
	public String onValueChange() {
		messages.add(0, now() + " valueChange");
		return "Hallo";
	}
	public String onClick() {
		messages.add(0, now() + " click");
		return "Hallo";
	}
	public String onDblClick() {
		messages.add(0, now() + " dblclick");
		return "Hallo";
	}
	public String onFocus() {
		messages.add(0, now() + " focus");
		return "Hallo";
	}
	public String onKeyDown() {
		messages.add(0, now() + " keydown");
		return "Hallo";
	}
	public String onKeyPress() {
		messages.add(0, now() + " keypress");
		return "Hallo";
	}
	public String onKeyUp() {
		messages.add(0, now() + " keyup");
		return "Hallo";
	}
	public String onMouseDown() {
		messages.add(0, now() + " mousedown");
		return "Hallo";
	}
	public String onMouseMove() {
		messages.add(0, now() + " mousemove");
		return "Hallo";
	}
	public String onMouseOut() {
		messages.add(0, now() + " mouseout");
		return "Hallo";
	}
	public String onMouseOver() {
		messages.add(0, now() + " mouseover");
		return "Hallo";
	}
	public String onMouseUp() {
		messages.add(0, now() + " mouseup");
		return "Hallo";
	}
	public String onSelect() {
		messages.add(0, now() + " select");
		return "Hallo";
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
}
