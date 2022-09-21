package ir.shorttextclassification.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class TextClassificationImpl implements TextClassification {

	@Inject
	public TextClassificationImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Result getResult() {
		HttpServletRequest request = RequestFactoryServlet
				.getThreadLocalRequest();
		Object trainObject = request.getSession().getAttribute(
				ConstantName.TRAIN_FILE);
		if (trainObject == null) {
			Result result = new Result();
			result.setStatus(-1);
			return result;
		}
		if (trainObject instanceof File == false) {
			Result result = new Result();
			result.setStatus(-2);
			return result;
		}
		File trainFile = (File) trainObject;
		Object testObject = request.getSession().getAttribute(
				ConstantName.TEST_FILE);
		if (testObject == null) {
			Result result = new Result();
			result.setStatus(-3);
			return result;
		}
		if (testObject instanceof File == false) {
			Result result = new Result();
			result.setStatus(-4);
			return result;
		}
		File testFile = (File) testObject;

		Map<String, List<String>> trainTexts = getTexts(trainFile);
		if (trainTexts.isEmpty()) {
			Result result = new Result();
			result.setStatus(-5);
			return result;
		}
		List<String> trainLabels = getLabels(trainTexts);
		List<String> trainVocabs = getTrainVocabs(trainTexts);
		int trainColumnSize = getColumnSize(trainTexts);
		Matrix trainMatrix = new Matrix(trainVocabs.size(), trainColumnSize, 0);
		initializeMatrix(trainMatrix, trainLabels, trainTexts, trainVocabs);
		SingularValueDecomposition svd = trainMatrix.svd();
		int numberOfSingularValue = getReduceSingularValueLength(svd
				.getSingularValues());

		Matrix newU = svd.getU().getMatrix(0, svd.getU().getRowDimension() - 1,
				0, numberOfSingularValue - 1);

		Matrix newS;
		try {
			newS = svd.getS().getMatrix(0, numberOfSingularValue - 1, 0,
					numberOfSingularValue - 1);
		} catch (Exception e) {
			newS = new Matrix(numberOfSingularValue, numberOfSingularValue, 0);
			for (int i = 0; i < numberOfSingularValue; i++) {
				newS.set(i, i, svd.getSingularValues()[i]);
			}
		}

		Matrix newV = svd.getV().getMatrix(0, svd.getV().getRowDimension() - 1,
				0, numberOfSingularValue - 1);

		Matrix product = newU.times(newS).times(newV.transpose());
		Matrix labelAvgProduct = getAverage(product, trainLabels, trainTexts);

		Map<String, List<String>> testTexts = getTexts(testFile);
		int testColumnSize = getColumnSize(testTexts);
		Matrix testMatrix = new Matrix(trainVocabs.size(), testColumnSize, 0);
		initializeMatrix(testMatrix, trainLabels, testTexts, trainVocabs);
		Matrix resultMatrix = new Matrix(labelAvgProduct.getColumnDimension(),
				testMatrix.getColumnDimension(), 0);
		calculateSimilarity(newU, newV, labelAvgProduct, testColumnSize,
				testMatrix, resultMatrix);
		int[] testFoundLabelIndex = getMostSimilarLabelIndex(resultMatrix);
		List<String> trueLabelNo = getTruLabelNo(trainLabels, testTexts,
				testFoundLabelIndex);
		// double accuracy = ((double)(trueLabelNo /testColumnSize) * 100);
		// resultMatrix.print(resultMatrix.getRowDimension(),
		// resultMatrix.getColumnDimension());
		Result result = new Result();
		result.setTrueLabelsNo(trueLabelNo);
		result.setTestNo(testColumnSize);
		result.setStatus(0);
		return result;
	}

	private void calculateSimilarity(Matrix newU, Matrix newV,
			Matrix labelAvgProduct, int testColumnSize, Matrix testMatrix,
			Matrix resultMatrix) {
		for (int i = 0; i < testColumnSize; i++) {
			Matrix testVector = testMatrix.getMatrix(0,
					testMatrix.getRowDimension() - 1, i, i);

			Matrix singularTestVector = testVector.transpose().times(newU)
					.times(newV.inverse());

			for (int j = 0; j < labelAvgProduct.getColumnDimension(); j++) {
				Matrix labelAvgVector = labelAvgProduct.getMatrix(0,
						labelAvgProduct.getRowDimension() - 1, j, j);

				double similarity = getSimilarity(labelAvgVector,
						singularTestVector.transpose());

				resultMatrix.set(j, i, similarity);
			}
		}
	}

	private List<String> getTruLabelNo(List<String> trainLabels,
			Map<String, List<String>> testTexts, int[] testFoundLabelIndex) {
		List<String> trueLabelsNo = new ArrayList<String>();
		int testIndex = 0;
		for (int i = 0; i < trainLabels.size(); i++) {
			List<String> tests = testTexts.get(trainLabels.get(i));
			int trueLabelNo = 0;
			for (int j = 0; j < tests.size(); j++) {
				if (testFoundLabelIndex[testIndex] == i) {
					trueLabelNo++;
				}
				testIndex++;
			}
			String label = trainLabels.get(i) + "###" + trueLabelNo + "###"
					+ tests.size();
			trueLabelsNo.add(label);
		}
		return trueLabelsNo;
	}

	private int[] getMostSimilarLabelIndex(Matrix result) {
		int[] testFoundLabelIndex = new int[result.getColumnDimension()];
		for (int col = 0; col < result.getColumnDimension(); col++) {
			double maxSimilar = 0;
			for (int row = 0; row < result.getRowDimension(); row++) {
				if (Math.abs(result.get(row, col)) > Math.abs(maxSimilar)) {
					maxSimilar = result.get(row, col);
					testFoundLabelIndex[col] = row;
				}
			}
		}
		return testFoundLabelIndex;
	}

	private double getSimilarity(Matrix trainVector, Matrix testVector) {
		int rowDimension = Math.min(testVector.getRowDimension(),
				trainVector.getRowDimension());
		double sumMultiplie = 0;
		double sumTrainPower = 0;
		double sumTestPower = 0;
		for (int i = 0; i < rowDimension; i++) {
			double trainCell = trainVector.get(i, 0);
			double testCell = testVector.get(i, 0);
			double multiplie = trainCell * testCell;
			sumMultiplie += multiplie;
			sumTrainPower += (trainCell * trainCell);
			sumTestPower += (testCell * testCell);
		}
		double similarity = sumMultiplie
				/ (Math.sqrt(sumTrainPower) * Math.sqrt(sumTestPower));
		return similarity;
	}

	private Matrix getAverage(Matrix product, List<String> labels,
			Map<String, List<String>> trainTexts) {

		int labelSize = labels.size();
		int rowDimension = product.getRowDimension();
		Matrix matrix = new Matrix(rowDimension, labelSize, 0);

		for (int i = 0; i < labelSize; i++) {
			List<String> texts = trainTexts.get(labels.get(i));
			int textSize = texts.size();
			for (int j = 0; j < textSize; j++) {
				for (int row = 0; row < rowDimension; row++) {
					double cell = matrix.get(row, i);
					cell += product.get(row, i + j);
					matrix.set(row, i, cell);
				}
			}
			for (int row = 0; row < rowDimension; row++) {
				double cell = matrix.get(row, i) / textSize;
				matrix.set(row, i, cell);
			}
		}
		return matrix;
	}

	private int getReduceSingularValueLength(double[] singularValues) {
		int length = 0;
		for (double d : singularValues) {
			if (d > 0) {
				length++;
			}
		}
		return length - 4;
	}

	private void initializeMatrix(Matrix matrix, List<String> labels,
			Map<String, List<String>> map, List<String> vocabs) {

		int col = 0;
		for (int i = 0; i < labels.size(); i++) {
			List<String> texts = map.get(labels.get(i));
			for (int j = 0; j < texts.size(); j++) {
				for (int row = 0; row < vocabs.size(); row++) {
					if (texts.get(j).toLowerCase().contains(vocabs.get(row))) {
						int number = getNumberOfVocab(texts.get(j),
								vocabs.get(row));
						matrix.set(row, col, number);
					}
				}
				col++;
			}
		}
	}

	private int getNumberOfVocab(String text, String vocab) {
		int number = 0;
		String[] split = text.trim().split(" ");
		for (String word : split) {
			word=word.replace("[", "").replace("]", "").replace("#", "").replace("@", "").replace("\"", "").replace("'", "").replace(":", "").toLowerCase();
			if (word.trim().equals(vocab)) {
				number++;
			}
		}
		return number;
	}

	private List<String> getLabels(Map<String, List<String>> texts) {
		List<String> labels = new ArrayList<String>();
		for (String label : texts.keySet()) {
			labels.add(label);
		}
		return labels;
	}

	private int getColumnSize(Map<String, List<String>> trainTexts) {
		int size = 0;
		for (String label : trainTexts.keySet()) {
			List<String> textLabel = trainTexts.get(label);
			size += textLabel.size();
		}
		return size;
	}

	private List<String> getTrainVocabs(Map<String, List<String>> trainTexts) {
		List<String> vocabs = new LinkedList<String>();
		Map<String, Integer> words = new HashMap<String, Integer>();
		int mostRepetetiveWord = 0;
		for (String label : trainTexts.keySet()) {
			List<String> textLabel = trainTexts.get(label);
			for (String txt : textLabel) {
				String[] split = txt.trim().split(" ");
				for (String string : split) {
					string = string.toLowerCase();
					string=string.replace("[", "").replace("]", "").replace("#", "").replace("@", "").replace("\"", "").replace("'", "").replace(":", "");
					if (string.trim().equals("") || string.trim().length() < 3
							|| string.trim().equals("the")
							|| string.trim().equals(" ")
							|| string.trim().equals("are")
							|| string.trim().equals("and")
							|| string.trim().equals("you")
							|| string.trim().equals("with")) {
						continue;
					}
					if (string
							.trim()
							.matches(
									"^http\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?$")) {
						continue;
					}
					if (words.containsKey(string.trim())) {
						Integer number = words.get(string.trim());
						number += 1;
						words.put(string.trim(), number);
						if (mostRepetetiveWord < number) {
							mostRepetetiveWord = number;
						}
					} else {
						words.put(string.trim(), 1);
					}
				}
			}
		}
		for (String string : words.keySet()) {
			Integer number = words.get(string);
			if (number > 4 && number < (mostRepetetiveWord - 2)) {
				vocabs.add(string);
			}
		}
		return vocabs;
	}

	private Map<String, List<String>> getTexts(File file) {
		Map<String, List<String>> texts = new HashMap<String, List<String>>();
		try {
			InputStream inStream = new FileInputStream(file);
			InputStreamReader ipsr = new InputStreamReader(inStream);
			BufferedReader br = new BufferedReader(ipsr);
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("")) {
					continue;
				}
				String[] split = line.trim().split("\\t");
				String label = split[0];
				String text = split[1];
				List<String> labeledText = null;
				if (texts.containsKey(label)) {
					labeledText = texts.get(label);
				} else {
					labeledText = new ArrayList<String>();
				}
				labeledText.add(text);
				texts.put(label, labeledText);
			}
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return texts;
	}

}
