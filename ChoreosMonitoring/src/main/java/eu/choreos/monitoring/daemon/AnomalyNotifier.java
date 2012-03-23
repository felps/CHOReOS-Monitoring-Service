package eu.choreos.monitoring.daemon;

import java.util.Map;
import eu.choreos.monitoring.*;

public class AnomalyNotifier {

	private GmondDataReader dataReader;
	private Map<String, Gmetric> metricsMap;

	public Map<String, Gmetric> getMetricsMap() {
		return metricsMap;
	}

	public AnomalyNotifier(GmondDataReader dataReader) {
		this.dataReader = dataReader;
	}

	public void setDataReader(GmondDataReader dataReader) {
		this.dataReader = dataReader;
	}

	public boolean identifySurpassedUpperThreshold(String metricName,
			double threshold) {
		return (getMetricNumericalValue(metricName) > threshold);
	}

	public boolean identifySurpassedLowerThreshold(String metricName,
			double threshold) {
		return (getMetricNumericalValue(metricName) < threshold);
	}

	private double getMetricNumericalValue(String metricName) {
		metricsMap = dataReader.getAllMetrics();

		if (metricsMap == null) {
			System.out.println("ERROR: Could not get metrics");
			System.exit(1);
		}
		return Double.parseDouble(metricsMap.get(metricName).getValue());
	}

}