package com.andrewshirtz.CG.RadixTree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.andrewshirtz.CG.Util.CanAddr;

public class LeafNode extends Node {
	
	private Node 				parent 	= null;
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
			
			this.parent.adoptNode(intNode);
			intNode.adoptNode(this);
			
			intNode.adoptNode(new LeafNode(opDPE));
		} else {
			this.entries.add(opDPE);
		}
	}
	
	public DataPointEntry getDPE () {
		return this.entries.iterator().next();
	}

	// No-op
	@Override
	public void adoptNode(Node newChild) {}

	// No-op
	@Override
	public void setChild(Node newChild) {}

	@Override
	public void setParent(Node newParent) {
		this.parent = newParent;
	}

	@Override
	public Node getParent() {
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

	@Override
	public int getChildAggLevel() {
		return -1;
	}

}
