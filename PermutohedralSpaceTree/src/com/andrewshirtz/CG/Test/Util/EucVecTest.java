package com.andrewshirtz.CG.Test.Util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.andrewshirtz.CG.Util.EucVec;

public class EucVecTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAdd() {
		// TODO: Add test case for dimension mismatch exception
		EucVec a = new EucVec(4.0, 3.0, 2.0);
		EucVec b = new EucVec(1.5, 5.0, 7.0);
		EucVec c = new EucVec(0.8, 2.7, 1.1);
		
		EucVec t = EucVec.add(a, b, c);
		EucVec s = new EucVec(4.0+1.5+0.8, 3.0+5.0+2.7, 2.0+7.0+1.1);
		
		assertEquals(t,s);
	}

	@Test
	public void testScaleDoubleEucVec() {
		EucVec a1 = new EucVec(4.0, 3.0, 2.0);
		double m1 = a1.getMagnitude();
		
		EucVec a2 = EucVec.scale(2.0, a1);
		double m2 = a2.getMagnitude();
		
		assertTrue (m1*2.0 == m2);
		
		EucVec a3 = EucVec.scale(0.5, a2);
		assertEquals(a1,a3);
	}

	@Test
	public void testScaleDoubleEucVecArray() {
		EucVec a = new EucVec(4.0, 3.0, 2.0);
		EucVec b = new EucVec(1.5, 5.0, 7.0);
		EucVec c = new EucVec(0.8, 2.7, 1.1);
		
		double ma = a.getMagnitude();
		double mb = b.getMagnitude();
		double mc = c.getMagnitude();
		
		EucVec [] ps = EucVec.scale(2.0, a, b, c);
		
		assertTrue (ma*2.0 == ps[0].getMagnitude());
		assertTrue (mb*2.0 == ps[1].getMagnitude());
		assertTrue (mc*2.0 == ps[2].getMagnitude());
	}

	@Test
	public void testDot() {
		EucVec a = new EucVec(4.0, 3.0, 2.0);
		EucVec b = new EucVec(1.5, 5.0, 7.0);
		
		assertTrue ((4.0*1.5 + 3.0*5.0 + 2.0*7.0) == EucVec.dot(a, b));
	}

	@Test
	public void testHadamard() {
		// TODO: Add test case for dimension mismatch exception
		
		EucVec a = new EucVec(4.0, 3.0, 2.0);
		EucVec b = new EucVec(1.5, 5.0, 7.0);
		EucVec c = new EucVec(0.8, 2.7, 1.1);
		
		EucVec res = EucVec.hadamard(a, b, c);
		EucVec base = new EucVec (4.0*1.5*0.8, 3.0*5.0*2.7, 2.0*7.0*1.1);
		
		assertEquals(res, base);
	}
	
	@Test
	public void testDist () {
		EucVec a = new EucVec(4.0, 3.0, 2.0);
		
		assertTrue (0.0 == EucVec.dist(a, a));
		
		EucVec b = EucVec.add(a, new EucVec(1.0, 0.0,0.0));
		
		assertTrue (1.0 == EucVec.dist(a, b));
	}

	@Test
	public void testZero() {
		EucVec v = EucVec.zero(3);
		assertEquals(3, v.getDimension());
		assertTrue(0.0 == v.getMagnitude());
		assertTrue(0.0 == v.getSqrMagnitude());
	}

	@Test
	public void testEucVec() {
		EucVec v = new EucVec(3.0, 4.0);
		
		assertTrue (25.0 == v.getSqrMagnitude());
	}

	@Test
	public void testGetDimension() {
		EucVec a = new EucVec(4.0, 3.0, 2.0);
		assertEquals (3, a.getDimension());
	}

	@Test
	public void testGetCopy() {
		EucVec v = new EucVec(3.0, 4.0);
		EucVec u = new EucVec(v);
		
		assertTrue (25.0 == v.getSqrMagnitude());
		assertTrue (5.0 == u.getMagnitude());
		assertEquals (u,v);
	}

	@Test
	public void testGetSqrMagnitude() {
		EucVec v = new EucVec(3.0, 4.0);
		assertTrue (25.0 == v.getSqrMagnitude());
	}

	@Test
	public void testGetMagnitude() {
		EucVec v = new EucVec(3.0, 4.0);
		assertTrue (5.0 == v.getMagnitude());
	}
	
	@Test
	public void testEquals() {
		EucVec v = new EucVec(3.0, 4.0);
		EucVec u = new EucVec(v);
		
		assertEquals (u,v);
		
		EucVec z = EucVec.zero(3);
		EucVec a = new EucVec (3.0, 4.0, 0.0);
		
		assertNotEquals (u, z);
		assertNotEquals (u, a);
	}
	
	@Test
	public void testHashCode () {
		
		EucVec v = new EucVec(3.0, 4.0);
		EucVec u = new EucVec(v);
		
		assertEquals (u.hashCode(),v.hashCode());
		
		EucVec z = EucVec.zero(3);
		EucVec a = new EucVec (3.0, 4.0, 0.0);
		
		assertNotEquals (u.hashCode(), z.hashCode());
		assertNotEquals (u.hashCode(), a.hashCode());
	}
}
