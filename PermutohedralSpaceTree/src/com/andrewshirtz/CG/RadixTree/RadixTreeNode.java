package com.andrewshirtz.CG.RadixTree;

import java.util.Collection;

import com.andrewshirtz.CG.Util.CanAddr;

public interface RadixTreeNode {

	public void insertDPE	(DataPointEntry opDPE);
	
	public void notifyOfChildSplit	(CanAddr truncatedAddr, int sigDiff);
	
	public void AdoptChild 	(RadixTreeNode newChild);
	public void setChild 	(RadixTreeNode newChild);
	public void setParent 	(RadixTreeNode newParent);
	
	public RadixTreeNode getParent ();
	
	// TODO: Temporary method, remove after testing
	public Collection<RadixTreeNode> getChildren ();
	
	public int getOrder ();
	
	// This is the significance of the least significant digit represented by this node.
	// This will always be one greater than getChildAggLevel.
	public int getMinAggLevel ();
	
	// This is the significance of the most significant digit represented by this node.
	// This is the significance by which this node is indexed in its parent's children.
	public int getMaxAggLevel ();
	
	// This is the significance of digit by which a node's children are indexed.
	// This is always one less than getMinAggLevel.
	public int getChildAggLevel();
	
	public CanAddr getCanAddr ();
}
