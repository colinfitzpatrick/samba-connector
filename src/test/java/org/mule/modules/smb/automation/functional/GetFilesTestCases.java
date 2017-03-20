package org.mule.modules.smb.automation.functional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotSame;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.smb.SmbConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import jcifs.smb.SmbFile;

public class GetFilesTestCases extends BaseSmbTestCase {

	public GetFilesTestCases() {
		super();
	}

	@Before
	public void setup() {
		getConnector().copyFile(payload, printerFileExtensionType, fileName, null);	
	}

	@After
	public void tearDown() throws MalformedURLException {
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType;
		SmbFile sFile = new SmbFile(path,getAuth());
		getConnector().deleteFile(sFile);
	}

	@Test
	public void verify() {
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType;
		List<java.lang.String> expected = new ArrayList();
		expected.add(path);
		java.lang.String payload = null;
		java.lang.String filePattern = "*.txt";
		ArrayList files = (ArrayList) getConnector().getFiles(payload, filePattern);
		assertEquals(getConnector().getFiles(payload, filePattern).size(), expected.size());
		
		expected.add(path);
		assertThat(getConnector().getFiles(payload, filePattern), IsNot.not(IsEqual.equalTo(expected)));
	}

}