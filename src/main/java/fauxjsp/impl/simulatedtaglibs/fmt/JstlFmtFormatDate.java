package fauxjsp.impl.simulatedtaglibs.fmt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fauxjsp.api.nodes.JspTaglibInvocation;
import fauxjsp.api.nodes.TaglibDefinition;
import fauxjsp.api.renderer.JspRenderException;
import fauxjsp.api.renderer.RenderSession;

/**
 * fmt:formatDate simulation
 * 
 * @author George Georgovassilis
 *
 */
public class JstlFmtFormatDate extends TaglibDefinition {

	public final static String ATTR_RESOURCE_BUNDLE = "__fauxjsp_resource_bundle";

	public JstlFmtFormatDate() {
		super("formatDate");
		declareAttribute("value", String.class.getName(), true, true);
		declareAttribute("type", String.class.getName(), true, false);
		declareAttribute("dateStyle", String.class.getName(), true, false);
		declareAttribute("timeStyle", String.class.getName(), true, false);
		declareAttribute("pattern", String.class.getName(), true, false);
		declareAttribute("timeZone", String.class.getName(), true, false);
		declareAttribute("var", String.class.getName(), true, false);
		declareAttribute("scope", String.class.getName(), true, false);
	}

	protected Date getDate(RenderSession session, JspTaglibInvocation invocation) {
		String valueExpression = getAttribute("value", invocation);
		Object value = evaluate(valueExpression, session);
		if (value == null)
			throw new JspRenderException("'" + valueExpression + "' is null", null);
		if (!(value instanceof Date))
			throw new JspRenderException(
					"'" + valueExpression + "' is a " + value.getClass() + " but I need a java.util.Date", null);
		Date date = (Date) value;
		return date;
	}
	
	protected int formatLength(String format, String what, JspTaglibInvocation invocation){
		if (StringUtils.isEmpty(format))
			return DateFormat.MEDIUM;
		format = format.toLowerCase();
		if ("long".equals(format))
			return DateFormat.LONG;
		if ("medium".equals(format))
			return DateFormat.MEDIUM;
		if ("short".equals(format))
			return DateFormat.SHORT;
		error("Unknown "+what+" '"+format+"'", invocation);
		return -1;
	}

	protected DateFormat getPattern(RenderSession session, JspTaglibInvocation invocation) {
		String type = ("" + getAttribute("type", invocation)).toLowerCase();
		int dateStyle = formatLength(getAttribute("dateStyle", invocation), "dateStyle", invocation);
		int timeStyle = formatLength(getAttribute("timeStyle", invocation), "timeStyle", invocation);
		Locale locale = session.request.getLocale();
		if ("time".equals(type))
			return DateFormat.getTimeInstance(timeStyle, locale);
		if ("date".equals(type))
			return DateFormat.getDateInstance(dateStyle, locale);
		if ("both".equals(type))
			return DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
		String pattern = getAttribute("pattern", invocation);

		try {
			return new SimpleDateFormat(pattern);
		} catch (Exception e) {
			error(e, invocation);
			return null;
		}
	}

	protected void run(RenderSession session, JspTaglibInvocation invocation) {

		Date date = getDate(session, invocation);
		DateFormat format = getPattern(session, invocation);
		String result = format.format(date);
		String variableName = getAttribute("var", invocation);
		if (StringUtils.isEmpty(variableName))
			write(result, session);
		else{
			session.request.overwriteAttribute(variableName, result);
		}
	}

	@Override
	protected void renderNode(RenderSession session, JspTaglibInvocation invocation) {
		if (!invocation.getTaglib().equals("formatDate"))
			error("This isn't a formatDate taglib", invocation);
		run(session, invocation);
	}

}
