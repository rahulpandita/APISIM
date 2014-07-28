package edu.ncsu.csc.ase.apisim.eval.util;

import java.util.Comparator;

public class CustomComparator implements Comparator<ResultRep> {

	@Override
	public int compare(ResultRep o1, ResultRep o2) {
		if (o1.getRank() > o2.getRank())
			return 1;
		return -1;
	}

}
