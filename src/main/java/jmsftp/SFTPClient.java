package jmsftp;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.Selectors;
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
		remote = manager.resolveFile(
				getConnection(SFTPSettings.HOST, SFTPSettings.PORT, SFTPSettings.USERNAME, SFTPSettings.PASSWORD));
		System.out.println("isExists " + remote.exists());
		System.out.println("isFolder " + remote.isFolder());
				
		FileObject file = remote.resolveFile("test.txt");
		System.out.println("isExists " + file.exists());
		System.out.println("isFolder " + file.isFolder());
		
		
		download(file, Paths.get("C:\\!!"));
	}

	private String getConnection(String host, String port, String username, String password) {
		return String.format("sftp://%s:%s@%s:%s", username, password, host, port);
	}

	public void download(FileObject src, Path local) throws FileSystemException {
//		LocalFile localFileObject = (LocalFile) manager.resolveFile(local.toUri().toString());
//		try {
//			localFileObject.copyFrom(file, new AllFileSelector());
//		} finally {
//			localFileObject.close();
//			file.close();
//		}

		FileObject dest = manager.resolveFile("C:\\!!");
		if (dest.exists() && dest.getType() == FileType.FOLDER) {
			dest = dest.resolveFile(src.getName().getBaseName());
		}

		dest.copyFrom(src, Selectors.SELECT_ALL);
	}
}
