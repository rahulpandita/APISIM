package edu.ncsu.csc.ase.apisim.topicModel;

import edu.ncsu.csc.ase.apisim.configuration.Configuration.EvalMode;

public class Main {
	
	private EvalMode mode = EvalMode.UNKNOWN;
	
	public static void main(String[] args) {
		Main mn = new Main(EvalMode.ANDROID_J2ME);
		
		
		//mn.evalPkgSum();
		//mn.evalPkgClsSum();
		//mn.evalClassSum();
		mn.evalClassMtdSum();
		//mn.evalPkgClsMtdSum();
		
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
	
	void evalPkgClsMtdSum() {
		TopicModelFactory tm;
		try 
		{
			tm = new TopicModelPkgClassMtd(mode);
			tm.eval();
		} catch (Exception e) {
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
