package com.andrewshirtz.CG.Exceptions;

// TODO: Design and fill in this class, ensure it is thrown appropriately

public class DimensionMismatchException extends ArithmeticException {

	
	public DimensionMismatchException () {}
	
	public DimensionMismatchException (String msg) {
		super (msg);
	}
}
