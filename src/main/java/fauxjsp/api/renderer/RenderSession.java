package fauxjsp.api.renderer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import fauxjsp.api.nodes.JspPage;

/**
 * A {@link RenderSession} conveniently groups objects a {@link JspRenderer} needs to render a {@link JspPage}
 * @author George Georgovassilis
 *
 */
public class RenderSession {

	public JspRenderer renderer;
	public ServletRequest request;
	public ServletResponse response;
	public ELEvaluation elEvaluation;

}