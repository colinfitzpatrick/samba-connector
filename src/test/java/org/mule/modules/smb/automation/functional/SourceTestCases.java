package org.mule.modules.smb.automation.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.smb.SmbConnector;
import org.mule.modules.smb.config.ConnectorConfig;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

public class SourceTestCases extends BaseSmbTestCase{

	public SourceTestCases() {
		super();
	}

	@Before
	public void setup() {
		getConnector().copyFile(payload, printerFileExtensionType, fileName, null);
	}
	
    @Before
    public void setUp() throws Throwable{
        Object[] signature = {filePattern,null};
        getDispatcher().initializeSource(eventSource, signature);
    }

    @Test
    public void verify() throws InterruptedException {
    	Thread.sleep(2000);
        List<Object> events = getDispatcher().getSourceMessages(eventSource);
        assertTrue(!events.isEmpty());
        byte[] expected = (TestProperties.TEST_EXPECTED_PAYLOAD).getBytes();
        byte[] contents = (byte[]) events.get(0);
        assertEquals(Arrays.toString(contents), Arrays.toString(expected));
    } 
    
    @After
    public void tearDown() throws Throwable{
		String path = "smb://" + host + "/" + folder + "/" + fileName + "." + printerFileExtensionType + ".processing";
		SmbFile sFile = new SmbFile(path,getAuth());
		getConnector().deleteFile(sFile);
    	getDispatcher().shutDownSource(eventSource);
    }
    
	
}
