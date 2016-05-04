package com.idega.facelets.ui.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.idega.servlet.filter.IWBundleResourceFilter;
import com.idega.util.expression.ELUtil;

/**
 * simple hack for resolving iw resources, as el doesn't support parameters in method vbs.
 *
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/02/14 15:48:48 $ by $Author: civilis $
 *
 */
public class IWUIResource {

	IWUIResourceMap resMap;

	public Map<String, String> getRes() {
		if (resMap == null) {
			resMap = new IWUIResourceMap();
		}

		return resMap;
	}

	public class IWUIResourceMap implements Map<String, String> {

		@Override
		public void clear() {
		}

		@Override
		public boolean containsKey(Object obj) {
			return false;
		}

		@Override
		public boolean containsValue(Object obj) {
			return false;
		}

		@Override
		public Set<java.util.Map.Entry<String, String>> entrySet() {
			return null;
		}

		@Override
		public String get(Object obj) {
			String resourceURI = String.valueOf(obj);

			if (ELUtil.isExpression(resourceURI)) {
				try {
					resourceURI = (String) ELUtil.getInstance().evaluateExpression(resourceURI);
				} catch (Exception e) {
					Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error evaluating expression: " + resourceURI, e);
				}
			}

			IWBundleResourceFilter.checkCopyOfResourceToWebapp(FacesContext.getCurrentInstance(), resourceURI);

			return resourceURI;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public Set<String> keySet() {
			return null;
		}

		@Override
		public String put(String key, String value) {
			return null;
		}

		@Override
		public void putAll(Map<? extends String, ? extends String> t) {
		}

		@Override
		public String remove(Object obj) {
			return null;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public Collection<String> values() {
			return null;
		}
	}
}