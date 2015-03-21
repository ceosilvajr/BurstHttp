package ph.coreproc.android.bursthttp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * @author chrisbjr
 */
public class BurstError {

	private String code;
	private int httpCode;
	private String message;

	private String TAG = "HttpClient";

	public static final int INTERNET_UNAVAILABLE = 0;
	public static final int URL_INVALID = 1;

	public BurstError() {
		// we can initialize this with an empty constructor
	}

	public BurstError(Response<JsonObject> response) {

		if (response.getResult() == null) {
			httpCode = response.getHeaders().code();
			message = BurstResponse.getHttpResponseMessage(httpCode);
			return;
		}

		JsonObject result = response.getResult();

		if (!result.has("error")) {
			// This is an error we cannot read
			httpCode = response.getHeaders().code();
			message = BurstResponse.getHttpResponseMessage(httpCode);
			return;
		}

		JsonObject error = result.get("error").getAsJsonObject();

		// we set the values
		code = error.get("code").getAsString();
		httpCode = error.get("http_code").getAsInt();

		// parse message
		if (error.has("message")) {
			if (error.get("message").isJsonObject()) {
				message = "";
				JsonObject messageObject = error.get("message")
						.getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entrySet = messageObject
						.entrySet();
				for (Map.Entry<String, JsonElement> entry : entrySet) {
					message += messageObject.get(entry.getKey()).getAsString()
							+ "\n";
				}
			} else {
				// we assume that message is a string?
				message = error.get("message").getAsString();
			}
		} else {
			message = BurstResponse.getHttpResponseMessage(httpCode);
		}
	}

	public static BurstError noInternetConnection() {
		BurstError burstError = new BurstError();
		burstError.httpCode = BurstError.INTERNET_UNAVAILABLE;
		burstError.message = "No Internet Connection";
		burstError.code = "NO-INTERNET-CONNECTION";
		return burstError;
	}

	public static BurstError invalidUrl() {
		BurstError burstError = new BurstError();
		burstError.httpCode = BurstError.URL_INVALID;
		burstError.message = "Invalid URL";
		burstError.code = "INVALID-URL";
		return burstError;
	}

    public static BurstError failedToDownloadFile() {
        BurstError burstError = new BurstError();
        burstError.httpCode = BurstError.URL_INVALID;
        burstError.message = "Failed to download file";
        burstError.code = "FILE-ERROR";
        return burstError;
    }

	public String getCode() {
		return code;
	}

	public int getHttpCode() {
		return httpCode;
	}

	public String getMessage() {
		return message;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setHttpCode(int httpCode) {
		this.httpCode = httpCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
