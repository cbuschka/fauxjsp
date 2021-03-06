package fauxjsp.api.parser;


/**
 * Creates {@link JspParser} instances
 * @author George Georgovassilis
 */
public interface JspParserFactory {

	/**
	 * Return a new {@link JspParser} instance
	 * @return
	 */
	JspParser create();
	
	/**
	 * Return a new {@link JspParser} instance with a given parent parser. The parent parser may be
	 * consulted for resolution of taglibs or scoped properties
	 * @param parent
	 * @return
	 */
	JspParser create(JspParser parent);
	
}
