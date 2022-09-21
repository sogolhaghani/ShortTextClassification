package ir.shorttextclassification.client.widget;

import ir.shorttextclassification.client.ResultItem;

import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextAlign;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.user.client.ui.Composite;

public class Chart extends Composite {

	private Canvas canvas;

	public Chart() {
		this.canvas = Canvas.createIfSupported();
		initWidget(canvas);
	}

	public Chart(int width, int height) {
		this.canvas = Canvas.createIfSupported();
		initWidget(canvas);
		this.canvas.setCoordinateSpaceHeight(height);
		this.canvas.setCoordinateSpaceWidth(width);
		this.canvas.getElement().setAttribute("style", "float:left");
	}

	public void drawChart(List<ResultItem> items) {
		double minAverage = 0;// getMinAverage(items);
		double maxAverage = 1;// getMaxAverage(items);
		int noPoints = items.size();

		Context2d context2d = canvas.getContext2d();
		context2d.setLineWidth(1);
		context2d.setFillStyle(CssColor.make(128, 128, 128));
		context2d.setStrokeStyle(CssColor.make(128, 128, 128));
		context2d.beginPath();
		context2d.rect(1, 1, canvas.getCoordinateSpaceWidth() - 2,
				canvas.getCoordinateSpaceHeight() - 2);
		int availableHeight = canvas.getCoordinateSpaceHeight() - 2;
		int availableWidth = canvas.getCoordinateSpaceWidth() - 2;
		int lineNo = (int) ((maxAverage - minAverage) / 0.05);
		double spaceLines = (double) (availableHeight - 50) / lineNo;
		for (int i = 1; i <= lineNo; i++) {
			context2d.moveTo(50, spaceLines * i);
			context2d.lineTo(availableWidth, spaceLines * i);
			String text = ((maxAverage - (i * 0.05)) * 100 + "")
					.substring(0, 2) + "%";
			context2d.setTextAlign(TextAlign.LEFT);
			context2d.fillText(text, 20, spaceLines * i + 2);
		}
		int nameSpace = (availableWidth - 50) / noPoints;
		double[] heights = new double[noPoints];
		double[] widths = new double[noPoints];
		for (int i = 0; i < noPoints; i++) {
			int x = (nameSpace * i) + 60;
			context2d.fillText(items.get(i).getName(), x, availableHeight - 25);
			double avg = ((double) items.get(i).getCorrectNo() / items.get(i)
					.getTotalNo()) * 100;
			double y = (100 - avg) * (availableHeight - 50) / 100;
			//Window.alert("avg : " + avg + " , Y : " + y);
			heights[i] = y;
			widths[i] = x;
			context2d.rect(x - 2, y - 2, 4, 4);
		}
		for (int i = 0; i < widths.length - 1; i++) {
			context2d.moveTo(widths[i], heights[i]);
			context2d.lineTo(widths[i + 1], heights[i + 1]);
		}
		context2d.stroke();
		context2d.closePath();

	}

}
