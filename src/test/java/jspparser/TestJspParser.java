package jspparser;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import fauxjsp.api.RenderSession;
import fauxjsp.api.nodes.JspInstruction;
import fauxjsp.api.nodes.JspPage;
import fauxjsp.api.nodes.JspTaglibInvocation;
import fauxjsp.api.nodes.JspText;
import fauxjsp.impl.parser.JspParserImpl;
import static org.junit.Assert.*;

/**
 * Test of {@link JspParserImpl}
 * 
 * @author George Georgovassilis
 *
 */

public class TestJspParser extends BaseTest{

	@Test
	public void testParser() throws Exception {
		JspPage page = parser.parse("WEB-INF/jsp/homepage.jsp");
		assertEquals(5, page.getChildren().size());

		JspInstruction j1 = (JspInstruction) page.getChildren().get(0);
		assertEquals("taglib", j1.getName());
		assertEquals("c", j1.getArguments().get("prefix"));
		assertEquals("http://java.sun.com/jsp/jstl/core", j1.getArguments()
				.get("uri"));

		JspText j2 = (JspText) page.getChildren().get(1);
		assertEquals("\r\n", new String(j2.getContent()));

		JspInstruction j3 = (JspInstruction) page.getChildren().get(2);
		assertEquals("taglib", j3.getName());
		assertEquals("az", j3.getArguments().get("prefix"));
		assertEquals("/WEB-INF/tags", j3.getArguments().get("tagdir"));

		JspText j4 = (JspText) page.getChildren().get(3);
		assertTrue(new String(j4.getContent()).equals("\r\n"));

		JspTaglibInvocation j5 = (JspTaglibInvocation) page.getChildren()
				.get(4);
		assertEquals("az:structure", j5.getName());

		assertEquals(3, j5.getChildren().size());

		JspText j5_1 = (JspText) j5.getChildren().get(0);
		assertTrue(j5_1.getContentAsString().startsWith("\r\n<p>This"));
		assertTrue(j5_1.getContentAsString().endsWith("</p>\r\n"));

		JspTaglibInvocation j5_2 = (JspTaglibInvocation) j5.getChildren()
				.get(1);
		assertEquals("az:news", j5_2.getName());
		assertTrue(j5_2.getChildren().isEmpty());
		assertEquals("${listOfNews}", j5_2.getArguments().get("listOfNews"));

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testJspRenderer() {
		JspPage page = parser.parse("WEB-INF/jsp/homepage.jsp");
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		ByteArrayOutputStream baos = response.getBaos();
		RenderSession session = new RenderSession();
		session.request = request;
		session.out = baos;
		session.renderer = renderer;
		session.elEvaluation = elEvaluation;
		session.response = response;

		session.request.setAttribute("navigation", Arrays.asList(
				new NavigationItem("path 1", "label 1"), new NavigationItem(
						"path 2", "label 2")));
		session.request
				.setAttribute("listOfStocks", Arrays.asList(new Stock("S1",
						"Stock one", 10, 20),
						new Stock("S2", "Stock 2", -9, 88)));
		session.request.setAttribute("listOfNews", Arrays.asList(new News("1",
				"headline 1", "description 1", "full text of news 1", false),
				new News("2", "headline 2", "description 2",
						"full text of news 2", false)));

		session.request.setAttribute("date", new Date(100, 2, 2));
		renderer.render(page, session);
		String text = new String(baos.toByteArray());
		assertTrue(text.contains("Thu Mar 02"));
		assertTrue(text
				.contains("<a href=\"news?id=2\" class=headline>headline 2</a>"));
		assertTrue(text.contains("<span class=price>0.2 €</span>"));
		
		//lazy man's arse-covering insurance that we didn't change something in the implementation without knowing about it
		assertEquals("d5f8562921a8db99d496bd64218bfb30",TestUtils.MD5(text));
	}

	@Test
	public void testJspParserNews() {
		JspPage page = parser.parse("WEB-INF/jsp/news.jsp");
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		ByteArrayOutputStream baos = response.getBaos();
		RenderSession session = new RenderSession();
		session.request = request;
		session.out = baos;
		session.renderer = renderer;
		session.elEvaluation = elEvaluation;
		session.response = response;

		session.request.setAttribute("navigation", Arrays.asList(
				new NavigationItem("path 1", "label 1"), new NavigationItem(
						"path 2", "label 2")));
		session.request.setAttribute("listOfStocks", Arrays.asList(new Stock(
				"S1", "Stock one", 25000, 1), new Stock("S2", "Stock two",
				10000, -2), new Stock("S3", "Stock3", 9999, 12)));
		session.request.setAttribute("listOfNews", Arrays.asList(new News("1",
				"headline 1", "description 1", "full text of news 1", true),
				new News("2", "headline 2", "description 2",
						"full text of news 2", false)));
		session.request.setAttribute("news", new News("1", "headline 1",
				"description 1", "full text of news 3", true));

		renderer.render(page, session);
		String text = new String(baos.toByteArray());
		assertTrue(text.contains("+++headline 1"));
	}

	@Test
	public void testRendererForEach() {
		JspPage page = parser.parse("WEB-INF/jsp/foreach.jsp");
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		ByteArrayOutputStream baos = response.getBaos();
		RenderSession session = new RenderSession();
		session.request = request;
		session.out = baos;
		session.renderer = renderer;
		session.elEvaluation = elEvaluation;
		session.response = response;

		session.request.setAttribute("listOfNews", Arrays.asList(new News("0",
				"headline 0", "description 0", "full text of news 0", false),
				new News("1", "headline 1", "description 1",
						"full text of news 1", false), new News("2", "headline 2",
						"description 2", "full text of news 2", false)));
		renderer.render(page, session);
		String text = new String(baos.toByteArray());
		assertEquals("\nNEWS SECTION 1: 012\nNEWS SECTION 2: 12\nNEWS SECTION 3: 0\nVARSTATUS: 1,0,true,false\n2,1,false,false\n3,2,false,true\n", text);
	}

}
