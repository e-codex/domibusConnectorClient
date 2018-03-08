package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class DomibusStandaloneConnectorFileSystemUtil {
	
	static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusStandaloneConnectorFileSystemUtil.class);
	
	private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public static File renameMessageFolder(File messageFolder, String folderPath, String newFolderPathExtension ) throws DomibusStandaloneConnectorFileSystemException {
		File newMessageFolder = new File(folderPath + newFolderPathExtension);

		LOGGER.debug("Try to rename message folder {} to {}", messageFolder.getAbsolutePath(),
				newMessageFolder.getAbsolutePath());
		try {
			FileUtils.moveDirectory(messageFolder, newMessageFolder);
		} catch (IOException e1) {
			throw new DomibusStandaloneConnectorFileSystemException("Could not rename folder "
					+ messageFolder.getAbsolutePath() + " to " + newMessageFolder.getAbsolutePath());
		}
		
		return newMessageFolder;
	}

	public static String convertDateToProperty(Date date){
		return sdf2.format(date);
	}
}
