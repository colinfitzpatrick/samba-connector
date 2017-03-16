package org.mule.modules.smb.automation.functional;

import java.net.MalformedURLException;
import java.util.Properties;

import org.mule.modules.smb.SmbConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class BaseSmbTestCase extends AbstractTestCase<SmbConnector>{

	String payload = TestProperties.TEST_PAYLOAD;
	String printerFileExtensionType= TestProperties.TEST_FILEEXTENSION;
	String fileName= TestProperties.TEST_TESTFILE;
	String filePattern= TestProperties.TEST_FILEPATTERN;	
	String eventSource = TestProperties.TEST_EVENTSOURCE;
	String host="";
	String username="";
	String password="";
	String folder="";
	String outputFolder="";
	String domain="";
	
	Properties config = new TestProperties().getConfig();
	
	public BaseSmbTestCase() {
		super(SmbConnector.class);
		this.host=config.getProperty("config.host");
		this.username=config.getProperty("config.username");
		this.password=config.getProperty("configpassword");
		this.folder=config.getProperty("config.folder");
		this.outputFolder=config.getProperty("config.outputFolder");
		this.domain=config.getProperty("config.domain");
	}
	

	public NtlmPasswordAuthentication getAuth(){
	    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(config.getProperty("config.domain") == null ? "" : config.getProperty("config.domain"), config.getProperty("config.username"), config.getProperty("config.password"));
	    return auth;
	}
	
}
