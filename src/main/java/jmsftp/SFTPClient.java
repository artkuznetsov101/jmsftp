package jmsftp;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class SFTPClient {
	private FileSystemManager manager;
	private FileObject remote;
	
	public void test() throws FileSystemException {
		
		connect();
		System.out.println(remote);
	}
	
	
	public void connect() throws FileSystemException {
		manager = VFS.getManager();
		remote = manager.resolveFile(getConnection(SFTPSettings.HOST, SFTPSettings.PORT, SFTPSettings.USERNAME, SFTPSettings.PASSWORD));
		
	}


	private String getConnection(String host, String port, String username, String password) {
		return String.format("sftp://%s:%s@%s:%s", username, password, host, port);
	}
	
}
