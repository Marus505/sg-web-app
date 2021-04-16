package com.dsw.core.util.builder;


/**
 * Class Name : SourceBuilder.java
 * Description : SourceBuilder class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013.11.06
 * @version 1.0
 *
 */
public interface SourceBuilder {

	public static final String CRLF = "\r\n";

	/**
	 * @param properties
	 */
	public void build(SourceBuilderProperties<String, Object> properties) throws Exception;

	/**
	 *
	 * @return String
	 */
	public String getSourceString();

	/**
	 * Class Name : SourceBuilder.Utility.java
	 * Description : SourceBuilder.Utility class
	 * Modification Information
	 *
	 * @author Y.S.Kim
	 * @since 2013.11.06
	 * @version 1.0
	 *
	 */
	public class Utility {

		/**
		 * 패키지명을 포함한 Class명을 받아 Class명으로만 리턴
		 * @param fullClassName
		 * @return String
		 */
		public static String toSimpleClassName(String fullClassName) {
			String rtnStr = null;

			if (fullClassName != null) {

				if (fullClassName.indexOf(".") > -1) {
					rtnStr = fullClassName.substring(fullClassName.lastIndexOf(".")+1);
				} else {
					rtnStr = fullClassName;
				}
			}
			return rtnStr;
		}

		/**
		 *
		 * @param simpleClassName
		 * @return String
		 */
		public static String toFieldName(String simpleClassName) {
			String rtnStr = null;

			if (simpleClassName != null) {
				rtnStr = simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
			}

			return rtnStr;
		}
	}
}
