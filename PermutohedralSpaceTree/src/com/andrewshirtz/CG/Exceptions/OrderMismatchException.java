package com.andrewshirtz.CG.Exceptions;

//TODO: Design and fill in this class, ensure it is thrown appropriately

public class OrderMismatchException extends ArithmeticException {

	public OrderMismatchException () {}
	
	public OrderMismatchException (String msg) {
		super (msg);
	}
}
