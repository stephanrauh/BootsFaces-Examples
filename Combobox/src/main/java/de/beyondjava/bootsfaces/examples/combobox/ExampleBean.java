package de.beyondjava.bootsfaces.examples.combobox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author developer
 */
@SessionScoped
@ManagedBean
public class ExampleBean implements Serializable {
//    private static final long serialVersionUID = 1L;

    private List<String> words;
    private String word = "";

    @PostConstruct
    public void init() {
        words = new ArrayList<>();
        words.add("Car");
        words.add("Phone");
        words.add("Tree");
        words.add("Home");
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

}