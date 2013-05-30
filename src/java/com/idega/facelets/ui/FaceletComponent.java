package com.idega.facelets.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWBaseComponent;
import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.StringUtil;
import com.idega.util.expression.ELUtil;
import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;

/**
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/02/14 21:29:08 $ by $Author: civilis $
 *
 */
public class FaceletComponent extends IWBaseComponent {

	private String faceletURI, bundleIdentifier, faceletFile;

	private static final String faceletFactoryFactoryBeanId = "faceletFactoryFactory";
	public static final String COMPONENT_TYPE = "FaceletComponent";

	public FaceletComponent() { }

	public FaceletComponent(String faceletURI) {
		setFaceletURI(faceletURI);
	}

	@Override
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);

		String resourceURI = getFaceletURI();
		if (StringUtil.isEmpty(resourceURI))
			return;

		IWBundleResourceFilter.checkCopyOfResourceToWebapp(context, resourceURI);
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);

		try {
			String resourceURI = getFaceletURI();

			if (StringUtil.isEmpty(resourceURI))
				return;

			List<UIParameter> paramList = new ArrayList<UIParameter>();
			for (UIComponent c: getChildren()) {
				if (c instanceof UIParameter) {
					paramList.add((UIParameter) c);
				}
			}

			FaceletFactory faceletFactory = ((FaceletFactoryFactory) getBeanInstance(faceletFactoryFactoryBeanId)).createFaceletFactory(paramList);
			Facelet facelet = faceletFactory.getFacelet(resourceURI);

			facelet.apply(context, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[4];
		values[0] = super.saveState(ctx);
		values[1] = faceletURI;
		values[2] = bundleIdentifier;
		values[3] = faceletFile;
		return values;
	}

	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		faceletURI = (String) values[1];
		bundleIdentifier = (String) values[2];
		faceletFile = (String) values[3];
	}

	public String getFaceletURI() {
		if (faceletURI != null)
			return faceletURI;

		if (faceletURI == null)	{
			ValueExpression vb = getValueExpression("faceletURI");
			if (vb != null)			{
			    return (String) vb.getValue(getFacesContext().getELContext());
			}
	    }
		if (ELUtil.isExpression(faceletURI)) {
			FacesContext fContext = FacesContext.getCurrentInstance();
	    	ValueExpression valExpr = ELUtil.createValueExpression(fContext, faceletURI, String.class);
			return (String) valExpr.getValue(fContext.getELContext());
	    }

		String bundleIdentifier = getBundleIdentifier();
		String faceletFile = getFaceletFile();
		if (!StringUtil.isEmpty(bundleIdentifier) && !StringUtil.isEmpty(faceletFile)) {
			IWBundle bundle = IWMainApplication.getDefaultIWMainApplication().getBundle(bundleIdentifier);
			faceletURI = bundle.getFaceletURI(faceletFile);
		}

		return faceletURI;
	}

	public void setFaceletURI(String faceletURI) {
		if (this.faceletURI != null)
			throw new UnsupportedOperationException("Facelet URI already set. Create new component. URI: "+this.faceletURI);

		this.faceletURI = faceletURI;
	}

	public String getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setBundleIdentifier(String bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	public String getFaceletFile() {
		return faceletFile;
	}

	public void setFaceletFile(String faceletFile) {
		this.faceletFile = faceletFile;
	}

}