package eu.choreos.monitoring.platform.daemon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.choreos.monitoring.platform.daemon.datatypes.Gmetric;
import eu.choreos.monitoring.platform.exception.GangliaException;
import eu.choreos.monitoring.platform.utils.GmondDataReader;

public class ThresholdManager {

	private GmondDataReader dataReader;
	private Set<Threshold> thresholds;
	private Map<String, Gmetric> metricsMap;

	public ThresholdManager(GmondDataReader dataReader) {
		this.dataReader = dataReader;
		thresholds = new HashSet<Threshold>();
	}

	public void addThreshold(Threshold threshold) {
			thresholds.add(threshold);
		return;
	}

	public void addMultipleThresholds(List<Threshold> thresholdList) {
		thresholds.addAll(thresholdList);
	}

	public boolean wasSurpassed(Threshold threshold) throws GangliaException {
		return threshold.wasSurpassed(getMetricNumericalValue(threshold
				.getName()));
	}

	private double getMetricNumericalValue(String metricName) throws GangliaException {
		metricsMap = dataReader.getAllMetrics();
		return Double.parseDouble(metricsMap.get(metricName).getValue());
	}

	public List<Threshold> getAllSurpassedThresholds() throws GangliaException {

		List<Threshold> surpassedThresholds = new ArrayList<Threshold>();

		for (Threshold threshold : thresholds) {

			String thresholdName = threshold.getName();
			Double metricValue = getMetricNumericalValue(thresholdName);

			if (threshold.wasSurpassed(metricValue))
				surpassedThresholds.add(threshold);
		}
		return surpassedThresholds;
	}

	public int getThresholdSize() {
		return thresholds.size();
	}

}