package org.mule.modules.smb.config;

import org.mule.api.annotations.components.Configuration;
import org.mule.api.annotations.Configurable;

@Configuration(friendlyName = "Configuration")
public class ConnectorConfig {

    @Configurable
   	private String username;
    @Configurable
	private String password;
    @Configurable
    private String host;
    @Configurable
    private String domain;
    @Configurable
    private String folder;
    

    
    
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

}