package ir.shorttextclassification.server.guice;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.opensymphony.oscache.web.filter.CacheFilter;

public class DispatchServletModule extends ServletModule {

	public DispatchServletModule() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureServlets() {
		bind(CacheFilter.class).in(Singleton.class);
	}
}
