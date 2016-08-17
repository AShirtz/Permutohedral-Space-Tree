package com.andrewshirtz.CG.Test.Util;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import com.andrewshirtz.CG.Util.CanAddr;
import com.andrewshirtz.CG.Util.Conversions;
import com.andrewshirtz.CG.Util.EucVec;

public class ConversionsTest {

	// Note: This Test will have to be removed when the initSubspaceBasis method turns private again
	// 		I just wanted a to test the implementation of Gram-Schmidt w/o the EucVec -> LatAddr conversion
	// TODO: Intermediate test, Remove
	@Test
	public void testInitSubspaceBasis() {
		int minOrder = 3;
		int maxOrder = 11;
		
		double epsilon = 1e-10;
		
		for (int curOrder = minOrder; curOrder < maxOrder; curOrder++) {
			Conversions.initSubspaceBasis(curOrder);
			
			assertTrue (Conversions.subspaceBasis.containsKey(curOrder));
			
			EucVec ones = Conversions.getOnesVector(curOrder);
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
			
			assertTrue (epsilon > Math.abs(aMag - t1Mag));
			
			double bMag = b.getMagnitude();
			double t2Mag = t2.getMagnitude();
			
			assertTrue (epsilon > Math.abs(bMag - t2Mag));
			
			double abDot = EucVec.dot(a, b);
			double t1t2Dot = EucVec.dot(t1, t2);
			
			assertTrue (epsilon > Math.abs(abDot - t1t2Dot));
			
			double t1OnesDot = EucVec.dot(t1, ones);
			double t2OnesDot = EucVec.dot(t2, ones);
			
			assertTrue (epsilon > Math.abs(t1OnesDot));
			assertTrue (epsilon > Math.abs(t2OnesDot));
		}
		
	}
	
	// TODO: Intermediate test, Remove
	@Test
	public void testInitSimplexVectors() {
		int minOrder = 3;
		int maxOrder = 11;
		
		double epsilon = 0.00001;
		
		for (int curOrder = minOrder; curOrder < maxOrder; curOrder++) {
			Conversions.initSimplexVectors(curOrder);
			
			assertTrue (Conversions.simplexVectors.containsKey(curOrder));
			
			EucVec ones = Conversions.getOnesVector(curOrder);
			EucVec a = EucVec.zero(curOrder);
			EucVec b = EucVec.zero(curOrder);
			
			Random rand = new Random();
			
			// Making a random Lattice Point as a vector
			for (int i = 0; i < curOrder; i++) {
				a.setValue(i, rand.nextInt(100) - 50);
				b.setValue(i, rand.nextInt(100) - 50);
			}
			
			double min = a.getValue(0);
			
			for (int i = 1; i < curOrder; i++) {
				if (a.getValue(i) < min) { min = (int) a.getValue(i); }
			}
			
			for (int i = 0; i < curOrder; i++) {
				a.setValue(i, a.getValue(i) - min);
			}
			
			min = b.getValue(0);
			
			for (int i = 1; i < curOrder; i++) {
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
			
			assertTrue (epsilon > Math.abs(aOnesDot));
			assertTrue (epsilon > Math.abs(bOnesDot));
			
			EucVec t1 = EucVec.linearCombination(a, Conversions.simplexVectors.get(curOrder));
			EucVec t2 = EucVec.linearCombination(b, Conversions.simplexVectors.get(curOrder));
			
			double aMag = a.getMagnitude();
			double t1Mag = t1.getMagnitude();
			
			assertTrue (epsilon > Math.abs(aMag - t1Mag));
			
			double bMag = b.getMagnitude();
			double t2Mag = t2.getMagnitude();
			
			assertTrue (epsilon > Math.abs(bMag - t2Mag));
			
			double abDot = EucVec.dot(a, b);
			double t1t2Dot = EucVec.dot(t1, t2);
			
			assertTrue (epsilon > Math.abs(t1t2Dot - abDot));
		}
	}

	// TODO: Intermediate test, Remove when finished
	@Test
	public void testRoundTrip1 () {
		int minOrder = 3;
		int maxOrder = 11;
		int numIters = 1000;
		
		double epsilon = 1e-10;
		
		for (int rep = 0; rep < numIters; rep++) {
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
				
				assertTrue (a.roughEquals(b));
			}
		}
	}
	
	@Test
	public void testFullRoundTrip () {
		int minOrder = 3;
		int maxOrder = 11;
		int numIters = 5000;
		
		float cubeRadius = 10000f;
		float originOffset = 0;
		
		Random rand = new Random();
		
		for (int curOrder = minOrder; curOrder <= maxOrder; curOrder++) {
			for (int rep = 0; rep < numIters; rep++) {
				EucVec a = EucVec.zero(curOrder-1);
				
				for (int i = 0; i < curOrder-1; i++) {
					a.setValue(i, ((rand.nextDouble()*cubeRadius) - (cubeRadius/2f)) + originOffset);
				}
				
				EucVec lOff = EucVec.zero(curOrder);
				
				CanAddr b = Conversions.EucVecToCanAddr(a, lOff);
				
				EucVec out = Conversions.CanAddrToEucVec(b, lOff);
				
				assertTrue(a.roughEquals(out));
			}
		}
	}
	
	@Test
	public void test () {
		EucVec a = new EucVec(646.1, 228.146, 55.7715);
		EucVec b = new EucVec(648.011, 221.683, 56.2091);
		EucVec c = new EucVec(654.935, 215.393, 54.0165);
		
		CanAddr aAddr = Conversions.EucVecToCanAddr(a);
		CanAddr bAddr = Conversions.EucVecToCanAddr(b);
		CanAddr cAddr = Conversions.EucVecToCanAddr(c);
		
		System.out.println("");
	}
}
