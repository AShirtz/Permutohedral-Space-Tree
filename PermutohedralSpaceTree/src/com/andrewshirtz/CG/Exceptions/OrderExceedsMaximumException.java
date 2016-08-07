package com.andrewshirtz.CG.Exceptions;

public class OrderExceedsMaximumException extends Exception {
	
	public OrderExceedsMaximumException () {}
	
	public OrderExceedsMaximumException (String msg) {
		super (msg);
	}
}
