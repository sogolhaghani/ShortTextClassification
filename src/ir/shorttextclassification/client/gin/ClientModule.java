package ir.shorttextclassification.client.gin;

import ir.shorttextclassification.client.place.NameTokens;
import ir.shorttextclassification.client.test.TestModule;
import ir.shorttextclassification.shared.MyRequestFactory;
import ir.shorttextclassification.shared.MyRequestFactory.TextClassificationContext;

import com.google.gwt.core.client.GWT;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new TestModule());
		install(new DispatchAsyncModule());
		install(new DefaultModule());
		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.test);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(
				NameTokens.test);
		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.test);

	}

	@Singleton
	@Provides
	public MyRequestFactory createMyRequestFactory(EventBus eventBus) {
		MyRequestFactory myRF = GWT.create(MyRequestFactory.class);
		myRF.initialize(eventBus);
		return myRF;
	}

	
	@Provides
	public TextClassificationContext createTextClassificationContext(
			MyRequestFactory factory) {
		return factory.getTextClassificationContext();
	}
}
