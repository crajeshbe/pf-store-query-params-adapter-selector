package com.pingidentity.pf.selectors.http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sourceid.saml20.adapter.conf.Configuration;
import org.sourceid.saml20.adapter.conf.Row;
import org.sourceid.saml20.adapter.gui.TableDescriptor;
import org.sourceid.saml20.adapter.gui.TextFieldDescriptor;
import org.sourceid.saml20.adapter.state.SessionStateSupport;

import com.pingidentity.sdk.AuthenticationSelector;
import com.pingidentity.sdk.AuthenticationSelectorContext;
import com.pingidentity.sdk.AuthenticationSelectorDescriptor;
import com.pingidentity.sdk.AuthenticationSourceKey;
import com.pingidentity.sdk.GuiConfigDescriptor;

public class StoreQueryParamsAdapterSelector implements AuthenticationSelector {

	Log log = LogFactory.getLog(StoreQueryParamsAdapterSelector.class);

	private static String PARAMS = "Parameters";
	ArrayList<String> parameters;

	public void configure(Configuration conf) {
		parameters = new ArrayList<String>();
		List<Row> extraParams = conf.getTable(PARAMS).getRows();
		for (Iterator<Row> iter = extraParams.iterator(); iter.hasNext();) {
			Row tuple = iter.next();
			parameters.add(tuple.getFieldValue("Name"));
		}

	}

	public AuthenticationSelectorDescriptor getPluginDescriptor() {
		GuiConfigDescriptor guiConfigDesc = new GuiConfigDescriptor();
		guiConfigDesc
				.setDescription("This adapter selector doesn't select an adapter, it just grabs query parameters for later usage.");

		TableDescriptor requestParamsTable = new TableDescriptor(PARAMS,
				"Names of query parameters to store");
		TextFieldDescriptor nameColumn = new TextFieldDescriptor("Name",
				"The name of the query parameter.");
		requestParamsTable.addRowField(nameColumn);

		guiConfigDesc.addTable(requestParamsTable);

		Set<String> results = new HashSet<String>();
		// results.add("Dummy");
		AuthenticationSelectorDescriptor authAdapterSelectorDescriptor = new AuthenticationSelectorDescriptor(
				"Store Query Parameters", this, guiConfigDesc, results, "1.0");
		return authAdapterSelectorDescriptor;
	}

	@SuppressWarnings("rawtypes")
	public void callback(HttpServletRequest arg0, HttpServletResponse arg1,
			Map arg2, AuthenticationSourceKey arg3,
			AuthenticationSelectorContext arg4) {
	}

	public AuthenticationSelectorContext selectContext(
			HttpServletRequest request, HttpServletResponse response,
			Map<AuthenticationSourceKey, String> mappedAuthnSourcesNames,
			Map<String, Object> extraParameters, String resumePath) {

		log.debug("enter...");
		
		AuthenticationSelectorContext context = new AuthenticationSelectorContext();
		context.setResultType(AuthenticationSelectorContext.ResultType.CONTEXT);

		SessionStateSupport sessionStateSupport = new SessionStateSupport();
		for (Iterator<String> iter = parameters.iterator(); iter.hasNext();) {
			String name = iter.next();
			if (request.getParameter(name) != null) {
				String value = request.getParameter(name);
				log.debug("storing: " + name + " = " + value);
				sessionStateSupport.setAttribute(name, value, request, response, false);
			}
		}

		if ((request.getParameter("IdpAdapterId") != null)) {
			context.setResult(request.getParameter("IdpAdapterId"));
		} else if ((request.getParameter("idp") != null)) {
			context.setResult(request.getParameter("idp"));
		}
		
		log.debug("leave: " + context.getResult());
		
		return context;
	}
}
