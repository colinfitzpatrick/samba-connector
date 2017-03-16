package org.mule.modules.smb.automation.functional;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.anything;

import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.smb.SmbConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class DeleteFileTestCases extends BaseSmbTestCase {

	
	public DeleteFileTestCases() {
		super();
	}

	@Before
	public void setup() {
		getConnector().copyFile(payload, printerFileExtensionType, fileName);	
	}

	@After
	public void tearDown() {
		// TODO
	}

	@Test
	public void verify() throws MalformedURLException {
		boolean expected = true;;
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType;
		SmbFile sFile = new SmbFile(path,getAuth());
		
		//test file is deleted
		assertEquals(getConnector().deleteFile(sFile), expected);
		
		//test file does not exist
		byte[] contents = getConnector().readFile(fileName + "." + printerFileExtensionType);	
		assertEquals(contents,null);
	
	}

	@Test
	public void verifyThrowsError() throws MalformedURLException {
		String printerFileExtensionType = "txt";
		String fileName= "donotexist";
		boolean expected = false;
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType;
		SmbFile sFile = new SmbFile(path,getAuth());
		
		// test false is returned if no file to delete
		assertEquals(getConnector().deleteFile(sFile), expected);
	
	}	
	
}