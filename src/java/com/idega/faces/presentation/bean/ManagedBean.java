/**
 * @(#)ManagedBean.java    1.0.0 21:43:47
 *
 * Idega Software hf. Source Code Licence Agreement x
 *
 * This agreement, made this 10th of February 2006 by and between 
 * Idega Software hf., a business formed and operating under laws 
 * of Iceland, having its principal place of business in Reykjavik, 
 * Iceland, hereinafter after referred to as "Manufacturer" and Agura 
 * IT hereinafter referred to as "Licensee".
 * 1.  License Grant: Upon completion of this agreement, the source 
 *     code that may be made available according to the documentation for 
 *     a particular software product (Software) from Manufacturer 
 *     (Source Code) shall be provided to Licensee, provided that 
 *     (1) funds have been received for payment of the License for Software and 
 *     (2) the appropriate License has been purchased as stated in the 
 *     documentation for Software. As used in this License Agreement, 
 *     Licensee shall also mean the individual using or installing 
 *     the source code together with any individual or entity, including 
 *     but not limited to your employer, on whose behalf you are acting 
 *     in using or installing the Source Code. By completing this agreement, 
 *     Licensee agrees to be bound by the terms and conditions of this Source 
 *     Code License Agreement. This Source Code License Agreement shall 
 *     be an extension of the Software License Agreement for the associated 
 *     product. No additional amendment or modification shall be made 
 *     to this Agreement except in writing signed by Licensee and 
 *     Manufacturer. This Agreement is effective indefinitely and once
 *     completed, cannot be terminated. Manufacturer hereby grants to 
 *     Licensee a non-transferable, worldwide license during the term of 
 *     this Agreement to use the Source Code for the associated product 
 *     purchased. In the event the Software License Agreement to the 
 *     associated product is terminated; (1) Licensee's rights to use 
 *     the Source Code are revoked and (2) Licensee shall destroy all 
 *     copies of the Source Code including any Source Code used in 
 *     Licensee's applications.
 * 2.  License Limitations
 *     2.1 Licensee may not resell, rent, lease or distribute the 
 *         Source Code alone, it shall only be distributed as a 
 *         compiled component of an application.
 *     2.2 Licensee shall protect and keep secure all Source Code 
 *         provided by this this Source Code License Agreement. 
 *         All Source Code provided by this Agreement that is used 
 *         with an application that is distributed or accessible outside
 *         Licensee's organization (including use from the Internet), 
 *         must be protected to the extent that it cannot be easily 
 *         extracted or decompiled.
 *     2.3 The Licensee shall not resell, rent, lease or distribute 
 *         the products created from the Source Code in any way that 
 *         would compete with Idega Software.
 *     2.4 Manufacturer's copyright notices may not be removed from 
 *         the Source Code.
 *     2.5 All modifications on the source code by Licencee must 
 *         be submitted to or provided to Manufacturer.
 * 3.  Copyright: Manufacturer's source code is copyrighted and contains 
 *     proprietary information. Licensee shall not distribute or 
 *     reveal the Source Code to anyone other than the software 
 *     developers of Licensee's organization. Licensee may be held 
 *     legally responsible for any infringement of intellectual property 
 *     rights that is caused or encouraged by Licensee's failure to abide 
 *     by the terms of this Agreement. Licensee may make copies of the 
 *     Source Code provided the copyright and trademark notices are 
 *     reproduced in their entirety on the copy. Manufacturer reserves 
 *     all rights not specifically granted to Licensee.
 *
 * 4.  Warranty & Risks: Although efforts have been made to assure that the 
 *     Source Code is correct, reliable, date compliant, and technically 
 *     accurate, the Source Code is licensed to Licensee as is and without 
 *     warranties as to performance of merchantability, fitness for a 
 *     particular purpose or use, or any other warranties whether 
 *     expressed or implied. Licensee's organization and all users 
 *     of the source code assume all risks when using it. The manufacturers, 
 *     distributors and resellers of the Source Code shall not be liable 
 *     for any consequential, incidental, punitive or special damages 
 *     arising out of the use of or inability to use the source code or 
 *     the provision of or failure to provide support services, even if we 
 *     have been advised of the possibility of such damages. In any case, 
 *     the entire liability under any provision of this agreement shall be 
 *     limited to the greater of the amount actually paid by Licensee for the 
 *     Software or 5.00 USD. No returns will be provided for the associated 
 *     License that was purchased to become eligible to receive the Source 
 *     Code after Licensee receives the source code. 
 */
package com.idega.faces.presentation.bean;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;

import javax.faces.event.ValueChangeEvent;

import com.idega.core.persistence.GenericDao;
import com.idega.presentation.IWContext;
import com.idega.util.ArrayUtil;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;

/**
 * <p>Some common stuff for JSF managed beans in ePlatform</p>
 * <p>You can report about problems to: 
 * <a href="mailto:martynas@idega.is">Martynas Stakė</a></p>
 *
 * @version 1.0.0 2015 kov. 1
 * @author <a href="mailto:martynas@idega.is">Martynas Stakė</a>
 * @param <ENTITY>
 */
public abstract class ManagedBean<ENTITY> implements Serializable {

	private static final long serialVersionUID = 6783135339378964474L;

	public static final String 
		PARAMETER_ID = "prm_id",
		PARAMETER_SUBMITTED = "prm_submitted";

	private ENTITY entity;

	protected Long id;

	private String editorLink = null;

	private boolean submitted = Boolean.FALSE;

	protected Type getGenericClassRecursively(Class<?> clazz) {
		if (clazz != null) {
			Type type = clazz.getGenericSuperclass();
	        if (type instanceof ParameterizedType) {
	        	return type;
	        } else {
	        	java.util.logging.Logger.getLogger(ManagedBean.class.getName()).log(Level.INFO, "Supertype found: " + type);
	        	return getGenericClassRecursively(clazz.getSuperclass());
	        }
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Class<ENTITY> getTypeParameterClass() {
        Type type = getGenericClassRecursively(getClass());
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] genericTypes = paramType.getActualTypeArguments();
            if (!ArrayUtil.isEmpty(genericTypes)) {
            	Type eType = genericTypes[0];
            	if (eType instanceof Class) {
                    return (Class<ENTITY>) eType;
            	}
            }
        }

        return null;
    }

	public abstract GenericDao getDAO();

	public String getIdClass() {
		return PARAMETER_ID;
	}

	public ENTITY getEntity() {
		if (this.entity == null) {
			IWContext context = CoreUtil.getIWContext();
			String parameter = null;
			if (this.id == null) {
				if (context != null) {
					parameter = context.getParameter(getIdClass());
					if (!StringUtil.isEmpty(parameter)) {
						this.id = Long.valueOf(parameter);
					}
				}
			}

			if (this.id != null && this.id != 0) {
				this.entity = getDAO().find(getTypeParameterClass(), id);
			}
		}

		return entity;
	}
	
	public void setEntity(ENTITY entity) {
		this.entity = entity;
	}

	public abstract Long getId();

	public void setId(Long id) {
		this.id = id;
	}

	public void selectedIdChange(ValueChangeEvent event) {
 		Object value = event.getNewValue();
 		if (value != null) {
			setId(Long.valueOf(value.toString()));
 		}
	}

	public String getEditorLink() {
		return editorLink;
	}

	public void setEditorLink(String editorLink) {
		this.editorLink = editorLink;
	}

	public boolean isSubmitted() {
		String submitted = CoreUtil.getIWContext().getParameter(PARAMETER_SUBMITTED);
		if (Boolean.TRUE.toString().equals(submitted)) {
			this.submitted = Boolean.TRUE;
		}

		return this.submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}

	public abstract void save();
}
