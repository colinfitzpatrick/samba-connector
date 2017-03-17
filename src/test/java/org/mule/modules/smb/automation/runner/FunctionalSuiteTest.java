package org.mule.modules.smb.automation.runner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.modules.smb.automation.functional.ReadFileTestCases;
import org.mule.modules.smb.SmbConnector;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

import jcifs.smb.SmbFile;

import org.mule.modules.smb.automation.functional.CopyFileTestCases;
import org.mule.modules.smb.automation.functional.DeleteFileTestCases;
import org.mule.modules.smb.automation.functional.GetFilesTestCases;
import org.mule.modules.smb.automation.functional.MoveFileTestCases;
import org.mule.modules.smb.automation.functional.SourceTestCases;

@RunWith(Suite.class)
@SuiteClasses({ ReadFileTestCases.class, CopyFileTestCases.class, DeleteFileTestCases.class, GetFilesTestCases.class,
		MoveFileTestCases.class, SourceTestCases.class })

public class FunctionalSuiteTest {

	@BeforeClass
	public static void initialiseSuite() {
		ConnectorTestContext.initialize(SmbConnector.class);
	}

	@AfterClass
	public static void shutdownSuite() {
		ConnectorTestContext.shutDown();
	}

}