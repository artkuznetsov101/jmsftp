package jmsftp;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;

public class SFTPClient {
	private FileSystemManager manager;
	private FileObject remote;
	private FileObject local;

	private String filename = "test.txt";
	private String path = "C:\\!!!";

	public void test() throws FileSystemException {

		connect();
		info();

		copy(remote.resolveFile(filename));

		upload(manager.resolveFile("file:///C:/!!!/test.txt"));

		System.out.println("isExists " + remote.exists());
		System.out.println("isFolder " + remote.isFolder());

		FileObject file = remote.resolveFile("test.txt");
		System.out.println("isExists " + file.exists());
		System.out.println("isFolder " + file.isFolder());

		download(file, Paths.get("C:\\!!"));

		System.out.println(remote);
	}

	public void copy(FileObject from, FileObject to) throws FileSystemException {
		if (from.exists() && from.isFile())
			from.moveTo(to);
		else
			throw new FileSystemException("wrong parameters");
	}

	public void connect() throws FileSystemException {
		manager = VFS.getManager();
		// remote = manager.resolveFile(
		// getSFTPConnection(SFTPSettings.HOST, SFTPSettings.PORT,
		// SFTPSettings.USERNAME,
		// SFTPSettings.PASSWORD));
		remote = manager.resolveFile(getFileConnection());
		if (!remote.isFolder())
			throw new FileSystemException("not a remote directory");

	}

	private String getRemotePath(String host, String port, String username, String password) {
		return String.format("sftp://%s:%s@%s:%s", username, password, host, port);
	}

	private String getLocalPath() {
		return "file:///C:/!!";
	}

	public void download(FileObject src, Path local) throws FileSystemException {
		// LocalFile localFileObject = (LocalFile)
		// manager.resolveFile(local.toUri().toString());
		// try {
		// localFileObject.copyFrom(file, new AllFileSelector());
		// } finally {
		// localFileObject.close();
		// file.close();
		// }

		FileObject dest = manager.resolveFile("C:\\!!");
		if (dest.exists() && dest.getType() == FileType.FOLDER) {
			dest = dest.resolveFile(src.getName().getBaseName());
		}

		dest.copyFrom(src, Selectors.SELECT_ALL);
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
