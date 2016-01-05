package edu.ncsu.csc.ase.apisim.topicModel;


public class Main {
	public static void main(String[] args) {
		Main mn = new Main();
		mn.evalPkgSum();
		mn.evalPkgClsSum();
		mn.evalClassSum();
		mn.evalClassMtdSum();
		mn.evalPkgClsMtdSum();
		
	}

	void evalClassMtdSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelClassMtd();
			tm.eval();
		} catch (Exception e) {
	 		e.printStackTrace();
		}
	}
 
	void evalClassSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelClass();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void evalPkgClsSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClass();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void evalPkgClsMtdSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClassMtd();
			tm.eval();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void evalPkgSum() {
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
