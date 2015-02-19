package com.idega.facelets.ui.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.presentation.IWBaseComponent;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.PresentationUtil;
import com.idega.util.StringUtil;

/**
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2009/03/08 13:32:22 $ by $Author: valdas $
 *
 */
public class AddResource extends IWBaseComponent {

	public static final String resourcePositionHeader = "header";
	public static final String javascriptFileExt = ".js";
	public static final String cssFileExt = ".css";

	private static final String resourcePathProperty = "resourcePath";
	private String resourcePath;
	private String resourceLocation = resourcePositionHeader;
	private String resourceType;

	public String getResourcePath() {
		return resourcePath;
	}

	public String getResourcePath(FacesContext context) {
		ValueExpression ve = getValueExpression(resourcePathProperty);
		if(ve != null){
			resourcePath = ve.getValue(context.getELContext()).toString();
		}
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		if (resourcePath != null)
			this.resourcePath = resourcePath;
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);

		String resourcePath = getResourcePath(context);

		if (resourcePath != null && !CoreConstants.EMPTY.equals(resourcePath)) {
			IWMainApplicationSettings settings = IWContext.getIWContext(context).getIWMainApplication().getSettings();
			String disabledResources = settings.getProperty("disabled_facelet_resources");
			if (!StringUtil.isEmpty(disabledResources)) {
				List<String> resources = Arrays.asList(disabledResources.split(CoreConstants.COMMA));
				if (resources.contains(resourcePath))
					return;
			}

			String resourceLocation = getResourceLocation();
			if (resourceLocation == null || CoreConstants.EMPTY.equals(resourceLocation))
				resourceLocation = resourcePositionHeader;

			IWContext iwc = IWContext.getIWContext(context);
			String resourceType = getResourceType(resourcePath);
			if (resourcePath.endsWith(javascriptFileExt) || resourceType.equals("javascript"))
				PresentationUtil.addJavaScriptSourceLineToHeader(iwc, resourcePath);
			else if(resourceType.equals("css"))
				PresentationUtil.addStyleSheetToHeader(iwc, resourcePath);
		}
	}

	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[2];
		values[0] = super.saveState(ctx);
		values[1] = this.resourcePath;
		return values;
	}

	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(ctx, values[0]);
		this.resourcePath = ((String) values[1]);
	}

	public String getResourceType(String resourcePath) {
		if(resourceType != null){
			return resourceType;
		}
		if(resourcePath.endsWith(javascriptFileExt))
			resourceType = "javascript";
		else if(resourcePath.endsWith(cssFileExt))
			resourceType = "css";
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
}