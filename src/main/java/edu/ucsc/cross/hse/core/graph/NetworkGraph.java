package edu.ucsc.cross.hse.core.graph;

import Jama.Matrix;
import org.jgrapht.graph.DirectedWeightedMultigraph;

public class NetworkGraph extends DirectedWeightedMultigraph<Integer, NetworkEdge>
{

	private static final long serialVersionUID = -8399798809340510860L;

	private Matrix adjacency;

	private Matrix laplacian;

	public NetworkGraph()
	{
		super(new NetworkGraphFactory());
	}

	public void addAllVertices(int max)
	{
		for (int i = 0; i <= max; i++)
		{
			if (!this.vertexSet().contains(i))
			{
				this.addVertex(i);
				adjacency = null;
			}
		}
	}

	public Matrix getAdjacencyMatrix()
	{
		if (adjacency == null)
		{
			double[][] matrixArray = new double[vertexSet().size()][vertexSet().size()];
			for (NetworkEdge e : edgeSet())
			{
				matrixArray[e.origin][e.destination] = 1;
			}
			adjacency = new Matrix(matrixArray);
		}
		return adjacency;
	}

	public Matrix getLaplacian()
	{
		if (laplacian == null)
		{
			laplacian = computeLaplacian(this.getAdjacencyMatrix());
		}
		return laplacian;
	}

	private static Matrix computeArrayDiagonal(Matrix matrix)
	{
		Integer numColumns = matrix.getColumnDimension();
		double[][] columnValues = new double[numColumns][numColumns];
		for (int colInd = 0; colInd < numColumns; colInd++)
		{
			columnValues[colInd][colInd] = matrix.get(0, colInd);
		}
		Matrix diagonal = new Matrix(columnValues);
		return diagonal;
	}

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

	public static Matrix computeLaplacian(Matrix matrix)
	{
		Matrix transpose = matrix.transpose();
		Matrix transposeSum = computeSum(transpose);
		Matrix transposeSumDiagonal = computeDiagonal(transposeSum);
		Matrix laplacian = transposeSumDiagonal.minus(matrix);
		return laplacian;
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

}
