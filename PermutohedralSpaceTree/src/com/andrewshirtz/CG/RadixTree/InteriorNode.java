package com.andrewshirtz.CG.RadixTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.OriginCanAddr;

public class InteriorNode implements RadixTreeNode {
	
	private Map<Integer, RadixTreeNode> children 		= null;
	private RadixTreeNode 				parent 			= null;
	private CanAddr 					cAddr 			= null;
	
	private int							childAggLevel 	= -1;
	
	public InteriorNode (CanAddr cAddr, int childAggLevel) {
		this.cAddr 			= cAddr;
		this.childAggLevel 	= childAggLevel;
	}

	@Override
	public void insertDPE(DataPointEntry opDPE) {
		int mostSigDiffIndex = CanAddr.getMostSigDiffIndex(opDPE.getCanAddr(), this.getCanAddr(), this.getMaxAggLevel(), this.getMinAggLevel());
		
		if (mostSigDiffIndex > this.getChildAggLevel()) {
			InteriorNode intNode = new InteriorNode(this.getCanAddr().truncate(mostSigDiffIndex), mostSigDiffIndex);
			
			this.parent.AdoptChild(intNode);
			intNode.AdoptChild(this);
			
			intNode.AdoptChild(new LeafNode(opDPE));
		} else {
			int childValue = opDPE.getCanAddr().getTuple(this.getChildAggLevel());
			
			if (this.children.containsKey(childValue)) {
				this.children.get(childValue).insertDPE(opDPE);
			} else {
				this.AdoptChild(new LeafNode(opDPE));
			}
		}
	}

	@Override
	public void notifyOfChildSplit(CanAddr truncatedAddr, int sigDiff) {
		int childIndex = truncatedAddr.getTuple(this.getChildAggLevel());
		
		// Create a new InteriorNode
		RadixTreeNode newChild = new InteriorNode(truncatedAddr, sigDiff);

		// Have the new child adopt the old child
		newChild.AdoptChild(this.children.get(childIndex));
		this.AdoptChild(newChild);
	}

	@Override
	public void AdoptChild(RadixTreeNode newChild) {
		// TODO: Ensure compatibility
		newChild.setParent(this);
		this.setChild(newChild);
	}

	@Override
	public void setChild(RadixTreeNode newChild) {
		if (this.children == null) {
			this.children = new HashMap<Integer, RadixTreeNode> ();
		}
		this.children.put(newChild.getCanAddr().getTuple(this.getChildAggLevel()), newChild);
	}

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
		return this.cAddr.getOrder();
	}

	// This is the significance of the least significant digit represented by this node.
	// This will always be one greater than the significance by which the children are indexed.
	@Override
	public int getMinAggLevel() {
		return this.getChildAggLevel() + 1;
	}

	// This is the significance of the most significant digit represented by this node.
	// This is the significance by which this node is indexed in its parent's children.
	@Override
	public int getMaxAggLevel() {
		return this.parent.getChildAggLevel();
	}

	@Override
	public CanAddr getCanAddr() {
		return this.cAddr;
	}

	@Override
	public Collection<RadixTreeNode> getChildren() {
		return this.children.values();
	}

	@Override
	public int getChildAggLevel() {
		return this.childAggLevel;
	}

}
