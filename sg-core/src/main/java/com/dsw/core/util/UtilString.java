package com.dsw.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class Name : UtilString.java
 * Description : UtilString class
 * Modification Information
 *
 * @author Y.S.Kim
 * @since 2013. 11. 14.
 * @version 1.0
 *
 */
public class UtilString {

	/**
	 * 날자값인지 확인
	 *
	 * @param str
	 * @param format
	 * @return
	 */
	public static boolean isDate(String str, String format) {
		if (isEmpty(str))
			return false;
		if (isEmpty(format))
			return false;
		try {
			DateFormat df = new SimpleDateFormat(format);
			df.parse(str);
		} catch (Exception ex) {
			return false;
		}

		return true;
	}

	/**
	 * 문자열을 리스트로 반환
	 * aaa|bbb|ccc|ddd , stringToArrayList(str, "|")
	 *
	 * @param str
	 * @param reg
	 * @return
	 */
	public static ArrayList<String> stringToArrayList(String str, String reg) {

		ArrayList<String> tmp = new ArrayList<String>();
		if (str == null)
			return tmp;
		String[] strArr = StringUtils.split(str, reg);
		if (str.length() > 0) {
			for (int i = 0; i < strArr.length; ++i) {

				tmp.add(strArr[i].toLowerCase());
			}
		}
		return tmp;
	}

	/**
	 * 문자열의 각 첫 문자를 대문자로
	 *
	 * @param name
	 * @return String
	 */
	public static String toCapitalize(String name) {
		char[] chars = name.toLowerCase().toCharArray();
		StringBuilder lastest = new StringBuilder();

		boolean isNextSpace = false;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (i == 0) {
				if (c >= 97 && c <= 122) {
					c = (char) (c - 32);
					lastest.append(c);
					continue;
				}
			} else {
				if (c == '_' || c == '-' || c == ' ') {
					isNextSpace = true;
					continue;
				}
				if (isNextSpace) {
					if (c >= 97 && c <= 122) {
						c = (char) (c - 32);
					}
					lastest.append(c);
				} else {
					lastest.append(c);
				}
				isNextSpace = false;
			}
		}
		return lastest.toString();
	}

	/**
	 * 문자열이 null 이거나 공백이면 true, 아닌경우 false를 리턴한다.
	 *
	 * @param str
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		if (str == null)
			return true;
		if (str.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 두 문자열의 값을 비교하여 동일할 경우 true 다를 경우 false를 리턴한다.
	 *
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean isEqual(String a, String b) {
		if (a == null) {
			if (b == null)
				return true;
			return false;
		} else {
			return a.equals(b);
		}
	}

	/**
	 * 두 문자열의 값을 비교하여 동일할 경우 true 다를 경우 false를 리턴한다. (공백도 Null로 판단하여)
	 *
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static boolean isSame(String a, String b) {
		if (a == null) {
			if (b == null)
				return true;
			else if (b.length() == 0)
				return true;
			else
				return false;
		} else if (b == null) {
			if (a.length() == 0)
				return true;
			else
				return false;
		} else {
			return a.equals(b);
		}
	}

	/**
	 * @param value
	 * @param cuttingLength
	 * @return String
	 */
	public static String cutting(String value, int cuttingLength) {
		String suffix = "..";
		byte[] bytes = value.getBytes();
		if (bytes.length > cuttingLength) {
			String r = new String(bytes, 0, cuttingLength);
			if (r.substring(r.length() - 1).getBytes()[0] == 63)
				r = r.substring(0, r.length() - 1);
			return r + suffix;
		} else {
			return value;
		}
	}

	/**
	 * @param value
	 * @param cuttingLength
	 * @param suffix
	 * @return String
	 */
	public static String cutting(String value, int cuttingLength, String suffix) {
		byte[] bytes = value.getBytes();
		if (bytes.length > cuttingLength) {
			String r = new String(bytes, 0, cuttingLength);
			if (r.substring(r.length() - 1).getBytes()[0] == 63)
				r = r.substring(0, r.length() - 1);
			return r + suffix;
		} else {
			return value;
		}
	}

	/**
	 * @param value
	 * @param cuttingLength
	 * @return String
	 */
	public static String stringCut(String value, int cuttingLength) {
		String suffix = "..";

		if(value.length() < cuttingLength)
			return value;

		return value.substring(0,cuttingLength)+ suffix;

	}

	/**
	 * StringTokenizer를 이용한 tokening 을 구현. (String의 split 보다 성능이 우수함)
	 *
	 * @param value
	 * @param delim
	 * @return String[]
	 */
	public static String[] toTokens(String value, String delim) {
		StringTokenizer st = new StringTokenizer(value, delim);
		String[] tokens = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			tokens[i++] = st.nextToken();
		}
		return tokens;
	}

	/**
	 * split 의 반대 기능을 함.
	 *
	 * @param array
	 * @param delim
	 * @return String
	 */
	public static String join(String[] array, String delim) {
		String rtn = "";
		if (array.length < 1) return rtn;

		for (String value: array) {
			rtn += value + delim;
		}
		rtn = rtn.substring(0, rtn.length()-delim.length());
		return rtn;
	}

	/**
	 * 주어진 String str의 뒤에 제공된 문자 c를 채워 총길이 totalLength로 만들어 리턴한다.
	 *
	 * @param str
	 * @param c
	 * @param totalLength
	 * @return String
	 */
	public static String fillFixedLengthStringBack(String str, char c, int totalLength) {
		for (int i = str.length(); i < totalLength; i++) {
			str += c;
		}
		return str;
	}

	/**
	 * 주어진 String str의 앞에 제공된 문자 c를 채워 총길이 totalLength로 만들어 리턴한다.
	 *
	 * @param str
	 * @param c
	 * @param totalLength
	 * @return String
	 */
	public static String fillFixedLengthStringFront(String str, char c, int totalLength) {
		for (int i = str.length(); i < totalLength; i++) {
			str = c + str;
		}
		return str;
	}

	/**
	 * HOST계열의 2byte 문자를 1byte로 치환
	 *
	 * @param str
	 * @return String
	 */
	public static String toSingleByteString(String str) {
		StringBuffer sb = new StringBuffer();
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] >= 0x10 && chars[i] <= 0xFF) { // ASCII
				sb.append(chars[i]);
			} else if (chars[i] >= 0xAC00 && chars[i] <= 0xD7A3) { // 한글
				sb.append(chars[i]);
			} else if (chars[i] == 12644) {
				sb.append(' ');
			} else {
				sb.append((char) (chars[i] + 288));
			}
		}
		return sb.toString();
	}

	/**
	 * ASCII 와 한글을 제외한 나머지 문자는 제거
	 *
	 * @param str
	 * @return String
	 */
	public static String trimStringScum(String str) {
		if (str == null)
			return null;
		StringBuffer sb = new StringBuffer();
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] >= 0x10 && chars[i] <= 0xFF) { // ASCII
				sb.append(chars[i]);
			} else if (chars[i] >= 0xAC00 && chars[i] <= 0xD7A3) { // 한글
				sb.append(chars[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * 입력된 사업자/주민번호를 123-45-95678/871122-4578111 형식으로 리턴한다.
	 *
	 * @param dlPlcNo
	 * @return String
	 */
	public static String toOutputDlPlcNo(String dlPlcNo) {
		if (dlPlcNo == null)
			return null;
		int length = dlPlcNo.length();
		switch (length) {
		case 13:
			return dlPlcNo.substring(0, 6) + "-" + dlPlcNo.substring(6, 13);
		case 10:
			return dlPlcNo.substring(0, 3) + "-" + dlPlcNo.substring(3, 5)
					+ "-" + dlPlcNo.substring(5, 10);
		default:
			return dlPlcNo;
		}
	}

	/**
	 * 금액을 한글로 표현하기(1)
	 *
	 * @param amt
	 * @return String
	 */
	public static String convertAmountHangul(String amt) {
		String tmpamt = "";
		amt = "000000000000" + amt;
		int j = 0;
		for (int i = amt.length(); i > 0; i--) {
			j++;
			if (!amt.substring(i - 1, i).equals("0")) {
				if (j % 4 == 2)
					tmpamt = "십" + tmpamt;
				if (j % 4 == 3)
					tmpamt = "백" + tmpamt;
				if (j > 1 && (j % 4) == 0)
					tmpamt = "천" + tmpamt;
			}
			if (j == 5
					&& Integer.parseInt(amt.substring(amt.length() - 8,
							amt.length() - 4)) > 0)
				tmpamt = "만" + tmpamt;
			if (j == 9
					&& Integer.parseInt(amt.substring(amt.length() - 12,
							amt.length() - 8)) > 0)
				tmpamt = "억" + tmpamt;
			if (j == 13
					&& Integer.parseInt(amt.substring(amt.length() - 16,
							amt.length() - 12)) > 0)
				tmpamt = "조" + tmpamt;
			if (amt.substring(i - 1, i).equals("1"))
				tmpamt = "일" + tmpamt;
			if (amt.substring(i - 1, i).equals("2"))
				tmpamt = "이" + tmpamt;
			if (amt.substring(i - 1, i).equals("3"))
				tmpamt = "삼" + tmpamt;
			if (amt.substring(i - 1, i).equals("4"))
				tmpamt = "사" + tmpamt;
			if (amt.substring(i - 1, i).equals("5"))
				tmpamt = "오" + tmpamt;
			if (amt.substring(i - 1, i).equals("6"))
				tmpamt = "육" + tmpamt;
			if (amt.substring(i - 1, i).equals("7"))
				tmpamt = "칠" + tmpamt;
			if (amt.substring(i - 1, i).equals("8"))
				tmpamt = "팔" + tmpamt;
			if (amt.substring(i - 1, i).equals("9"))
				tmpamt = "구" + tmpamt;
		}

		tmpamt = "금  " + tmpamt + "  원정";
		return tmpamt;
	}

	/**
	 * 뒤 공백 제거
	 *
	 * @param str
	 * @return String
	 */
	public static String getTrim(String str) {
		try {
			return str == null ? str : str.trim();
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 널문자 체크
	 *
	 * @param str
	 * @return String
	 */
	public static String getNvl(String str) {
		return str == null ? "" : str;
	}

	/**
	 * 널문자 체크
	 *
	 * @param str
	 * @param rep
	 * @return String
	 */
	public static String getNvl(String str, String rep) {
		return str == null || str.length() < 1 ? rep : str;
	}

	/**
	 * 널문자 체크
	 *
	 * @param str
	 * @param rep
	 * @return int
	 */
	public static int getNvl(String str, int rep) {
		try {
			return str == null ? rep : Integer.parseInt(str);
		} catch (Exception e) {
			return rep;
		}
	}

	/**
	 * 널문자 체크
	 *
	 * @param str
	 * @param rep
	 * @return long
	 */
	public static long getNvl(String str, long rep) {
		try {
			return str == null ? rep : Long.parseLong(str);
		} catch (Exception e) {
			return rep;
		}
	}

	/**
	 * 널문자 체크
	 *
	 * @param str
	 * @param rep
	 * @return int
	 */
	public static int getNvl(int str, int rep) {
		try {
			return str < 1 ? rep : str;
		} catch (Exception e) {
			return rep;
		}
	}

	/**
	 * 널문자 체크
	 *
	 * @param str
	 * @param rep
	 * @return long
	 */
	public static long getNvl(long str, long rep) {
		try {
			return str < 1 ? rep : str;
		} catch (Exception e) {
			return rep;
		}
	}

	/**
	 * 바이트 단위로 문자열 얻기
	 *
	 * @param str
	 * @param s
	 * @param e
	 * @return String
	 */
	public static String getBytes(String str, int s, int e) {
		byte[] strArr = str.getBytes();
		byte[] retArr = new byte[strArr.length];

		for (int i = s; i < s + e; i++)
			if (strArr.length > i)
				retArr[i - s] = strArr[i];

		return new String(retArr).trim();
	}

	/**
	 * 바이트 단위로 문자열 자르기 - 2byte
	 *
	 * @param str
	 * @param length
	 * @return String
	 */
	public static String getBytesCut(String str, int length) {
		String ret = str, conv, piece1, piece2, piece;
		String tail = "...";
		char tmp;

		try {
			if (str == null || str.getBytes().length <= length)
				return str;

			ret = getBytes(str, 0, length); //parameter str 을 length 만큼 자른 것
			piece1 = getBytes(str, 0, length - 1); //str 을 length-1 만큼 자른 것
			piece2 = getBytes(str, length - 1, 1); //str 을 length-1 만큼 자른 것의 나머지
			piece = getBytes(str, length - 1, 2); //str 을 length-1 만큼 자른 것의 나머지 2바이트
			tmp = ret.charAt(ret.length() - 1); //ret의 마지막 자리의 char 값

			conv = String.valueOf(tmp);

			if (conv.getBytes().length == 2 && conv.getBytes()[1] > 128) {
				return ret + tail;
			}
			if (piece1.getBytes().length + piece2.getBytes().length <= length) {
				if (ret.getBytes()[ret.getBytes().length - 2] < 129
						&& ret.getBytes()[ret.getBytes().length - 1] > 128)
					return ret + tail;
				else if (ret.getBytes()[ret.getBytes().length - 1] < 128) {
					if (piece.length() == piece.getBytes().length)
						return ret + tail;
					else {
						if (ret.substring(0, ret.length() - 1).getBytes().length + 2 > length)
							ret = ret.substring(0, ret.length() - 1);
					}
				}

				return ret;
			}

			if (piece.getBytes().length == 2) {
				ret = ret.substring(0, ret.length() - 1);
			} else {
				ret = ret.substring(0, ret.length() - 1);
			}
		} catch (Exception e) {
		} finally {
		}

		return ret + tail;
	}


	/**
	 * 바이트 단위로 문자열 자르기(한글깨짐 없이)
	 *
	 * @param szText
	 * @param szKey
	 * @param nLength
	 * @param nPrev
	 * @param isNotag
	 * @param isAdddot
	 * @return String
	 * strCut(대상문자열, 시작위치로할키워드, 자를길이, 키워드위치에서얼마나이전길이만큼포함할것인가, 태그를없앨것인가, 긴문자일경우"..."을추가할것인가)
	 */
	public static String strCut(String szText, String szKey, int nLength, int nPrev, boolean isNotag, boolean isAdddot){  // 문자열 자르기

	    String r_val = szText;
	    int oF = 0, oL = 0, rF = 0, rL = 0;
	    int nLengthPrev = 0;
	    Pattern p = Pattern.compile("<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE);  // 태그제거 패턴

	    if(isNotag) {r_val = p.matcher(r_val).replaceAll("");}  // 태그 제거
	    r_val = r_val.replaceAll("&amp;", "&");
	    r_val = r_val.replaceAll("(!/|\r|\n|&nbsp;)", "");  // 공백제거

	    try {
	      byte[] bytes = r_val.getBytes("UTF-8");     // 바이트로 보관
	      if(szKey != null && !szKey.equals("")) {
	        nLengthPrev = (r_val.indexOf(szKey) == -1)? 0: r_val.indexOf(szKey);  // 일단 위치찾고
	        nLengthPrev = r_val.substring(0, nLengthPrev).getBytes("MS949").length;  // 위치까지길이를 byte로 다시 구한다
	        nLengthPrev = (nLengthPrev-nPrev >= 0)? nLengthPrev-nPrev:0;    // 좀 앞부분부터 가져오도록한다.
	      }

	      // x부터 y길이만큼 잘라낸다. 한글안깨지게.
	      int j = 0;

	      if(nLengthPrev > 0) while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          oF+=2; rF+=3; if(oF+2 > nLengthPrev) {break;} j+=3;
	        } else {if(oF+1 > nLengthPrev) {break;} ++oF; ++rF; ++j;}
	      }

	      j = rF;

	      while(j < bytes.length) {
	        if((bytes[j] & 0x80) != 0) {
	          if(oL+2 > nLength) {break;} oL+=2; rL+=3; j+=3;
	        } else {if(oL+1 > nLength) {break;} ++oL; ++rL; ++j;}
	      }

	      r_val = new String(bytes, rF, rL, "UTF-8");  // charset 옵션

	      if(isAdddot && rF+rL+3 <= bytes.length) {r_val+="...";}  // ...을 붙일지말지 옵션
	    } catch(UnsupportedEncodingException e){ e.printStackTrace(); }

	    return r_val;
	}


	/**
	 * 통화(1,000,000) 형식으로 변환
	 * convert currency from string
	 *
	 * @param curr
	 * @return String
	 */
	public static String getCurrency(double d) {
		return NumberFormat.getInstance().format(d);
	}

	/**
	 * 문자열의 구분자를 치환한다
	 * @param str 대상 문자열
	 * @param oldDelimiter 기존 구분자
	 * @param newDelimiter 새로운 구분자
	 * @return
	 */
	public static String getReplaceDelimiter(String str, String oldDelimiter, String newDelimiter) {
		if (str == null || "".equals(str))
			return "";
		if (oldDelimiter == null || "".equals(oldDelimiter))
			oldDelimiter = ",";		// 기본값
		if (newDelimiter == null || "".equals(newDelimiter))
			newDelimiter = ", ";	// 기본값

		try {
			String[] strArray;
			String result = "";
			strArray = str.split(oldDelimiter);
			for(int i=0; i<strArray.length; i++) {
				result += strArray[i];
				if((i+1) < strArray.length) result += newDelimiter;
			}
			return result;
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 문자의 끝에서 부터 입력한 수만큼을 특수 문자로 변경
	 * @param str 일부를 특수문자로 변경할 내용
	 * @param length 특수문자가 필요 한만큼의 수
	 * @param asterisk 입력하지 않으면 * 로 표시
	 * @returns {String}  ex : stri**(String)
	 */
	public static String getReplaceAsterisk(String str,int length ,String asterisk) {
		String replaceStr = "";

		if(length < 1) {
			return "";
		}

		if(asterisk == null || "".equals(asterisk)) {
			asterisk = "*";
		}
		for(int i=0;i<length;i++){
			replaceStr += asterisk;
		}
		return str.substring(0,(str.length())-length)+replaceStr;
	}

	/**
	 * string cut
	 *
	 * @param str string
	 * @param beg start location
	 * @param end end location
	 * @return String
	 */
	public static String getStringFrom(String str, int beg, int end) {
		try {
			return str.substring(beg, end);
		} catch (Exception e) {
			return str.length() > beg ? str.substring(beg, str.length())
					: "";
		}
	}

	/**
     * string cut from spec
     * @param pData string
     * @param pStart start with string
     * @param pEnd end with string
     * @return String
     */
    public static String cutStringFrom(String pData, String pStart, String pEnd) {
        int vPos = 0;
        String vData = pData;

        try {
            if ((vPos = vData.indexOf (pStart)) == -1) return vData;

            String vHead = vData.substring (0, vPos);
            vData = vData.substring (vPos, vData.length ());
            return vHead
                   + vData.substring (vData.indexOf (pEnd) + pEnd.length (), vData.length ());
        }

        catch (Exception e) { return pData; }
    }

    /**
     * string cut from spec
     * @param pData string
     * @param pStart start with string
     * @param pEnd end with string
     * @return String
     */
    public static String cutAndInsertStringFrom(String pData, String pStart, String pEnd, String pIns) {
        int vPos = 0;
        String vData = pData;

        try {
            if ((vPos = vData.indexOf (pStart)) == -1) return vData;

            String vHead = vData.substring (0, vPos);
            vData = vData.substring (vPos, vData.length ());
            return vHead
                   + pIns + vData.substring (vData.indexOf (pEnd) + pEnd.length (), vData.length ());
        }

        catch (Exception e) { return pData; }
    }

    /**
     * string cut from spec
     * @param pData string
     * @param pStart start with string
     * @param pEnd end with string
     * @return String
     */
    public static String getStringFrom(String pData, String pStart, String pEnd) {
        int vPos = 0;
        String vData = pData;

        try {
            if ((vPos = vData.indexOf (pStart)) == -1) return vData;

            vData = vData.substring (vPos, vData.length ());
            vData = vData.substring (0, vData.indexOf (pEnd) > 0 ? vData.indexOf (pEnd) + pEnd.length() : 0);
            return vData;
        }

        catch (Exception e) { return pData; }
    }

    /**
     * string replace
     * @param pText full text
     * @param pOrg
     * @param pTar
     * @return String
     */
    public static String cvtStringFrom(String pText, String pOrg, String pTar)
    {
        int vLocate = 0, vPrev = -1;
        String vTmp = "";

        if (pText == null || "".equals (pText))
            return "";

        while (vLocate != -1) {
            if ((vLocate = pText.indexOf (pOrg)) == -1) break;

            if (vLocate == vPrev)
                break;

            vPrev = vLocate;

            vTmp = pText.substring (0, vLocate);
            vTmp += pTar;
            vTmp += pText.substring (vLocate + pOrg.length (), pText.length ());

            pText = vTmp;
        };

        return pText;
    }

    /**
     * trim from string
     * @param pStr string
     * @return String
     */
    public static String cutStringFromSpace (String pStr) {
        StringBuffer vRet = new StringBuffer (512);

        for (int cnt = 0; cnt < pStr.length (); cnt++)
            if (pStr.charAt (cnt) != ' ')
                vRet.append (pStr.charAt (cnt));

        return vRet.toString ();
    }

	/**
	 * date dash append
	 *
	 * @param date
	 *            ex) 20020101
	 * @param dash
	 *            true or false
	 * @return String ex) 2002/01/01
	 */
	public static String cvtDate(String date, String dash) {
		try {
			if (isEmpty(date) | date.length() != 8 | isEmpty(dash)) return date;
			return date.substring(0, 4) + dash + date.substring(4, 6) + dash
					+ date.substring(6, 8);
		} catch (Exception e) {
			return date;
		}
	}

	/**
	 * date convert kor
	 *
	 * @param date
	 *            ex) 20020101
	 * @return String ex) 2002년 01월 01일
	 */
	public static String cvtDateToKor(String date) {
		try {
			if (isEmpty(date) | date.length() != 8) return date;
			return date.substring(0, 4) + "년 " + date.substring(4, 6) + "월 "
					+ date.substring(6, 8) + "일";
		} catch (Exception e) {
			return date;
		}
	}

	/**
	 * time dash append
	 *
	 * @param time
	 *            ex) 192100
	 * @return String ex) 19:21:00
	 */
	public static String cvtTimeToKor(String time) {
		try {
			if (isEmpty(time) | time.length() != 6) return time;
			return time.substring(0, 2) + "시 " + time.substring(2, 4) + "분 "
					+ time.substring(4, 6) + "초";
		} catch (Exception e) {
			return time;
		}
	}

	/**
	 * 주민등록번호로 연령 계산하기 (만 나이)
	 * @param resdNum
	 * @return int
	 */
	public static int getFullAge(String resdNum) {
		if(resdNum == null){
			throw new NullPointerException("주민등록번호가 NULL입니다.");
		}
		if(resdNum.length() != 13){
			throw new RuntimeException("주민등록번호 자릿수가 13자리가 아닙니다.");
		}
		int year = Integer.parseInt(resdNum.substring(0, 2));
		int month = Integer.parseInt(resdNum.substring(2, 4));
		int y2k = Integer.parseInt(resdNum.substring(6, 7));
		if(y2k == 1 || y2k == 2 || y2k == 5 || y2k == 6){
			//1900년도출생
			year= 1900 + year;
		}else{
			//2000년도 출생
			year= 2000 + year;
		}
		String today = UtilDate.getCurrent("yyyyMM");
		int cyear = Integer.parseInt(today.substring(0, 4));
		int cmonth = Integer.parseInt(today.substring(4, 6));
		int age = cyear - year - (cmonth - month < 1 ? 1 : 0);
		return age;
	}

	/**
	 * 귀속년 까지의 주민등록번호상 나이 가져오기
	 * @param rvsnYr
	 * @param resdNum
	 * @return int
	 */
	public static int getAges(String rvsnYr, String resdNum){
		if(resdNum == null){
			throw new NullPointerException("주민등록번호가 NULL입니다.");
		}
		if(resdNum.length() != 13){
			throw new RuntimeException("주민등록번호 자릿수가 13자리가 아닙니다.");
		}
		int year = Integer.parseInt(resdNum.substring(0, 2));
		int y2k = Integer.parseInt(resdNum.substring(6, 7));
		if(y2k == 1 || y2k == 2 || y2k == 5 || y2k == 6){
			//1900년도출생
			year= 1900 + year;
		}else{
			//2000년도 출생
			year= 2000 + year;
		}
		int cyear = Integer.parseInt(rvsnYr);
		return (cyear - year);
	}
	/**
	 * 카멜표기법 문자를 언더스코어 문자로 변경
	 * @param str
	 * @return
	 */
	public static String convCamelToUnderscore(String str) {
		String regex = "([a-z])([A-Z])";
		String replacement = "$1_$2";
        String value = "";
        value = str.replaceAll(regex, replacement).toUpperCase();
        return value;
	}

	/**
	 * 우편번호 형식으로 변경
	 * @param str
	 * @return
	 */
	public static String getZipCode(String str) {
		return str.replaceAll("(\\d{3})(\\d{3})", "$1-$2");
	}

	/**
	 * 태그를 삭제 HTML -> Text
	 * @param source 원본 HTML 텍스트
	 * @return Html->Text로 변환된 문자열
	 */
	public static String htmlToText(String source) {
		if(source == null || source.length() == 0) return "";
		//source = source.replaceAll("\\<br\\s*/?>", "\n");
		source = source.replaceAll("\\<.*?>", "");
		source = source.replace("&nbsp;", "");
		source = source.replace("&amp;", "");
		//source = source.replace("\n", "<br />");
		if(source.length() > 300) source = source.substring(0, 300) + "...";
		return source;
	}

	/**
	 * 태그를 삭제하고 두번째 인자값까지의 길이까지 잘라서 보여줌(뒤에 ... 붙임)
	 * @param source 원본 HTML 텍스트
	 * @param size 길이
	 * @return Html->Text로 변환된 문자열
	 */
	public static String htmlToText(String source, Integer size) {
		if(source == null || source.length() == 0) return "";
		//source = source.replaceAll("\\<br\\s*/?>", "\n");
//		source = source.replaceAll("\\<.*?>", "");
//		source = source.replace("&nbsp;", "");
//		source = source.replace("&amp;", "");
		//source = source.replace("\n", "<br />");
		if(size < 0) return source;
		//if(source.length() > size) source = source.substring(0, size) + "...";
		//String rtn = UtilString.getBytesCut(source, size);
		String rtn = UtilString.strCut(source, null, size, 0, true, true);
		return rtn;
	}

	/**
	 * 유튜브 재생시간
	 * @param str
	 * @return
	 */
	public static String getYoutubeRunningTime(String str) {
		if (StringUtils.isEmpty(str)) return str;
		StringBuilder sb = new StringBuilder();

		String[] arr = str.split(":");
		switch (arr.length) {
		case 1:
			sb.append("00:").append(arr[0]);
			break;
		case 3:
			if (StringUtils.equals("00", arr[0])) {
				sb.append(arr[1]).append(":").append(arr[2]);
			} else {
				sb.append(str);
			}
			break;
		default:
			sb.append(str);
			break;
		}

		return sb.toString();
	}

	/**
	 * 이메일 형태 문자열 유효성체크
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		return email.matches("^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$");
	}

	/**
	 * 비밀번호 유효성체크(6~16자, 영문자 1개 이상 필수, 숫자 1개 이상 필수)
	 * @param pwd
	 * @return
	 */
	public static boolean isValidPwd(String pwd) {
		if(pwd.matches(".{6,16}")) {
			if(pwd.matches(".*[a-zA-Z]+.*")) {
				if(pwd.matches(".*[0-9]+.*")) {
					 return true;
				}
			}
		}
		return false;
	}

	/**
	 * 휴대전화 유효성체크
	 * @param celNo
	 * @return
	 */
	public static boolean isValidCelNo(String celNo) {
		String _celNo = celNo.replaceAll("-", "");
		return _celNo.matches("[0-9]{10,11}");
	}

	/**
	 * Line Feed 값을 <br /> 태그로 변경
	 * @param str
	 * @return
	 */
	public static String lf2br(String str) {
		return (str != null) ? str.replace("\n", "<br />") : "";
	}

	/**
	 * Line Feed 값을 <br /> 태그로 변경
	 * 지정한 줄 수를 넘으면 ... 처리
	 * @param str
	 * @param limit
	 * @return
	 */
	public static String lf2br2(String str, int limit) {
		String result = "";

		if (str == null) return result;

		String[] arr = str.split("\n");
		for (int i = 0; i < arr.length; i++) {
			if (i == limit) {
				result += "<br />...";
				break;
			}

			if (i > 0) {
				result += "<br />";
			}

			result += arr[i];
		}

		return result;
	}

	/**
	 * 전화번호에 하이픈(-) 붙이기
	 * @param str 전화번호(ex: 01012341234)
	 * @return
	 */
	public static String getPhoneNumberWithHyphen(String str) {
		String rtn = "";

		if(UtilString.isEmpty(str)) {
			return rtn;
		}

		str = str.replaceAll("-", "");

		if(str.length() == 9) {		// 021231234 → 02-123-1234
			rtn = str.substring(0,2) + "-" + str.substring(2,5) + "-" + str.substring(5);
		} else if(str.length() == 10) {		// 0101231234 → 010-123-1234
			if (!"02".equals(str.substring(0,2)) ) { // 0212351234 → 02-1235-1234
				rtn = str.substring(0,2) + "-" + str.substring(2,6) + "-" + str.substring(6);
			} else {				// 0102351234 → 010-235-1234 or 0311233354 -> 031-123-3354
				rtn = str.substring(0,3) + "-" + str.substring(3,6) + "-" + str.substring(6);
			}
		} else if(str.length() == 11) {		// 01012341234 → 010-1234-1234 or 0312351234 → 031-1235-1234
			rtn = str.substring(0,3) + "-" + str.substring(3,7) + "-" + str.substring(7);
		} else if(str.length() == 12) {		// 034212341234 → 0342-1234-1234
			rtn = str.substring(0,4) + "-" + str.substring(4,8) + "-" + str.substring(8);
		}

		return rtn;
	}

	/**
	 * 전화번호 배열로 분할하기
	 * 01021231234 -> rtn[0] = 010, rtn[1] = 2123, rtn[2] = 1234
	 * @param str
	 * @return
	 */
	public static String[] seperateTelNum(String str) {
		String[] rtn = {"", "", ""};

		if(UtilString.isEmpty(str)) {
			return rtn;
		}

		str = str.replaceAll("-", "");

		if(str.length() == 9) {		// 021231234 → 02-123-1234
			rtn[0] = str.substring(0,2);
			rtn[1] = str.substring(2,5);
			rtn[2] = str.substring(5);
		} else if(str.length() == 10) {		// 0101231234 → 010-123-1234
			if (!"02".equals(str.substring(0,2)) ) { // 0212351234 → 02-1235-1234
				rtn[0] = str.substring(0,2);
				rtn[1] = str.substring(2,6);
				rtn[2] = str.substring(6);
			} else {				// 0102351234 → 010-235-1234 or 0311233354 -> 031-123-3354
				rtn[0] = str.substring(0,3);
				rtn[1] = str.substring(3,6);
				rtn[2] = str.substring(6);
			}
		} else if(str.length() == 11) {		// 01012341234 → 010-1234-1234 or 0312351234 → 031-1235-1234
			rtn[0] = str.substring(0,3);
			rtn[1] = str.substring(3,7);
			rtn[2] = str.substring(7);
		} else if(str.length() == 12) {		// 034212341234 → 0342-1234-1234
			rtn[0] = str.substring(0,4);
			rtn[1] = str.substring(4,8);
			rtn[2] = str.substring(8);
		}
		return rtn;
	}

	/**
	 * 전화번호 분할된 것의 일부를 내보냄
	 * 01021231234, 0 -> rtn = 010
	 * 01021231234, 1 -> rtn = 2123
	 * 01021231234, 2 -> rtn = 1234
	 *
	 * @param str
	 * @return
	 */
	public static String seperateTelNum(String str, int idx) {
		return seperateTelNum(str)[idx];
	}


	/**
	 * 태그를 삭제하고 br 은 &amp;nbsp; 로 변환
	 * @param source 원본 HTML 텍스트
	 * @return Html->Text로 변환된 문자열
	 */
	public static String htmlToText2(String source) {
		if(source == null || source.length() == 0) return "";
		source = source.replaceAll("\\<br\\s*/?>", "&nbsp;");
		source = source.replaceAll("\\<.*?>", "");
		return source;
	}

	/**
	 * 반올림 후 3자리마다 콤마
	 */
	public static String getCurrencyWithRound(double d) {
		long l = Math.round(d);
		return NumberFormat.getInstance().format(l);
	}

	/**
	 * 3 자리마다 콤마. 0 일 경우 공백 리턴
	 * @param d
	 * @return
	 */
	public static String getCurrencyWithRoundEmpty(double d) {
		String rtn = UtilString.getCurrencyWithRound(d);
		return "0".equals(rtn) ? "" : rtn;
	}

	public static String getDateToMMdd(String date) {
		if ( UtilString.isDate(date, "yyyyMMdd") ) {
			date = date.substring(4);

			return date.substring(0, 2) + "." + date.substring(2);
		}

		return "";
	}

	/**
	 * 사업자 등록번호 하이픈 붙이기
	 * @param companyLicenseNumber
	 * @return
	 */
	public static String getCompanyLicenseNumberHyphen(String companyLicenseNumber) {
		if ( UtilString.isEmpty(companyLicenseNumber) ) {
			return companyLicenseNumber;
		} else if (companyLicenseNumber.length() == 10) {
			return companyLicenseNumber.substring(0, 3) + "-" + companyLicenseNumber.substring(3, 5) + "-" + companyLicenseNumber.substring(5);
		}

		return companyLicenseNumber;
	}

	/**
	 * 수입신고번호 하이픈 붙이기
	 * @param productImportDeclNum
	 * @return
	 */
	public static String getProductImportDeclNumHyphen(String productImportDeclNum) {
		if ( UtilString.isEmpty(productImportDeclNum) ) {
			return productImportDeclNum;
		} else if (productImportDeclNum.length() == 14 ) {
			return productImportDeclNum.substring(0, 5) + "-" + productImportDeclNum.substring(5, 7) + "-" + productImportDeclNum.substring(7);
		}
		return productImportDeclNum;
	}

	/**
	 * 441340 -> rtn[0] = 441, rtn[1] = 340
	 * @param str
	 * @return
	 */
	public static String[] seperateZipCode(String str) {
		String[] rtn = {"", ""};

		if(UtilString.isEmpty(str)) {
			return rtn;
		}

		str = str.replaceAll("-", "");

		if(str.length() == 6) {		// 0101231234 → 010-123-1234
			rtn[0] = str.substring(0,3);
			rtn[1] = str.substring(3,6);
			return rtn;
		} else {
			return rtn;
		}
	}

	/**
	 * 빈 값이 들어왔을 경우 치환
	 * @param str
	 * @param newValue
	 * @return
	 */
	public static String convertEmptyToNewValue(String str, String newValue) {
		if(UtilString.isEmpty(str)) {
			return newValue;
		}

		return str;
	}

	/**
	 * textarea 에 들어온 hash 코드를 뽑아 HashSet 으로 리턴
	 * ex>
	 * 		text = "우리는 #21세기 가 지나가기 전에 #통일 을 이뤄야 한다고 한 #역사학자 는 #TV특강 에서 주장했다.";
	 * 		return = {"#21세기", "#통일", "#역사학자", "#TV특강"}
	 */
	public static Set<String> getHashTags(String text) {
		String[] words = text.split(" ");
        Set<String> hashTags = new HashSet<String>();
        for (String word : words) {
        	if(word.startsWith("#") && !hashTags.contains(word)){
                hashTags.add(word);
            }
        }
        return hashTags;
    }

    /**
     * 10진수로 된 2자리의 정수 형식으로 변환(자리수가 부족할 경우 0으로 해당부분을 채움)
     * @param str
     * @return
     */
    public static String convertIntToString2Digit(int str) {
        return String.format("%02d", str);
    }

}