package ir.shorttextclassification.server.guice;

import ir.shorttextclassification.server.TextClassification;
import ir.shorttextclassification.server.TextClassificationImpl;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TextClassification.class).to(TextClassificationImpl.class);
		
	}

}
