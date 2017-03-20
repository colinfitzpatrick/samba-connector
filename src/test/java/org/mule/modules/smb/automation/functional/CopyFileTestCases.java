package org.mule.modules.smb.automation.functional;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.smb.SmbConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class CopyFileTestCases extends BaseSmbTestCase {
	

	public CopyFileTestCases() {
		super();
	}

	@Before
	public void setup() {
		// TODO
	}

	@After
	public void tearDown() throws MalformedURLException {
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType;
		SmbFile sFile = new SmbFile(path,getAuth());
		getConnector().deleteFile(sFile);
	}

	@Test
	public void verify() {
		java.lang.Boolean expected = true;
		assertEquals(getConnector().copyFile(payload, printerFileExtensionType, fileName, null), expected);

	}

}