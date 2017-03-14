package org.mule.modules.smb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.SourceStrategy;
import org.mule.api.annotations.SourceThreadingModel;
import org.mule.api.annotations.lifecycle.OnException;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Payload;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.smb.config.ConnectorConfig;
import org.mule.modules.smb.error.ErrorHandler;
import org.mule.util.IOUtils;

import com.ning.http.client.providers.netty.Callback;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
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
            NtlmPasswordAuthentication auth = this.getAuth();
			
			String path = "smb://" + config.getHost() + "/" + config.getFolder() + "/"+fileName+"."+printerFileExtensionType;
			logger.info("TARGET PATH = " + path);
			
			SmbFile sFile = new SmbFile(path,auth);
			SmbFileOutputStream sfos = new SmbFileOutputStream(sFile);
			
			logger.info("Writing file now"  );
			logger.info("fileContents are : " + payload);
			
			
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
    
	/**
	* 	The inbound receiver polls for files in a specified smb share and creates a message for Mule to process.
	* 	The polling period is configurable in the Connector.
	*
	*	@param filePattern to poll
	*	@param sourceCallback
	*/    
    
    @Source(threadingModel=SourceThreadingModel.SINGLE_THREAD, sourceStrategy=SourceStrategy.POLLING, pollingPeriod=1000, name="receiver")
    public void receiver(final String filePattern, final SourceCallback sourceCallback) {
    	logger.debug(">> SMB POLLING BEGIN FOR " + config.getFolder());
		List<String> files = new ArrayList();
		
        NtlmPasswordAuthentication auth = this.getAuth();
		
		String path = "smb://" + config.getHost() + "/" + config.getFolder() + "/";
		try {
			SmbFile resource = new SmbFile(path,auth);
			if (resource.list().length > 0) {
				SmbFile[] fileList = resource.listFiles(filePattern);
				for (int i=0; i<fileList.length; i++) {
					if (validateFile(fileList[i])) {
						HashMap props = new HashMap();
						props.put("smbOriginalFilePath", fileList[i].getPath());
						SmbFile renamedResource = new SmbFile(fileList[i].getPath()+".processing",auth);
						fileList[i].renameTo(renamedResource);
						props.put("smbFilePath",renamedResource.getPath());
						sourceCallback.process(this.readFileContents(renamedResource),props);
						logger.debug("<< SMB CONNECTOR FILE FOUND " + fileList[i].getPath());
					}
				}
			}
		} catch (SmbException e) {
            if (e.getMessage().equals("The system cannot find the file specified.")) {
                logger.info("A file with the filename pattern '" + filePattern + "'could not be found.");
            } else {
            	logger.error(e);
            }
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("<< SMB POLLING COMPLETE FOR "  + config.getFolder());
    }
    
	/**
	* 	Returns a list of files from SMB Share based on a file pattern
	*
	*	@param payload 
	*	@param filePattrn 
	*	@return list of files
	*/
	@Processor  
	public List<String> getFiles(@Payload String payload, String filePattern) {
		logger.debug(">> SMB CONNECTOR GET FILES BEGIN");
		List<String> files = new ArrayList();
		
        NtlmPasswordAuthentication auth = this.getAuth();
		
		String path = "smb://" + config.getHost() + "/" + config.getFolder() + "/";
		logger.debug("TARGET PATH = " + path);
			try {
				SmbFile resource = new SmbFile(path,auth);
				if (resource.isFile()) {
					files.add(resource.getPath());
					logger.debug(">> FILE FOUND " + resource.getPath());
				} else if (resource.list().length > 0) {
					SmbFile[] fileList = resource.listFiles(filePattern);
					for (int i=0; i<fileList.length; i++) {
						files.add(fileList[i].getPath());
						logger.debug(">> FILE FOUND " + fileList[i].getPath());
					}
				}
			} catch (Exception e) {
				logger.error(e);
				logger.debug("<< SMB CONNECTOR GET FILES END");
				return null;
			}

		logger.debug(">>SMB CONNECTOR GET FILES END");
		return files;
	}
    
	/**
	*	Message processor that can be directly called to read a specified File	
	*
	*	@param payload
	*	@param smbPath is the File Path
	*	@return returns file contents as byte array
	*/
	@Processor
	public byte[] readFile(@Payload String payload, String smbPath) {			
		logger.debug(">> SMB CONNECTOR READ FILE BEGIN");
		
		SmbFileInputStream inFile = null;
		SmbFile sFile = null;
		
		try {
            NtlmPasswordAuthentication auth = this.getAuth();
			
			String path = smbPath;
			
			sFile = new SmbFile(path,auth);		
			inFile = new SmbFileInputStream(sFile);
			byte[] sBytes = IOUtils.toByteArray(inFile);
			inFile.close();
			logger.debug("<< SMB CONNECTOR READ FILE ENDÍ");
			return sBytes;
			
		} catch (Exception e) {
			logger.error(e);	
			return null;
		}		
	}  
 
	/**
    * 	Deletes a specified file
    *
	*	@param payload
	*	@param smbPath is the File Path
	*	@return returns boolean to indicate successful deletion of a file
    */
    @Processor
    public boolean deleteFile(@Payload String payload, String smbPath) {
         
    	 logger.debug(">> SMB CONNECTOR DELETE FILE BEGIN");
         SmbFile sFile = null;
         
         try {
             NtlmPasswordAuthentication auth = this.getAuth();
             
             String path = smbPath;
             logger.debug("TARGET PATH = " + path);
             
             sFile = new SmbFile(path,auth);  
             sFile.delete();
            
         } catch (Exception e) {
             logger.error(e);
             logger.debug("<< SMB CONNECTOR DELETE FILE END");
             return false;
         }
         
         logger.debug("<< SMB CONNECTOR DELETE FILE END");   
         return true;
     }
    
	protected byte[] readFileContents(SmbFile sFile) {
		
		logger.debug(">> SMB CONNECTOR READ FILE CONTENT BEGIN");
		
		SmbFileInputStream inFile = null;
		
		try {
			inFile = new SmbFileInputStream(sFile);
			byte[] sBytes = IOUtils.toByteArray(inFile);
			inFile.close();
			logger.debug("<< SMB CONNECTOR READ FILE CONTENT END");
			return sBytes;
			
		} catch (Exception e) {
			logger.error(e);	
			logger.debug("<< SMB CONNECTOR READ FILE CONTENT END");
			return null;
		}
			
	}  	
	
    protected boolean validateFile(SmbFile file)
    {
        if (config.getCheckFileAge())
        {
            long fileAge = config.getFileAge();
            long lastMod = file.getLastModified();
            long now = System.currentTimeMillis();
            long thisFileAge = now - lastMod;

            logger.debug("fileAge = " + thisFileAge + ", expected = " + fileAge + ", now = " + now
                         + ", lastMod = " + lastMod);
            if (thisFileAge < fileAge)
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("The file has not aged enough yet, will return nothing for: " + file.getName());
                }
                return false;
            }
        }
        return true;
    }
	     
    private NtlmPasswordAuthentication getAuth() {
        return new NtlmPasswordAuthentication(config.getDomain() == null ? "" : config.getDomain(), config.getUsername(), config.getPassword());
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