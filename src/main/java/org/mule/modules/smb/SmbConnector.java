package org.mule.modules.smb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.SourceStrategy;
import org.mule.api.annotations.lifecycle.OnException;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.smb.config.ConnectorConfig;
import org.mule.modules.smb.error.ErrorHandler;
import org.mule.util.IOUtils;

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
	*	@param filePattern The file pattern to poll
	*	@param sourceCallback
	*/    
    
    @Source(sourceStrategy=SourceStrategy.POLLING, pollingPeriod=1000, name="receiver")
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
						props.put("smbOriginalFile", fileList[i].getPath());
						SmbFile renamedResource = new SmbFile(fileList[i].getPath()+".processing",auth);
						if (renamedResource.exists())
							renamedResource.delete();
						fileList[i].renameTo(renamedResource);
						props.put("smbFile",renamedResource.getPath());
						props.put("smbPath",path);
						props.put("smbObject",renamedResource);
						sourceCallback.process(this.readFileContents(renamedResource),props);
						logger.debug("<< SMB CONNECTOR FILE FOUND " + fileList[i].getPath());
					}
				}
			} else {
				logger.debug("A file with the filename pattern '" + filePattern + "'could not be found.");
			}
		} catch (SmbException e) {
            if (e.getMessage().equals("The system cannot find the file specified.")) {
                logger.debug("A file with the filename pattern '" + filePattern + "'could not be found.");
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
	*	@param filePattrn The file pattern to poll
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
	*	@param fileName The name of the file to read (excluding path)
	*	@return returns file contents as byte array
	*/
	@Processor
	public byte[] readFile(@Payload String payload, String fileName) {			
		logger.debug(">> SMB CONNECTOR READ FILE BEGIN");

        NtlmPasswordAuthentication auth = this.getAuth();
		
		String path = "smb://" + config.getHost() + "/" + config.getFolder() + "/";
		try {
			SmbFile resource = new SmbFile(path,auth);		
			return readFileContents(resource);
			
		} catch (Exception e) {
			logger.error(e);	
			return null;
		}		
	}  
 
	/**
    * 	Deletes a specified file. Requires the SmbFile object.
    *
	*	@param SmbFile The SmbFile object to delete
	*	@return returns boolean to indicate successful deletion of a file
    */
    @Processor
    public boolean deleteFile(@InboundHeaders("smbObject") SmbFile smbObject) {
         
    	 logger.debug(">> SMB CONNECTOR DELETE FILE BEGIN");
         
         try {
        	 smbObject.delete();           
         } catch (Exception e) {
             logger.error(e);
             logger.debug("<< SMB CONNECTOR DELETE FILE END");
             return false;
         }
         
         logger.debug("<< SMB CONNECTOR DELETE FILE END");   
         return true;
     }

	/**
	* 	Moves a specified file to the output folder. Requires inbound properties to be set.
	*
	*	@param smbOriginalFilePath The original file path
	* 	@param smbFilePath The SmbFile object to move
	*	@return returns boolean to indicate successful move of a file
	*/
     @Processor
     public boolean moveFile(@InboundHeaders("smbObject") SmbFile smbObject, @InboundHeaders("smbOriginalFile") String smbOriginalFilePath) {          
     	 logger.debug(">> SMB CONNECTOR MOVE FILE BEGIN");
         SmbFile targetFile = null;

         if (config.getOutputFolder()!=null)  {
          try {
              NtlmPasswordAuthentication auth = this.getAuth();           
              String targetPath = "smb://" + config.getHost() + "/" + config.getOutputFolder() + "/" + smbOriginalFilePath.substring(smbOriginalFilePath.lastIndexOf('/') + 1);;
              logger.debug("TARGET PATH " + targetPath);
              targetFile = new SmbFile(targetPath,auth);
              if (targetFile.exists()) {
            	  targetFile.delete();
              }
              smbObject.renameTo(targetFile);       
          } catch (Exception e) {
              logger.error(e);
              logger.debug("<< SMB CONNECTOR MOVE FILE END");
              return false;
          }
          
          logger.debug("<< SMB CONNECTOR MOVE FILE END");   
          return true;
         } else {
        	 logger.error("The output folder must be specified to move a file");
        	 return false;
         }
         
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