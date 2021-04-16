package com.dsw.core.util;


import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

/**
 * Class Name : UtilMath.java
 * Description : UtilMath class
 * Modification Information
 *
 * @author Y.S.KIM
 * @since 2014. 5. 29.
 * @version 1.0
 *
 */

public class UtilMath {

	/**
	 * 2차 방정식 근 구하기 (+- 값 리턴)
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static double[] solveEquationQuadratic(double a, double b, double c){
		double q = (b * b) - (4 * a * c);
		if(q < 0){
			throw new RuntimeException("'b^2 - 4ac' less zero");
		}
		double solve[] = new double[2];
		solve[0] = (-b + Math.sqrt(q)) / (2 * a);
		solve[1] = (-b - Math.sqrt(q)) / (2 * a);
		return solve;
	}



	/**
	 * 3차 방정식 근 구하기 (x1, x2, x3 값 리턴)
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static double[] cubicEquationCalculate(double a, double b, double c, double d){

		double x1=0D;
		double x2=0D;
		double x3=0D;

		double f = (((3D*c)/a) - (((b*b)/(a*a))))/3D;
		double g = ((2D*((b*b*b)/(a*a*a))-(9D*b*c/(a*a)) + ((27D*(d/a)))))/27D;
		double h = (((g*g)/4D) + ((f*f*f)/27D));

		if (h > 0){

			double m = (((g/2D)*-1)+ (Math.sqrt(h)));

			double k = 1D;
			if (m < 0)
				k= -1D;
			else
				k= 1D;

			double m2 = (Math.pow((m*k),(1D/3D)));
			m2 = m2*k;
			k=1;
			double n = (((g/2D)*-1)- (Math.sqrt(h)));
			if (n < 0)
				k=-1D;
			else
				k=1D;

			double n2 = (Math.pow((n*k),(1D/3D)));
			n2 = n2*k;
			k=1D;
			x1 = ((m2 + n2) - (b/(3D*a)));
			x2=0D;
			x3=0D;
		}

		if (h<=0){
			double r = (Math.sqrt((g*g/4D)-h));
			double k=1D;
			if (r<0)
				k=-1D;
			double rc = Math.pow((r*k),(1D/3D))*k;
			k=1;
			double theta = Math.acos((-g/(2D*r)));
			x1 = (2D*(rc*Math.cos(theta/3D))-(b/(3D*a)));
			double x2a= rc*-1D;
			double x2b= Math.cos(theta/3D);
			double x2c= Math.sqrt(3)*(Math.sin(theta/3D));
			x2= (x2a*(x2b + x2c))-(b/(3D*a));
			x3= (x2a*(x2b - x2c))-(b/(3D*a));

			x1=x1*1E+14;
			x1=(x1/1E+14);
			x2=x2*1E+14;
			x2=(x2/1E+14);
			x3=x3*1E+14;
			x3=(x3/1E+14);
		}

		if ((f+g+h) == 0D){
			double dans = 0D;
			if (d<0){
				d=d*-1D;
				dans=Math.pow((d/a),(1D/3D));
			}
			if (d>=0) {
				dans=Math.pow((d/a),(1D/3D));
				dans=dans*-1D;
			}

			x1=dans;
			x2=dans;
			x3=dans;
		}

		return new double[]{x1, x2, x3};
	}

	/**
	 * 절상, 절하, 반올림 처리
	 * @param strMode  - 수식
	 * @param nCalcVal - 처리할 값(소수점 이하 데이터 포함)
	 * @param nDigit   - 연산 기준 자릿수(오라클의 ROUND함수 자릿수 기준)
	 *                   -2:십단위, -1:원단위, 0:소수점 1자리
	 *                   1:소수점 2자리, 2:소수점 3자리, 3:소수점 4자리, 4:소수점 5자리 처리
	 * @return String nCalcVal
	 */
	public static double calcMath(String strMode, double nCalcVal, int nDigit) {
	    if(strMode.equals("ROUND")) {        //반올림
	        if(nDigit < 0) {
	            nDigit = -(nDigit);
	            nCalcVal = Math.round(nCalcVal / Math.pow(10, nDigit)) * Math.pow(10, nDigit);
	        } else {
	            nCalcVal = Math.round(nCalcVal * Math.pow(10, nDigit)) / Math.pow(10, nDigit);
	        }
	    } else if(strMode.equals("CEIL")) {  //절상
	        if(nDigit < 0) {
	            nDigit = -(nDigit);
	            nCalcVal = Math.ceil(nCalcVal / Math.pow(10, nDigit)) * Math.pow(10, nDigit);
	        } else {
	            nCalcVal = Math.ceil(nCalcVal * Math.pow(10, nDigit)) / Math.pow(10, nDigit);
	        }
	    } else if(strMode.equals("FLOOR")) { //절하
	        if(nDigit < 0) {
	            nDigit = -(nDigit);
	            nCalcVal = Math.floor(nCalcVal / Math.pow(10, nDigit)) * Math.pow(10, nDigit);
	        } else {
	            nCalcVal = Math.floor(nCalcVal * Math.pow(10, nDigit)) / Math.pow(10, nDigit);
	        }
	    } else {                        //그대로(무조건 소수점 첫째 자리에서 반올림)
	        nCalcVal = Math.round(nCalcVal);
	    }

	    return nCalcVal;
	}

	/**
	 * 세액 구하기
	 * @param amount
	 * @return
	 */
	public static double getTaxWon(double amount){
		if(amount == 0D)
			return 0D;
		BigDecimal _amount = new BigDecimal(amount);
		return _amount.divide(new BigDecimal(1.1F), MathContext.DECIMAL32).multiply(new BigDecimal(0.1F)).longValue();
	}

	/**
	 * 공급가액 구하기
	 * @param amount
	 * @return
	 */
	public static double getSupplyWon(double amount){
		BigDecimal _amount = new BigDecimal(amount);
		return _amount.subtract(new BigDecimal(getTaxWon(amount))	).doubleValue();
	}


	/**
	 * 최대공약수 구하기 (유클리드 알고리즘)
	 * @param a
	 * @param b
	 * @return
	 */
	public static long gcm(long a, long b){
		long tmp = 0;
		while(a != 0){
			if(a < b){
				tmp = a;
				a = b;
				b = tmp;
			}
			a -= b;
		}
		return b;
	}

	/**
	 * 증감율 구하기. digit 자리수에서 반올림
	 * @param pre
	 * @param now
	 * @param digit 소수점자리수
	 * @return
	 */
	public static double getVariatioinRate(double pre, double now, int digit) {
	    double tmpPre = 0d;
	    double tmpNow = 0d;

	    if (pre == 0) {
	        tmpPre = 1d;
	    } else {
	        tmpPre = pre;
	    }

	    if (now == 0) {
	        tmpNow = 1d;
	    } else {
	        tmpNow = now;
	    }

	    double result = 0d;

	    if (pre != 0 && now != 0) {
	        result = ((tmpNow - tmpPre) / tmpPre ) * 100d;
	    }

	    return UtilMath.calcMath("ROUND", result, digit);
	}

	/**
	 * 소수점 3자리까지 구함(4자리에서 반올림)
	 * 소수점이 0일 경우에는 정수 형태
	 * @param d
	 * @return
	 */
	public static String getDecimalPoint(double d) {
		if (d == (int) d) {
			return String.valueOf((int) d);
		}

		DecimalFormat format = new DecimalFormat(".###");
		return format.format(d);
	}

}