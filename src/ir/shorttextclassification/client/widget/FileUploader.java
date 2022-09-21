package ir.shorttextclassification.client.widget;

import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.PreloadedImage;
import gwtupload.client.PreloadedImage.OnLoadPreloadedImageHandler;
import gwtupload.client.SingleUploader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FileUploader extends Composite {

	private FlowPanel panelImages = new FlowPanel();
	private final VerticalPanel root = new VerticalPanel();
	private SingleUploader uploader = new SingleUploader();

	public FileUploader() {
		root.add(uploader);
		root.add(panelImages);
		try {
			Element element = uploader.getElement().getFirstChildElement()
					.getFirstChildElement().getFirstChildElement()
					.getFirstChildElement().getFirstChildElement()
					.getFirstChildElement();
			element.setAttribute("size", "20");
			String style = element.getNextSiblingElement()
					.getAttribute("class");
			if (style == null) {
				style = "";
			}
			// style.replace(" imageUploader_sendButton", "");
			// style += " image_uploader_send_button";
			element.getNextSiblingElement().setAttribute("class", style);
		} catch (Exception ex) {
			GWT.log(ex.getMessage());
		}

		// uploader.addStyleName("singleUploader");
		// uploader.addOnFinishUploadHandler(onFinishUploaderHandler);
		this.initWidget(root);
	}

	public IUploader.OnFinishUploaderHandler onFinishUploaderHandler = new IUploader.OnFinishUploaderHandler() {
		public void onFinish(IUploader uploader) {
			if (uploader.getStatus() == Status.SUCCESS) {
				new PreloadedImage(uploader.fileUrl(), showImage);

			}
		}
	};

	// Attach an image to the pictures viewer
	private OnLoadPreloadedImageHandler showImage = new OnLoadPreloadedImageHandler() {
		public void onLoad(PreloadedImage image) {
			image.setWidth("75px");
			panelImages.add(image);
		}
	};

	public SingleUploader getUploader() {
		return uploader;
	}
}
