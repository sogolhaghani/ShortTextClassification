package ir.shorttextclassification.shared;

import ir.shorttextclassification.server.MyServiceLocator;
import ir.shorttextclassification.server.TextClassificationImpl;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.Service;

public interface MyRequestFactory extends RequestFactory {

	@Service(value =TextClassificationImpl.class, locator = MyServiceLocator.class)
	public interface TextClassificationContext extends RequestContext {

		Request<ResultProxy> getResult();
	}

	TextClassificationContext getTextClassificationContext();
}