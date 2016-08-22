package com.andrewshirtz.CG.RadixTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.andrewshirtz.CG.Util.CanAddr;

public class RootNode extends Node {
	
	private Map<Integer, Node> 	children = null;

	private CanAddr origin;
	private int childAggLevel 	= -1;		// This is the digit significance by which the children are indexed.
	
	public int population = 0;
	
	protected RootNode (int order) {
		this.origin = new CanAddr(order);
	}
	
	
	// TODO: Check order of opDPE
	@Override
	public void insertDPE (DataPointEntry opDPE) {
		if (this.children == null || this.children.isEmpty()) {
			this.childAggLevel = opDPE.getCanAddr().getMostSigIndex();
			this.adoptNode(new LeafNode(opDPE));
		} else {
			int opMSigDigIndex = opDPE.getCanAddr().getMostSigIndex();
			
			if (opMSigDigIndex > this.getChildAggLevel()) {
				
				if (this.children.size() == 1) {
					Node child = this.children.values().iterator().next();
					int childValue = child.getCanAddr().getTuple(this.getChildAggLevel());
					this.children.remove(childValue);
					
					this.childAggLevel = opMSigDigIndex;
					
					this.adoptNode(child);
				} else {
					InteriorNode intNode = new InteriorNode(this.getCanAddr().truncate(this.getChildAggLevel()), this.getChildAggLevel());
					
					for (Node child : this.children.values()) {
						intNode.adoptNode(child);
					}
					
					this.children = new HashMap<Integer, Node>();
					
					this.childAggLevel = opMSigDigIndex;
					
					this.adoptNode(intNode);
				}
				
				this.adoptNode(new LeafNode(opDPE));
			} else {
				int opChildValue = opDPE.getCanAddr().getTuple(this.getChildAggLevel());
				
				if (this.children.containsKey(opChildValue)) {
					this.children.get(opChildValue).insertDPE(opDPE);
				} else {
					this.adoptNode(new LeafNode(opDPE));
				}
			}
		}
	}

	@Override
	protected void adoptNode(Node newChild) {
		// TODO: Ensure Compatibility 
		newChild.setParent(this);
		this.setChild(newChild);
	}

	@Override
	protected void setChild(Node newChild) {
		if (this.children == null) {
			this.children = new HashMap<Integer, Node> ();
		}
		int childIndex = newChild.getCanAddr().getTuple(this.getChildAggLevel());
		this.children.put(childIndex, newChild);
	}

	// No-op
	@Override
	protected void setParent(Node newParent) {}

	@Override
	protected Node getParent() {
		return this;
	}

	@Override
	protected int getOrder() {
		return this.origin.getOrder();
	}

	// This is the significance of the least significant digit represented by this node.
	// This will always be one greater than the significance by which the children are indexed.
	@Override
	protected int getMinAggLevel() {
		return this.childAggLevel + 1;
	}

	// As this is the RootNode, this question does not have an easy answer.
	@Override
	protected int getMaxAggLevel() {
		return this.getMinAggLevel();
	}

	@Override
	protected CanAddr getCanAddr() {
		return this.origin;
	}

	@Override
	protected int getChildAggLevel() {
		return this.childAggLevel;
	}

}
