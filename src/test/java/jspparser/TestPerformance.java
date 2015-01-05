package jspparser;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import jspparser.dto.NavigationItem;
import jspparser.dto.News;
import jspparser.dto.Stock;
import jspparser.utils.MockHttpServletRequest;
import jspparser.utils.MockHttpServletResponse;
import jspparser.utils.TestUtils;

import org.junit.Test;

import fauxjsp.api.RenderSession;
import fauxjsp.api.nodes.JspPage;
import fauxjsp.impl.parser.JspParserImpl;
import fauxjsp.impl.renderer.JspRendererImpl;
import static org.junit.Assert.*;

/**
 * Performance test of {@link JspParserImpl} and {@link JspRendererImpl}
 * 
 * @author George Georgovassilis
 *
 */

public class TestPerformance extends BaseTest{

	final long WARMUP_MS = 2000;
	final long RUNS_MS = 2000;
	
	protected int run(Runnable r, long duration){
		int loops = 0;
		long start=System.currentTimeMillis();
		while (start+duration>System.currentTimeMillis()){
			r.run();
			loops++;
		}
		return loops;
	}
	
	@Test
	public void testJspParser() {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				newParser().parse("WEB-INF/jsp/homepage.jsp");
			}
		};
		run(r, WARMUP_MS);
		int loops = run(r, RUNS_MS);
		System.out.println("Parse runs / sec : "+loops*1000/RUNS_MS);
	}

	@Test
	public void testJspRenderer() {
		final JspPage page = newParser().parse("WEB-INF/jsp/homepage.jsp");
		final Date date = new GregorianCalendar(2000, 2, 2).getTime();
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				MockHttpServletRequest request = new MockHttpServletRequest();
				MockHttpServletResponse response = new MockHttpServletResponse();
				ByteArrayOutputStream baos = response.getBaos();
				RenderSession session = new RenderSession();
				session.request = request;
				session.out = baos;
				session.renderer = newRenderer();
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

				session.request.setAttribute("date",  date);
				session.renderer.render(page, session);
				String text;
				try {
					text = new String(baos.toByteArray(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				assertEquals("d5f8562921a8db99d496bd64218bfb30",TestUtils.MD5(text));
			}
		};
		run(r, WARMUP_MS);
		int loops = run(r, RUNS_MS);
		System.out.println("Render runs / sec : "+loops*1000/RUNS_MS);
	}

}