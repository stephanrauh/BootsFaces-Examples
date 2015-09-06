package de.beyondjava.bootsfaces.examples;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "AJAXBean")
@SessionScoped
public class AJAXBean {
	private int counter = 0;

	private String lastMessage = null;

	private List<String> messages = new ArrayList<>();

	private boolean bool;
	private boolean bool1;
	private boolean bool2;
	private boolean bool3;
	private boolean bool4;
	
	private boolean spinning=true;
	private boolean readOnly=false;
	private boolean disabled=true;
	private boolean buttonRed=false;

	public boolean isSpinning() {
		return spinning;
	}

	public void setSpinning(boolean spinning) {
		this.spinning = spinning;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isBool1() {
		return bool1;
	}

	public void setBool1(boolean bool1) {
		this.bool1 = bool1;
	}

	public boolean isBool2() {
		return bool2;
	}

	public void setBool2(boolean bool2) {
		this.bool2 = bool2;
	}

	public boolean isBool3() {
		return bool3;
	}

	public void setBool3(boolean bool3) {
		this.bool3 = bool3;
	}

	public boolean isBool4() {
		return bool4;
	}

	public void setBool4(boolean bool4) {
		this.bool4 = bool4;
	}

	private String input = "Ignore the text. It's not important.";

	public AJAXBean() {
		getMessages().add("No message yet.");
		getMessages().add("Play with the combobox to add messages.");
	}

	private int brand = 1;

	public void clearMessages() {
		messages.clear();
		messages.add("Messages cleared by tab change AJAX event");
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
		String event = " change";
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
		return null; // "landingPage.jsf";
	}
	
	public void facetListener(javax.faces.event.AjaxBehaviorEvent event) throws javax.faces.event.AbortProcessingException {
		report("f:ajax listener called");
	}
	
	public void mouseOverListener(javax.faces.event.AjaxBehaviorEvent event) throws javax.faces.event.AbortProcessingException {
		report("f:ajax mouseOver listener called");
		buttonRed=true;
	}

	
	public void mouseOutListener(javax.faces.event.AjaxBehaviorEvent event) throws javax.faces.event.AbortProcessingException {
		report("f:ajax mouseOut listener called");
		buttonRed=false;
	}


	public void standardJSFActionListener() {
		report("Standard JSF actionlistener without parameters called");
	}
	public void standardJSFActionListener(ActionEvent even) {
		report("Standard JSF actionlistener called");
	}

	public boolean isButtonRed() {
		return buttonRed;
	}

	public void setButtonRed(boolean buttonRed) {
		this.buttonRed = buttonRed;
	}
}
