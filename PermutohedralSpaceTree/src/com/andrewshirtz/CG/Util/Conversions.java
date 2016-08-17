package com.andrewshirtz.CG.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * This class contains static methods for conversion between data-types
 * as well as any helper methods necessary for converting between said types.
 */

public class Conversions {

	/*
	 * 		HELPER METHODS
	 */
	
	public static Map <Integer, EucVec[]> 	subspaceBasis 	= null;
	public static Map <Integer, EucVec[]> 	simplexVectors 	= null;
	
	//TODO: turn the helper methods private when finished with intermediate testing
	public static void initSubspaceBasis (int order) {
		if (subspaceBasis == null) {
			subspaceBasis = new HashMap <Integer, EucVec[]>();
			createSubspaceBasis(order);
		} else if (!subspaceBasis.containsKey(order)) {
			createSubspaceBasis(order);
		}
	}
	
	/* 
	 * The process required to arrive at having an orthonormal basis for the 
	 * subspace, normal to the 'ones vector', is as follows:
	 * 
	 * 		For the order 'n' (looking for the (n-1)-dimensional subspace)
	 * 		Select (arbitrary but consistent) n-1 vertices from the 'standard (n-1)-simplex'
	 * 		For each vertex, create a corresponding vector which is the offset 
	 * 		of the vertex from the simplex's centroid.
	 * 
	 * 		These n-1 vectors span the subspace we are trying to describe.
	 * 		Running the modified Gram-Schmidt will give n-1 vectors that are orthonormal. 
	 * 		The (n-1) vectors of the corresponding (n-1 dimensional) Euclidean space 
	 * 		can be mapped (directly or with dimensional scaling, etc.) to this basis set.
	 */
	
	public static void createSubspaceBasis (int order) {
		
		EucVec [] basisVectors = new EucVec[order-1];
		for (int i = 0; i < (order-1); i++) {
			basisVectors[i] = EucVec.allDimEqual(order, (-1.0/order));
			basisVectors[i].setValue(i, 1.0 + basisVectors[i].getValue(i));
		}
		
		// Orthonormalize the set with Modified Gram-Schmidt
		basisVectors[0] = EucVec.normalize(basisVectors[0]);
		
		for (int i = 1; i < (order-1); i++) {
			for (int j = 0; j < i; j++) {
				basisVectors[i] = EucVec.add(basisVectors[i], EucVec.scale(-((EucVec.dot(basisVectors[i], basisVectors[j]))/(basisVectors[j].getSqrMagnitude())), basisVectors[j]));
			}
			basisVectors[i] = EucVec.normalize(basisVectors[i]);
		}
		
		// Save the orthonormal basis for future use
		subspaceBasis.put(order, basisVectors);
	}
	
	public static void initSimplexVectors (int order) {
		if (simplexVectors == null) { 
			simplexVectors = new HashMap <Integer, EucVec[]>();
			createSimplexVectors(order);
		} else if (!simplexVectors.containsKey(order)) {
			createSimplexVectors(order);
		}
	}
	
	/*
	 * 	Use the recursive solution detailed on
	 *		https://en.wikipedia.org/wiki/Simplex#Cartesian_coordinates_for_regular_n-dimensional_simplex_in_Rn
	 *	To find the set of vectors for the n-simplex for the given order.
	 *	Store the set of vectors in the simplexVectors map, keyed by the order
	 */
	
	private static void createSimplexVectors (int order) {
		
		double invOrder = -1.0/(order-1);
		
		EucVec [] simplexVecs = new EucVec [order];
		for (int i = 0; i < order; i++) { simplexVecs[i] = EucVec.zero(order-1); }
		
		for (int i = 0; i < order-2; i++) {
			double tmp = 0.0;
			
			// Note: Set the i-th dimension of the current vector so that its magnitude is 1
			simplexVecs[i].setValue(i, Math.sqrt(1.0 - simplexVecs[i].getSqrMagnitude()));
			
			// Note: set the i-th dimension of all further vectors in the set so that
			//		 the dot product between the current vector and those vectors is -(1/order)
			tmp = ((invOrder - EucVec.dot(simplexVecs[i], simplexVecs[i+1])) / simplexVecs[i].getValue(i));
			for (int j = i+1; j < order; j++) {
				simplexVecs[j].setValue(i, tmp);
			}
		}
		
		simplexVecs[order-2].setValue(order-2, Math.sqrt(1.0 - simplexVecs[order-2].getSqrMagnitude()));
		simplexVecs[order-1].setValue(order-2, -Math.sqrt(1.0 - simplexVecs[order-1].getSqrMagnitude()));
		
		// Note: This number was found empirically, but has been tested and holds up to 1000+ dimensions
		//		 It's "a pretty magical number", as one person put it.
		double magic = 1.0/Math.sqrt(1.0+(1.0/(order-1)));
		
		for (int i = 0; i < order; i++) {
			simplexVecs[i] = EucVec.scale(magic, simplexVecs[i]);
		}
		
		// Note: Save the vector set for future use
		simplexVectors.put(order, simplexVecs);
	}
	
	public static EucVec getOnesVector (int order) {
		return EucVec.allDimEqual(order, 1.0);
	}
	
	// Standardize latPoint with the projection equivalence.
	// See the README for more information.
	public static void standardizeLatPoint (long[] latPoint) {
		long min = latPoint[0];
		for (int index = 1; index < latPoint.length; index++) {
			if (min > latPoint[index]) { min = latPoint[index]; }
		}
		
		for (int index = 0; index < latPoint.length; index++) {
			latPoint[index] -= min;
		}
	}
	
	// Checks a latPoint for equivalence to the zero vector after projection
	// Returns true if non-zero, false otherwise
	public static boolean nonZeroLatPoint (long[] latPoint) {
		long val = latPoint[0];
		for (int index = 1; index < latPoint.length; index++) {
			if (val != latPoint[index]) { return true; }
		}
		return false;
	}
	
	/*
	 * 		CONVERION METHODS
	 * 
	 * 	These functions are detailed by Kitto et al (1991).
	 * 	See the README for more information.
	 */
	
	public static int getRadixForOrder (int order) {
		return (1 << order) - 1;
	}
	
	/*
	 * The following two functions are related to the 'B' Matrix from Kitto et. al.
	 * See the README for more information.
	 */
	
	public static long getInverseBMatrixValue (int col, int row, int order) {
		return (1L << ((order - 1) - ((((row - col) % order) + order) % order)));
	}
	
	public static long getInverseBMatrixDivisor (int order) {
		return (1L << order) - 1;
	}
	
	/*
	 * Inverses of the CanAddrToEucVec functions.
	 * 
	 * These function creates a Canonical Address for the lattice point
	 * closest to the given inVec.
	 * 
	 * These functions assume that inVec is in the space to be mapped,
	 * and thus needs to first be projected into the higher dimensioned space.
	 * 
	 * The second function offers option of supplying a localOffset. If included, the
	 * values of this EucVec are overwritten with the offset of inVec to its
	 * closest Lattice Point. The localOffset must have the correct number of dimensions
	 * for the values to be saved.
	 */
	
	public static CanAddr EucVecToCanAddr (EucVec inVec) {
		return EucVecToCanAddr(inVec, null);
	}
	
	public static CanAddr EucVecToCanAddr (EucVec inVec, EucVec localOffset) {
		int order = inVec.getDimension()+1;
		int index = 0;
		int curLevel = 0;
		int curTup = 0;
		int radix = getRadixForOrder(order);
		
		CanAddr result = new CanAddr(order);
		
		initSubspaceBasis(order); 
		
		// Find equivalent (n+1)-dimensional vector in the n-dimensional subspace 
		EucVec mappedVec = EucVec.linearCombination(inVec, subspaceBasis.get(order));
		
		// Find the integer components of inVec
		long [] latPoint = new long [order];
		long [] tmp = new long[order];
		
		// If a EucVec localOffset is included as a parameter, set the values of localOffset to be the remainders
		// The provided localOffset object must have the correct dimension/order
		if (localOffset != null && localOffset.getDimension() != order) {
			localOffset = null;
		}
		
		for (index = 0; index < order; index++) {
			latPoint[index] += (long) Math.round(mappedVec.getValue(index));
			if (localOffset != null && (Math.round(mappedVec.getValue(index))) != mappedVec.getValue(index)) {
				localOffset.setValue(index, (mappedVec.getValue(index) - Math.round(mappedVec.getValue(index))));
			}
		}
		
		standardizeLatPoint(latPoint);
		
		// The interior of this loop is taken from the Kitto et. al. paper.
		// See the README for more information.
		while (nonZeroLatPoint(latPoint)) {
			curTup = 0;
			
			for (index = 0; index < order; index++) {
				curTup += (((latPoint[index]) << index) % radix);
			}
			curTup %= radix;
			
			// Set the 'curLevel' tuple value in 'result' CanAddr to be 'curTup'
			result.setTuple(curLevel, curTup);
			
			// Center the latPoint in its containing 'curLevel' level Aggregate
			for (index = 0; index < order; index++) {
				latPoint[index] -= ((curTup >>> index) & 1);
			}
			
			standardizeLatPoint(latPoint);
			
			// Apply the inverse B matrix
			// TODO: Add in an overflow check and throw exception if appropriate
			for (int row = 0; row < order; row++) {
				int rem = 0;
				int qot = 0;
				for (int col = 0; col < order; col++) {
					qot += (latPoint[col] / radix) * Conversions.getInverseBMatrixValue(col, row, order);
					rem += (latPoint[col] % radix) * Conversions.getInverseBMatrixValue(col, row, order);
				}
				tmp[row] = qot + (rem / radix);
			}
			
			latPoint = Arrays.copyOf(tmp, tmp.length);
			
			standardizeLatPoint(latPoint);
			
			// Increment level counter
			curLevel++;
		}
		
		return result;
	}
	
	/*
	 * 	Inverses of the EucVecToCanAddr functions.
	 * 
	 *	These functions create a EucVec for the given Canonical address, inCan.
	 *	Both of these functions will map the resulting EucVec to the lower 
	 *	dimensioned space before returning.
	 * 
	 *  The second of these functions offers the option of including a localOffset.
	 *  This localOffset is added to the resulting EucVec BEFORE being mapped back
	 *  to the lower dimensioned space.
	 */
	
	public static EucVec CanAddrToEucVec (CanAddr inCan) {
		return CanAddrToEucVec (inCan, null);
	}
	
	public static EucVec CanAddrToEucVec (CanAddr inCan, EucVec localOffset) {
		// OriginCanAddr Guard Clause
		if (inCan.getClass() == OriginCanAddr.class) {
			return EucVec.zero(inCan.getOrder() - 1);
		}
		
		int order = inCan.getOrder();
		int index = 0;
		
		EucVec result = EucVec.zero(order);
		
		long [] latPoint = new long[order];
		
		for (index = inCan.getLeastSigIndex(); index <= inCan.getMostSigIndex(); index++) {
			for (int j = 0; j < order; j++) {
				latPoint[j] = (inCan.getBit(index, j)) ? (1) : (0);
			}
			
			// Apply the 'index'-th power of the B Matrix
			// Due to the structure of the B Matrix,
			// we can avoid using general vector-matrix multiplication
			// TODO: Add in an overflow check and throw exception if appropriate
			for (int k = 0; k < index; k++) {
				long lastElem = latPoint[order - 1];
				for (int i = order - 1; i > 0; i--) {
					latPoint[i] = (latPoint[i] - latPoint[i - 1]) + latPoint[i];
				}
				latPoint[0] = (latPoint[0] - lastElem) + latPoint[0];
			}
			
			// Add in values for the current aggregate center
			for (int i = 0; i < order; i++) {
				result.setValue(i, (result.getValue(i) + latPoint[i]));
			}
		}
		
		// If a localOffset was provided, add that to the 'result' vector
		// The provided localOffset object must have the correct dimension/order
		if (localOffset != null && localOffset.getDimension() == order) {
			result = EucVec.add(localOffset, result);
		}
		
		// Map the resulting vector into the lower dimension
		initSimplexVectors(order);
		return EucVec.linearCombination(result, simplexVectors.get(order));
	}
	
	
}
