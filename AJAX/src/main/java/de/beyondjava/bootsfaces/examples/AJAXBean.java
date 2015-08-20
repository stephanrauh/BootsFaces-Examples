package de.beyondjava.bootsfaces.examples;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="AJAXBean")
@SessionScoped
public class AJAXBean {
	private int counter = 0;

	private String lastMessage = null;

	private List<String> messages = new ArrayList<>();

	private boolean bool;

	private String input = "Ignore the text. It's not important.";

	public AJAXBean() {
		getMessages().add("No message yet.");
		getMessages().add("Play with the combobox to add messages.");
	}

	private int brand = 1;
	
	public void clearMessages() {
		messages.clear();
	}

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
		String event = " blur";
		report(event);
		return "Hallo";
	}

	private void report(String event) {
		if (event.equals(lastMessage)) {
			counter++;
			messages.set(0, now() + event + " (" + counter + ")");
		} else {
			counter = 1;
			messages.add(0, now() + event);
		}
		lastMessage = event;

	}

	public String onChange() {
		String event = " c	hange";
		report(event);
		return "Hallo";
	}

	public String onValueChange() {
		String event = " valueChange";
		report(event);
		return "Hallo";
	}

	public String onClick() {
		String event = " click";
		report(event);
		return "Hallo";
	}

	public String onDblClick() {
		String event = " dblclick";
		report(event);
		return "Hallo";
	}

	public String onFocus() {
		String event = " focus";
		report(event);
		return "Hallo";
	}

	public String onKeyDown() {
		String event = " keydown";
		report(event);
		return "Hallo";
	}

	public String onKeyPress() {
		String event = " keypress";
		report(event);
		return "Hallo";
	}

	public String onKeyUp() {
		String event = " keyup";
		report(event);
		return "Hallo";
	}

	public String onMouseDown() {
		String event = " mousedown";
		report(event);
		return "Hallo";
	}

	public String onMouseMove() {
		String event = " mousemove";
		report(event);
		return "Hallo";
	}

	public String onMouseOut() {
		String event = " mouseout";
		report(event);
		return "Hallo";
	}

	public String onMouseOver() {
		String event = " mouseover";
		report(event);
		return "Hallo";
	}

	public String onMouseUp() {
		String event = " mouseup";
		report(event);
		return "Hallo";
	}

	public String onSelect() {
		String event = " select";
		report(event);
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

	public String standardJSFAction() {
		report("Standard JSF action called");
		return null;
	}

	public String standardJSFActionListener() {
		report("Standard JSF actionlistener called");
		return null;
	}
}
