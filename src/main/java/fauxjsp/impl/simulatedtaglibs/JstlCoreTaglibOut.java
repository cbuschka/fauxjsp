package fauxjsp.impl.simulatedtaglibs;

import fauxjsp.api.RenderSession;
import fauxjsp.api.nodes.JspTaglibInvocation;
import fauxjsp.api.nodes.TaglibDefinition;
import fauxjsp.api.renderer.JspRenderException;

/**
 * Implementation of c:out
 * @author George Georgovassilis
 *
 */
public class JstlCoreTaglibOut extends TaglibDefinition{

	protected void runOut(RenderSession session, JspTaglibInvocation invocation) {
		String outExpression = invocation.getArguments().get("value");
		if (outExpression == null)
			throw new RuntimeException("Missing value argument");
		Object value = session.elEvaluation.evaluate(outExpression, session);
		try {
			session.response.getOutputStream().write(("" + value).getBytes());
		} catch (Exception e) {
			throw new JspRenderException(invocation, e);
		}
	}

	@Override
	public void render(RenderSession session, JspTaglibInvocation invocation) {
		if (!invocation.getTaglib().equals("out"))
			throw new JspRenderException(invocation, new RuntimeException("This isn't an out taglib"));
		runOut(session, invocation);
	}
	
	public JstlCoreTaglibOut() {
		this.name="out";
		this.attributes.put("value", new AttributeDefinition("value", Object.class.getName(), true, true));
	}
}
