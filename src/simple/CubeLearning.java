package simple;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.informed.Heuristic;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.IDAStar;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.singleagent.planning.deterministic.uninformed.dfs.DFS;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.oomdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.oomdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.environment.Environment;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.SimpleHashableStateFactory;
import burlap.oomdp.visualizer.Visualizer;

public class CubeLearning {
	CubeDomain cdom;
	Domain domain;
	RewardFunction rf;
	TerminalFunction tf;
	StateConditionTest goalCondition;
	State initialState;
	HashableStateFactory hashingFactory;
	Environment env;
	
	public CubeLearning(){
		cdom = new CubeDomain();
		domain = cdom.generateDomain();
		//rf = new UniformCostRF();
		rf = new CubeDomain.TestRF();
		//rf = new CubeDomain.ExampleRF();
		//tf = new CubeDomain.singleLayerTF();
		tf = new CubeDomain.ExampleTF();
		goalCondition = new TFGoalCondition(tf);
		//initialState = CubeDomain.getInitialState(domain);
		initialState = CubeDomain.getExampleState(domain,0,4);
		//initialState = CubeDomain.getTestState(domain);
		hashingFactory = new SimpleHashableStateFactory();

		env = new SimulatedEnvironment(domain, rf, tf, initialState);
	}
	
	public void visualize(String outputpath){
		Visualizer v = cdom.getVisualizer();
		new EpisodeSequenceVisualizer(v, domain, outputpath);
	}

	public void BFSExample(String outputPath){

		DeterministicPlanner planner = new BFS(domain, goalCondition, hashingFactory);
		Policy p = planner.planFromState(initialState);
		p.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "bfs");

	}
	
//	public void AStarExample(String outputPath){
//		
//		Heuristic mdistHeuristic = new Heuristic() {
//
//			public double h(State s) {
//				//GridAgent a = ((GridWorldState)s).agent;
//				double mdist = Math.abs(a.x-10) + Math.abs(a.y-10);
//
//				return -mdist;
//			}
//		};
//
//		DeterministicPlanner planner = new AStar(domain, rf, goalCondition, hashingFactory,
//												 mdistHeuristic);
//
//		Policy p = planner.planFromState(initialState);
//		PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "astar");
//		
//	}	

	public void valueIterationExample(String outputPath){

		Planner planner = new ValueIteration(domain, rf, tf, 0.99, hashingFactory, 0.001, 100);
		Policy p = planner.planFromState(initialState);

		//PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "vi");
		p.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "VI");
	

	}
	
	public void AStarExample(String outputPath){

		Heuristic md3dHeuristic = new Heuristic() {
			public double h(State s) {
				
				ObjectInstance cube = s.getFirstObjectOfClass(CubeDomain.CLASSCUBE);
				int[] curTop = cube.getIntArrayValForAttribute(CubeDomain.ATTTOP);
				int[] curLeft = cube.getIntArrayValForAttribute(CubeDomain.ATTLEFT);
				int[] curFront = cube.getIntArrayValForAttribute(CubeDomain.ATTFRONT);
				int[] curRight = cube.getIntArrayValForAttribute(CubeDomain.ATTRIGHT);
				int[] curBack = cube.getIntArrayValForAttribute(CubeDomain.ATTBACK);
				int[] curDown = cube.getIntArrayValForAttribute(CubeDomain.ATTDOWN);
				int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};				

				return -1 * cdom.md3d(cubeArray);
			}
		};

		DeterministicPlanner planner = new AStar(domain, rf, goalCondition, hashingFactory, md3dHeuristic);
		Policy p = planner.planFromState(initialState);

		p.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "astar");

	}
	
	public void IDAStarEx(String outputPath){
		Heuristic md3dHeuristic = new Heuristic() {
			public double h(State s) {
				
				ObjectInstance cube = s.getFirstObjectOfClass(CubeDomain.CLASSCUBE);
				int[] curTop = cube.getIntArrayValForAttribute(CubeDomain.ATTTOP);
				int[] curLeft = cube.getIntArrayValForAttribute(CubeDomain.ATTLEFT);
				int[] curFront = cube.getIntArrayValForAttribute(CubeDomain.ATTFRONT);
				int[] curRight = cube.getIntArrayValForAttribute(CubeDomain.ATTRIGHT);
				int[] curBack = cube.getIntArrayValForAttribute(CubeDomain.ATTBACK);
				int[] curDown = cube.getIntArrayValForAttribute(CubeDomain.ATTDOWN);
				int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};				

				return -1 * cdom.md3d(cubeArray);
			}
		};

		DeterministicPlanner planner = new IDAStar(domain, rf, goalCondition, hashingFactory, md3dHeuristic);
		Policy p = planner.planFromState(initialState);

		p.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "IDAStar");

	}
	
	public void DFSExample(String outputPath){

		DeterministicPlanner planner = new DFS(domain, goalCondition, hashingFactory);
		Policy p = planner.planFromState(initialState);
		p.evaluateBehavior(initialState, rf, tf).writeToFile(outputPath + "dfs");

	}
	
	public void qLearningExample(String outputPath){

		QLearning agent = new QLearning(domain, 0.99, hashingFactory, 0., 1., 50);
		//run learning for 50 episodes
		System.out.println("Starting");
		for(int i = 0; i < 50; i++){
			System.out.println("Inside at i = " + i);
			EpisodeAnalysis ea = agent.runLearningEpisode(env);
			
			ea.writeToFile(outputPath + "ql_" + i);
			System.out.println(i + ": " + ea.maxTimeStep());

			//reset environment for next learning episode
			env.resetEnvironment();
			System.out.println("at end of iteration");
		}

	}

	
	public void sarsaLearningExample(String outputPath){

		SarsaLam agent = new SarsaLam(domain, 0.99, hashingFactory, 0., 0.5, 0.3);

		//run learning for 50 episodes
		for(int i = 0; i < 50; i++){
			EpisodeAnalysis ea = agent.runLearningEpisode(env);

			ea.writeToFile(outputPath + "sarsa_" + i);
			System.out.println(i + ": " + ea.maxTimeStep());

			//reset environment for next learning episode
			env.resetEnvironment();
		}

	}
	
	public static void main(String[] args) {
		System.out.println("Starting");
		CubeLearning example = new CubeLearning();
		String outputPath = "output/";

		example.IDAStarEx(outputPath);
		//example.AStarExample(outputPath);
		//example.BFSExample(outputPath);
		//example.valueIterationExample(outputPath);
		//example.DFSExample(outputPath);
		//example.qLearningExample(outputPath);
		//example.sarsaLearningExample(outputPath);
		//example.experimentAndPlotter();

		example.visualize(outputPath);

	}
}
