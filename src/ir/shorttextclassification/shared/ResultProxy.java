package ir.shorttextclassification.shared;

import ir.shorttextclassification.server.Result;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.google.web.bindery.requestfactory.shared.ValueLocator;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyFor(value = Result.class, locator = ValueLocator.class)
public interface ResultProxy extends ValueProxy {
	
	public List<String> getTrueLabelsNo();
	public void setTrueLabelsNo(List<String> trueLabelsNo);
	public int getStatus();
	public void setStatus(int status);
	public int getTestNo();
	public void setTestNo(int testNo);
}
