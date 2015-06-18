package android.sipgate.lunch.ormlite;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Helper class to generate the ormlite config file for database structure infos.
 * <p />
 * <b>Important:</b><br />
 * Customize the static variable CONFIG_FILE (atm. only an absolute path seem to work correctly
 * with IntelliJ / AndroidStudio!)
 *
 * @author schafm
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static final File CONFIG_FILE =
            new File("~/AndroidStudioProjects/SipgateLunch/app/src/main/res/raw/ormlite_config.txt");
	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile(CONFIG_FILE);
	}
}
