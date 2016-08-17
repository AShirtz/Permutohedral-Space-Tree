package com.andrewshirtz.CG.RadixTree;

import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.Conversions;
import com.andrewshirtz.CG.Util.EucVec;

public class DataPointEntry {

	private CanAddr 	cAddr		= null;
	private EucVec 		localOffset	= null;
	private EucVec		originalVec	= null;
	
	public DataPointEntry (CanAddr cAddr) {
		this.cAddr = cAddr;
	}
	
	public DataPointEntry (EucVec origVec) {
		this.originalVec = origVec;
		this.localOffset = EucVec.zero(this.originalVec.getDimension() + 1);
		this.cAddr = Conversions.EucVecToCanAddr(origVec, this.localOffset);
	}
	
	public CanAddr getCanAddr () {
		return this.cAddr;
	}
	
	@Override
	public String toString() {
		return this.cAddr.toString();
	}
	
	// TODO: Temporary Method for testing
	public EucVec getOriginal () {
		return this.originalVec;
	}
}
