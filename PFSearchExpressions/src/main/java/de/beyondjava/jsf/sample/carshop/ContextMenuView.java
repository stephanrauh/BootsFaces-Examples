package de.beyondjava.jsf.sample.carshop;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

@ManagedBean(name="dtContextMenuView")
@SessionScoped
public class ContextMenuView implements Serializable {

	@ManagedProperty(value="#{carPool.carPool}")
    private List<Car> cars;
     
    private Car selectedCar;
     
    public List<Car> getCars() {
        return cars;
    }
 
    public Car getSelectedCar() {
        return selectedCar;
    }
 
    public void setSelectedCar(Car selectedCar) {
        this.selectedCar = selectedCar;
    }
 
    public void deleteCar() {
        getCars().remove(selectedCar);
        selectedCar = null;
    }

	public void setCars(List<Car> cars) {
		this.cars = cars;
	}
}