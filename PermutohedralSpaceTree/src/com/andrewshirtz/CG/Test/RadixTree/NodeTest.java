package com.andrewshirtz.CG.Test.RadixTree;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.andrewshirtz.CG.RadixTree.DataPointEntry;
import com.andrewshirtz.CG.RadixTree.RootNode;
import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.EucVec;

public class NodeTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
	}
	
	@Test
	public void testAdoption() {
		int order = 5;
		
		CanAddr aAddr = new CanAddr(order);
		
		aAddr.setTuple(0, 1);
		aAddr.setTuple(1, 2);
		aAddr.setTuple(2, 3);
		aAddr.setTuple(3, 4);
		aAddr.setTuple(4, 5);
		aAddr.setTuple(5, 6);
		aAddr.setTuple(6, 7);
		aAddr.setTuple(7, 8);
		
		//fail("Not yet implemented");
	}

	/*	//TODO: update test to use facade
	@Test
	public void testInsertion() {
		int order = 5;
		RootNode root = new RootNode(order);
		
		// Test Root: Leaf Creation
		CanAddr a = new CanAddr(order);
		
		a.setTuple(0, 1);
		a.setTuple(1, 2);
		a.setTuple(2, 3);
		a.setTuple(3, 4);
		a.setTuple(4, 5);
		a.setTuple(5, 6);
		
		DataPointEntry aDPE = new DataPointEntry(a);
		root.insertDPE(aDPE);
		
		// Test Leaf: DPE Bundling
		CanAddr b = new CanAddr(order);
		
		b.setTuple(0, 1);
		b.setTuple(1, 2);
		b.setTuple(2, 3);
		b.setTuple(3, 4);
		b.setTuple(4, 5);
		b.setTuple(5, 6);
		
		DataPointEntry bDPE = new DataPointEntry(b);
		root.insertDPE(bDPE);
		
		// Test Leaf-Root Interaction: create Interior Node
		b = new CanAddr(order);
		
		b.setTuple(0, 1);
		b.setTuple(1, 2);
		b.setTuple(2, 3);
		b.setTuple(3, 3);
		b.setTuple(4, 5);
		b.setTuple(5, 6);
		
		bDPE = new DataPointEntry(b);
		root.insertDPE(bDPE);
		
		// Test Root: Create higher Interior Node
		b = new CanAddr(order);
		
		b.setTuple(2, 1);
		b.setTuple(8, 2);
		b.setTuple(9, 3);
		
		bDPE = new DataPointEntry(b);
		
		root.insertDPE(bDPE);
		
		// Test Interior-Interior interaction: create Interior Node
		b = new CanAddr(order);
		
		b.setTuple(6, 5);
		b.setTuple(7, 6);
		
		bDPE = new DataPointEntry(b);
		root.insertDPE(bDPE);
		
		// Test Leaf-Interior interaction: create Interior Node
		b = new CanAddr(order);
		
		b.setTuple(0, 1);
		b.setTuple(1, 5);
		b.setTuple(2, 3);
		b.setTuple(3, 4);
		b.setTuple(4, 5);
		b.setTuple(5, 6);
		
		bDPE = new DataPointEntry(b);
		
		root.insertDPE(bDPE);
	}
	*/
	
	/*
	 * // NOTE: This test was to validate correctness of methods that are now private.
	@Test
	public void smokeTest() {
		int minOrder = 3;
		int maxOrder = 10;
		int numReps = 10000;
		
		double cubeSize = 10000;
		
		Random rand = new Random();
		
		for (int curOrder = minOrder; curOrder <= maxOrder; curOrder++) {
			RootNode root = new RootNode(curOrder);
			
			for (int curRep = 0; curRep < numReps; curRep++) {
				EucVec a = EucVec.zero(curOrder-1);
				
				for (int i = 0; i < curOrder-1; i++) {
					a.setValue(i, (rand.nextDouble() * cubeSize) - (cubeSize/2));
				}
				
				DataPointEntry b = new DataPointEntry(a);
				root.insertDPE(b);
			}
			root.toString();
		}
	}
	*/
}
