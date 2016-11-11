package edu.ncsu.csc.ase.apisim.topicModel;

import edu.ncsu.csc.ase.apisim.configuration.Configuration.EvalMode;

public class Main {
	
	private EvalMode mode = EvalMode.UNKNOWN;
	
	public static void main(String[] args) {
		Main mn = new Main(EvalMode.ANDROID_J2ME);
		
		
		//mn.evalPkgSum();
		//mn.evalPkgClsSum();
		//mn.evalClassSum();
		//mn.evalClassMtdSum();
		
		
		mn.evalPkgClsMtdSumJN();
		
	}
	public Main(EvalMode mode) {
		this.mode = mode;
	}

	void evalClassMtdSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelClassMtd(mode);
			tm.eval();
		} catch (Exception e) {
	 		e.printStackTrace();
		}
	}
 
	void evalClassSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelClass(mode);
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void evalPkgClsSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClass(mode);
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void evalPkgClsMtdSum()
	{
		evalPkgClsMtdSum(10);
		System.gc();
		evalPkgClsMtdSum(25);
		System.gc();
		evalPkgClsMtdSum(50);
		System.gc();
		evalPkgClsMtdSum(75);
		System.gc();
		evalPkgClsMtdSum(100);
		System.gc();
		evalPkgClsMtdSum(250);
		System.gc();
		evalPkgClsMtdSum(500);
		System.gc();
		evalPkgClsMtdSum(750);
		System.gc();
		evalPkgClsMtdSum(1000);
		System.gc();
		evalPkgClsMtdSum(2000);
		System.gc();		
	}
			
	void evalPkgClsMtdSum(int numTopics) {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClassMtd(numTopics, mode);
			tm.eval();
		} catch (Exception e) {
			System.err.println(numTopics);
			e.printStackTrace();
		}
	}
	
	void evalPkgClsMtdSumJN()
	{
		evalPkgClsMtdSumJN(1000);
		System.gc();
		/*evalPkgClsMtdSumJN(25);
		System.gc();
		evalPkgClsMtdSumJN(50);
		System.gc();
		evalPkgClsMtdSumJN(75);
		System.gc();
		evalPkgClsMtdSumJN(100);
		System.gc();
		evalPkgClsMtdSumJN(250);
		System.gc();
		
		/*
		evalPkgClsMtdSumJN(500);
		System.gc();
		evalPkgClsMtdSumJN(750);
		System.gc();
		evalPkgClsMtdSumJN(1000);
		System.gc();
		evalPkgClsMtdSumJN(2000);
		System.gc();
		*/		
	}
			
	void evalPkgClsMtdSumJN(int numTopics) {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClassMtdJavaNET(numTopics, mode);
			tm.eval();
		} catch (Exception e) {
			System.err.println(numTopics);
			e.printStackTrace();
		}
	}

	void evalPkgSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPackage(mode);
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
