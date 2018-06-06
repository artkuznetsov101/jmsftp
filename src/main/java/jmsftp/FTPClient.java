package jmsftp;

import java.nio.file.Paths;
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
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

public class FTPClient {
	FileSystemManager manager;
	FileObject remote;
	FileObject local;

	public FTPClient() {

		// move(local.resolveFile(filename), remote.resolveFile(filename));
		// move(remote.resolveFile(filename), local.resolveFile(filename));

		// copy(local.resolveFile(filename), remote.resolveFile(filename));
		// delete(local.resolveFile(filename));
	}

	public void upload(String filename) throws FileSystemException {
		init(Config.JMS.TEMP_DIR, Config.JMS.FTP_DIR);
		move(local.resolveFile(filename), remote.resolveFile(filename));
	}

	public void download(String filename) throws FileSystemException {
		init(Config.FTP.TEMP_DIR, Config.FTP.FTP_DIR);
		copy(remote.resolveFile(filename), local.resolveFile(filename));
	}

	public String get() {

		try {
			if (remote != null) {
				remote.refresh();
				final FileObject[] children = remote.getChildren();
				for (final FileObject child : children) {
					String filename = child.getName().getBaseName();
					download(filename);
					return filename;

				}
			}
		} catch (FileSystemException e) {
			System.out.println("ftp2jms ->  ftp no connection");
		}
		return null;
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
			// System.out.println("");
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
			// System.out.println("");
		}
	}

	public void delete(String path, String file) throws FileSystemException {
		delete(manager.resolveFile(Paths.get(path, file).toUri()));
	}

	public void delete(String file) throws FileSystemException {
		delete(remote.resolveFile(file));
	}

	public void delete(FileObject from) throws FileSystemException {
		if (from.exists() && from.isFile()) {
			from.delete(new FileTypeSelector(FileType.FILE));

			from.close();
		} else
			throw new FileSystemException("wrong parameters");

		// TODO
		if (!from.exists() || !from.isFile()) {
			// System.out.println("");
		}
	}

	public void connect(String temp, String ftp) throws FileSystemException {
		manager = VFS.getManager();

		// setup our FTP configuration
		FileSystemOptions opts = new FileSystemOptions();
		FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
		FtpFileSystemConfigBuilder.getInstance().setConnectTimeout(opts, Config.COMMON.TIMEOUT);

		// FtpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts,
		// "no");
		// FtpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

		init(temp, ftp);
	}

	public void init(String temp, String ftp) throws FileSystemException {
		try {
			if (local == null) {
				local = manager.resolveFile(temp);
				if (!local.isFolder())
					throw new FileSystemException("local path is not a directory");
			}

			if (remote == null) {
				// getRemoteTest(ftp)
				remote = manager.resolveFile(
						getRemoteFTP(Config.FTP.HOST, Config.FTP.PORT, Config.FTP.USERNAME, Config.FTP.PASSWORD, ftp));
				if (!remote.isFolder())
					throw new FileSystemException("remote path is not a directory");
			}
		} catch (Exception ignored) {
			System.out.println("ftp2jms ->  ftp no connection");
		}
	}

	private String getRemoteFTP(String host, String port, String username, String password, String dir) {
		return String.format("ftp://%s:%s@%s:%s/%s", username, password, host, port, dir);
	}

	private String getRemoteTest(String dir) {
		return String.format("C:\\!ftp\\%s", dir);
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
