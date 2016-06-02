package com.andrewshirtz.CG.Test.Util;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.andrewshirtz.CG.Util.Conversions;
import com.andrewshirtz.CG.Util.EucVec;

public class ConversionsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEucVecToLatAddr() {
		fail("Not yet implemented");
	}

	@Test
	public void testLatAddrToEucVec() {
		fail("Not yet implemented");
	}

	// Note: This Test will have to be removed when the initSubspaceBasis method turns private again
	// 		I just wanted a to test the implementation of Gram-Schmidt w/o the EucVec -> LatAddr conversion
	// TODO: Intermediate test, Remove
	@Test
	public void testInitSubspaceBasis() {
		int minOrder = 3;
		int maxOrder = 10;
		
		double epsilon = 0.00000000001;
		
		for (int curOrder = minOrder; curOrder < maxOrder; curOrder++) {
			Conversions.initSubspaceBasis(curOrder);
			
			assertTrue (Conversions.subspaceBasis.containsKey(curOrder));
			
			EucVec ones = EucVec.allDimEqual(curOrder, 1.0);
			EucVec a = EucVec.zero(curOrder-1);
			EucVec b = EucVec.zero(curOrder-1);
			
			Random rand = new Random();
			
			for (int i = 0; i < curOrder-1; i++) {
				a.setValue(i, (rand.nextDouble()*100) - 50);
				b.setValue(i, (rand.nextDouble()*100) - 50);
			}
			
			EucVec t1 = EucVec.linearCombination(a, Conversions.subspaceBasis.get(curOrder));
			EucVec t2 = EucVec.linearCombination(b, Conversions.subspaceBasis.get(curOrder));
			
			double aMag = a.getMagnitude();
			double t1Mag = t1.getMagnitude();
			
			assertTrue (epsilon > (aMag - t1Mag));
			
			double bMag = b.getMagnitude();
			double t2Mag = t2.getMagnitude();
			
			assertTrue (epsilon > (bMag - t2Mag));
			
			double abDot = EucVec.dot(a, b);
			double t1t2Dot = EucVec.dot(t1, t2);
			
			assertTrue (epsilon > (abDot - t1t2Dot));
			
			double t1OnesDot = EucVec.dot(t1, ones);
			double t2OnesDot = EucVec.dot(t2, ones);
			
			assertTrue (epsilon > t1OnesDot);
			assertTrue (epsilon > t2OnesDot);
		}
		
	}
	
	// TODO: Intermediate test, Remove
	@Test
	public void testInitSimplexVectors() {
		int minOrder = 3;
		int maxOrder = 150;
		
		double epsilon = 0.00001;
		
		for (int curOrder = minOrder; curOrder < maxOrder; curOrder++) {
			Conversions.initSimplexVectors(curOrder);
			
			assertTrue (Conversions.simplexVectors.containsKey(curOrder));
			
			EucVec ones = EucVec.allDimEqual(curOrder, 1.0);
			EucVec a = EucVec.zero(curOrder);
			EucVec b = EucVec.zero(curOrder);
			
			Random rand = new Random();
			
			// Making a random Lattice Address as a vector
			for (int i = 0; i < curOrder; i++) {
				a.setValue(i, rand.nextInt(100) - 50);
				b.setValue(i, rand.nextInt(100) - 50);
			}
			
			int min = 100;
			
			for (int i = 0; i < curOrder; i++) {
				if (a.getValue(i) < min) { min = (int) a.getValue(i); }
			}
			
			for (int i = 0; i < curOrder; i++) {
				a.setValue(i, a.getValue(i) - min);
			}
			
			min = 100;
			
			for (int i = 0; i < curOrder; i++) {
				if (b.getValue(i) < min) { min = (int) b.getValue(i); }
			}
			
			for (int i = 0; i < curOrder; i++) {
				b.setValue(i, b.getValue(i) - min);
			}
			
			// Note: This is a projection down to the subspace orthogonal to the ones vector
			a = EucVec.add(a, EucVec.scale(-1.0*(EucVec.dot(a, ones)/ones.getSqrMagnitude()),ones));
			b = EucVec.add(b, EucVec.scale(-1.0*(EucVec.dot(b, ones)/ones.getSqrMagnitude()),ones));
			
			double aOnesDot = EucVec.dot(a, ones);
			double bOnesDot = EucVec.dot(b, ones);
			
			assertTrue (epsilon > aOnesDot);
			assertTrue (epsilon > bOnesDot);
			
			EucVec t1 = EucVec.linearCombination(a, Conversions.simplexVectors.get(curOrder));
			EucVec t2 = EucVec.linearCombination(b, Conversions.simplexVectors.get(curOrder));
			
			double aMag = a.getMagnitude();
			double t1Mag = t1.getMagnitude();
			
			assertTrue (epsilon > (aMag - t1Mag));
			
			double bMag = b.getMagnitude();
			double t2Mag = t2.getMagnitude();
			
			assertTrue (epsilon > (bMag - t2Mag));
			
			double abDot = EucVec.dot(a, b);
			double t1t2Dot = EucVec.dot(t1, t2);
			
			assertTrue (epsilon > (t1t2Dot - abDot));
		}
	}

	// TODO: Intermediate test, Remove
	@Test
	public void testRoundTrip1 () {
		int minOrder = 3;
		int maxOrder = 15;
		
		double epsilon = 0.00001;
		
		for (int curOrder = minOrder; curOrder < maxOrder; curOrder++) {
			Conversions.initSimplexVectors(curOrder);
			Conversions.initSubspaceBasis(curOrder);
			
			assertTrue (Conversions.simplexVectors.containsKey(curOrder));
			assertTrue (Conversions.subspaceBasis.containsKey(curOrder));
			
			EucVec ones = EucVec.allDimEqual(curOrder, 1.0);
			EucVec a = EucVec.zero(curOrder-1);
			
			Random rand = new Random();
			
			for (int i = 0; i < curOrder-1; i++) {
				a.setValue(i, (rand.nextDouble()*100) - 50);
			}
			
			EucVec t1 = EucVec.linearCombination(a, Conversions.subspaceBasis.get(curOrder));
			
			EucVec b = EucVec.linearCombination(t1, Conversions.simplexVectors.get(curOrder));
			
			double magic = 1.0+(1.0/(curOrder-1));
			//b = EucVec.scale(1.0/(Math.sqrt(magic)), b);
			
			assertTrue (a.roughEquals(b));
			//System.out.println(EucVec.dist(a, b));
		}
	}
}
