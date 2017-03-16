package org.mule.modules.smb.automation.functional;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.smb.SmbConnector;
import org.mule.modules.smb.config.ConnectorConfig;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class ReadFileTestCases extends BaseSmbTestCase {
	
	public ReadFileTestCases() {
		super();
	}

	@Before
	public void setup() {
		getConnector().copyFile(payload, printerFileExtensionType, fileName);
	}

	@After
	public void tearDown() throws MalformedURLException { 
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType;
		SmbFile sFile = new SmbFile(path,getAuth());
		getConnector().deleteFile(sFile);
	}

	@Test
	public void verify() {
		byte[] expected = TestProperties.TEST_EXPECTED_PAYLOAD.getBytes();
		java.lang.String fileName = this.fileName + "." + this.printerFileExtensionType;
		byte[] contents =getConnector().readFile(fileName);
		assertEquals(Arrays.toString(contents), Arrays.toString(expected));
	}

}