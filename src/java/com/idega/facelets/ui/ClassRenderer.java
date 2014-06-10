package com.idega.facelets.ui;

import java.io.IOException;
import java.util.logging.Level;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.presentation.IWBaseComponent;
/**
 * 
 * This component simply renders other component that is specified.
 *
 */
public class ClassRenderer extends IWBaseComponent {
	
	private String renderedComponent;
	private FacesContext facesContext;

	private void addComponent(FacesContext context){
		try {
			if(renderedComponent == null){
				return;
			}
			Class componentClass = Class.forName(renderedComponent);
			UIComponent component = (UIComponent) componentClass.newInstance();
			add(component);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Failed creating component by class:" + renderedComponent, e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		setFacesContext(context);
		addComponent(context);
		super.encodeBegin(context);
	}
	public String getRenderedComponent() {
		if(renderedComponent != null){
			return renderedComponent;
		}
		renderedComponent = getExpressionValue(getFacesContext(),"renderedComponent");
		return renderedComponent;
	}
	public void setRenderedComponent(String renderedComponent) {
		this.renderedComponent = renderedComponent;
	}
	

	@Override
	public FacesContext getFacesContext() {
		return facesContext;
	}

	public void setFacesContext(FacesContext facesContext) {
		this.facesContext = facesContext;
	}
	
}
