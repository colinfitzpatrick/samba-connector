package org.mule.modules.smb.automation.functional;

import java.util.Properties;

public class TestProperties {

	public TestProperties() {
		// TODO Auto-generated constructor stub
	}

	public Properties getConfig() {
	
		Properties prop = new Properties();
		try {
	    //load a properties file from class path, inside static method
			prop.load(getClass().getClassLoader().getResourceAsStream("automation-credentials.properties"));
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
		return prop;
	}
	
	static final String TEST_PAYLOAD="Any String you want";
	static final String TEST_EXPECTED_PAYLOAD="Any String you want";
	static final String TEST_FILEEXTENSION="txt";
	static final String TEST_TESTFILE="file1-2";
	static final String TEST_FILEPATTERN="*.txt";
	static final String TEST_EVENTSOURCE="receiver";
	
}
