package com.andrewshirtz.CG.Util;

import java.util.HashMap;
import java.util.Map;

// TODO: Better description

/*
 * This class should be nothing but static methods for conversion between data-types
 * as well as any members necessary for converting between said types.
 * 
 * As an example: EucVec -> LatAddr conversions require the Euclidean Basis vectors
 * to be mapped to orthonormal vectors in the Permutohedral hyperplane. These hyperplane
 * basis vectors can be reused for all conversions for a specific dimension/order.
 */

public class Conversions {

	/*
	 * 		EUCVEC <-> LATADDR MEMBERS
	 * 
	 * 	This section has functions for converting between EucVecs and LatAddrs.
	 * 
	 * 	The EucVec -> LatAddr function requires an orthonormal basis in the lattice hyperplane.
	 * 	These orthonormal basis are stored in a map by dimension/order.
	 * 
	 * 	The LatAddr -> EucVec function requires the set of vectors for an n-simplex in R^n
	 * 	with centroid at the origin. These sets are stored in a map by dimension/order.
	 */
	
	public static Map <Integer, EucVec[]> subspaceBasis = null;
	public static Map <Integer, EucVec[]> simplexVectors = null;
	
	public static LatAddr EucVecToLatAddr (EucVec inVec) {
		//TODO:	As described in the README, a EucVec of dimension 'n' 
		//		is represented as a LatAddr of order 'n+1'
		initSubspaceBasis(inVec.getDimension()+1); 
		
		return null;
	}
	
	public static EucVec LatAddrToEucVec (LatAddr inLat) {
		//TODO:	As described in the README, a LatAddr of order 'n' 
		//		represents EucVecs of dimension 'n-1'
		initSimplexVectors(inLat.getOrder());
		
		return null;
	}
	
	public static void initSubspaceBasis (int order) {
		if (subspaceBasis == null) {
			subspaceBasis = new HashMap <Integer, EucVec[]>();
			createSubspaceBasis(order);
		} else if (!subspaceBasis.containsKey(order)) {
			createSubspaceBasis(order);
		}
	}
	
	public static void createSubspaceBasis (int order) {
		/*
		 * The process required to arrive at having an orthonormal basis for the 
		 * subspace, normal to the '1' vector, is as follows:
		 * 
		 * 		For the order 'n' (looking for the (n-1)-dimensional subspace)
		 * 		Select (arbitrary but consistent) n-1 vertices from the 'standard (n-1)-simplex'
		 * 		For each vertex, create a corresponding vector which is the offset 
		 * 		of the vertex from the simplex's centroid.
		 * 
		 * 		These n-1 vectors span the subspace we are trying to describe.
		 * 		Running the Gram-Schmidt (or Householder, preferred but unable to find description yet)
		 * 		will give n-1 vectors that are orthonormal. The (n-1) vectors of the corresponding (n-1 dimensional) 
		 * 		Euclidean space can be mapped (directly or with dimensional scaling) to this basis set.
		 */
		
		// Find (n-1) vertices of the 'standard (n-1)-simplex' (see README) as offsets from centroid
		EucVec negativeCentroid = EucVec.allDimEqual(order, (-1.0/order));
		
		EucVec [] standardVertexOffsets = new EucVec[order-1];
		for (int i = 0; i < (order-1); i++) {
			standardVertexOffsets[i] = EucVec.zero(order);
			standardVertexOffsets[i].setValue(i, 1.0);
			standardVertexOffsets[i] = EucVec.add(standardVertexOffsets[i], negativeCentroid);
		}
		
		// Orthonormalize the set with Modified Gram-Schmidt
		EucVec [] basisVectors = new EucVec[order-1];
		basisVectors[0] = standardVertexOffsets[0].getCopy();
		basisVectors[0] = EucVec.normalize(basisVectors[0]);
		
		for (int i = 1; i < (order-1); i++) {
			basisVectors[i] = standardVertexOffsets[i].getCopy();
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
	
	private static void createSimplexVectors (int order) {
		// TODO: 	Use the recursive solution detailed on
		//				https://en.wikipedia.org/wiki/Simplex#Cartesian_coordinates_for_regular_n-dimensional_simplex_in_Rn
		//			To find the set of vectors for the n-simplex for the given order.
		//			Store the set of vectors in the simplexVectors map, keyed by the order
		
		double invOrder = -1.0/(order-1);
		
		EucVec [] simplexVecs = new EucVec [order];
		for (int i = 0; i < order; i++) { simplexVecs[i] = EucVec.zero(order-1); }
		
		for (int i = 0; i < order-2; i++) {
			double tmp = 0.0;
			
			// Note: Set the i-th dimension of the current vector so that its magnitude is 1
			simplexVecs[i].setValue(i, Math.sqrt(1.0 - simplexVecs[i].getSqrMagnitude()));
			
			// Note: set the i-th dimension of all further vectors in the set so that
			//		 the dot product between the current vector and those vectors is 1/order
			tmp = ((invOrder - EucVec.dot(simplexVecs[i], simplexVecs[i+1])) / simplexVecs[i].getValue(i));
			for (int j = i+1; j < order; j++) {
				simplexVecs[j].setValue(i, tmp);
			}
		}
		
		simplexVecs[order-2].setValue(order-2, Math.sqrt(1.0 - simplexVecs[order-2].getSqrMagnitude()));
		simplexVecs[order-1].setValue(order-2, -Math.sqrt(1.0 - simplexVecs[order-1].getSqrMagnitude()));
		
		// Note: This number was found empirically, but has been tested and holds up to 1000 dimensions
		//		 It's "a pretty magical number, yo" as one person put it.
		double magic = 1.0/Math.sqrt(1.0+(1.0/(order-1)));
		
		for (int i = 0; i < order; i++) {
			simplexVecs[i] = EucVec.scale(magic, simplexVecs[i]);
		}
		
		// Note: Save the vector set for future use
		simplexVectors.put(order, simplexVecs);
	}
	
	/*
	 * 		LATADDR <-> CANADDR MEMBERS
	 * 
	 * 	This section has functions for converting between LatAddrs and CanAddrs.
	 * 
	 * 	These functions are detailed by Kitto et al (1991).
	 * 	See the README for more information.
	 */
	
	public static CanAddr LatAddrToCanAddr (LatAddr inLat) {
		
		return null;
	}
	
	public static LatAddr CanAddrToLatAddr (CanAddr inCan) {
		
		return null;
	}
}
