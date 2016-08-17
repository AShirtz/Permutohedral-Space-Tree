package com.andrewshirtz.CG.RadixTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.OriginCanAddr;

public class RootNode implements RadixTreeNode {
	
	private Map<Integer, RadixTreeNode> 	children = null;
	
	private int order 			= -1;
	private int childAggLevel 	= -1;		// This is the digit significance by which the children are indexed.
	
	public int population = 0;
	
	public RootNode (int order) {
		this.order = order;
	}
	
	
	// TODO: Check order of opDPE
	@Override
	public void insertDPE (DataPointEntry opDPE) {
		if (this.children == null || this.children.isEmpty()) {
			this.childAggLevel = opDPE.getCanAddr().getMostSigIndex();
			this.AdoptChild(new LeafNode(opDPE));
		} else {
			int opMSigDigIndex = opDPE.getCanAddr().getMostSigIndex();
			
			if (opMSigDigIndex > this.getChildAggLevel()) {
				
				if (this.children.size() == 1) {
					RadixTreeNode child = this.children.values().iterator().next();
					int childValue = child.getCanAddr().getTuple(this.getChildAggLevel());
					this.children.remove(childValue);
					
					this.childAggLevel = opMSigDigIndex;
					
					this.AdoptChild(child);
				} else {
					InteriorNode intNode = new InteriorNode(new OriginCanAddr(this.order), this.getChildAggLevel());
					
					for (RadixTreeNode child : this.children.values()) {
						intNode.AdoptChild(child);
					}
					
					this.children = new HashMap<Integer, RadixTreeNode>();
					
					this.childAggLevel = opMSigDigIndex;
					
					this.AdoptChild(intNode);
				}
				
				this.AdoptChild(new LeafNode(opDPE));
			} else {
				int opChildValue = opDPE.getCanAddr().getTuple(this.getChildAggLevel());
				
				if (this.children.containsKey(opChildValue)) {
					this.children.get(opChildValue).insertDPE(opDPE);
				} else {
					this.AdoptChild(new LeafNode(opDPE));
				}
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
		// TODO: Ensure Compatibility 
		newChild.setParent(this);
		this.setChild(newChild);
	}

	@Override
	public void setChild(RadixTreeNode newChild) {
		if (this.children == null) {
			this.children = new HashMap<Integer, RadixTreeNode> ();
		}
		int childIndex = newChild.getCanAddr().getTuple(this.getChildAggLevel());
		this.children.put(childIndex, newChild);
	}

	// No-op
	@Override
	public void setParent(RadixTreeNode newParent) {}

	@Override
	public RadixTreeNode getParent() {
		return this;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	// This is the significance of the least significant digit represented by this node.
	// This will always be one greater than the significance by which the children are indexed.
	@Override
	public int getMinAggLevel() {
		return this.childAggLevel + 1;
	}

	// As this is the RootNode, this question does not have an easy answer.
	@Override
	public int getMaxAggLevel() {
		return this.getMinAggLevel();
	}

	@Override
	public CanAddr getCanAddr() {
		return new OriginCanAddr(this.getOrder());
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
