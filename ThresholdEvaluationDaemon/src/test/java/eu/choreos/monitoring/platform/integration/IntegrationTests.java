package eu.choreos.monitoring.platform.integration;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import eu.choreos.monitoring.platform.daemon.ThresholdEvalDaemon;
import eu.choreos.monitoring.platform.daemon.HostManager;
import eu.choreos.monitoring.platform.daemon.Threshold;
import eu.choreos.monitoring.platform.daemon.ThresholdEvalDaemonLauncher;
import eu.choreos.monitoring.platform.exception.GangliaException;
import eu.choreos.monitoring.platform.utils.GmondDataReader;

public class IntegrationTests {

	private eu.choreos.monitoring.platform.utils.GmondDataReader dataReader;
	private HostManager hostManager;
	private ThresholdEvalDaemon thresholdManager;

	@Before
	public void setUp() {
		dataReader = new GmondDataReader("localhost", 8649);
		
	}

	//@Test
	public void shouldReadDataFromGanglia() throws GangliaException {

		assertFalse(dataReader.getUpToDateHostsInfo().isEmpty());
	}

	//@Test
	public void shouldGetAtLeastOneHost() throws GangliaException {
		hostManager = new HostManager(dataReader);
		
		assertTrue(hostManager.getHosts().size() >= 1);
	}
	
	//@Test
	public void shouldEvaluateAThreshold() throws GangliaException{
		Properties settings = ThresholdEvalDaemonLauncher.getProperties();
		thresholdManager = new ThresholdEvalDaemon(settings ,"localhost", 8649);
		
		Threshold shouldBeSurpassed = new Threshold("load_one", Threshold.MAX, 0);
		
		thresholdManager.addThreshold(shouldBeSurpassed);
		
		assertEquals(1, thresholdManager.getSurpassedThresholds().size());
	}
}
