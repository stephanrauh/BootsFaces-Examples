package de.beyondjava.bootsfaces.chess.common;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class Settings {
	private static int lookAhead = 4;
	private static int movesToConsider = 5;
	private static boolean multithreading = false;

	private boolean economyMode = true;

	public void unlockPowerMode() {
		setEconomyMode(false);
	}

	public int getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(int lookAhead) {
		if (isEconomyMode())
			if (lookAhead > 4) {
				lookAhead = 4;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Maximum look-ahead is 4 in economy mode. Run this application on your own PC to unlock the full power of the chess engine."));
			}
		Settings.lookAhead = lookAhead;
	}

	public int getMovesToConsider() {
		return movesToConsider;
	}

	public void setMovesToConsider(int movesToConsider) {
		if (isEconomyMode())
			if (movesToConsider > 5) {
				movesToConsider = 5;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Maximum value is 5 in economy mode. Run this application on your own PC to unlock the full power of the chess engine."));
			}
		Settings.movesToConsider = movesToConsider;
	}

	public boolean isMultithreading() {
		return multithreading;
	}

	public void setMultithreading(boolean multithreading) {
		if (isEconomyMode()) {
			if (multithreading) {
				multithreading = false;
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Can't activate multithreading in economy mode. Run this application on your own PC to unlock the full power of the chess engine."));
			}
		}
		Settings.multithreading = multithreading;
	}

	public boolean isEconomyMode() {
		return economyMode;
	}

	public void setEconomyMode(boolean economyMode) {
		this.economyMode = economyMode;
	}
}
