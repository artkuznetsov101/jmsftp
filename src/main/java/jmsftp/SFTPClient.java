package jmsftp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SFTPClient {
	private FileSystemManager manager;
	private FileObject remote;
	private FileObject local;

	private String filename = "test.txt";

	public void test() throws FileSystemException {

		connect();
		info();

		// move(local.resolveFile(filename), remote.resolveFile(filename));
		// move(remote.resolveFile(filename), local.resolveFile(filename));
		
		// copy(local.resolveFile(filename), remote.resolveFile(filename));
		// delete(local.resolveFile(filename));
	}

	public void move(FileObject from, FileObject to) throws FileSystemException {
		if (from.exists() && from.isFile()) {
			from.moveTo(to);
		
			from.close();
			to.close();
		} else
			throw new FileSystemException("wrong parameters");

		// TODO
		if (to.exists() && to.isFile()) {
			System.out.println("");
		}
	}

	public void copy(FileObject from, FileObject to) throws FileSystemException {
		if (from.exists() && from.isFile()) {
			to.copyFrom(from, new FileTypeSelector(FileType.FILE));
		
			from.close();
			to.close();
		} else
			throw new FileSystemException("wrong parameters");

		// TODO
		if (to.exists() && to.isFile()) {
			System.out.println("");
		}
	}
		
	public void delete(FileObject from) throws FileSystemException {
		if (from.exists() && from.isFile()) {
			from.delete(new FileTypeSelector(FileType.FILE));
		
			from.close();
		} else
			throw new FileSystemException("wrong parameters");

		// TODO
		if (!from.exists() || !from.isFile()) {
			System.out.println("");
		}
	}
	
	public void connect() throws FileSystemException {
		manager = VFS.getManager();
		// remote = manager.resolveFile(
		// getSFTPConnection(SFTPSettings.HOST, SFTPSettings.PORT,
		// SFTPSettings.USERNAME,
		// SFTPSettings.PASSWORD));

		// Setup our SFTP configuration
		FileSystemOptions opts = new FileSystemOptions();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
		SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

		local = manager.resolveFile(getLocalPath());
		if (!local.isFolder())
			throw new FileSystemException("local path is not a directory");

		remote = manager.resolveFile(getRemotePath());
		if (!remote.isFolder())
			throw new FileSystemException("remote path is not a directory");
	}

	private String getRemoteSFTP(String host, String port, String username, String password) {
		return String.format("sftp://%s:%s@%s:%s", username, password, host, port);
	}

	private String getRemotePath() {
		return "file:///C:/!!";
	}

	private String getLocalPath() {
		return "file:///C:/!!!";
	}

	public void info() throws FileSystemException {
		System.out.println("Default manager: \"" + manager.getClass().getName() + "\" " + "version "
				+ getVersion(manager.getClass()));
		String[] schemes = manager.getSchemes();
		List<String> virtual = new ArrayList<>();
		List<String> physical = new ArrayList<>();
		for (int i = 0; i < schemes.length; i++) {
			Collection<Capability> caps = manager.getProviderCapabilities(schemes[i]);
			if (caps != null) {
				if (caps.contains(Capability.VIRTUAL) || caps.contains(Capability.COMPRESS)
						|| caps.contains(Capability.DISPATCHER)) {
					virtual.add(schemes[i]);
				} else {
					physical.add(schemes[i]);
				}
			}
		}
		if (!physical.isEmpty()) {
			System.out.println("  Provider Schemes: " + physical);
		}
		if (!virtual.isEmpty()) {
			System.out.println("   Virtual Schemes: " + virtual);
		}
	}

	private static String getVersion(Class<?> cls) {
		try {
			return cls.getPackage().getImplementationVersion();
		} catch (Exception ignored) {
			return "N/A";
		}
	}

}
