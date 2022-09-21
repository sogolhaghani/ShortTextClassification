package ir.shorttextclassification.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4639151262507670407L;

	private int status = 0;
	private int testNo=0;
	private List<String> trueLabelsNo=new ArrayList<String>();

	public List<String> getTrueLabelsNo() {
		return trueLabelsNo;
	}
	
	public void setTrueLabelsNo(List<String> trueLabelsNo) {
		this.trueLabelsNo = trueLabelsNo;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTestNo() {
		return testNo;
	}

	public void setTestNo(int testNo) {
		this.testNo = testNo;
	}
	
	
	
}
