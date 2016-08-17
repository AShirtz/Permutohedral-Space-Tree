package com.andrewshirtz.CG.RadixTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.OriginCanAddr;

public class LeafNode implements RadixTreeNode {
	
	private RadixTreeNode 			parent 	= null;
	public Set<DataPointEntry> 	entries = null;
	
	public LeafNode (DataPointEntry initialEntry) {
		this.entries = new HashSet<DataPointEntry>();
		this.entries.add(initialEntry);
	}

	@Override
	public void insertDPE(DataPointEntry opDPE) {
		int mostSigDiffIndex = CanAddr.getMostSigDiffIndex(this.getCanAddr(), opDPE.getCanAddr(), this.getMaxAggLevel(), this.getMinAggLevel());
		
		if (mostSigDiffIndex >= 0) {
			InteriorNode intNode = new InteriorNode(this.getCanAddr().truncate(mostSigDiffIndex), mostSigDiffIndex);
			
			this.parent.AdoptChild(intNode);
			intNode.AdoptChild(this);
			
			intNode.AdoptChild(new LeafNode(opDPE));
		} else {
			this.entries.add(opDPE);
		}
	}
	
	public DataPointEntry getDPE () {
		return this.entries.iterator().next();
	}

	// No-op
	@Override
	public void notifyOfChildSplit(CanAddr truncatedAddr, int sigDiff) {}

	// No-op
	@Override
	public void AdoptChild(RadixTreeNode newChild) {}

	// No-op
	@Override
	public void setChild(RadixTreeNode newChild) {}

	@Override
	public void setParent(RadixTreeNode newParent) {
		this.parent = newParent;
	}

	@Override
	public RadixTreeNode getParent() {
		return this.parent;
	}

	@Override
	public int getOrder() {
		return this.entries.iterator().next().getCanAddr().getOrder();
	}

	// This is the significance of the least significant digit represented by this node.
	@Override
	public int getMinAggLevel() {
		return 0;
	}
	
	// This is the significance of the most significant digit represented by this node.
	// This is the significance by which this node is indexed in its parent's children.
	@Override
	public int getMaxAggLevel() {
		return this.parent.getChildAggLevel();
	}

	@Override
	public CanAddr getCanAddr() {
		return this.entries.iterator().next().getCanAddr();
	}

	// TODO: remove after testing
	@Override
	public Collection<RadixTreeNode> getChildren() {
		return null;
	}

	@Override
	public int getChildAggLevel() {
		return -1;
	}

}
