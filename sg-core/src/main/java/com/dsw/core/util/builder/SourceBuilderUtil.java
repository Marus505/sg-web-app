package com.dsw.core.util.builder;

import java.util.ArrayList;
import java.util.List;


/**
 * Class Name : SourceBuilderUtil.java
 * Description : SourceBuilderUtil class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class SourceBuilderUtil {
	private final static String VARIABLE = "${";
	private final static String VARIABLE_SUFFIX = "}";
	private final static String ITERATE = "${iterate";
	private final static String ITERATE_SUFFIX = "]}";

	/**
	 * @param templateString
	 * @param variable
	 * @param replacement
	 * @return String
	 */
	public static String replaceVariables(final String templateString, String variable, String replacement){
		if(replacement == null)
			return templateString;
		return templateString.replace(VARIABLE+variable+VARIABLE_SUFFIX, replacement);
	}

	/**
	 * @param templateString
	 * @param variable
	 * @return String
	 */
	public static String removeVariables(final String templateString, String variable){
		return templateString.replace(VARIABLE+variable+VARIABLE_SUFFIX, "");
	}

	/**
	 * @param templateString
	 * @param variable
	 * @param values
	 * @return String
	 */
	public static String replaceIterates(final String templateString, String variable, String[] values){
		if(values.length < 1){
			return templateString;
		}

		StringBuilder resultBuffer = new StringBuilder();
		List<String> results = new ArrayList<String>();

		int spos = templateString.indexOf(ITERATE + ":" + variable);
		int epos = templateString.indexOf(ITERATE_SUFFIX, spos) + 2;

		if(spos < 0){
			return templateString;
		}

		final String iterateString = templateString.substring(spos, epos);
		for(int r = 0; r < values.length; r++){
			String _iterateString = iterateString.substring(iterateString.indexOf("[") + 1, iterateString.lastIndexOf("]"));
			if(values[r] == null)
				continue;
			_iterateString = _iterateString.replace("$args[]", values[r]);
			results.add(_iterateString);
		}

		resultBuffer.append(templateString.substring(0, spos));
		for(int i = 0; i < results.size(); i++){
			resultBuffer.append(results.get(i));
		}
		resultBuffer.append(templateString.substring(epos));

		//순환처리를 사용하여 동일한 iterator를 처리한다.
		return replaceIterates(resultBuffer.toString(), variable, values);
	}


	/**
	 * @param templateString
	 * @param variable
	 * @param values
	 * @return String
	 */
	public static String replaceIterates(final String templateString, String variable, String[][] values){
		if(values.length < 1){
			return templateString;
		}

		StringBuilder resultBuffer = new StringBuilder();
		List<String> results = new ArrayList<String>();

		int spos = templateString.indexOf(ITERATE + ":" + variable);
		int epos = templateString.indexOf(ITERATE_SUFFIX, spos) + 2;

		if(spos < 0){
			return templateString;
		}

		final String iterateString = templateString.substring(spos, epos);
		for(int r = 0; r < values.length; r++){
			String _iterateString = iterateString.substring(iterateString.indexOf("[") + 1, iterateString.lastIndexOf("]"));
			if(values[r] == null)
				continue;
			for(int v = 0; v < values[r].length; v++){
				if(values[r][v] == null){
					continue;
				}
				_iterateString = _iterateString.replace("$"+v, values[r][v]);
			}
			results.add(_iterateString);
		}

		resultBuffer.append(templateString.substring(0, spos));
		for(int i = 0; i < results.size(); i++){
			resultBuffer.append(results.get(i));
		}
		resultBuffer.append(templateString.substring(epos));

		//순환처리를 사용하여 동일한 iterator를 처리한다.
		return replaceIterates(resultBuffer.toString(), variable, values);
	}

	/**
	 * @param templateString
	 * @param variable
	 * @return String
	 */
	public static String removeIterates(final String templateString, String variable){
		StringBuilder resultBuffer = new StringBuilder();
		int spos = templateString.indexOf(ITERATE + ":" + variable);
		int epos = templateString.indexOf(ITERATE_SUFFIX, spos) + 2;
		if(spos > 0 && epos > 0){
			resultBuffer.append(templateString.substring(0, spos)).append(templateString.substring(epos));
			return removeIterates(resultBuffer.toString(), variable);
		}else{
			return templateString;
		}
	}

}
