package poc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import poc.bean.RequestBean;
import poc.constants.POCConstants;
import poc.exception.CustomException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Backend {

	private static Backend singleton = null;

	private Boolean isManagedExternally;
	private Process process;
	private DefaultHttpClient httpclient;
	private HttpPost rpc;
	private HttpGet shutdown;
	private HttpGet ping;
	private HttpGet guid;
	private Gson gson;

	/**
	 * Returns a singleton object, for convenience. Indeed, nothing prevents you
	 * from creating and managing your own instances.
	 * 
	 * @return A singleton.
	 */
	public static Backend get() {
		if (Backend.singleton == null) {
			Backend.singleton = new Backend();
		}
		return Backend.singleton;
	}

	/**
	 * Builds a new backend instance.
	 */
	public Backend() {
		this.httpclient = new DefaultHttpClient();

		this.rpc = new HttpPost(POCConstants.DOMAIN + POCConstants.RPC_PATH);
		this.rpc.setHeader("Content-Type", "application/json");

		this.shutdown = new HttpGet(POCConstants.DOMAIN + POCConstants.SHUTDOWN_PATH);
		this.ping = new HttpGet(POCConstants.DOMAIN + POCConstants.PING_PATH);
		this.guid = new HttpGet(POCConstants.DOMAIN + POCConstants.INPUT_GUID);

		this.gson = new Gson();
	}

	/**
	 * Tells whether the backend is running or not.
	 * 
	 * @return true if the backend is running, false otherwise.
	 * @throws IOException
	 */
	public Boolean isRunning() throws IOException {
		// We don't know if it is an external process or not yet
		if (this.isManagedExternally == null) {
			try {
				if (this.get(guid).equals(POCConstants.OUTPUT_GUID)) {
					this.isManagedExternally = true;
					return true;
				}
			} catch (IOException exception) {
				exception.printStackTrace();
				this.isManagedExternally = false;
			}
		}
		// Externally managed
		if (this.isManagedExternally) {
			// TODO Maybe we could use the ping, but this method is safer to
			// ensure the server has not been replaced.
			return this.get(guid) == POCConstants.OUTPUT_GUID;
		}

		// We manage the process ourself
		if (process == null)
			return false;
		try {
			process.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * If not running, starts the backend.
	 * 
	 * @return the created Process instance behind
	 */
	public Process start() throws IOException, InterruptedException {
		if (!isRunning()) {
			// Launches the process
			ProcessBuilder processBuilder = new ProcessBuilder(POCConstants.COMMAND);
			processBuilder.directory(new File(POCConstants.PROGRAM_PATH));
			this.process = processBuilder.start();

			// Polling to check the backend is fully set up
			boolean started = false;
			int time = 0;
			while (!started && (time < POCConstants.POLLING_TIME_OUT)) {
				try {
					this.get(this.ping);
					started = true;
				} catch (IOException ex) {
					ex.printStackTrace();
					Thread.sleep(POCConstants.POLLING_SLEEP_TIME);
					time += POCConstants.POLLING_SLEEP_TIME;
				}
			}
		}

		return this.process;
	}

	/**
	 * If we manage the backend process ourself and it is running, stops it by
	 * sending a specific request, and ensures the process is stopped with
	 * process utilities.
	 * 
	 * @return If the backend properly stopped under the request, returns its
	 *         response (see <code>get</code>), otherwise returns
	 *         <code>null</code>.
	 * 
	 * @see isRunning
	 * @see get
	 * 
	 * @throws IOException
	 */
	public String stop() throws IOException {
		String response = null;

		if (!this.isManagedExternally && this.isRunning()) {
			response = this.get(this.shutdown);
			this.process.destroy();
			this.process = null;
		}

		return response;
	}

	// Communication

	/**
	 * To be used in the future, for every RPC related to a mode. This enforces
	 * the use of a session key.
	 * 
	 * @throws CustomException
	 */
	public Map<String, Object> call(String guid, String member, Map<String, Object> argument) throws ParseException,
			IOException, JsonSyntaxException, CustomException {
		RequestBean requestBean = new RequestBean();
		requestBean.setModule(POCConstants.MODE_MANAGER_MODULE_NAME);
		requestBean.setMethod(member);
		requestBean.getArgument().setGuid(guid);

		return this.rpc(requestBean);
	}

	/**
	 * Builds a JSON RPC request (compatible with the implementation of the
	 * backend) and executes it.
	 * 
	 * @param module
	 *            The name of the remote module
	 * @param member
	 *            The name of the member to access inside the remote module
	 * @param argument
	 *            If the member is expected to be a function, it will be called
	 *            with the given argument
	 * 
	 *            FIXME The backend should allow to pass a list of arguments,
	 *            instead of forcing to use an object. TODO When the backend
	 *            will implement fields aliasing, change the names of the field:
	 *            mod, member, arg
	 * 
	 * @return The JSON result of the RPC.
	 * 
	 * @see postJson
	 * 
	 * @throws IOException
	 * @throws CustomException
	 */
	public Map<String, Object> rpc(RequestBean requestBean) throws ParseException, IOException, JsonSyntaxException,
			CustomException {

		/*
		 * Map<String, Object> object = new HashMap<String, Object>();
		 * object.put(POCConstants.MODULE_KEY, module);
		 * object.put(POCConstants.MEMBER_KEY, member);
		 * object.put(POCConstants.ARGUMENT_KEY, argument);
		 */

		String json = gson.toJson(requestBean);

		StringEntity content = new StringEntity(json);
		this.rpc.setEntity(content);

		return this.postJson(this.rpc);
	}

	/*
	 * public Map<String, Object> rpc(String module, String member) throws
	 * IOException, JsonSyntaxException, ParseException, CustomException {
	 * return this.rpc(module, member, null); }
	 */

	/**
	 * Sends the given HTTP Get request and returns the response content as a
	 * String.
	 * 
	 * @param request
	 *            A HTTP Get request.
	 * 
	 * @return the response content as a string
	 * 
	 * @throws IOException
	 */
	private String get(HttpGet request) throws IOException {
		HttpEntity entity = httpclient.execute(request).getEntity();
		String response = EntityUtils.toString(entity);
		return response;
	}

	// TODO Maybe return "primitive" types too? (not necessarily a key/value
	// collection)
	/**
	 * Sends the given HTTP Postrequest and returns the response content as a
	 * JSON object.
	 * 
	 * @param request
	 *            A HTTP Post request.
	 * 
	 * @return the response content as a JSON object, that is a Map of
	 *         Strings/Objects in the Java system.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws JsonSyntaxException
	 * @throws CustomException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> postJson(HttpPost request) throws ParseException, IOException, JsonSyntaxException,
			CustomException {
		HttpResponse response = httpclient.execute(request);
		// response.getStatusLine()
		HttpEntity entity = response.getEntity();
		String content = EntityUtils.toString(entity);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new CustomException(response.getStatusLine().getReasonPhrase());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result = gson.fromJson(content, result.getClass());

		return result;

	}
}
