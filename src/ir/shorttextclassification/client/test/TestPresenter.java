package ir.shorttextclassification.client.test;

import ir.shorttextclassification.client.ResultItem;
import ir.shorttextclassification.client.place.NameTokens;
import ir.shorttextclassification.client.widget.Chart;
import ir.shorttextclassification.shared.MyRequestFactory.TextClassificationContext;
import ir.shorttextclassification.shared.ResultProxy;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;

public class TestPresenter extends
		Presenter<TestPresenter.MyView, TestPresenter.MyProxy> implements
		TestUiHandlers {
	interface MyView extends View, HasUiHandlers<TestUiHandlers> {
		public HTMLPanel getRoot();

		public FormPanel getDocFormTrain();

		public FlowPanel getInputPaneTrain();

		public FileUpload getTrainUploader();

		public FormPanel getDocFormTest();

		public FlowPanel getInputPaneTest();

		public FileUpload getTestUploader();

		public Button getViewResult();

		public Button getSubmitTrain();

		public Button getSubmitTest();
		
		public HTMLPanel getResultPanel();
	}

	private final Provider<TextClassificationContext> textClassificationContextProvider;

	@NameToken(NameTokens.test)
	@ProxyCodeSplit
	interface MyProxy extends ProxyPlace<TestPresenter> {
	}

	@Inject
	TestPresenter(
			EventBus eventBus,
			MyView view,
			MyProxy proxy,
			Provider<TextClassificationContext> textClassificationContextProvider) {
		super(eventBus, view, proxy, RevealType.Root);
		this.textClassificationContextProvider = textClassificationContextProvider;
		getView().setUiHandlers(this);
		getView().getDocFormTrain().setEncoding(FormPanel.ENCODING_MULTIPART);
		getView().getDocFormTrain().setMethod(FormPanel.METHOD_POST);
		getView().getDocFormTest().setEncoding(FormPanel.ENCODING_MULTIPART);
		getView().getDocFormTest().setMethod(FormPanel.METHOD_POST);
		getView().getTrainUploader().setName("file");
		getView().getTestUploader().setName("file");
		getView().getViewResult().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					TestPresenter.this.textClassificationContextProvider.get()
							.getResult().fire(new Receiver<ResultProxy>() {

								@Override
								public void onSuccess(ResultProxy response) {
									if (response == null) {
										Window.alert("خطا در عملیات.");
										return;
									}
									if (response.getStatus() < 0) {
									String error=null;	
										switch (response.getStatus()) {
										case -1:
											error="داده های آموزش وارد نشده است.";
											break;
										case -2:
											error="فایل داده های آموزش صحیح نمی باشد.";
											break;
										case -3:
											error="داده های تست وارد نشده است.";
											break;
										case -4:
											error="فایل داده های تست صحیح نمی باشد.";
											break;
										case -5:
											error="فرمت اطلاعات ورودی برای داده های آموزش صحیح نیست.";
											break;
										case -6:
											error="فرمت ورودی داده های آزمون صحیح نیست.";
											break;
										default:
											error="خطایی در عملیات";
											break;
										}
										Window.alert(error);
									} else {
										showResult(response);
									}
								}

								@Override
								public void onFailure(ServerFailure error) {
									Window.alert(error + "");
								}
							});
				} catch (Exception e) {
					GWT.log("exception : " + e.getMessage());
				}

			}
		});

		getView().getSubmitTrain().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getView().getDocFormTrain().submit();
			}
		});

		getView().getDocFormTrain().addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				getView().getDocFormTrain().setAction(
						"fileupload?actionType=train");
			}
		});

		getView().getSubmitTest().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getView().getDocFormTest().submit();
			}
		});

		getView().getDocFormTest().addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				getView().getDocFormTest().setAction(
						"fileupload?actionType=test");

			}
		});

	}

	private void showResult(ResultProxy response) {
		List<String> result=response.getTrueLabelsNo();
		List<ResultItem> resultItems=new ArrayList<ResultItem>();
		for (String str : result) {
			String[] array=str.trim().split("###");
			ResultItem resultItem=new ResultItem();
			resultItem.setName(array[0]);
			resultItem.setCorrectNo(Integer.parseInt(array[1]));
			resultItem.setTotalNo(Integer.parseInt(array[2]));
			resultItems.add(resultItem);
		//	Window.alert(array[0]+ " , "+" CORRECT :  "+array[1]+" , total : "+array[2]);
		}
		Chart chart=new Chart(600, 300);
		getView().getResultPanel().clear();
		getView().getResultPanel().add(chart);
		chart.drawChart(resultItems);
		
	}

	@Override
	public void prepareFromRequest(PlaceRequest request) {
		super.prepareFromRequest(request);
	}

	protected void onBind() {
		super.onBind();
	}

}
