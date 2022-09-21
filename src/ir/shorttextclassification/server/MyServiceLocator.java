package ir.shorttextclassification.server;

import com.google.web.bindery.requestfactory.shared.ServiceLocator;

public class MyServiceLocator implements ServiceLocator {

	@Override
	public Object getInstance(Class<?> clazz) {
		
		 try {
		      return clazz.newInstance();
		    } catch (InstantiationException e) {
		      throw new RuntimeException(e);
		    } catch (IllegalAccessException e) {
		      throw new RuntimeException(e);
		    }
		
		

//		HttpServletRequest request=RequestFactoryServlet.getThreadLocalRequest();
//		ServletContext context=request.getSession().getServletContext();
//		Injector injector=(Injector) context.getAttribute(Injector.class.getName());
//		if(injector==null){
//			throw new IllegalStateException("injector is null.");
//		}
//		return injector.getInstance(clazz);
		
	}

}
