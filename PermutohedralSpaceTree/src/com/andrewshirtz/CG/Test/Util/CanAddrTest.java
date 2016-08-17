package com.andrewshirtz.CG.Test.Util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.OriginCanAddr;

public class CanAddrTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		//fail("Not yet implemented");
	}

	@Test
	public void getSetTupleTest() {
		int order = 5;
		
		//Test trailing 0s
		CanAddr a = new CanAddr(order);
		a.setTuple(0, 0);
		a.setTuple(1, 0);
		a.setTuple(2, 3);
		a.setTuple(6, 2);
		
		assertTrue(a.getLeastSigIndex() == 2);
		assertTrue(a.getTuple(0) == 0);
		
		a.setTuple(4, 4);
		a.setTuple(0, 1);
		
		assertTrue(a.getTuple(0) == 1);
		assertTrue(a.getMostSigIndex() == 6);
		
		// Test general tuple get/set
		a = new CanAddr(order);
		a.setTuple(0, 1);
		a.setTuple(1, 2);
		a.setTuple(2, 3);
		a.setTuple(3, 0);
		a.setTuple(4, 5);
		
		assertTrue(a.getTuple(4) == 5);
		assertTrue(a.getTuple(10) == 0);
		
		// Test digit shifting for trailing 0s
		a = new CanAddr(order);
		
		a.setTuple(3, 1);
		a.setTuple(4, 2);
		a.setTuple(5, 3);
		a.setTuple(6, 4);
		
		assertTrue(a.getLeastSigIndex() == 3);
		
		a.setTuple(3, 0);
		
		assertTrue(a.getLeastSigIndex() == 4);
		
		// Test 'put 0 greater than most sig digit'
		a = new CanAddr(order);
		a.setTuple(3, 1);
		a.setTuple(5, 0);
		a.setTuple(6, 1);
		a.setTuple(7, 2);
		a.setTuple(8, 3);
		
		a.setTuple(8, 0);
		
	}
	
	@Test
	public void truncationTest() {
		int order = 5;
		
		CanAddr a = new CanAddr(order);
		
		a.setTuple(0, 1);
		a.setTuple(1, 2);
		a.setTuple(2, 3);
		a.setTuple(3, 4);
		a.setTuple(4, 5);
		a.setTuple(5, 6);
		a.setTuple(6, 7);
		a.setTuple(7, 8);
		a.setTuple(8, 9);
		a.setTuple(9, 10);
		
		CanAddr b = a.truncate(3);
		
		assertTrue(b.getTuple(3) == 4);
		assertTrue(b.getTuple(2) == 0);
	}
	
	@Test
	public void inconsistencyTest() {
		int order = 5;
		
		CanAddr a = new CanAddr(order);
		
		a.setTuple(0, 1);
		a.setTuple(1, 2);
		a.setTuple(2, 3);
		a.setTuple(3, 4);
		a.setTuple(4, 5);
		a.setTuple(5, 6);
		a.setTuple(6, 7);
		a.setTuple(7, 8);
		a.setTuple(8, 9);
		a.setTuple(9, 10);
		
		CanAddr b = new CanAddr(order);
		
		b.setTuple(0, 1);
		b.setTuple(1, 2);
		b.setTuple(2, 3);
		b.setTuple(3, 4);
		b.setTuple(4, 5);
		b.setTuple(5, 6);
		b.setTuple(6, 7);
		b.setTuple(7, 8);
		b.setTuple(8, 9);
		b.setTuple(9, 10);
		
		assertTrue(CanAddr.getMostSigDiffIndex(a, b) == -1);
		
		b.setTuple(2, 0);
		
		assertTrue(CanAddr.getMostSigDiffIndex(a, b) == 2);
		
		a.setTuple(7, 0);
		
		assertTrue(CanAddr.getMostSigDiffIndex(a, b) == 7);
	}
	
	@Test
	public void testOriginCanAddr() {
		
		CanAddr origin = new OriginCanAddr(5);
		
		assertTrue(origin.getTuple(10) == 0);
		assertTrue(!origin.getBit(20, 101));
		assertTrue(origin.getMostSigIndex() == 0);
		assertTrue(origin.getLeastSigIndex() == 0);
		
		int order = 5;
		
		CanAddr a = new CanAddr(order);
		
		a.setTuple(0, 1);
		a.setTuple(1, 2);
		a.setTuple(2, 3);
		a.setTuple(3, 4);
		a.setTuple(4, 5);
		a.setTuple(5, 6);
		a.setTuple(6, 7);
		a.setTuple(7, 8);
		a.setTuple(8, 9);
		a.setTuple(9, 10);
		
		assertTrue(CanAddr.getMostSigDiffIndex(a, new OriginCanAddr(order)) == a.getMostSigIndex());
		
		//fail("Not yet implemented");
	}
}
