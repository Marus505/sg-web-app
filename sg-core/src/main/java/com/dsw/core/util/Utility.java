package com.dsw.core.util;

import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Class Name : Utility.java
 * Description : Utility class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013.11.07
 * @version 1.0
 *
 */
public class Utility {

	/**
	 * 문자열로 입력받은 파일 크기를 계산하여 리턴. 10M, 2G 등.
	 * @param sizeString
	 * @return long
	 */
	public final static long parseFileSize(String sizeString){
		long size = 0;
		long multiple = 1;
		int pos = -1;
		String[] strs = new String[]{"K", "M", "G"};
		for(int i = 0; i < strs.length; i++){
			pos = sizeString.indexOf(strs[i]);
			if(pos > -1){
				multiple = (long)Math.pow(1024, i+1);
				size = Long.parseLong(sizeString.substring(0, pos));
				break;
			}
		}
		if(pos < 0)
			size = Long.parseLong(sizeString);
		return size * multiple;
	}

	/**
	 * ${ 로 시작되는 가변 인자를 판단함.
	 * @param str
	 * @return boolean
	 */
	public final static boolean isVariable(String str){
		if(str.indexOf("${") > -1){
			return true;
		}
		return false;
	}

	/**
	 * str에서 해당 variable을 arg로 치환.
	 * @param str
	 * @param variable
	 * @param arg
	 * @return String
	 */
	public final static String replaceVariable(String str, String variable, String arg){
		return str.replaceAll("\\$\\{"+variable+"\\}", arg);
	}

	/**
	 * 아스키 코드 값이 숫자인지 판단.
	 * @param s
	 * @return boolean
	 */
	public final static boolean isNumeric(String s){
		char[] ss = s.toCharArray();
		for(int i = 0; i < ss.length; i++) {
			if(!(ss[i]==45 || (ss[i] >= 48 && ss[i] <= 57)))
				return false;
		}
		return true;
	}

	/**
	 * buffer 안에 arg가 들어있는지 판단.
	 * @param buffer
	 * @param arg
	 * @return boolean
	 */
	public final static boolean isInArray(String[] buffer, String arg){
		for(int i = 0; i < buffer.length; i++){
			if(buffer[i] == null){
				continue;
			}
			if(buffer[i].equals(arg)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Says IP address is valid
	 * @param arg
	 * @return boolean
	 */
	public final static boolean isIPAddress(String arg){
		arg = arg.trim();
		int pos1 = arg.indexOf(".");
		if(pos1 < 0){
			return false;
		}
		int pos2 = arg.indexOf(".", pos1+1);
		if(pos2 < 0){
			return false;
		}
		int pos3 = arg.indexOf(".", pos2+1);
		if(pos3 < 0){
			return false;
		}

		try {
			int a1 = Integer.parseInt(arg.substring(0, pos1));
			if(a1 < 1 || a1 > 254)
				return false;
			int a2 = Integer.parseInt(arg.substring(pos1 + 1, pos2));
			if(a2 < 0 || a2 > 254)
				return false;
			int a3 = Integer.parseInt(arg.substring(pos2 + 1, pos3));
			if(a3 < 0 || a3 > 254)
				return false;
			int a4 = Integer.parseInt(arg.substring(pos3 + 1));
			if(a4 < 1 || a4 > 254)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

    /**
     * contentType of characterSet is returned.
     * @param contentType
     * @return String
     */
	public final static String getCharsetFromContentType(String contentType){
		return getCharsetFromContentType(contentType, null);
	}

    /**
     * contentType of characterSet with input characterSet is returned.
     * @param contentType
     * @param defaultCharset
     * @return String
     */
	public final static String getCharsetFromContentType(String contentType, String defaultCharset){
		if(contentType == null){
			return defaultCharset;
		}
        String str = "charset=";
        int i = contentType.indexOf(str);
        if(i == -1)
            return defaultCharset;
        i += str.length();
        int j = contentType.indexOf(';', i);
        if(j == -1)
            j = contentType.length();
        return contentType.substring(i, j).trim();
    }

	/**
	 * 문자열에서 key=value&key=value 로 이루어진 값을 분리하여 map 형식으로 리턴.
	 * @param queryString
	 * @return Map<String, String[]>
	 */
	public static Map<String, String[]> parseQueryString(String queryString) {
		String[] values = null;

		Map<String, String[]> parameters = new Hashtable<String, String[]>();

		if (queryString == null){
			return parameters;
		}
		if(queryString.indexOf("=") < 0){
			return parameters;
		}

		String key;
		for (StringTokenizer st = new StringTokenizer(queryString, "&"); st.hasMoreTokens();) {

			String parameter = st.nextToken();
			int equalPos = parameter.indexOf('=');

			if (equalPos > -1) {
				key = parseValue(parameter.substring(0, equalPos));
				String value = parseValue(parameter.substring(equalPos + 1, parameter.length()));
				if(value.length() < 1){
					value = null;
				}
				if (parameters.containsKey(key)) {
					String existsValues[] = (String[]) parameters.get(key);
					values = new String[existsValues.length + 1];
					for (int i = 0; i < existsValues.length; i++){
						values[i] = existsValues[i];
					}

					values[existsValues.length] = value;
				} else {
					values = new String[]{value};
				}
				parameters.put(key, values);
			}
		}
		return parameters;
	}


	/**
	 * 문자열에서 +, %를 제거
	 * @param parameterName
	 * @return String
	 */
	private static String parseValue(String parameterName) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < parameterName.length(); i++) {
			char c = parameterName.charAt(i);
			switch (c) {
			case 43: // '+'
				buffer.append(' ');
				break;

			case 37: // '%'
				try {
					buffer.append((char) Integer.parseInt(parameterName.substring(i + 1, i + 3), 16));
					i += 2;
					break;
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException();
				} catch (StringIndexOutOfBoundsException e) {
					String rest = parameterName.substring(i);
					buffer.append(rest);
					if (rest.length() == 2)
						i++;
				}
				break;

			default:
				buffer.append(c);
				break;
			}
		}

		return buffer.toString();
	}

}
