/*
 * $Id: IWViewHandlerImpl.java,v 1.15 2007/09/08 13:16:20 civilis Exp $
 * Created on 12.3.2004 by  tryggvil in project smile
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */

package com.idega.faces;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;

import com.idega.core.file.util.MimeTypeUtil;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.core.view.ViewNodeBase;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.repository.RepositoryService;
import com.idega.repository.event.RepositoryResourceLocalizer;
import com.idega.util.FacesUtil;
import com.idega.util.expression.ELUtil;


/**
 * This is the main JSF ViewHandler implementation for idegaWeb.<br>
 * The instance of this class handles the idegaWeb specific urls if it detects one
 * and uses the ViewNode structure to handle that.<br>
 * If there is not an incoming idegaWeb request coming in it delegates the
 * calls to the underlying system ViewHandler.<br>
 *
 * Copyright (C) idega software 2004<br>
 *
 * Last modified: $Date: 2007/09/08 13:16:20 $ by $Author: civilis $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.15 $
 */
public class IWViewHandlerImpl extends ViewHandler{

	private static Logger log = Logger.getLogger(IWViewHandlerImpl.class.getName());
	private ViewHandler parentViewHandler;
	private ViewManager viewManager;
	private ViewHandler jspViewHandler;
	private ViewHandler faceletsViewHandler;

	public IWViewHandlerImpl(){
		log.info("Loading IWViewHandlerImpl");
	}

	public IWViewHandlerImpl(ViewHandler parentViewHandler,IWMainApplication iwma){
		log.info("Loading IWViewHandlerImpl with constructor IWViewHandlerImpl(ViewHandler parentViewHandler)");
		this.setParentViewHandler(parentViewHandler);
		updateViewManagerViewHandler(iwma);
	}

	protected void updateViewManagerViewHandler(IWMainApplication iwma){
		//This updates the viewhandler Instance that the root viewnode has.
		// the ViewHandler before this is just the system ViewHandler
		this.viewManager = ViewManager.getInstance(iwma);
		ViewNode root = this.viewManager.getApplicationRoot();
		DefaultViewNode dRoot = (DefaultViewNode)root;
		dRoot.setViewHandler(new RootNodeViewHandler(this.getParentViewHandler()));
	}

	@Override
	public Locale calculateLocale(FacesContext ctx) {
		IWContext iwc = IWContext.getIWContext(ctx);
		Locale locale = iwc.getCurrentLocale();
		return locale;
	}

	@Override
	public String calculateRenderKitId(FacesContext ctx) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler != null) {
			return realHandler.calculateRenderKitId(ctx);
		} else {
			throw new RuntimeException ("No ViewHandler Found to calculate RenderKitId");
		}
	}

	@Override
	public UIViewRoot createView(FacesContext ctx, String viewId) {
		FacesUtil.registerRequestBegin(ctx);
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler != null) {
			UIViewRoot root = realHandler.createView(ctx,viewId);
			root.setLocale(calculateLocale(ctx));
			return root;
		} else {
			throw new RuntimeException ("No ViewHandler Found to create View");
		}
	}

	protected ViewHandler getViewHandlerForContext(FacesContext ctx) {
		ViewNode node = getViewManager().getViewNodeForContext(ctx);

		if (node != null) {
			ViewNodeBase viewBase = node.getViewNodeBase();
			if (viewBase == null) {
				return null;
			}

			switch (viewBase) {
				case JSP: {
					return jspViewHandler;
				}
				case FACELET : {
					return faceletsViewHandler;
				}
				default: {
					return node.getViewHandler();
				}
			}
		}

		if (getParentViewHandler() != null) {
			return getParentViewHandler();
		}

		throw new RuntimeException("No parent ViewHandler");
	}

	/**
	 * @param url
	 * @return
	 */
	/*private ViewHandler getViewHandlerForUrl(String url,FacesContext ctx) {

		ViewNode node = getViewManager().getViewNodeForUrl(url);
		if(node!=null){
			if(node.isJSP()){
				//try {
					//HttpServletRequest request = (HttpServletRequest)ctx.getExternalContext().getRequest();
					//HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
					//try {
					//	request.setParameter("isForwarding","true");
					//	request.getRequestDispatcher(node.getJSPURI()).include(request,response);
					//}
					//catch (ServletException e1) {
					//	e1.printStackTrace();
					//}
					//String uri = node.getJSPURI();
					//ctx.getViewRoot().setViewId(node.getJSPURI());
					//ctx.getExternalContext().dispatch(uri);
					return this.jspViewHandler;
					//ctx.responseComplete();
				//}
				//catch (IOException e) {
				//	e.printStackTrace();
				//}
			}
			return node.getViewHandler();
		}
		return null;
	}*/

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#getActionURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	@Override
	public String getActionURL(FacesContext ctx, String viewId) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler != null) {
			return realHandler.getActionURL(ctx,viewId);
		} else {
			throw new RuntimeException ("No ViewHandler Found for getActionURL");
		}
	}
	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext, java.lang.String)
	 */
	@Override
	public String getResourceURL(FacesContext ctx, String path) {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler != null) {
			return realHandler.getResourceURL(ctx,path);
		} else {
			throw new RuntimeException ("No ViewHandler Found for getResourceURL");
		}
	}

	@Override
	public void renderView(FacesContext ctx, UIViewRoot viewId) throws IOException, FacesException {
		FacesUtil.registerRequestBegin(ctx);
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler != null) {
			realHandler.renderView(ctx, viewId);

			IWContext iwc = IWContext.getIWContext(ctx);
			Object localizations = iwc.getRequest().getAttribute(RepositoryService.REQUEST_LOCALIZATIONS);
			if (localizations instanceof RepositoryResourceLocalizer) {
				try {
					ELUtil.getInstance().publishEvent((RepositoryResourceLocalizer) localizations);
				} catch (Exception e) {
					log.log(Level.WARNING, "Error publishing event " + localizations, e);
				}
			}
		} else {
			throw new RuntimeException ("No ViewHandler Found for getResourceURL");
		}
	}

	/**
	 * @see javax.faces.application.ViewHandler#renderView(javax.faces.context.FacesContext, javax.faces.component.UIViewRoot)
	 */
	protected void cbpRenderView(FacesContext ctx, UIViewRoot viewRoot) throws IOException, FacesException {
		// Apparently not all versions of tomcat have the same default content-type.  So we'll set it explicitly.
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		response.setContentType(MimeTypeUtil.MIME_TYPE_HTML);

		if (viewRoot == null)
			throw new RuntimeException("No component tree is available in " + ctx.getExternalContext().getRequest() + "!");

		String renderkitId = viewRoot.getRenderKitId();
		if (renderkitId == null)
			renderkitId = calculateRenderKitId(ctx);
		viewRoot.setRenderKitId(renderkitId);

		ResponseWriter out = ctx.getResponseWriter();
		try {
			out.startDocument();
			renderComponent(ctx.getViewRoot(), ctx);
			out.endDocument();
			ctx.getResponseWriter().flush();
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	/**
	 * Recursive operation to render a specific component in the view tree.
	 *
	 * @param component
	 * @param context
	 */
	private void renderComponent(UIComponent component, FacesContext ctx) {
		try {
			component.encodeBegin(ctx);
			if (component.getRendersChildren()) {
				component.encodeChildren(ctx);
			} else {
				for (Iterator<UIComponent> it = component.getChildren().iterator(); it.hasNext();) {
					renderComponent(it.next(), ctx);
				}
			}

			component.encodeEnd(ctx);
		} catch(IOException e) {
			log.severe("Component <" + component.getId() + "> could not render ! Continuing rendering of view <" + ctx.getViewRoot().getViewId() + ">...");
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#restoreView(javax.faces.context.FacesContext, java.lang.String)
	 */
	@Override
	public UIViewRoot restoreView(FacesContext ctx, String viewId) {
		FacesUtil.registerRequestBegin(ctx);
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler!=null) {
			UIViewRoot root = realHandler.restoreView(ctx,viewId);
			if (root != null) {
				root.setLocale(calculateLocale(ctx));
			}
			return root;
		} else {
			throw new RuntimeException ("No ViewHandler Found for restoreView");
		}
	}

/* (non-Javadoc)
	 * @see javax.faces.application.ViewHandler#writeState(javax.faces.context.FacesContext)
	 */
	@Override
	public void writeState(FacesContext ctx) throws IOException {
		ViewHandler realHandler = getViewHandlerForContext(ctx);
		if (realHandler != null) {
			realHandler.writeState(ctx);
		} else {
			throw new RuntimeException ("No ViewHandler Found for writeState");
		}
	}

	/**
	 * @return Returns the defaultViewHandler.
	 */
	public ViewHandler getParentViewHandler() {
		return this.parentViewHandler;
	}

	/**
	 * @param defaultViewHandler The defaultViewHandler to set.
	 */
	public void setParentViewHandler(ViewHandler parentViewHandler) {
		jspViewHandler = new IWJspViewHandler(parentViewHandler);
		faceletsViewHandler = new IWFaceletsViewHandler(parentViewHandler);
		this.parentViewHandler = parentViewHandler;
	}

	/**
	 * @return Returns the viewManager.
	 */
	protected ViewManager getViewManager() {
		return this.viewManager;
	}

	public ViewHandler getFaceletsViewHandler() {
		return faceletsViewHandler;
	}

	public void setFaceletsViewHandler(ViewHandler faceletsViewHandler) {
		this.faceletsViewHandler = faceletsViewHandler;
	}

}
