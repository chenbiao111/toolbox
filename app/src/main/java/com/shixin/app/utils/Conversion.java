package com.shixin.app.utils;

import android.annotation.SuppressLint;

import java.util.regex.Pattern;

@SuppressLint("DefaultLocale")
public class Conversion {
	public static String z10_2 (String text) {
		if(text.length()==0) return "";
		Pattern p=Pattern.compile("[0-9]+\\.?[0-9]*");
		if(!p.matcher(text).matches()) {
			return "无法转换";
		}
		double num = Double.parseDouble(text);
		long num1=(long)num;	//整数部分
		double num2=num%1;		//小数部分
		String out1="",out2="";
		while (num1>0) {
			out1=num1%2+out1;
			num1=(long)(num1/2);
		}
		for (int i=0;i<48;i++) {
			out2+=(int) (num2*2);
			num2=num2*2%1;
		}
		if(out1.length()==0) out1="0";
		return out1+"."+out2;
	}
	public static String z2_10(String text) {
		if(text.length()==0) return "";
		Pattern p=Pattern.compile("[0-1]+\\.?[0-1]*");
		if(!p.matcher(text).matches()) {
			return "无法转换";
		}
		int index=text.indexOf(".");
		char[] num1,num2;
		if(index<0) {
			num1=text.toCharArray();
			num2="0".toCharArray();
		}
		else {
			num1=text.substring(0,index).toCharArray();
			num2=text.substring(index+1,text.length()).toCharArray();
		}
		double out1=0;
		double out2=0;
		for (int i=0;i<num1.length;i++) {
			out1+=(num1[i]-'0')*Math.pow(2, num1.length-1-i);
		}
		for (int i=0;i<num2.length;i++) {
			out2+=(num2[i]-'0')*Math.pow(2, -1-i);
		}
		return String.format("%f", out1+out2);
	}
	public static String z8_2(String text) {
		if(text.length()==0) return "";
		Pattern p=Pattern.compile("[0-7]+\\.?[0-7]*");
		if(!p.matcher(text).matches()) {
			return "无法转换";
		}
		int index=text.indexOf(".");
		char[] num1,num2;
		if(index<0) {
			num1=text.toCharArray();
			num2="0".toCharArray();
		}
		else {
			num1=text.substring(0,index).toCharArray();
			num2=text.substring(index+1,text.length()).toCharArray();
		}
		String out1="",out2="";
		for(char i : num1) {
			i=(char) (i-'0');
			out1+=String.format("%d%d%d", i/4%2,i/2%2,i%2);
		}
		for(char i : num2) {
			i=(char) (i-'0');
			out2+=String.format("%d%d%d", i/4%2,i/2%2,i%2);
		}
		return out1+"."+out2;
	}
	public static String z2_8(String text) {
		if(text.length()==0) return "";
		Pattern p=Pattern.compile("[0-1]+\\.?[0-1]*");
		if(!p.matcher(text).matches()) {
			return "无法转换";
		}
		int index=text.indexOf(".");
		char[] num1,num2;
		if(index<0) {
			num1=text.toCharArray();
			num2="0".toCharArray();
		}
		else {
			num1=text.substring(0,index).toCharArray();
			num2=text.substring(index+1,text.length()).toCharArray();
		}
		String out1="",out2="";
		for (int a,b,c,i=num1.length-1;i>=0;i-=3) {
			a = num1[i]-'0';
			b = i-1<0 ? 0 : (num1[i-1]-'0')*2;
			c = i-2<0 ? 0 : (num1[i-2]-'0')*4;
			out1=String.valueOf(a+b+c)+out1;
		}
		for (int a,b,c,i=0;i<num2.length;i+=3) {
			a = (num2[i]-'0')*4;
			b = i+1<num2.length ? (num2[i+1]-'0')*2 : 0;
			c = i+2<num2.length ? num2[i+2]-'0' : 0;
			out2+=String.valueOf(a+b+c);
		}
		return out1+"."+out2;
	}
	public static String z16_2(String text) {
		if(text.length()==0) return "";
		Pattern p=Pattern.compile("([0-9]|[A-F])+\\.?([0-9]|[A-F])*");
		if(!p.matcher(text).matches()) {
			return "无法转换";
		}
		int index=text.indexOf(".");
		char[] num1,num2;
		if(index<0) {
			num1=text.toCharArray();
			num2="0".toCharArray();
		}
		else {
			num1=text.substring(0,index).toCharArray();
			num2=text.substring(index+1,text.length()).toCharArray();
		}
		String out1="",out2="";
		for(char i : num1) {
			if(i>='A' && i<='F') {
				i=(char) (i-'A'+10);
			}
			else {
				i=(char) (i-'0');
			}
			out1+=String.format("%d%d%d%d", i/8%2,i/4%2,i/2%2,i%2);
		}
		for(char i : num2) {
			if(i>='A' && i<='F') {
				i=(char) (i-'A'+10);
			}
			else {
				i=(char) (i-'0');
			}
			out2+=String.format("%d%d%d%d", i/8%2,i/4%2,i/2%2,i%2);
		}
		return out1+"."+out2;
	}
	public static String z2_16(String text) {
		if(text.length()==0) return "";
		Pattern p=Pattern.compile("[0-1]+\\.?[0-1]*");
		if(!p.matcher(text).matches()) {
			return "无法转换";
		}
		int index=text.indexOf(".");
		char[] num1,num2;
		if(index<0) {
			num1=text.toCharArray();
			num2="0".toCharArray();
		}
		else {
			num1=text.substring(0,index).toCharArray();
			num2=text.substring(index+1,text.length()).toCharArray();
		}
		String out1="",out2="";
		for (int i=num1.length-1;i>=0;i-=4) {
			int a = num1[i]-'0';
			int b = i-1<0 ? 0 : (num1[i-1]-'0')*2;
			int c = i-2<0 ? 0 : (num1[i-2]-'0')*4;
			int d = i-3<0 ? 0 : (num1[i-3]-'0')*8;
			char e = (char) (a+b+c+d<10 ? a+b+c+d+'0' : a+b+c+d-10+'A');
			out1=String.valueOf(e)+out1;
		}
		for (int i=0;i<num2.length;i+=4) {
			int a = (num2[i]-'0')*8;
			int b = i+1<num2.length ? (num2[i+1]-'0')*4 : 0;
			int c = i+2<num2.length ? (num2[i+2]-'0')*2 : 0;
			int d = i+3<num2.length ? num2[i+3]-'0' : 0;
			char e = (char) (a+b+c+d<10 ? a+b+c+d+'0' : a+b+c+d-10+'A');
			out2+=String.valueOf(e);
		}
		return out1+"."+out2;
	}
}
