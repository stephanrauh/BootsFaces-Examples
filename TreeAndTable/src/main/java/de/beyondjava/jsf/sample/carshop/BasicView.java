package de.beyondjava.jsf.sample.carshop;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean(name = "treeBasicView")
@ViewScoped
public class BasicView implements Serializable {

	private TreeNode root;

	@ManagedProperty("#{dynamicOptionBean}")
	private DynamicOptionBean dynamicOptions;

	@ManagedProperty("#{staticOptionBean}")
	private StaticOptionBean staticOptions;

	public StaticOptionBean getStaticOptions() {
		return staticOptions;
	}

	public void setStaticOptions(StaticOptionBean staticOptions) {
		this.staticOptions = staticOptions;
	}

	public DynamicOptionBean getDynamicOptions() {
		return dynamicOptions;
	}

	public void setDynamicOptions(DynamicOptionBean dynamicOptions) {
		this.dynamicOptions = dynamicOptions;
	}

	@PostConstruct
	public void init() {
		root = new DefaultTreeNode("Root", null);
		TreeNode all = new DefaultTreeNode("Car pool", root);
		List<String> brands = staticOptions.getBrands();
		for (String brand : brands) {
			if (brand != null && brand.length() > 0) {
				TreeNode b = new DefaultTreeNode(brand, all);
				List<String> types = dynamicOptions.getTypesToBrand(brand);
				for (String type : types) {
					if (type != null && type.length() > 0) {
						TreeNode t = new DefaultTreeNode(type, b);
					}
				}
			}
		}
	}

	public TreeNode getRoot() {
		return root;
	}
}