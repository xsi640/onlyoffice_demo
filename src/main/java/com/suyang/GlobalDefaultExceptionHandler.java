package com.suyang;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.suyang.domain.ResponseMessage;
import com.suyang.exceptions.APIException;
import com.suyang.exceptions.APIExceptionType;
import com.suyang.exceptions.ErrorType;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

	private static HashMap<String, HashMap<String, String>> errorMessage = new HashMap<>();
	private static final String ERROR_FILE = "errormessage.yml";
	private static final String DEFAULT_LOCAL = "cn";

	static  {
		
		try {
			initialize();
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseMessage defaultErrorHandler(HttpServletRequest request, Exception e) {
		String key = APIExceptionType.UnKnow.name();
		return createErrorMessage(request, key, ErrorType.System.getValue());
	}
	
	@ExceptionHandler(value = SQLException.class)
	public ResponseMessage sqlErrorHandler(HttpServletRequest request, Exception e) {
		String key = APIExceptionType.UnKnow.name();
		return createErrorMessage(request, key, ErrorType.Database.getValue());
	}

	@ExceptionHandler(APIException.class)
	public ResponseMessage handleAPIException(HttpServletRequest request, APIException ex) {
		return createErrorMessage(request, ex.getType().name(), ErrorType.API.getValue());
	}

	private ResponseMessage createErrorMessage(HttpServletRequest request, String key, int code) {
		ResponseMessage result = new ResponseMessage();
		result.setCode(code);
		
		String msg = "";
		String local = request.getHeader("local");
		if (StringUtils.isEmpty(local)) {
			local = DEFAULT_LOCAL;
		}
		HashMap<String, String> map = errorMessage.get(key);
		if (map != null) {
			msg = map.get(local);
			if (StringUtils.isEmpty(msg)) {
				msg = map.get(DEFAULT_LOCAL);
			}
		}
		result.setMessage(msg);
		return result;
	}

	private synchronized static void initialize() throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		JsonNode root = mapper.readTree(ClassLoader.getSystemResourceAsStream(ERROR_FILE));
		Iterator<Entry<String, JsonNode>> elements = root.fields();
		while (elements.hasNext()) {
			Entry<String, JsonNode> entry = elements.next();
			String key = entry.getKey();
			HashMap<String, String> map = new HashMap<>();
			JsonNode children = entry.getValue();
			Iterator<Entry<String, JsonNode>> els = children.fields();
			while (els.hasNext()) {
				Entry<String, JsonNode> v = els.next();
				map.put(v.getKey(), v.getValue().asText());
			}
			errorMessage.put(key, map);
		}
	}

}
