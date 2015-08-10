package de.beyondjava.bootsfaces.issue145;
public class Item {

    private long id;
    private String value;

    public Item() {
        // TODO Auto-generated constructor stub
    }

    public Item(long id, String value) {
        super();
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    }