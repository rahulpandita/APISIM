package edu.ncsu.csc.ase.apisim.topicModel;


public class Main {
	public static void main(String[] args) {
		//evalPkgSum();
		//evalPkgClsSum();
		//evalClassSum();
		//evalClassMtdSum();
		evalPkgClsMtdSum();
		
	}

	private static void evalClassMtdSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelClassMtd();
			tm.eval();
		} catch (Exception e) {
	 		e.printStackTrace();
		}
	}
 
	private static void evalClassSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelClass();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void evalPkgClsSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClass();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void evalPkgClsMtdSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClassMtd();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void evalPkgSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPackage();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
