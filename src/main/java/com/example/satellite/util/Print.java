package com.example.satellite.util;

import java.io.PrintStream;

/**
 * 
 * @Description: Thinking in Java 中的Print类
 * @author: GXW
 * @date:   2016年2月19日
 *
 */

public class Print {
	// Print with a newline:
	public static void print(Object obj) {
		System.out.println(obj);
	}

	// Print a newline by itself:
	public static void print() {
		System.out.println();
	}

	// Print with no line break:
	public static void printnb(Object obj) {
		System.out.print(obj);
	}

	// The new Java SE5 printf() (from C):
	public static PrintStream printf(String format, Object... args) {
		return System.out.printf(format, args);
	}
} 
