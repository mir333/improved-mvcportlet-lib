/**
 * Copyright (c) 2015-present Miroslav Ligas. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package im.ligas.util.bridges.mvc;

import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Miroslav Ligas
 */
public class MVCPortlet extends com.liferay.util.bridges.mvc.MVCPortlet {

	public static final String SET_MODEL = "setModel";

	@Override
	public void doDispatch(RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		callSetModeMethod(renderRequest, renderResponse);

		super.doDispatch(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		if (!callResourceMethod(resourceRequest, resourceResponse)) {
			super.serveResource(resourceRequest, resourceResponse);
		}
	}

	public void serveResourceJSP(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		super.serveResource(resourceRequest, resourceResponse);
	}

	public void setModel(Map<String, Object> model, RenderRequest request) {
		return;
	}

	protected void callSetModeMethod(
		RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String setModelName = ParamUtil.getString(renderRequest, SET_MODEL, SET_MODEL);

		try {
			Method method = getMethods(Phase.VIEW, setModelName,
				Map.class, RenderRequest.class);

			Map<String, Object> model = new HashMap<String, Object>();

			method.invoke(this, model, renderRequest);

			for (Map.Entry<String, Object> entry : model.entrySet()) {
				renderRequest.setAttribute(entry.getKey(), entry.getValue());
			}

		} catch (Exception e) {
			throw new PortletException(e);
		}
	}

	protected boolean callResourceMethod(
		ResourceRequest renderRequest, ResourceResponse renderResponse)
		throws PortletException {

		String resourceName = renderRequest.getResourceID();

		try {
			Method method = getMethods(Phase.RESOURCE, resourceName,
				ResourceRequest.class, ResourceResponse.class);

			method.invoke(this, renderRequest, renderResponse);
			return true;

		} catch (NoSuchMethodException nsme) {
			return false;
		} catch (Exception e) {
			throw new PortletException(e);
		}
	}


	protected Method getMethods(
		Phase phase, String methodName, Class<?>... parameterTypes)
		throws NoSuchMethodException {

		Method method = _controllerMethods.get(phase + methodName);

		if (method != null) {
			return method;
		}

		Class<?> clazz = getClass();

		method = clazz.getMethod(methodName, parameterTypes);

		_controllerMethods.put(phase + methodName, method);

		return method;
	}

	private enum Phase {VIEW, RESOURCE}

	private Map<String, Method> _controllerMethods =
		new ConcurrentHashMap<String, Method>();
}
