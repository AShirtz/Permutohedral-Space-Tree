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
	 * Returns the index of the most significant difference between the two given CanAddrs
	 * Returns -1 if the two CanAddrs are equivalent
	 */
	
	public static int getMostSigDiffIndex (CanAddr a, CanAddr b) {
		if (!CanAddr.orderCheck(a,b)) {
			throw new OrderMismatchException("Unable to compare CanAddrs, operands have different orders.");
		}
		
		int maxSig = (a.getMostSigIndex() > b.getMostSigIndex()) ? (a.getMostSigIndex()) : (b.getMostSigIndex());
		
		return getMostSigDiffIndex(a, b, maxSig, 0);
	}
	
	public static int getMostSigDiffIndex (CanAddr a, CanAddr b, int max, int min) {
		if (!CanAddr.orderCheck(a,b)) {
			throw new OrderMismatchException("Unable to compare CanAddrs, operands have different orders.");
		}
		
		for (int i = max; i >= min; i--) {
			if (a.getTuple(i) != b.getTuple(i)) { return i; }
		}
		
		return -1;
	}
	
	public static boolean orderCheck (CanAddr... addrs) {
		if (addrs.length > 1) {
			int order = -1;
			int i;
			
			for (i = 0; i < addrs.length; i++) { 
				if (addrs[i].getClass() != OriginCanAddr.class) { 
					order = addrs[i].getOrder(); 
					break;
				}
			}
			
			for (i++; i < addrs.length; i++) {
				if (addrs[i].getClass() != OriginCanAddr.class) {
					if (order != addrs[i].getOrder()) { return false; }
				}
			}
		}
		
		return true;
	}
	
	/*
	 * 		INSTANCE MEMBERS
	 */
	
	// TODO: Using an array of ints is a temporary measure so I can move forward now. 
	//		 I intend to change this to bit-field storage at a later date.
	//private byte[] 	storage;
	
	private 	int[]	tuples;
	protected 	int 	order;
	protected 	int 	leastSigIndex;
	
	/*
	 * 	TODO: Implement:
	 * 		CanAddr <-> EucVec conversions	DONE
	 * 		CanAddr Truncation				DONE	
	 * 		CanAddr Arithmetics
	 */
	
	protected CanAddr() {}
	
	// TODO: related to intermediate impl, sets a single (aggLev 0) digit 0
	public CanAddr (int order) {
		this.order = order;
		this.tuples = new int[1];
		this.leastSigIndex = 0;
	}
	
	// Copy Constructor
	public CanAddr (CanAddr orig) {
		this.order = orig.order;
		this.tuples = Arrays.copyOf(orig.tuples, orig.tuples.length);
		this.leastSigIndex = orig.leastSigIndex;
	}
	
	public int getOrder () {
		return this.order;
	}
	
	public int getLeastSigIndex () {
		return this.leastSigIndex;
	}
	
	public int getMostSigIndex () {
		return (this.leastSigIndex + this.tuples.length) - 1;
	}
	
	/*
	 * Returns a truncated CanAddr.
	 * In the returned CanAddr, all digits less significant than 'leastSigIndex' will be replaced with 0.
	 */
	public CanAddr truncate (int leastSigIndex) {
		if (leastSigIndex < 0 || leastSigIndex > this.getMostSigIndex()) {
			// TODO: Throw exception explaining issue
		} else if (leastSigIndex == 0) {
			// No truncation, return copy
			return new CanAddr(this);
		}
		
		CanAddr result = new CanAddr(this.getOrder());
		
		for (int i = leastSigIndex; i <= this.getMostSigIndex(); i++) {
			result.setTuple(i, this.getTuple(i));
		}
		
		return result;
	}
	
	// Returns boolean for specific bit in storage
	public boolean getBit (int tupIndex, int dim) {
		if (dim > this.order) {
			throw new OrderMismatchException("Unable to access dimension " + dim + ", as this CanAddr has order " + this.order);
		}
		
		// Guard clauses for tuples not explicitly stored
		if (tupIndex < leastSigIndex)			{ return false; }
		if (tupIndex > this.getMostSigIndex())	{ return false; }
		return ((this.tuples[tupIndex - this.leastSigIndex] & (1 << dim)) != 0);
	}
	
	public int getTuple (int tupIndex) {
		// Guard clauses for tuples not explicitly stored
		if (tupIndex < leastSigIndex)			{ return 0; }
		if (tupIndex > this.getMostSigIndex())	{ return 0; }
		
		return (this.tuples[tupIndex - this.leastSigIndex]);
	}
	
	//TODO: Include range checking on both tupIndex and tupVal
	//		no negative indices, values are members of Z((1 << order) - 1), etc.
	// TODO: This method requires a rewrite.
	//		 Example; a = |1,0,0,1,0,0,0|; a.leastSigDigit = 3;
	//				  a.setTuple(3,0); => a.leastSigDigit = 6;
	//		 Basically, I'll need to have a section that cleans the storage
	public void setTuple (int tupIndex, int tupVal) {
		
		int localIndex = tupIndex - this.leastSigIndex;
		
		if (localIndex < 0) {
			// Need to shift digits left (make space for less sig)
			// If the added digit is a 0, we do not need to act, as 0s are implicit
			if (tupVal != 0) {
				int[] tmp = new int[this.tuples.length - localIndex];
				
				tmp[0] = tupVal;
				
				for (int i = 0; i < this.tuples.length; i++) {
					tmp[i - localIndex] = this.tuples[i];
				}
				
				this.tuples = Arrays.copyOf(tmp, tmp.length);
				
				this.leastSigIndex = tupIndex;
			}
		} else if (localIndex > (this.tuples.length - 1)) {
			if (tupVal != 0) { 
				if (this.tuples.length == 1 && this.tuples[0] == 0) {
					// CanAddr currently has no digits
					this.tuples[0] = tupVal;
					this.leastSigIndex = tupIndex;
				} else {
					// Need to add space on the left (more sig)
					int[] tmp = new int[localIndex + 1];
					
					for (int i = 0; i < this.tuples.length; i++) {
						tmp[i] = this.tuples[i];
					}
					
					tmp[localIndex] = tupVal;
					this.tuples = Arrays.copyOf(tmp, tmp.length);
				}
			}
		} else if (localIndex == 0 && tupVal == 0) {
			if (this.tuples[localIndex] != 0) {
				// Need to shift digits to the right by one, as right most digit has been set to 0
				int[] tmp = new int[this.tuples.length - 1];
				
				for (int i = 1; i < this.tuples.length; i++) {
					tmp[i - 1] = this.tuples[i];
				}
				
				this.tuples = Arrays.copyOf(tmp, tmp.length);
			}
			
			this.leastSigIndex++;
		} else {
			this.tuples[localIndex] = tupVal;
		}
	}
	
	@Override
	public String toString () {
		String result = "|";
		for (int i = (this.tuples.length - 1) + this.leastSigIndex; i > 0; i--) {
			result += this.getTuple(i) + ", ";
		}
		return result + this.getTuple(0) + "|";
	}
}
