package com.andrewshirtz.CG.Util;

import java.util.Arrays;

import com.andrewshirtz.CG.Exceptions.OrderMismatchException;

/*
 * 	This class implements a dimension agnostic Canonical Address as Described by Kitto et. al. (See README)
 * 
 * 	In short, a Canonical Address is a list of digits, each digit requiring a number of bits determined
 *  the order of the address. The inner class, BitTuple, represents one of these digits. The digits,
 *  as stored in the Canonical Address, are packed into a byte array. Access to digits should be accomplished
 *  through the included helper functions.
 */

public class CanAddr {
	
	/*
	 * 		STATIC MEMBERS
	 */
	
	/*
	 * 		INSTANCE MEMBERS
	 */
	
	// TODO: This is just so I can move forward now. I intend to change this to bit-field storage at a later date.
	//private byte[] 	storage;
	private int[]	tuples;
	private int 	order;
	private int 	numDigits;
	private int 	leastSigIndex;
	
	/*
	 * 	TODO: Implement:
	 * 		CanAddr <-> (Prepared) EucVec conversions	DONE
	 * 		CanAddr Truncation
	 * 		CanAddr Arithmetics
	 */
	
	// TODO: related to intermediate impl, sets a single (aggLev 0) digit 0
	public CanAddr (int order) {
		this.order = order;
		this.tuples = new int[1];
		this.leastSigIndex = 0;
		this.numDigits = 1;
	}
	
	// Returns boolean for specific bit in storage
	public boolean getBit (int tupIndex, int dim) {
		if (dim > this.order) {
			throw new OrderMismatchException("Unable to access dimension " + dim + ", as this CanAddr has order " + this.order);
		}
		// Guard clauses for tuples not explicitly stored
		if (tupIndex < leastSigIndex)						{ return false; }
		if (tupIndex > this.numDigits + this.leastSigIndex)	{ return false; }
		return ((this.tuples[tupIndex - this.leastSigIndex] & (1 << dim)) != 0);
	}
	
	public int getOrder () {
		return this.order;
	}
	
	public int getLeastSigIndex () {
		return this.leastSigIndex;
	}
	
	public int getMostSigIndex () {
		return this.leastSigIndex + this.numDigits;
	}
	
	public int getTuple (int tupIndex) {
		if (tupIndex < leastSigIndex)						{ return 0; }
		if (tupIndex > this.numDigits + this.leastSigIndex)	{ return 0; }
		
		return (this.tuples[tupIndex - this.leastSigIndex]);
	}
	
	public void setTuple (int tupIndex, int tupVal) {
		if (tupIndex < leastSigIndex) {
			int[] tmp = new int[this.numDigits + (this.leastSigIndex - tupIndex)];
			
			for (int i = 0; i < this.numDigits; i++) {
				tmp[i - (this.leastSigIndex - tupIndex)] = this.tuples[i];
			}
			
			this.tuples = Arrays.copyOf(tmp, tmp.length);
			
			this.leastSigIndex = tupIndex;
			this.numDigits = this.tuples.length;
		} else if (tupIndex > (this.numDigits  - 1) + this.leastSigIndex) {
			int[] tmp = new int[tupIndex - this.leastSigIndex + 1];
			
			for (int i = 0; i < this.numDigits; i++) {
				tmp[i] = this.tuples[i];
			}
			
			this.tuples = Arrays.copyOf(tmp, tmp.length);
			
			this.numDigits = this.tuples.length;
		}
		
		this.tuples[tupIndex - this.leastSigIndex] = tupVal;
	}
	
	@Override
	public String toString () {
		String result = "|";
		for (int i = (this.numDigits - 1) + this.leastSigIndex; i > 0; i--) {
			result += this.getTuple(i) + ", ";
		}
		return result + this.getTuple(0) + "|";
	}
}
