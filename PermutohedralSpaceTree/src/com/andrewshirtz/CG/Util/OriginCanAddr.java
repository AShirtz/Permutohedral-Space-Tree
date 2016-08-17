package com.andrewshirtz.CG.Util;

public class OriginCanAddr extends CanAddr {
	
	/*
	 * 		STATIC MEMBERS
	 */
	
	
	/*
	 * 		INSTANCE MEMBERS
	 */
	
	private int mostSigIndex 	= -1;
	
	public OriginCanAddr (int order) {
		this.order	= order;
	}

	@Override
	public int getMostSigIndex () {
		return this.mostSigIndex;
	}
	@Override
	public int getLeastSigIndex () {
		return this.leastSigIndex;
	}
	
	@Override
	public CanAddr truncate (int leastSigIndex) {
		return new OriginCanAddr(this.order);
	}
	
	@Override
	public boolean getBit (int tupIndex, int dim) {
		return false;
	}
	
	@Override
	public int getTuple (int tupIndex) {
		return 0;
	}
	
	@Override
	public void setTuple (int tupIndex, int tupVal) {
		throw new ArithmeticException ("Cannot set value on read-only type: OriginCanAddr.");
	}
	
	@Override
	public String toString () {
		return "...|0|...";
	}
}
