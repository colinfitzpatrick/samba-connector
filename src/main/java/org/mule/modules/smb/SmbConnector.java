package org.mule.modules.smb;

import org.apache.log4j.Logger;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.OnException;
import org.mule.api.annotations.param.Payload;
import org.mule.modules.smb.config.ConnectorConfig;
import org.mule.modules.smb.error.ErrorHandler;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

@Connector(name="smb", friendlyName="Smb")
@OnException(handler=ErrorHandler.class)
public class SmbConnector {
	private static Logger logger = Logger.getLogger(SmbConnector.class);

    @Config
    ConnectorConfig config;

    /**
     * Custom processor
     *
     * @param friend Name to be used to generate a greeting message.
     * @return A greeting message
     */
    @Processor
    public Boolean copyFile(@Payload String payload, String printerFileExtensionType, String fileName) {
    	
    	logger.info(">>SMB CONNECTOR COPY FILE BEGIN");
    	Boolean isWriteSuccessful = false;
    	
    	
    	
    	try {
			String user = config.getDomain() + ";" + config.getUsername() + ":" + config.getPassword();
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);
			
			String path = "smb://" + config.getHost() + "/" + config.getFolder() + "/"+fileName+"."+printerFileExtensionType;
			logger.debug("TARGET PATH = " + path);
			
			SmbFile sFile = new SmbFile(path, auth);
			SmbFileOutputStream sfos = new SmbFileOutputStream(sFile);
			
			logger.debug("Writing file now"  );
			logger.debug("fileContents are : " + payload);
			
			
			sfos.write(payload.getBytes());
			isWriteSuccessful = true;
			sfos.close();

		} catch (Exception e) {
			isWriteSuccessful = false;
			logger.error(e);
			logger.info(">>SMB CONNECTOR COPY FILE END");
			isWriteSuccessful=false;
			return isWriteSuccessful;
		}
    	logger.info("<< SMB CONNECTOR COPY FILE END");
    	isWriteSuccessful=true;
		return isWriteSuccessful;

    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		SmbConnector.logger = logger;
	}

}