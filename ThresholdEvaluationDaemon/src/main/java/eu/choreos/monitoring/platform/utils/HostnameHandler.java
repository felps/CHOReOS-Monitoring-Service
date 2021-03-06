package eu.choreos.monitoring.platform.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import eu.choreos.platform.utils.CommandRuntimeException;
import eu.choreos.platform.utils.ShellHandler;

public class HostnameHandler {

	public static String getScriptCommand() {

		String command;
		URL location = (new HostnameHandler()).getClass().getClassLoader()
				.getResource("hostname.sh");

		File tmpFile = new File("/tmp/hostname.sh");
		try {
			if (tmpFile.exists())
				FileUtils.forceDelete(tmpFile);
			FileUtils.copyURLToFile(location, tmpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (tmpFile.exists()) {
			tmpFile.setExecutable(true);
			command = "/bin/bash "
					+ (tmpFile.getAbsolutePath()).replace("%20", " ");
		} else {
			command = "/bin/bash /tmp/hostname.sh";
		}

		return command;
	}

	public static String getHostName() throws CommandRuntimeException {
		return (new ShellHandler()).runLocalCommand(getScriptCommand())
				.replace("\n", "");
	}
}
