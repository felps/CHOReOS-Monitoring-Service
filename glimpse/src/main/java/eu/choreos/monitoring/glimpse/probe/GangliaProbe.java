package eu.choreos.monitoring.glimpse.probe;

import it.cnr.isti.labse.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labse.glimpse.probe.GlimpseAbstractProbe;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;

import eu.choreos.monitoring.daemon.Threshold;
import eu.choreos.monitoring.daemon.ThresholdEvalDaemon;
import eu.choreos.monitoring.utils.ShellHandler;

public class GangliaProbe extends GlimpseAbstractProbe {

	private ThresholdEvalDaemon daemon;


	public GangliaProbe(Properties settings) {
		super(settings);
	}
	
	public void notifyMessages(GlimpseBaseEvent<String> message,String host,int port) {
			thresholdEval(host,port,message);
	}
	
	private void thresholdEval(String host,int port, GlimpseBaseEvent<String> message){
		daemon = new ThresholdEvalDaemon(host,port);
		List<Threshold> thresholds = new ArrayList<Threshold>();
		thresholds.add(new Threshold("load_one", Threshold.MAX, 1));
		daemon.setThresholdList(thresholds);
		
		while (true) {
			sendAllThresholdMsgs(message);
			sleep(6000);
		}
	}
	
	private void sendAllThresholdMsgs(GlimpseBaseEvent<String> event) {
		List<Threshold> triggeredThresholds = daemon.evaluateThresholds();
		event.setNetworkedSystemSource(this.getHostName());
		for(Threshold threshold:triggeredThresholds){
			event.setData(threshold.toString());
			sendMessage(event);
		}
	}

	private void sendMessage(GlimpseBaseEvent<String> event) {
		try {
			this.sendEventMessage(event,false);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getHostName() {
		return ShellHandler.runLocalCommand(getScriptCommand()).replace("\n",
				"");
	}

	public String getScriptCommand() {

		String command;
		URL location = this.getClass().getClassLoader().getResource("hostname.sh");		
		
		File tmpFile = new File("/tmp/hostname.sh");
		try {
			if (tmpFile.exists())
				FileUtils.forceDelete(tmpFile);
			FileUtils.copyURLToFile(location, tmpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (tmpFile.exists()){
			tmpFile.setExecutable(true);
			command = "/bin/bash "
					+ (tmpFile.getAbsolutePath()).replace("%20", " ");
		}
		else{
			command = "/bin/bash /tmp/hostname.sh";
		}
		
		return command;
	}
	
	@Override
	public void sendMessage(GlimpseBaseEvent<?> event, boolean debug) {
		//TODO
	}

}