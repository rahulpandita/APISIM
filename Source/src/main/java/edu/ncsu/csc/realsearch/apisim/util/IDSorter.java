package edu.ncsu.csc.realsearch.apisim.util;

public class IDSorter<T>
{
	private T doc;
	
	private Double rank;
	
	public IDSorter(T doc, Double rank)
	{
		this.doc = doc;
		this.rank = rank;
	}

	public void setDoc(T doc) {
		this.doc = doc;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}
	
	public T getDoc() {
		return doc;
	}

	public Double getRank() {
		return rank;
	}

}