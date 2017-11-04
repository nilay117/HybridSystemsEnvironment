package edu.ucsc.cross.hse.toolbox.math.matrix;

import Jama.Matrix;

public class MatrixOperator
{

	public static Matrix computeDiagonal(Matrix matrix)
	{
		Integer numRows = matrix.getRowDimension();
		if (numRows > 1)
		{
			return computeSquareDiagonal(matrix);
		} else
		{
			return computeArrayDiagonal(matrix);
		}
	}

	private static Matrix computeSquareDiagonal(Matrix matrix)
	{
		Integer numColumns = matrix.getColumnDimension();
		Integer numRows = matrix.getRowDimension();
		Integer arraySize = Math.min(numColumns, numRows);
		double[][] columnValues = new double[arraySize][1];
		for (int colInd = 0; colInd < numColumns; colInd++)
		{
			columnValues[colInd][0] = matrix.get(colInd, colInd);
		}
		Matrix diagonal = new Matrix(columnValues);
		return diagonal;
	}

	private static Matrix computeArrayDiagonal(Matrix matrix)
	{
		Integer numColumns = matrix.getColumnDimension();
		double[][] columnValues = new double[numColumns][numColumns];
		System.out.println(numColumns);
		for (int colInd = 0; colInd < numColumns; colInd++)
		{
			columnValues[colInd][colInd] = matrix.get(0, colInd);
		}
		Matrix diagonal = new Matrix(columnValues);
		return diagonal;
	}

	public static Matrix computeLaplacian(Matrix matrix)
	{
		Matrix transpose = matrix.transpose();
		Matrix transposeSum = computeSum(transpose);
		transposeSum.print(10, 1);
		Matrix transposeSumDiagonal = computeDiagonal(transposeSum);
		Matrix laplacian = transposeSumDiagonal.minus(matrix);
		return laplacian;
	}

	public static Matrix computeSum(Matrix matrix)
	{
		Integer numColumns = matrix.getColumnDimension();
		Integer numRows = matrix.getRowDimension();
		double[][] sumValues = new double[1][numColumns];
		for (int colInd = 0; colInd < numColumns; colInd++)
		{
			sumValues[0][colInd] = 0.0;
			for (int rowInd = 0; rowInd < numRows; rowInd++)
			{
				sumValues[0][colInd] += matrix.get(rowInd, colInd);
			}
		}
		Matrix diagonal = new Matrix(sumValues);
		return diagonal;
	}

	public static void main(String args[])
	{
		// operatorTest();
		consensusTest();
	}

	private static void operatorTest()
	{
		System.out.println("Operator Test:");
		double[][] array =
		{
				{ 1., 2., 3 },
				{ 4., 5., 6. },
				{ 7., 8., 10. } };
		Matrix A = new Matrix(array);
		Matrix diag = computeDiagonal(A);
		System.out.println("Rows: " + diag.getRowDimension());
		System.out.println("Values : ");
		diag.print(10, 2);
		Matrix sum = computeSum(A);
		sum.print(10, 1);
		Matrix lap = computeLaplacian(A);
		lap.print(10, 1);
	}

	private static void consensusTest()
	{
		System.out.println("Consensus Test:");
		double[][] array =
		{
				{ 0., 1., 1., 0., 1. },
				{ 1., 0., 1., 0., 0. },
				{ 1., 0., 0., 1., 0. },
				{ 0., 0., 1., 0., 1. },
				{ 1., 0., 1., 1., 0. } };
		Matrix A = new Matrix(array);
		Matrix lap = computeLaplacian(A);
		lap.print(10, 1);
	}
}
