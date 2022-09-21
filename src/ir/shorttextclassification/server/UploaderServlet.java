package ir.shorttextclassification.server;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploaderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1044428817735653857L;
	private final static String ACTION_TYPE = "actionType";
	private final static String ACTION_TYPE_TEST = "test";
	private final static String ACTION_TYPE_TRAIN = "train";
	private final static String URL = "E:\\workspace\\ShortTextClassification\\war\\temp";

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String actionType = req.getParameter(ACTION_TYPE);
		if (actionType == null) {
			System.out.println("actionType is null");
			resp.setStatus(-1);
			return;
		}
		if (actionType.trim().equals(ACTION_TYPE_TRAIN) == false
				&& actionType.trim().equals(ACTION_TYPE_TEST) == false) {
			resp.setStatus(-1);
			System.out.println("actionType is not valid its : " + actionType);
			return;
		}
		if (req.getSession() == null) {
			System.out.println("http session is null");
			resp.setStatus(-2);
			
			return;
		} else {
			if (actionType.trim().equals(ACTION_TYPE_TRAIN) == true) {
				req.getSession().removeAttribute(ConstantName.TRAIN_FILE);
			}
			if (actionType.trim().equals(ACTION_TYPE_TEST) == true) {
				req.getSession().removeAttribute(ConstantName.TEST_FILE);
			}
		}
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (isMultipart == true) {
			try {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setRepository(new File(URL));
				factory.setSizeThreshold(10000000);
				ServletFileUpload servletFileUpload = new ServletFileUpload(
						factory);
				Iterator<FileItem> fileItems = servletFileUpload.parseRequest(
						req).iterator();
				while (fileItems.hasNext()) {
					FileItem item = fileItems.next();
					int dotIndex = item.getName().lastIndexOf('.');
					if (dotIndex == -1) {
						resp.setContentType("not sent");
						resp.setCharacterEncoding("kjsdjkasd");
						
						resp.setStatus(-3);
						return;
					}
					String extension = item.getName().substring(dotIndex);
					if(extension.equals(".txt")==false){
						resp.setStatus(-4);
						return;
					}
					File file = File.createTempFile("upload-", extension);
					item.write(file);
					if (actionType.trim().equals(ACTION_TYPE_TRAIN) == true) {
						req.getSession().setAttribute(ConstantName.TRAIN_FILE,
								file);
					}
					if (actionType.trim().equals(ACTION_TYPE_TEST) == true) {
						req.getSession().setAttribute(ConstantName.TEST_FILE,
								file);
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return;
	}

}
