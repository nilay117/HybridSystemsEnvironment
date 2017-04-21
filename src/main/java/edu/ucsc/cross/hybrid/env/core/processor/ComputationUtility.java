package edu.ucsc.cross.hybrid.env.core.processor;

public class ComputationUtility{
	  /**
	   * Returns distance between 3D set of coords
	   * 
	   * @param x1
	   *            first x coord
	   * @param y1
	   *            first y coord
	   * @param z1
	   *            first z coord
	   * @param x2
	   *            second x coord
	   * @param y2
	   *            second y coord
	   * @param z2
	   *            second z coord
	   * @return distance between coords
	   */
	  public static double getDistance(float x1, float y1, float z1, float x2, float y2, float z2)
	  {
	    float dx = x1 - x2;
	    float dy = y1 - y2;
	    float dz = z1 - z2;

	    // We should avoid Math.pow or Math.hypot due to perfomance reasons
	    return Math.sqrt(dx * dx + dy * dy + dz * dz);
	  }


	}