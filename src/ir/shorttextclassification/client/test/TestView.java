package ir.shorttextclassification.client.test;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

class TestView extends ViewWithUiHandlers<TestUiHandlers> implements
		TestPresenter.MyView {
	interface Binder extends UiBinder<Widget, TestView> {
	}
	@UiField
	HTMLPanel root;
	@UiField FormPanel docFormTrain;
	@UiField FlowPanel inputPaneTrain;
	@UiField FileUpload trainUploader;
	
	@UiField FormPanel docFormTest;
	@UiField FlowPanel inputPaneTest;
	@UiField FileUpload testUploader;
	@UiField Button viewResult;
	@UiField Button submitTrain;
	@UiField Button submitTest;
	@UiField HTMLPanel resultPanel;
	
	@Inject
	TestView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public HTMLPanel getRoot() {
		return root;
	}

	public FormPanel getDocFormTrain() {
		return docFormTrain;
	}

	public FlowPanel getInputPaneTrain() {
		return inputPaneTrain;
	}

	public FileUpload getTrainUploader() {
		return trainUploader;
	}

	public FormPanel getDocFormTest() {
		return docFormTest;
	}

	public FlowPanel getInputPaneTest() {
		return inputPaneTest;
	}

	public FileUpload getTestUploader() {
		return testUploader;
	}

	public Button getViewResult() {
		return viewResult;
	}
	
	public Button getSubmitTrain() {
		return submitTrain;
	}
	
	public Button getSubmitTest() {
		return submitTest;
	}
	
	public HTMLPanel getResultPanel() {
		return resultPanel;
	}
}
