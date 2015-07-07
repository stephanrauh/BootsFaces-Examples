package de.beyondjava.bootsfaces.examples.combobox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@SessionScoped
@ManagedBean
public class ThemeSelectorBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> themes;
    private String theme = "default";

    @PostConstruct
    public void init() {
        themes=new ArrayList<>();
        themes.add("cosmo");
        themes.add("flatly");
        themes.add("darkly");
        themes.add("journal");
        themes.add("sandstone");
        themes.add("united");
        themes.add("default");
    }

    public boolean isBootswatchTheme() {
    	return !"default".equals(theme);
    }

	public List<String> getThemes() {
		return themes;
	}

	public void setThemes(List<String> themes) {
		this.themes = themes;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

}