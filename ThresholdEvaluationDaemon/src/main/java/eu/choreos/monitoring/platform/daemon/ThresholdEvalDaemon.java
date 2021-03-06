package eu.choreos.monitoring.platform.daemon;

import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import eu.choreos.monitoring.platform.daemon.datatypes.Host;
import eu.choreos.monitoring.platform.daemon.notifier.GlimpseMessageHandler;
import eu.choreos.monitoring.platform.daemon.notifier.MessageHandlingFault;
import eu.choreos.monitoring.platform.exception.GangliaException;
import eu.choreos.monitoring.platform.utils.GmondDataReader;

public class ThresholdEvalDaemon {

	private static final int NOTIFICATION_INTERVAL = 6000;
	private GlimpseMessageHandler messageHandler;
	private ThresholdManager thresholdManager;

	public ThresholdEvalDaemon(Properties settings, String host, int port)
			throws GangliaException {
		this(settings, host, port, (new GlimpseMessageHandler(settings)),
				(new ThresholdManager(new HostManager(new GmondDataReader(host,
						port)))));
	}

	public ThresholdEvalDaemon(Properties settings, String host, int port,
			GlimpseMessageHandler msgHandler, ThresholdManager tshdManager)
			throws GangliaException {
		messageHandler = msgHandler;
		thresholdManager = tshdManager;
	}

	public void addThreshold(Threshold threshold) {
		thresholdManager.addThreshold(threshold);
	}

	public void addMultipleThreshold(List<Threshold> thresholdList) {
		thresholdManager.addMultipleThresholds(thresholdList);
	}

	public void continuouslyEvaluateThresholdsAndSendMessages(
			GlimpseBaseEvent<String> message) {

		while (true) {
			try {
				evaluateThresholdsSendMessagesAndSleep(message,
						NOTIFICATION_INTERVAL);
			} catch (GangliaException e) {
				e.handleException();
				sleep(NOTIFICATION_INTERVAL);
			}
		}
	}

	public void evaluateThresholdsSendMessagesAndSleep(
			GlimpseBaseEvent<String> message, int sleepingTime)
			throws GangliaException {

		if (thereAreSurpassedThresholds()) {
			sendAllSurpassedThresholdMessages(message);
		}

		sleep(sleepingTime);
	}

	public boolean thereAreSurpassedThresholds() throws GangliaException {
		return thresholdManager.thereAreSurpassedThresholds();
	}

	public Map<String, List<Threshold>> getSurpassedThresholds()
			throws GangliaException {

		Map<String, List<Threshold>> evaluateAllThresholds = thresholdManager
				.getSurpassedThresholds();
		return evaluateAllThresholds;
	}

	private void sendMessage(GlimpseBaseEvent<String> message) {
		try {
			messageHandler.sendMessage(message);
		} catch (MessageHandlingFault e) {
			e.handleException();
		}
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void sendAllSurpassedThresholdMessages(
			GlimpseBaseEvent<String> message) throws GangliaException {

		Map<String, List<Threshold>> surpassedThresholds = getSurpassedThresholds();

		for (String host : surpassedThresholds.keySet()) {
			for (Threshold threshold : surpassedThresholds.get(host)) {
				message.setData(host + ": " + threshold.toString());
				sendMessage(message);
			}
		}
	}
}
