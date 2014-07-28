package edu.ncsu.csc.ase.apisim.eval.util;

import org.apache.lucene.document.Document;

public class ResultRep
	{
		private Document doc;
		
		private Float rank;
		
		public ResultRep(Document doc, Float rank)
		{
			this.doc = doc;
			this.rank = rank;
		}

		public void setDoc(Document doc) {
			this.doc = doc;
		}

		public void setRank(Float rank) {
			this.rank = rank;
		}
		
		public Document getDoc() {
			return doc;
		}

		public Float getRank() {
			return rank;
		}
	
	}