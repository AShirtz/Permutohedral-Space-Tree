package com.andrewshirtz.CG.Util;

import java.util.Arrays;

import com.andrewshirtz.CG.Exceptions.DimensionMismatchException;

/*
 * 	This class provides data members and methods for handling elements of Rn.
 */

public class EucVec {

	/*
	 * 		STATIC MEMBERS
	 */
	
	private static double epsilon = 1e-10;
	
	// Note: Commutative Automorphism functions have variable number of parameters 
	public static EucVec add (EucVec... vecs) {
		if (!EucVec.dimCheck(vecs)) {
			throw new DimensionMismatchException ("Unable to add vectors, mismatched dimensions.");
		}
		
		EucVec result = EucVec.zero(vecs[0].getDimension());
		for (EucVec v : vecs) {
			for (int i = 0; i < v.getDimension(); i++) {
				result.values[i] += v.values[i];
			}
		}
		
		return result;
	}
	
	public static EucVec scale (double scale, EucVec vec) {
		EucVec result = new EucVec(vec);
		for (int i = 0; i < result.getDimension(); i++) {
			result.values[i] *= scale;
		}
		return result;
	}
	
	public static EucVec[] scale (double scale, EucVec... vecs) {
		EucVec[] result = new EucVec[vecs.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = EucVec.scale(scale, vecs[i]);
		}
		return result;
	}
	
	public static double dot (EucVec a, EucVec b) {
		if (!EucVec.dimCheck(a,b)) {
			throw new DimensionMismatchException ("Unable to take dot product of vectors, mismatched dimensions.");
		}
		
		double result = 0.0;
		for (int i = 0; i < a.getDimension(); i++) {
			result += a.values[i] * b.values[i];
		}
		return result;
	}
	
	// Note: Commutative Automorphism functions have variable number of parameters
	public static EucVec hadamard (EucVec... vecs) {
		if (!EucVec.dimCheck(vecs)) {
			throw new DimensionMismatchException ("Unable to take Hadamard product of vectors, mismatched dimensions.");
		}
		
		EucVec result = new EucVec(vecs[0]);
		for (int i = 1; i < vecs.length; i++) {
			for (int j = 0; j < result.getDimension(); j++) {
				result.values[j] *= vecs[i].values[j];
			}
		}
		
		return result;
	}
	
	
	public static EucVec linearCombination (EucVec coefs, EucVec... vecs) {
		return linearCombination(coefs.values, vecs);
	}
	
	public static EucVec linearCombination (double[] coefs, EucVec... vecs) {
		if (!EucVec.dimCheck(vecs)) {
			throw new DimensionMismatchException ("Unable to evaluate linear combination, mismatched dimensions in the set of vectors.");
		}
		if (coefs.length != vecs.length) {
			throw new ArithmeticException ("Unable to evaluate linear combination, mismatched number of coefficients");
		}
		EucVec result = EucVec.zero(vecs[0].getDimension());
		
		for (int i = 0; i < vecs.length; i++) {
			result = EucVec.add(result, EucVec.scale(coefs[i], vecs[i]));
		}
		
		return result;
	}
	
	public static double dist (EucVec a, EucVec b) {
		if (!EucVec.dimCheck(a, b)) {
			throw new DimensionMismatchException ("Unable to calculate distance between vectors, mismatched dimensions.");
		}
		
		return (EucVec.add(a, EucVec.scale(-1.0, b))).getMagnitude();
	}
	
	public static EucVec normalize (EucVec v) {
		return EucVec.scale((1.0)/(v.getMagnitude()), v);
	}
	
	public static EucVec zero (int dim) {
		return new EucVec(dim);
	}
	
	// Note: This function creates a EucVec with all dimensions set to the given value
	public static EucVec allDimEqual (int dim, double value) {
		EucVec result = EucVec.zero(dim);
		result.setAllValues(value);
		return result;
	}
	
	private static boolean dimCheck (EucVec... vecs) {
		if (vecs.length > 1) {
			int dim = vecs[0].getDimension();
			for (int i = 1; i < vecs.length; i++) {
				if (dim != vecs[i].getDimension()) { return false; }
			}
		}
		return true;
	}
	
	/*
	 * 		INSTANCE MEMBERS
	 */
	
	private double [] values;
	
	private EucVec (int dim) {
		this.values = new double[dim];
	}
	
	// Note: Shallow copy constructor
	public EucVec (EucVec orig) {
		this.values = new double[orig.values.length];
		System.arraycopy(orig.values, 0, this.values, 0, orig.values.length);
	}
	
	public EucVec (double... vals) {
		this.values = vals;
	}
	
	public int getDimension	() { return this.values.length; }
	
	public double getValue (int dim) {
		if (this.getDimension() < dim) {
			throw new DimensionMismatchException ("Unable to access value, vector does not contain dimension.");
		}
		return this.values[dim];
	}
	
	protected void setValue (int dim, double value) {
		if (this.getDimension() < dim) {
			throw new DimensionMismatchException ("Unable to mutate value, vector does not contain dimension.");
		}
		this.values[dim] = value;
	}
	
	public void setAllValues (double value) {
		for (int i = 0; i < this.getDimension(); i++) {
			this.values[i] = value;
		}
	}
	
	public double getSqrMagnitude () {
		double result = 0.0;
		
		for (int i = 0; i < this.getDimension(); i++) {
			result += this.values[i] * this.values[i];
		}
		
		return result;
	}
	
	public double getMagnitude () {
		return Math.sqrt(this.getSqrMagnitude());
	}
	
	@Override
	public boolean equals (Object other) {
		if (!this.getClass().equals(other.getClass()))			{ return false; }
		if (!EucVec.dimCheck(this, (EucVec) other)) 			{ return false; }
		
		return Arrays.equals(this.values, ((EucVec)other).values);
	}
	
	// Note: This function is for comparison of vectors after numerically unstable operations.
	public boolean roughEquals (Object other) {
		if (!this.getClass().equals(other.getClass()))			{ return false; }
		if (!EucVec.dimCheck(this, (EucVec) other)) 			{ return false; }
		
		return (epsilon > EucVec.dist(this, (EucVec) other));
	}
	
	@Override
	public int hashCode () {
		double hash = 17;
		
		for (int i = 0; i < this.getDimension(); i++) {
			hash *= hash + this.values[i];
		}
		
		return (int) hash;
	}
	
	@Override
	public String toString () {
		String result = "{ ";
		for (int i = 0; i < this.getDimension()-1; i++) {
			result += this.values[i] + ", ";
		}
		result += this.values[this.getDimension()-1] + " }";
		return result;
	}
}
