package com.dsw.core.util;

import java.text.DecimalFormat;


/**
 * Class Name : UtilFileSize.java
 * Description : UtilFileSize class
 * Modification Information
 *
 * @author H.S.JANG
 * @since 2014. 02. 11.
 * @version 1.0
 *
 */
public class UtilFileSize {


	/**
	 * 파일 사이즈 입력시 B,KB,MB,GB,TB 에 따른 분류
	 * @param size
	 * @return
	 */
	public static String getReadableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

}
