package poc.constants;

public class POCConstants {

	public static final String MODULE_KEY = "module";
	public static final String MEMBER_KEY = "method";
	public static final String ARGUMENT_KEY = "argument";
	public static final String MODE_MANAGER_MODULE_NAME = "mode";
	public static final String KEY_GUID = "guid";
	public static final String RPC_PATH = "rpc";
	public static final String SHUTDOWN_PATH = "shutdown";
	public static final String PING_PATH = "ping";

	public static final String MODULE_EDITOR = "editor";
	public static final String METHOD_CONFIGURATION = "configuration";
	public final static String METHOD_UPDATE = "update";
	public final static String METHOD_INIT = "init";
	public final static String METHOD_EXEC = "exec";
	public final static String METHOD_OUTLINE = "outline";

	public final static String SERVICE_STYLESHEET = "stylesheet";
	public final static String SERVICE_HIGHLIGHT = "highlight";

	public final static String MODE_HTML = "html";

	// folding
	public static final String FOLD_METHOD = "fold";
	public static final String FOLDS_KEY = "folds";
	public static final String START_KEY = "start";
	public static final String END_KEY = "end";
	public static final String STYLES_KEY = "styles";

	// Runtime
	public static final String INPUT_GUID = "80d007698d534c3d9355667f462af2b0";
	public static final String OUTPUT_GUID = "e531ebf04fad4e17b890c0ac72789956";

	public static final int POLLING_SLEEP_TIME = 50; // ms
	public static final int POLLING_TIME_OUT = 1000; // ms

	public static final String TOKENS_KEY = "tokens";

	/*
	 * Constants - to be configured FIXME Don't make it hard-coded and absolute!
	 * Use Eclipse preferences system Otherwise use PATH Otherwise use packaged
	 * node version, with a relative path
	 * 
	 * private static final String launcherPath =
	 * ".\\node_modules\\LiveScript\\bin\\lsc";
	 */
	public static final String launcherPath = "C:\\Documents and Settings\\ymeine\\Application Data\\npm\\node_modules\\LiveScript\\bin\\lsc";

	// FIXME Make it relative!
	// private static final String programPath = "./app/";
	public static final String PROGRAM_PATH = "D:/atgit/eclipsePlugin/ultimate-poc/resources/app/";
	public static final String[] COMMAND = { "node", "\"" + launcherPath + "\"", "index"
	// "\"" + programPath + "index" + "\""
	};

	// Construction
	public static final String DOMAIN = "http://localhost:3000/";

	public static final String COLOR_KEY = "color";
	public static final String RED_KEY = "r";
	public static final String GREEN_KEY = "g";
	public static final String BLUE_KEY = "b";

	public static final String TOKEN_TYPE_KEY = "type";
	public static final String WS_TOKEN_TYPE_NAME = "ws";

	public static final String KEY_STYLE = "default";

	public static final String KEY_TAB_WIDTH = "tabWidth";
	public static final Double DEFAULT_TAB_WIDTH = 4.0;
	public static final String PARTITION_NAME = "MAIN";

	public static final String KEY_ROOT = "ast";
	public static final String KEY_CHILDREN = "children";
}
