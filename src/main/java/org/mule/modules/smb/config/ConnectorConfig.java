package org.mule.modules.smb.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.Configurable;


@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

    @Configurable
    @Placement(group="General Settings")
   	private String username;
    
    @Configurable
    @Placement(group="General Settings")
	private String password;
    
    @Configurable
    @Placement(group="General Settings")
    private String host;
    
    @Configurable
    @Placement(group="General Settings")
    @Optional
    private String domain;
    
    @Configurable
    @Placement(group="General Settings")
    private String folder;
    
    @Configurable
    @Placement(group="Filters")
    @Optional
    @Default("false") 
    @Summary("Filter to check file age")
    private boolean checkFileAge;
    
    @Configurable
    @Placement(group="Filters")
    @Optional
    @Summary("Process files older than file age in ms")
    private long fileAge;

    @Configurable
    @Placement(tab="Advanced", group="File Archiving")
    @Optional
    @Summary("Process files older than file age in ms")
    private String outputFolder;
       
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public boolean getCheckFileAge() {
		return checkFileAge;
	}
	public void setCheckFileAge(boolean checkFileAge) {
		this.checkFileAge = checkFileAge;
	}
	public long getFileAge() {
		return fileAge;
	}
	public void setFileAge(long fileAge) {
		this.fileAge = fileAge;
	}
	public String getOutputFolder() {
		return outputFolder;
	}
	public void setOutputFolder(String folder) {
		this.outputFolder = folder;
	}
}