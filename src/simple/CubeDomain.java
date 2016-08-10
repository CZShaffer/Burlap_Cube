package simple;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.*;
import burlap.oomdp.core.Attribute.AttributeType;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.*;
import burlap.oomdp.singleagent.common.SimpleAction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.VisualExplorer;
import burlap.oomdp.visualizer.ObjectPainter;
import burlap.oomdp.visualizer.StateRenderLayer;
import burlap.oomdp.visualizer.StaticPainter;
import burlap.oomdp.visualizer.Visualizer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;


public class CubeDomain implements DomainGenerator {
	
	public static final String ATTTOP = "TOP";
	public static final String ATTLEFT = "LEFT";
	public static final String ATTFRONT = "FRONT";
	public static final String ATTRIGHT = "RIGHT";
	public static final String ATTBACK = "BACK";
	public static final String ATTDOWN = "DOWN";
	
	public static final String ACTIONU = "U";
	public static final String ACTIONL = "L";
	public static final String ACTIONF = "F";
	public static final String ACTIONR = "R";
	public static final String ACTIONB = "B";
	public static final String ACTIOND = "D";
	public static final String ACTIONUPRIME = "U_PRIME";
	public static final String ACTIONLPRIME = "L_PRIME";
	public static final String ACTIONFPRIME = "F_PRIME";
	public static final String ACTIONRPRIME = "R_PRIME";
	public static final String ACTIONBPRIME = "B_PRIME";
	public static final String ACTIONDPRIME = "D_PRIME";
	public static final String ACTIONU2 = "U2";
	public static final String ACTIONL2 = "L2";
	public static final String ACTIONF2 = "F2";
	public static final String ACTIONR2 = "R2";
	public static final String ACTIONB2 = "B2";
	public static final String ACTIOND2 = "D2";
	
	public static final String CLASSCUBE = "cube";
	
	public Domain generateDomain() {
		SADomain domain = new SADomain();
		
		Attribute topAtt = new Attribute(domain,ATTTOP,AttributeType.INTARRAY);
		topAtt.setLims(0, 5);
		Attribute leftAtt = new Attribute(domain,ATTLEFT,AttributeType.INTARRAY);
		leftAtt.setLims(0, 5);
		Attribute frontAtt = new Attribute(domain,ATTFRONT,AttributeType.INTARRAY);
		frontAtt.setLims(0, 5);
		Attribute rightAtt = new Attribute(domain,ATTRIGHT,AttributeType.INTARRAY);
		rightAtt.setLims(0, 5);
		Attribute backAtt = new Attribute(domain,ATTBACK,AttributeType.INTARRAY);
		backAtt.setLims(0, 5);
		Attribute downAtt = new Attribute(domain,ATTDOWN,AttributeType.INTARRAY);
		downAtt.setLims(0, 5);
		
		ObjectClass cubeClass = new ObjectClass(domain, CLASSCUBE);
		cubeClass.addAttribute(topAtt);
		cubeClass.addAttribute(leftAtt);
		cubeClass.addAttribute(frontAtt);
		cubeClass.addAttribute(rightAtt);
		cubeClass.addAttribute(backAtt);
		cubeClass.addAttribute(downAtt);
		
		new turn(ACTIONU, domain, 0, false, false);
		new turn(ACTIONL, domain, 1, false, false);
		new turn(ACTIONF, domain, 2, false, false);
		new turn(ACTIONR, domain, 3, false, false);
		new turn(ACTIONB, domain, 4, false, false);
		new turn(ACTIOND, domain, 5, false, false);
		new turn(ACTIONUPRIME, domain, 0, true, false);
		new turn(ACTIONLPRIME, domain, 1, true, false);
		new turn(ACTIONFPRIME, domain, 2, true, false);
		new turn(ACTIONRPRIME, domain, 3, true, false);
		new turn(ACTIONBPRIME, domain, 4, true, false);
		new turn(ACTIONDPRIME, domain, 5, true, false);
		new turn(ACTIONU2, domain, 0, false, true);
		new turn(ACTIONL2, domain, 1, false, true);
		new turn(ACTIONF2, domain, 2, false, true);
		new turn(ACTIONR2, domain, 3, false, true);
		new turn(ACTIONB2, domain, 4, false, true);
		new turn(ACTIOND2, domain, 5, false, true);
		
		new firstCross(domain);
		new singleLayer(domain);
		new doubleLayer(domain);
		new thirdCross(domain);
		new isSolved(domain);
		
		return domain;
	}

	protected class turn extends SimpleAction implements FullActionModel {
		protected int dir;
		protected boolean prime;
		protected boolean twice;
		protected double [] directionProbs = new double[18];
		//0=TOP,1=LEFT,2=FRONT,3=RIGHT,4=BACK,5=DOWN
		public turn(String actionName, Domain domain, int direction, boolean isPrime, boolean isTwice){
			super(actionName, domain);
			dir = direction;
			prime = isPrime;
			twice = isTwice;
			if(isTwice){
				for(int i = 0; i < directionProbs.length; i++){
					int temp = 17;
					if(i == dir){
						directionProbs[i + 12] = 1;
						temp = i + 12;
					}
					else if(i == temp){
						directionProbs[i] = 1;
					}
					else{
						directionProbs[i] = 0;
					}
				}
			}
			else{
				if(!prime){
					for(int i = 0; i < directionProbs.length; i++){
						if(i == dir){
							directionProbs[i] = 1;
						}
						else{
							directionProbs[i] = 0;
						}
					}
				}
				else{
					for(int i = 0; i < directionProbs.length; i++){
						int temp = 11;
						if(i == dir){
							directionProbs[i + 6] = 1;
							temp = i + 6;
						}
						else if(i == temp){
							directionProbs[i] = 1;
						}
						else{
							directionProbs[i] = 0;
						}
					}
				}
			}
		}
		
		@Override
		protected State performActionHelper(State s, GroundedAction groundedAction) {
			ObjectInstance cube = s.getFirstObjectOfClass(CLASSCUBE);
			int[] curTop = cube.getIntArrayValForAttribute(ATTTOP);
			int[] curLeft = cube.getIntArrayValForAttribute(ATTLEFT);
			int[] curFront = cube.getIntArrayValForAttribute(ATTFRONT);
			int[] curRight = cube.getIntArrayValForAttribute(ATTRIGHT);
			int[] curBack = cube.getIntArrayValForAttribute(ATTBACK);
			int[] curDown = cube.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};
			int[][] newCube = this.cubeResult(cubeArray, dir, prime, twice);
			
			cube.setValue(ATTTOP, newCube[0]);
			cube.setValue(ATTLEFT, newCube[1]);
			cube.setValue(ATTFRONT, newCube[2]);
			cube.setValue(ATTRIGHT, newCube[3]);
			cube.setValue(ATTBACK, newCube[4]);
			cube.setValue(ATTDOWN, newCube[5]);
			
			return s;
		}
		
		protected int[][] cubeResult(int[][] cubeArray, int direction, boolean isPrime,boolean isTwice){
			RubiksCube rubiks = new RubiksCube();
			rubiks.turn(cubeArray, direction, isPrime, isTwice);
			return rubiks.returnCube();
		}

		public List<TransitionProbability> getTransitions(State s, GroundedAction groundedAction) {
			//get agent and current position
			ObjectInstance cube = s.getFirstObjectOfClass(CLASSCUBE);
			int[] curTop = cube.getIntArrayValForAttribute(ATTTOP);
			int[] curLeft = cube.getIntArrayValForAttribute(ATTLEFT);
			int[] curFront = cube.getIntArrayValForAttribute(ATTFRONT);
			int[] curRight = cube.getIntArrayValForAttribute(ATTRIGHT);
			int[] curBack = cube.getIntArrayValForAttribute(ATTBACK);
			int[] curDown = cube.getIntArrayValForAttribute(ATTDOWN);
			int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};
			

			List<TransitionProbability> tps = new ArrayList<TransitionProbability>(6);
			TransitionProbability noChangeTransition = null;
			for(int i = 0; i < directionProbs.length; i++){
				if(i<=5){
					int[][] newCube = this.cubeResult(cubeArray, i, false, false);
					if(Arrays.deepEquals(cubeArray, newCube)){
						//new possible outcome
						State ns = s.copy();
						ObjectInstance nagent = ns.getFirstObjectOfClass(CLASSCUBE);
						nagent.setValue(ATTTOP, newCube[0]);
						nagent.setValue(ATTLEFT, newCube[1]);
						nagent.setValue(ATTFRONT, newCube[2]);
						nagent.setValue(ATTRIGHT, newCube[3]);
						nagent.setValue(ATTBACK, newCube[4]);
						nagent.setValue(ATTDOWN, newCube[5]);

						//create transition probability object and add to our list of outcomes
						tps.add(new TransitionProbability(ns, this.directionProbs[i]));
					}
					else{
						//this direction didn't lead anywhere new
						//if there are existing possible directions
						//that wouldn't lead anywhere, aggregate with them
						if(noChangeTransition != null){
							noChangeTransition.p += this.directionProbs[i];
						}
						else{
							//otherwise create this new state and transition
							noChangeTransition = new TransitionProbability(s.copy(),
									this.directionProbs[i]);
							tps.add(noChangeTransition);
						}
					}
				}
				else if(i<=11){
					int[][] newCube = this.cubeResult(cubeArray, i, true, false);
					if(Arrays.deepEquals(cubeArray, newCube)){
						//new possible outcome
						State ns = s.copy();
						ObjectInstance nagent = ns.getFirstObjectOfClass(CLASSCUBE);
						nagent.setValue(ATTTOP, newCube[0]);
						nagent.setValue(ATTLEFT, newCube[1]);
						nagent.setValue(ATTFRONT, newCube[2]);
						nagent.setValue(ATTRIGHT, newCube[3]);
						nagent.setValue(ATTBACK, newCube[4]);
						nagent.setValue(ATTDOWN, newCube[5]);

						//create transition probability object and add to our list of outcomes
						tps.add(new TransitionProbability(ns, this.directionProbs[i]));
					}
					else{
						//this direction didn't lead anywhere new
						//if there are existing possible directions
						//that wouldn't lead anywhere, aggregate with them
						if(noChangeTransition != null){
							noChangeTransition.p += this.directionProbs[i];
						}
						else{
							//otherwise create this new state and transition
							noChangeTransition = new TransitionProbability(s.copy(),
									this.directionProbs[i]);
							tps.add(noChangeTransition);
						}
					}
				}
				else{
					int[][] newCube = this.cubeResult(cubeArray, i, true, true);
					if(Arrays.deepEquals(cubeArray, newCube)){
						//new possible outcome
						State ns = s.copy();
						ObjectInstance nagent = ns.getFirstObjectOfClass(CLASSCUBE);
						nagent.setValue(ATTTOP, newCube[0]);
						nagent.setValue(ATTLEFT, newCube[1]);
						nagent.setValue(ATTFRONT, newCube[2]);
						nagent.setValue(ATTRIGHT, newCube[3]);
						nagent.setValue(ATTBACK, newCube[4]);
						nagent.setValue(ATTDOWN, newCube[5]);

						//create transition probability object and add to our list of outcomes
						tps.add(new TransitionProbability(ns, this.directionProbs[i]));
					}
					else{
						//this direction didn't lead anywhere new
						//if there are existing possible directions
						//that wouldn't lead anywhere, aggregate with them
						if(noChangeTransition != null){
							noChangeTransition.p += this.directionProbs[i];
						}
						else{
							//otherwise create this new state and transition
							noChangeTransition = new TransitionProbability(s.copy(),
									this.directionProbs[i]);
							tps.add(noChangeTransition);
						}
					}
				}
			}
			return tps;
		}
	}
	
	/*
	public int[] manhattanDist3D(int[][] cube){
		
		int cornerSum = 0;
		int edgeSum = 0;
		
		for(int i =0; i<6; i++){
			for(int j =0; j<9; j++){
				if(j%2 ==0){
					if(j != 4){
						if(cube[i][j] == 0){
							if(i == 0){
								cornerSum+=0;
							}
							else if(i == 5){
								cornerSum+=2;
							}
							else{
								if(j < 3){
									cornerSum+=0;
								}
								else if(j <6){
									cornerSum+=1;
								}
								else{
									cornerSum+=2;
								}
							}
						}
						else if(cube[i][j] == 1){
							if(i == 1){
								cornerSum+=0;
							}
							else if(i == 3){
								cornerSum+=2;
							}
							else{
								if(j%3 == 0){
									cornerSum+=0;
								}
								else if(j%3 == 1){
									cornerSum+=1;
								}
								else{
									cornerSum+=2;
								}
							}
						}
						else if(cube[i][j] == 2){
							if(i == 2){
								cornerSum+=0;
							}
							else if(i == 4){
								cornerSum+=2;
							}
							else if(i == 0){
								if(j < 3){
									cornerSum+=2;
								}
								else if(j <6){
									cornerSum+=1;
								}
								else{
									cornerSum+=0;
								}
							}
							else if(i == 5){
								if(j < 3){
									cornerSum+=0;
								}
								else if(j <6){
									cornerSum+=1;
								}
								else{
									cornerSum+=2;
								}
							}
							else if(i == 1){
								if(j%3 == 0){
									cornerSum+=2;
								}
								else if(j%3 == 1){
									cornerSum+=1;
								}
								else{
									cornerSum+=0;
								}
							}
							else{
								if(j%3 == 0){
									cornerSum+=0;
								}
								else if(j%3 == 1){
									cornerSum+=1;
								}
								else{
									cornerSum+=2;
								}
							}
						}
						else if(cube[i][j] == 3){
							if(i == 1){
								cornerSum+=2;
							}
							else if(i == 3){
								cornerSum+=0;
							}
							else{
								if(j%3 == 0){
									cornerSum+=2;
								}
								else if(j%3 == 1){
									cornerSum+=1;
								}
								else{
									cornerSum+=0;
								}
							}
						}
						else if(cube[i][j] == 4){
							if(i == 2){
								cornerSum+=2;
							}
							else if(i == 4){
								cornerSum+=0;
							}
							else if(i == 0){
								if(j < 3){
									cornerSum+=0;
								}
								else if(j <6){
									cornerSum+=1;
								}
								else{
									cornerSum+=2;
								}
							}
							else if(i == 5){
								if(j < 3){
									cornerSum+=2;
								}
								else if(j <6){
									cornerSum+=1;
								}
								else{
									cornerSum+=0;
								}
							}
							else if(i == 1){
								if(j%3 == 0){
									cornerSum+=0;
								}
								else if(j%3 == 1){
									cornerSum+=1;
								}
								else{
									cornerSum+=2;
								}
							}
							else{
								if(j%3 == 0){
									cornerSum+=2;
								}
								else if(j%3 == 1){
									cornerSum+=1;
								}
								else{
									cornerSum+=0;
								}
							}
						}
						else if(cube[i][j] == 5){
							if(i == 0){
								cornerSum+=2;
							}
							else if(i == 5){
								cornerSum+=0;
							}
							else{
								if(j < 3){
									cornerSum+=2;
								}
								else if(j <6){
									cornerSum+=1;
								}
								else{
									cornerSum+=0;
								}
							}
						}
					}
				}
				else{
					if(cube[i][j] == 0){
						if(i == 0){
							edgeSum+=0;
						}
						else if(i == 5){
							edgeSum+=2;
						}
						else{
							if(j < 3){
								edgeSum+=0;
							}
							else if(j <6){
								edgeSum+=1;
							}
							else{
								edgeSum+=2;
							}
						}
					}
					else if(cube[i][j] == 1){
						if(i == 1){
							edgeSum+=0;
						}
						else if(i == 3){
							edgeSum+=2;
						}
						else{
							if(j%3 == 0){
								edgeSum+=0;
							}
							else if(j%3 == 1){
								edgeSum+=1;
							}
							else{
								edgeSum+=2;
							}
						}
					}
					else if(cube[i][j] == 2){
						if(i == 2){
							edgeSum+=0;
						}
						else if(i == 4){
							edgeSum+=2;
						}
						else if(i == 0){
							if(j < 3){
								edgeSum+=2;
							}
							else if(j <6){
								edgeSum+=1;
							}
							else{
								edgeSum+=0;
							}
						}
						else if(i == 5){
							if(j < 3){
								edgeSum+=0;
							}
							else if(j <6){
								edgeSum+=1;
							}
							else{
								edgeSum+=2;
							}
						}
						else if(i == 1){
							if(j%3 == 0){
								edgeSum+=2;
							}
							else if(j%3 == 1){
								edgeSum+=1;
							}
							else{
								edgeSum+=0;
							}
						}
						else{
							if(j%3 == 0){
								edgeSum+=0;
							}
							else if(j%3 == 1){
								edgeSum+=1;
							}
							else{
								edgeSum+=2;
							}
						}
					}
					else if(cube[i][j] == 3){
						if(i == 1){
							edgeSum+=2;
						}
						else if(i == 3){
							edgeSum+=0;
						}
						else{
							if(j%3 == 0){
								edgeSum+=2;
							}
							else if(j%3 == 1){
								edgeSum+=1;
							}
							else{
								edgeSum+=0;
							}
						}
					}
					else if(cube[i][j] == 4){
						if(i == 2){
							edgeSum+=2;
						}
						else if(i == 4){
							edgeSum+=0;
						}
						else if(i == 0){
							if(j < 3){
								edgeSum+=0;
							}
							else if(j <6){
								edgeSum+=1;
							}
							else{
								edgeSum+=2;
							}
						}
						else if(i == 5){
							if(j < 3){
								edgeSum+=2;
							}
							else if(j <6){
								edgeSum+=1;
							}
							else{
								edgeSum+=0;
							}
						}
						else if(i == 1){
							if(j%3 == 0){
								edgeSum+=0;
							}
							else if(j%3 == 1){
								edgeSum+=1;
							}
							else{
								edgeSum+=2;
							}
						}
						else{
							if(j%3 == 0){
								edgeSum+=2;
							}
							else if(j%3 == 1){
								edgeSum+=1;
							}
							else{
								edgeSum+=0;
							}
						}
					}
					else if(cube[i][j] == 5){
						if(i == 0){
							edgeSum+=2;
						}
						else if(i == 5){
							edgeSum+=0;
						}
						else{
							if(j < 3){
								edgeSum+=2;
							}
							else if(j <6){
								edgeSum+=1;
							}
							else{
								edgeSum+=0;
							}
						}
					}
				}
			}
		}
		//Corner algorithm needs to be checked, edge cases wrong for incorrectly oriented cubies
		//Special cases: Correct position incorrect orientation
		return new int[]{cornerSum,edgeSum};
	}
	*/
	
	
	//colors should be input in position priority order (F/B -> L/R)
	public double calculateDistEdge(int color1, int color2, int currX, int currY, int currZ){
		int[] correctPos = new int[]{-1,-1,-1};
		int[] current = new int[]{currX,currY,currZ};
		int dir = 0;
		int returnDist = 0;
		int tmp = 0;
		if(color1 == 0){
			correctPos[2] = 2;
			dir = 1;
		}
		else if(color1 == 1){
			correctPos[1] = 0;
		}
		else if(color1 == 2){
			correctPos[0] = 0;
			dir = 0;
		}
		else if(color1 == 3){
			correctPos[1] = 2;
		}
		else if(color1 == 4){
			correctPos[0] = 2;
			dir = 0;
		}
		else if(color1 == 5){
			correctPos[2] = 0;
			dir = 1;
		}
		
		if(color2 == 0){
			correctPos[2] = 2;
		}
		else if(color2 == 1){
			correctPos[1] = 0;
		}
		else if(color2 == 2){
			correctPos[0] = 0;
			dir = 1;
		}
		else if(color2 == 3){
			correctPos[1] = 2;
		}
		else if(color2 == 4){
			correctPos[0] = 2;
			dir = 1;
		}
		else if(color2 == 5){
			correctPos[2] = 0;
		}
		
		for(int i = 0; i < 3; i++){
			if(correctPos[i] == -1){
				correctPos[i] = 1;
				tmp = i;
			}
		}
		
		if(correctPos[0] == currX && correctPos[1] == currY && correctPos[2] == currZ){
			if(dir == 0){
				returnDist = 0;
			}
			else{
				returnDist = 3;
			}
		}
		else if(Math.abs(correctPos[0] - currX) <= 1 && Math.abs(correctPos[1] - currY) <= 1 && Math.abs(correctPos[2] - currZ) <= 1){
			if(dir == 0){
				returnDist = 1;
			}
			else{
				returnDist = 2;
			}
		}
		else {
			int tmpSum = 0;
			for(int i = 0; i < 3; i++){
				if(i != tmp){
					tmpSum += Math.abs(current[i] - correctPos[i]);
				}
			}
			if(tmpSum == 4){
				if(dir == 0){
					returnDist = 4;
				}
				else{
					returnDist = 3;
				}
			}
			else{
				if(tmp != 0){
					if(currX == 1){
						if(dir == 0){
							returnDist = 3;
						}
						else{
							returnDist = 2;
						}
					}
					else{
						if(dir == 0){
							returnDist = 2;
						}
						else{
							returnDist = 3;
						}
					}
				}
				else{
					if(currY == 1){
						if(dir == 0){
							returnDist = 3;
						}
						else{
							returnDist = 2;
						}
					}
					else{
						if(dir == 0){
							returnDist = 2;
						}
						else{
							returnDist = 3;
						}
					}
				}
			}
		}
		return returnDist;
	}
	
	//colors should be input by priority position (F/B -> T/D -> L/R)
	public double calculateDistCorner(int color1, int color2, int color3, int currX, int currY, int currZ){
		int[] correctPos = new int[]{-1,-1,-1};
		int[] current = new int[]{currX,currY,currZ};
		int dir = 0;
		int returnDist = 0;
		int tmp = 0;
		if(color1 == 0){
			correctPos[2] = 2;
		}
		else if(color1 == 1){
			correctPos[1] = 0;
		}
		else if(color1 == 2){
			correctPos[0] = 0;
			dir = 0;
		}
		else if(color1 == 3){
			correctPos[1] = 2;
		}
		else if(color1 == 4){
			correctPos[0] = 2;
			dir = 0;
		}
		else if(color1 == 5){
			correctPos[2] = 0;
		}
		
		if(color2 == 0){
			correctPos[2] = 2;
		}
		else if(color2 == 1){
			correctPos[1] = 0;
		}
		else if(color2 == 2){
			correctPos[0] = 0;
			dir = 1;
		}
		else if(color2 == 3){
			correctPos[1] = 2;
		}
		else if(color2 == 4){
			correctPos[0] = 2;
			dir = 1;
		}
		else if(color2 == 5){
			correctPos[2] = 0;
		}
		
		if(color3 == 0){
			correctPos[2] = 2;
		}
		else if(color3 == 1){
			correctPos[1] = 0;
		}
		else if(color3 == 2){
			correctPos[0] = 0;
			dir = 2;
		}
		else if(color3 == 3){
			correctPos[1] = 2;
		}
		else if(color3 == 4){
			correctPos[0] = 2;
			dir = 2;
		}
		else if(color3 == 5){
			correctPos[2] = 0;
		}
		
		for(int i =0; i < 3; i++){
			tmp += (Math.abs(correctPos[i] - current[i]));
		}
		
		if(tmp == 0){
			if(dir == 0){
				returnDist = 0;
			}
			else{
				returnDist = 2;
			}
		}
		else if(tmp == 1){
			if(dir == 2){
				returnDist = 3;
			}
			else{
				returnDist = 1; 
			}
		}
		else{
			returnDist = tmp;
		}
		
		return returnDist;
		
	}
	
	public double md3d(int[][] cube){
		double cornerSum = 0;
		double edgeSum = 0;
		double dist;
		/* (0,0,0) is the corner of FRD 
		 * x goes F->B, y goes  R->L, z goes D->T
		 * Orientation Priority for edges: Orientation for Red/Orange, then Blue/Green 
		 * Orientation 0 if color is aligned on correct axis, 1 if not
		 * 2/4 -> 1/3 -> 0/5 
		 * (1,0,0) {[3][7],[5][5]}
		 * (1,2,0) {[1][7],[5][3]}
		 * (1,0,2) {[3][1],[0][5]}
		 * (1,2,2) {[1][1],[0][3]}
		 * (0,1,0) {[2][7],[5][1]}
		 * (2,1,0) {[4][7],[5][7]}
		 * (0,1,2) {[2][1],[0][7]}
		 * (2,1,2) {[4][1],[0][1]}
		 * (0,0,1) {[2][5],[3][3]}
		 * (2,0,1) {[4][3],[3][5]}
		 * (0,2,1) {[2][3],[1][5]}
		 * (2,2,1) {[4][5],[1][3]}
		 * Priority for corners: Red/Orange ([2][X] or [4][X])
		 * 2/4 -> 0/5 -> 1/3
		 * Orientation 0 if R/O color is aligned on x axis, 1 if on Z, 2 if on Y
		 * (0,0,0) {[2][8],[3][6],[5][2]}
		 * (2,0,0) {[3][8],[4][6],[5][8]}
		 * (0,2,0) {[1][8],[2][6],[5][0]}
		 * (0,0,2) {[0][8],[2][2],[3][0]}
		 * (2,2,0) {[1][6],[4][8],[5][6]}
		 * (2,0,2) {[0][2],[3][2],[4][0]}
		 * (0,2,2) {[0][6],[1][2],[2][0]}
		 * (2,2,2) {[0][0],[1][0],[4][2]}
		 */
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[3][7],cube[5][5],1,0,0);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[1][7],cube[5][3],1,2,0);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[3][1],cube[0][5],1,0,2);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[1][1],cube[0][3],1,2,2);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[2][7],cube[5][1],0,1,0);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[4][7],cube[5][7],2,1,0);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[2][1],cube[0][7],0,1,2);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[4][1],cube[0][1],2,1,2);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[2][5],cube[3][3],0,0,1);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[4][3],cube[3][5],2,0,1);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[2][3],cube[1][5],0,2,1);
		edgeSum+=CubeDomain.this.calculateDistEdge(cube[4][5],cube[1][3],2,2,1);
		
		edgeSum = edgeSum/4;
		
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[2][8],cube[5][2],cube[3][6],0,0,0);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[4][6],cube[5][8],cube[3][8],2,0,0);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[2][6],cube[5][0],cube[1][8],0,2,0);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[2][2],cube[0][8],cube[3][0],0,0,2);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[4][8],cube[5][6],cube[1][6],2,2,0);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[4][0],cube[0][2],cube[3][2],2,0,2);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[2][0],cube[0][6],cube[1][2],0,2,2);
		cornerSum+=CubeDomain.this.calculateDistCorner(cube[4][2],cube[0][0],cube[1][0],2,2,2);
		
		cornerSum = cornerSum/4;
		
		dist = edgeSum + cornerSum;
		
		return dist;
	}
	
	public static State getInitialState(Domain domain){
		State s = new MutableState();
		ObjectInstance cube = new MutableObjectInstance(domain.getObjectClass(CLASSCUBE), "cube0");
		cube.setValue(ATTTOP, new int[]{0,0,0,0,0,0,0,0,0});
		cube.setValue(ATTLEFT, new int[]{1,1,1,1,1,1,1,1,1});
		cube.setValue(ATTFRONT, new int[]{2,2,2,2,2,2,2,2,2});
		cube.setValue(ATTRIGHT, new int[]{3,3,3,3,3,3,3,3,3});
		cube.setValue(ATTBACK, new int[]{4,4,4,4,4,4,4,4,4});
		cube.setValue(ATTDOWN, new int[]{5,5,5,5,5,5,5,5,5});
		
		s.addObject(cube);

		return s;
	}

	public static State getTestState(Domain domain){
		State s = new MutableState();
		ObjectInstance cube = new MutableObjectInstance(domain.getObjectClass(CLASSCUBE), "cube0");
		cube.setValue(ATTTOP, new int[]{0,0,0,0,0,0,0,0,0});
		cube.setValue(ATTLEFT, new int[]{1,1,1,1,1,1,1,1,1});
		cube.setValue(ATTFRONT, new int[]{2,2,2,2,2,2,2,2,2});
		cube.setValue(ATTRIGHT, new int[]{3,3,3,3,3,3,3,3,3});
		cube.setValue(ATTBACK, new int[]{4,4,4,4,4,4,4,4,4});
		cube.setValue(ATTDOWN, new int[]{5,5,5,5,5,5,5,5,5});
		int[] curTop = cube.getIntArrayValForAttribute(ATTTOP);
		int[] curLeft = cube.getIntArrayValForAttribute(ATTLEFT);
		int[] curFront = cube.getIntArrayValForAttribute(ATTFRONT);
		int[] curRight = cube.getIntArrayValForAttribute(ATTRIGHT);
		int[] curBack = cube.getIntArrayValForAttribute(ATTBACK);
		int[] curDown = cube.getIntArrayValForAttribute(ATTDOWN);
		int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};
		
		int[][] newCube;
		RubiksCube rubiks = new RubiksCube();
		rubiks.turn(cubeArray, 0, true, false);
		rubiks.turn(cubeArray, 5, true, false);
		rubiks.turn(cubeArray, 3, true, false);
		rubiks.turn(cubeArray, 5, false, false);
		rubiks.turn(cubeArray, 3, false, false);
		rubiks.turn(cubeArray, 5, true, false);
		rubiks.turn(cubeArray, 3, true, false);
		rubiks.turn(cubeArray, 5, false, false);
		rubiks.turn(cubeArray, 3, false, false);
		rubiks.turn(cubeArray, 0, true, false);
		rubiks.turn(cubeArray, 0, true, false);
		rubiks.turn(cubeArray, 5, true, false);
		rubiks.turn(cubeArray, 3, true, false);
		rubiks.turn(cubeArray, 5, false, false);
		rubiks.turn(cubeArray, 3, false, false);
		rubiks.turn(cubeArray, 5, true, false);
		rubiks.turn(cubeArray, 3, true, false);
		rubiks.turn(cubeArray, 5, false, false);
		rubiks.turn(cubeArray, 3, false, false);
		rubiks.turn(cubeArray, 0, true, false);
		rubiks.turn(cubeArray, 5, true, false);
		rubiks.turn(cubeArray, 3, true, false);
		rubiks.turn(cubeArray, 5, false, false);
		rubiks.turn(cubeArray, 3, false, false);
		rubiks.turn(cubeArray, 5, true, false);
		rubiks.turn(cubeArray, 3, true, false);
		rubiks.turn(cubeArray, 5, false, false);
		rubiks.turn(cubeArray, 3, false, false);
		
		newCube = rubiks.returnCube();
		
		cube.setValue(ATTTOP, newCube[0]);
		cube.setValue(ATTLEFT, newCube[1]);
		cube.setValue(ATTFRONT, newCube[2]);
		cube.setValue(ATTRIGHT, newCube[3]);
		cube.setValue(ATTBACK, newCube[4]);
		cube.setValue(ATTDOWN, newCube[5]);

		s.addObject(cube);

		return s;
	}
	
	public static State getExampleState(Domain domain){
		State s = new MutableState();
		ObjectInstance cube = new MutableObjectInstance(domain.getObjectClass(CLASSCUBE), "cube0");
		cube.setValue(ATTTOP, new int[]{0,0,0,0,0,0,0,0,0});
		cube.setValue(ATTLEFT, new int[]{4,4,4,1,1,1,1,1,1});
		cube.setValue(ATTFRONT, new int[]{1,1,1,2,2,2,2,2,2});
		cube.setValue(ATTRIGHT, new int[]{2,2,2,3,3,3,3,3,3});
		cube.setValue(ATTBACK, new int[]{3,3,3,4,4,4,4,4,4});
		cube.setValue(ATTDOWN, new int[]{5,5,5,5,5,5,5,5,5});
		
		s.addObject(cube);

		return s;
	}
	
	public static State getExampleState(Domain domain, int seed){
		State s = new MutableState();
		ObjectInstance cube = new MutableObjectInstance(domain.getObjectClass(CLASSCUBE), "cube0");
		cube.setValue(ATTTOP, new int[]{0,0,0,0,0,0,0,0,0});
		cube.setValue(ATTLEFT, new int[]{1,1,1,1,1,1,1,1,1});
		cube.setValue(ATTFRONT, new int[]{2,2,2,2,2,2,2,2,2});
		cube.setValue(ATTRIGHT, new int[]{3,3,3,3,3,3,3,3,3});
		cube.setValue(ATTBACK, new int[]{4,4,4,4,4,4,4,4,4});
		cube.setValue(ATTDOWN, new int[]{5,5,5,5,5,5,5,5,5});
		int[] curTop = cube.getIntArrayValForAttribute(ATTTOP);
		int[] curLeft = cube.getIntArrayValForAttribute(ATTLEFT);
		int[] curFront = cube.getIntArrayValForAttribute(ATTFRONT);
		int[] curRight = cube.getIntArrayValForAttribute(ATTRIGHT);
		int[] curBack = cube.getIntArrayValForAttribute(ATTBACK);
		int[] curDown = cube.getIntArrayValForAttribute(ATTDOWN);
		int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};
		
		int[][] newCube;
		Random rnd = new Random(seed);
		RubiksCube rubiks = new RubiksCube();
		
		for(int i =0; i < 45; i++){
			int dir = (int)(rnd.nextDouble()*5);
			rubiks.turn(cubeArray, dir, false, false);
		}
		newCube = rubiks.returnCube();
		
		cube.setValue(ATTTOP, newCube[0]);
		cube.setValue(ATTLEFT, newCube[1]);
		cube.setValue(ATTFRONT, newCube[2]);
		cube.setValue(ATTRIGHT, newCube[3]);
		cube.setValue(ATTBACK, newCube[4]);
		cube.setValue(ATTDOWN, newCube[5]);

		s.addObject(cube);

		return s;
	}

	public static State getExampleState(Domain domain, int seed , int numTurns){
		State s = new MutableState();
		ObjectInstance cube = new MutableObjectInstance(domain.getObjectClass(CLASSCUBE), "cube0");
		cube.setValue(ATTTOP, new int[]{0,0,0,0,0,0,0,0,0});
		cube.setValue(ATTLEFT, new int[]{1,1,1,1,1,1,1,1,1});
		cube.setValue(ATTFRONT, new int[]{2,2,2,2,2,2,2,2,2});
		cube.setValue(ATTRIGHT, new int[]{3,3,3,3,3,3,3,3,3});
		cube.setValue(ATTBACK, new int[]{4,4,4,4,4,4,4,4,4});
		cube.setValue(ATTDOWN, new int[]{5,5,5,5,5,5,5,5,5});
		int[] curTop = cube.getIntArrayValForAttribute(ATTTOP);
		int[] curLeft = cube.getIntArrayValForAttribute(ATTLEFT);
		int[] curFront = cube.getIntArrayValForAttribute(ATTFRONT);
		int[] curRight = cube.getIntArrayValForAttribute(ATTRIGHT);
		int[] curBack = cube.getIntArrayValForAttribute(ATTBACK);
		int[] curDown = cube.getIntArrayValForAttribute(ATTDOWN);
		int[][] cubeArray = new int[][]{curTop,curLeft,curFront,curRight,curBack,curDown};
		
		int[][] newCube;
		Random rnd = new Random(seed);
		RubiksCube rubiks = new RubiksCube();
		
		int tmp = 7;
		
		
		for(int i =0; i < numTurns; i++){
			int dir = (int)(rnd.nextDouble()*5);
			while(tmp == dir){
				dir = (int)(rnd.nextDouble()*5);
			}
			tmp = dir;
			rubiks.turn(cubeArray, dir, rnd.nextBoolean(), rnd.nextBoolean());
		}
		newCube = rubiks.returnCube();
		
		cube.setValue(ATTTOP, newCube[0]);
		cube.setValue(ATTLEFT, newCube[1]);
		cube.setValue(ATTFRONT, newCube[2]);
		cube.setValue(ATTRIGHT, newCube[3]);
		cube.setValue(ATTBACK, newCube[4]);
		cube.setValue(ATTDOWN, newCube[5]);

		s.addObject(cube);

		return s;
	}

	public class WallPainter implements StaticPainter{

		public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
			
			//walls will be filled in black
			g2.setColor(Color.BLACK);
			
			//set up floats for the width and height of our domain
//			float fWidth = 12;
//			float fHeight = 9;
			
			//determine the width of a single cell on our canvas 
			//such that the whole map can be painted
//			float width = cWidth / fWidth;
//			float height = cHeight / fHeight;

			g2.fill(new Rectangle2D.Float(0, 0, cWidth, cHeight));
			
		}
		

	}
	
	public class CubePainter implements ObjectPainter{

		public void paintObject(Graphics2D g2, State s, ObjectInstance ob,
								float cWidth, float cHeight) {

			//agent will be filled in gray
			g2.setColor(Color.GRAY);

			//set up floats for the width and height of our domain
			float fWidth = 12;
			float fHeight = 9;

			//determine the width of a single cell on our canvas
			//such that the whole map can be painted
			float width = cWidth / fWidth;
			float height = cHeight / fHeight;

			int[] topVal = ob.getIntArrayValForAttribute(ATTTOP);
			int[] leftVal = ob.getIntArrayValForAttribute(ATTLEFT);
			int[] frontVal = ob.getIntArrayValForAttribute(ATTFRONT);
			int[] rightVal = ob.getIntArrayValForAttribute(ATTRIGHT);
			int[] backVal = ob.getIntArrayValForAttribute(ATTBACK);
			int[] downVal = ob.getIntArrayValForAttribute(ATTDOWN);
			//TOP
			for(int i = 3; i < 6; i++){
				for(int j = 0; j< 3; j++){
					int currColor = downVal[(i-3) + Math.abs(j-2)*3];
					g2.setColor(CubeDomain.this.getColor(currColor));
					float rx = i*width;
					float ry = cHeight - height - j*height;
					g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				}
			}
			//LEFT
			for(int i = 0; i < 3; i++){
				for(int j = 3; j< 6; j++){
					int currColor = leftVal[(i) + Math.abs(j-5)*3];
					g2.setColor(CubeDomain.this.getColor(currColor));
					float rx = i*width;
					float ry = cHeight - height - j*height;
					g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				}
			}
			//FRONT
			for(int i = 3; i < 6; i++){
				for(int j = 3; j< 6; j++){
					int currColor = frontVal[(i-3) + Math.abs(j-5)*3];
					g2.setColor(CubeDomain.this.getColor(currColor));
					float rx = i*width;
					float ry = cHeight - height - j*height;
					g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				}
			}
			//RIGHT
			for(int i = 6; i < 9; i++){
				for(int j = 3; j< 6; j++){
					int currColor = rightVal[(i-6) + Math.abs(j-5)*3];
					g2.setColor(CubeDomain.this.getColor(currColor));
					float rx = i*width;
					float ry = cHeight - height - j*height;
					g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				}
			}
			//BACK
			for(int i = 9; i < 12; i++){
				for(int j = 3; j< 6; j++){
					int currColor = backVal[(i-9) + Math.abs(j-5)*3];
					g2.setColor(CubeDomain.this.getColor(currColor));
					float rx = i*width;
					float ry = cHeight - height - j*height;
					g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				}
			}
			//DOWN
			for(int i = 3; i < 6; i++){
				for(int j = 6; j< 9; j++){
					int currColor = topVal[(i-3) + Math.abs(j-8)*3];
					g2.setColor(CubeDomain.this.getColor(currColor));
					float rx = i*width;
					float ry = cHeight - height - j*height;
					g2.fill(new Ellipse2D.Float(rx, ry, width, height));
				}
			}
		}

	}
	
	public Color getColor(int colorVal){
		if(colorVal == 0){
			return Color.WHITE;
		}
		else if(colorVal == 1){
			return Color.BLUE;
		}
		else if(colorVal == 2){
			return Color.RED;
		}
		else if(colorVal == 3){
			return Color.GREEN;
		}
		else if(colorVal == 4){
			return Color.ORANGE;
		}
		else if(colorVal == 5){
			return Color.YELLOW;
		}
		return Color.PINK;//If pink shows up, we have a problem
	}
	
	public static class ExampleRF implements RewardFunction {

		int[] goalTop;
		int[] goalLeft;
		int[] goalFront;
		int[] goalRight;
		int[] goalBack;
		int[] goalDown;

		public ExampleRF(){
			this.goalTop = new int[]{0,0,0,0,0,0,0,0,0};
			this.goalLeft = new int[]{1,1,1,1,1,1,1,1,1};
			this.goalFront = new int[]{2,2,2,2,2,2,2,2,2};
			this.goalRight = new int[]{3,3,3,3,3,3,3,3,3};
			this.goalBack = new int[]{4,4,4,4,4,4,4,4,4};
			this.goalDown = new int[]{5,5,5,5,5,5,5,5,5};
		}

		public double reward(State s, GroundedAction a, State sprime) {

			//get location of agent in next state
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);

			//are they at goal location?
			if(Arrays.equals(goalTop, aTop) && Arrays.equals(goalLeft, aLeft) && Arrays.equals(goalFront, aFront) && Arrays.equals(goalRight, aRight) && Arrays.equals(goalBack, aBack) && Arrays.equals(goalDown, aDown)){
				return 10000.;
			}

			return -1;
		}


	}
	
	public static class TestRF implements RewardFunction {

		int[] goalTop;
		int[] goalLeft;
		int[] goalFront;
		int[] goalRight;
		int[] goalBack;
		int[] goalDown;

		public TestRF(){
			this.goalTop = new int[]{0,0,0,0,0,0,0,0,0};
			this.goalLeft = new int[]{1,1,1,1,1,1,1,1,1};
			this.goalFront = new int[]{2,2,2,2,2,2,2,2,2};
			this.goalRight = new int[]{3,3,3,3,3,3,3,3,3};
			this.goalBack = new int[]{4,4,4,4,4,4,4,4,4};
			this.goalDown = new int[]{5,5,5,5,5,5,5,5,5};
		}

		public double reward(State s, GroundedAction a, State sprime) {
			CubeDomain cdom = new CubeDomain();
			//get location of agent in next state
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			int[][] cubeArray = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
//			ObjectInstance aPrime = sprime.getFirstObjectOfClass(CLASSCUBE);
//			int[] pTop = aPrime.getIntArrayValForAttribute(ATTTOP);
//			int[] pLeft = aPrime.getIntArrayValForAttribute(ATTLEFT);
//			int[] pFront = aPrime.getIntArrayValForAttribute(ATTFRONT);
//			int[] pRight = aPrime.getIntArrayValForAttribute(ATTRIGHT);
//			int[] pBack = aPrime.getIntArrayValForAttribute(ATTBACK);
//			int[] pDown = aPrime.getIntArrayValForAttribute(ATTDOWN);
//			int[][] cubePrime = new int[][]{pTop,pLeft,pFront,pRight,pBack,pDown};
		
			
			//are they at goal location?
			if(Arrays.equals(goalTop, aTop) && Arrays.equals(goalLeft, aLeft) && Arrays.equals(goalFront, aFront) && Arrays.equals(goalRight, aRight) && Arrays.equals(goalBack, aBack) && Arrays.equals(goalDown, aDown)){
				return 10000.;
			}
//			if(cdom.md3d(cubePrime) < cdom.md3d(cubeArray)){
//					return 0;
//			}
			return (0 - cdom.md3d(cubeArray));
			//return -1;
		}


	}
	
	protected class thirdCross extends PropositionalFunction {

		public thirdCross(Domain domain){
			super("Third Layer Cross Solved", domain, new String []{CLASSCUBE});
		}

		@Override
		public boolean isTrue(State s, String[] params) {
			ObjectInstance agent = s.getObject(params[0]);

			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);

			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			return CubeDomain.thirdCrossSolved(cube);
			
			
		}



	}
	
	protected class doubleLayer extends PropositionalFunction {

		public doubleLayer(Domain domain){
			super("Double Layer Solved", domain, new String []{CLASSCUBE});
		}

		@Override
		public boolean isTrue(State s, String[] params) {
			ObjectInstance agent = s.getObject(params[0]);

			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);

			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			return CubeDomain.twoLayersSolved(cube);
			
			
		}



	}
	
	protected class singleLayer extends PropositionalFunction {

		public singleLayer(Domain domain){
			super("Single Layer Solved", domain, new String []{CLASSCUBE});
		}

		@Override
		public boolean isTrue(State s, String[] params) {
			ObjectInstance agent = s.getObject(params[0]);

			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);

			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			return CubeDomain.oneLayerSolved(cube);
			
			
		}



	}

	protected class firstCross extends PropositionalFunction {

		public firstCross(Domain domain){
			super("First Layer Cross Solved", domain, new String []{CLASSCUBE});
		}

		@Override
		public boolean isTrue(State s, String[] params) {
			ObjectInstance agent = s.getObject(params[0]);

			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);

			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			return CubeDomain.firstCrossSolved(cube);
			
			
		}



	}
	
	protected class isSolved extends PropositionalFunction {

		public isSolved(Domain domain){
			super("Fully Solved", domain, new String []{CLASSCUBE});
		}

		@Override
		public boolean isTrue(State s, String[] params) {
			int[][] solvedCube = new int[][]{{0,0,0,0,0,0,0,0,0},{1,1,1,1,1,1,1,1,1},{2,2,2,2,2,2,2,2,2},
				{3,3,3,3,3,3,3,3,3},{4,4,4,4,4,4,4,4,4},{5,5,5,5,5,5,5,5,5}};
				
			ObjectInstance agent = s.getObject(params[0]);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};

			return Arrays.deepEquals(solvedCube, cube);
		}



	}
	
	public static boolean firstCrossSolved(int[][] inputCube){
		boolean tripped = false;
		for(int i = 0; i < 6; i++){
			for(int j = 1; j < 8; j+=2){
				if(inputCube[i][4] != inputCube[i][j]){
					tripped = true;
				}
			}
			if(tripped == false){
				return true;
			}
			tripped = false;
		}
		return false;
	}
	
	public static boolean oneLayerSolved(int[][] inputCube){
		int[][] solved = new int[][]{{0,0,0,0,0,0,0,0,0},{1,1,1,1,1,1,1,1,1},{2,2,2,2,2,2,2,2,2},
			{3,3,3,3,3,3,3,3,3},{4,4,4,4,4,4,4,4,4},{5,5,5,5,5,5,5,5,5}};
		boolean tripped = false;
		for(int k = 0; k < 1;){
			if(Arrays.equals(inputCube[0],solved[0])){
				for(int i = 1; i< 5;i++){
					for(int j = 0; j < 3; j++){
						if(inputCube[i][4] != inputCube[i][j]){
							tripped = true;
							break;
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			if(Arrays.equals(inputCube[1],solved[1])){
				for(int i = 0; i< 6;i++){
					if(i!= 1 || i != 3 ){
						if(i!= 4){
							for(int j = 0; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
						else{
							for(int j = 2; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			if(Arrays.equals(inputCube[2],solved[2])){
				for(int i = 0; i< 6;i++){
					if(i== 0){
						for(int j = 6; j < 9; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 1){
						for(int j = 2; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 3){
						for(int j = 0; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 5){
						for(int j = 0; j < 3; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			
			}
			tripped = false;
			if(Arrays.equals(inputCube[3],solved[3])){
				for(int i = 0; i< 6;i++){
					if(i!= 1 || i != 3 ){
						if(i!= 4){
							for(int j = 2; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
						else{
							for(int j = 0; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			if(Arrays.equals(inputCube[4],solved[4])){
				for(int i = 0; i< 6;i++){
					if(i== 0){
						for(int j = 0; j < 3; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 1){
						for(int j = 0; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 3){
						for(int j = 2; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 5){
						for(int j = 6; j < 9; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			if(Arrays.equals(inputCube[5],solved[5])){
				for(int i = 1; i< 5;i++){
					for(int j = 6; j < 9; j++){
						if(inputCube[i][4] != inputCube[i][j]){
							tripped = true;
							break;
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean twoLayersSolved(int[][] inputCube){
		int[][] solved = new int[][]{{0,0,0,0,0,0,0,0,0},{1,1,1,1,1,1,1,1,1},{2,2,2,2,2,2,2,2,2},
			{3,3,3,3,3,3,3,3,3},{4,4,4,4,4,4,4,4,4},{5,5,5,5,5,5,5,5,5}};
		boolean tripped = false;
		for(int k = 0; k < 1;){
			if(Arrays.equals(inputCube[0],solved[0])){
				for(int i = 1; i< 5;i++){
					for(int j = 0; j < 6; j++){
						if(inputCube[i][4] != inputCube[i][j]){
							tripped = true;
							break;
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			//////////
			if(Arrays.equals(inputCube[1],solved[1])){
				for(int i = 0; i< 6;i++){
					if(i!= 1 || i != 3 ){
						if(i!= 4){
							for(int j = 0; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
						else{
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 2; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			////////////
			if(Arrays.equals(inputCube[2],solved[2])){
				for(int i = 0; i< 6;i++){
					if(i== 0){
						for(int j = 3; j < 9; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 1){
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 2; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 3){
						for(int j = 0; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 5){
						for(int j = 0; j < 6; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			
			}
			tripped = false;
			////////////////
			if(Arrays.equals(inputCube[3],solved[3])){
				for(int i = 0; i< 6;i++){
					if(i!= 1 || i != 3 ){
						if(i!= 4){
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 2; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
						else{
							for(int j = 0; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			/////////////////
			if(Arrays.equals(inputCube[4],solved[4])){
				for(int i = 0; i< 6;i++){
					if(i== 0){
						for(int j = 0; j < 6; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 1){
						for(int j = 0; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 3){
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 2; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 5){
						for(int j = 3; j < 9; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			////////////////
			if(Arrays.equals(inputCube[5],solved[5])){
				for(int i = 1; i< 5;i++){
					for(int j = 3; j < 9; j++){
						if(inputCube[i][4] != inputCube[i][j]){
							tripped = true;
							break;
						}
					}
				}
				if(!tripped){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static boolean thirdCrossSolved(int[][] inputCube){
		int[][] solved = new int[][]{{0,0,0,0,0,0,0,0,0},{1,1,1,1,1,1,1,1,1},{2,2,2,2,2,2,2,2,2},
			{3,3,3,3,3,3,3,3,3},{4,4,4,4,4,4,4,4,4},{5,5,5,5,5,5,5,5,5}};
		boolean tripped = false;
		for(int k = 0; k < 1;){
			if(Arrays.equals(inputCube[0],solved[0])){
				for(int i = 1; i< 5;i++){
					for(int j = 0; j < 8; j++){
						if(j != 6){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				for(int i = 1; i<8 ;i+=2){
					if(inputCube[5][4] != inputCube[5][i]){
						tripped = true;
						break;
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			//////////
			if(Arrays.equals(inputCube[1],solved[1])){
				for(int i = 0; i< 6;i++){
					if(i!= 1 || i != 3 ){
						if(i!= 4){
							for(int j = 0; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
						else{
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 2; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
					}
				}
				if(inputCube[0][4] != inputCube[0][5]){
					tripped = true;
				}
				if(inputCube[2][4] != inputCube[2][5]){
					tripped = true;
				}
				if(inputCube[4][4] != inputCube[4][3]){
					tripped = true;
				}
				if(inputCube[5][4] != inputCube[5][5]){
					tripped = true;
				}
				for(int i = 1; i<8 ;i+=2){
					if(inputCube[3][4] != inputCube[3][i]){
						tripped = true;
						break;
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			////////////
			if(Arrays.equals(inputCube[2],solved[2])){
				for(int i = 0; i< 6;i++){
					if(i== 0){
						for(int j = 3; j < 9; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 1){
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 2; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 3){
						for(int j = 0; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 5){
						for(int j = 0; j < 6; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				if(inputCube[0][4] != inputCube[0][1]){
					tripped = true;
				}
				if(inputCube[1][4] != inputCube[1][3]){
					tripped = true;
				}
				if(inputCube[3][4] != inputCube[3][5]){
					tripped = true;
				}
				if(inputCube[5][4] != inputCube[5][7]){
					tripped = true;
				}
				for(int i = 1; i<8 ;i+=2){
					if(inputCube[4][4] != inputCube[4][i]){
						tripped = true;
						break;
					}
				}
				if(!tripped){
					return true;
				}
			
			}
			tripped = false;
			////////////////
			if(Arrays.equals(inputCube[3],solved[3])){
				for(int i = 0; i< 6;i++){
					if(i!= 1 || i != 3 ){
						if(i!= 4){
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 2; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
						else{
							for(int j = 0; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
							for(int j = 1; j < 9; j+=3){
								if(inputCube[i][4] != inputCube[i][j]){
									tripped = true;
									break;
								}
							}
						}
					}
				}
				if(inputCube[0][4] != inputCube[0][3]){
					tripped = true;
				}
				if(inputCube[2][4] != inputCube[2][3]){
					tripped = true;
				}
				if(inputCube[4][4] != inputCube[4][5]){
					tripped = true;
				}
				if(inputCube[5][4] != inputCube[5][3]){
					tripped = true;
				}
				for(int i = 1; i<8 ;i+=2){
					if(inputCube[1][4] != inputCube[1][i]){
						tripped = true;
						break;
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			/////////////////
			if(Arrays.equals(inputCube[4],solved[4])){
				for(int i = 0; i< 6;i++){
					if(i== 0){
						for(int j = 0; j < 6; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 1){
						for(int j = 0; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 3){
						for(int j = 1; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
						for(int j = 2; j < 9; j+=3){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
					else if(i == 5){
						for(int j = 3; j < 9; j+=1){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				if(inputCube[0][4] != inputCube[0][7]){
					tripped = true;
				}
				if(inputCube[1][4] != inputCube[1][5]){
					tripped = true;
				}
				if(inputCube[3][4] != inputCube[3][3]){
					tripped = true;
				}
				if(inputCube[5][4] != inputCube[5][1]){
					tripped = true;
				}
				for(int i = 1; i<8 ;i+=2){
					if(inputCube[2][4] != inputCube[2][i]){
						tripped = true;
						break;
					}
				}
				if(!tripped){
					return true;
				}
			}
			tripped = false;
			////////////////
			if(Arrays.equals(inputCube[5],solved[5])){
				for(int i = 1; i< 5;i++){
					for(int j = 1; j < 9; j++){
						if(j!=2){
							if(inputCube[i][4] != inputCube[i][j]){
								tripped = true;
								break;
							}
						}
					}
				}
				for(int i = 1; i<8 ;i+=2){
					if(inputCube[0][4] != inputCube[0][i]){
						tripped = true;
						break;
					}
				}
				if(!tripped){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	public static class ExampleTF implements TerminalFunction {

		int[] goalTop;
		int[] goalLeft;
		int[] goalFront;
		int[] goalRight;
		int[] goalBack;
		int[] goalDown;

		public ExampleTF(){
			this.goalTop = new int[]{0,0,0,0,0,0,0,0,0};
			this.goalLeft = new int[]{1,1,1,1,1,1,1,1,1};
			this.goalFront = new int[]{2,2,2,2,2,2,2,2,2};
			this.goalRight = new int[]{2,3,3,2,3,3,2,3,3};
			this.goalBack = new int[]{4,4,4,4,4,4,4,4,4};
			this.goalDown = new int[]{5,5,5,5,5,5,5,5,5};
		}

		public boolean isTerminal(State s) {

			//get location of agent in next state
			
			int[][] solvedCube = new int[][]{{0,0,0,0,0,0,0,0,0},{1,1,1,1,1,1,1,1,1},{2,2,2,2,2,2,2,2,2},
				{3,3,3,3,3,3,3,3,3},{4,4,4,4,4,4,4,4,4},{5,5,5,5,5,5,5,5,5}};
				
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};

			return Arrays.deepEquals(solvedCube, cube);
		}
	}
	
	public static class singleLayerTF implements TerminalFunction {

		public singleLayerTF(){
		
		}

		public boolean isTerminal(State s) {

			//get location of agent in next state
			
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			
			return CubeDomain.oneLayerSolved(cube);
		}
	}
	
	public static class doubleLayerTF implements TerminalFunction {

		public doubleLayerTF(){
		
		}

		public boolean isTerminal(State s) {

			//get location of agent in next state
			
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			
			return CubeDomain.twoLayersSolved(cube);
		}
	}
	
	public static class thirdCrossTF implements TerminalFunction {

		public thirdCrossTF(){
		
		}

		public boolean isTerminal(State s) {

			//get location of agent in next state
			
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			
			return CubeDomain.thirdCrossSolved(cube);
		}
	}
	
	public static class firstCrossTF implements TerminalFunction {

		public firstCrossTF(){
		
		}

		public boolean isTerminal(State s) {

			//get location of agent in next state
			
			ObjectInstance agent = s.getFirstObjectOfClass(CLASSCUBE);
			int[] aTop = agent.getIntArrayValForAttribute(ATTTOP);
			int[] aLeft = agent.getIntArrayValForAttribute(ATTLEFT);
			int[] aFront = agent.getIntArrayValForAttribute(ATTFRONT);
			int[] aRight = agent.getIntArrayValForAttribute(ATTRIGHT);
			int[] aBack = agent.getIntArrayValForAttribute(ATTBACK);
			int[] aDown = agent.getIntArrayValForAttribute(ATTDOWN);
			
			int[][] cube = new int[][]{aTop,aLeft,aFront,aRight,aBack,aDown};
			
			
			return CubeDomain.firstCrossSolved(cube);
		}
	}
	
	public StateRenderLayer getStateRenderLayer(){
		StateRenderLayer rl = new StateRenderLayer();
		rl.addStaticPainter(new WallPainter());
		rl.addObjectClassPainter(CLASSCUBE, new CubePainter());

		return rl;
	}
	
	public Visualizer getVisualizer(){
		return new Visualizer(this.getStateRenderLayer());
	}
	
	public static void main(String[] args){
		CubeDomain gen = new CubeDomain();
		Domain domain = gen.generateDomain();

		State initialState = CubeDomain.getInitialState(domain);
		//State initialState = CubeDomain.getExampleState(domain,54326,1);
		//State initialState = CubeDomain.getTestState(domain);
		
		RewardFunction rf = new ExampleRF();
		TerminalFunction tf = new ExampleTF();
		
		SimulatedEnvironment env = new SimulatedEnvironment(domain, rf, tf, initialState);
		
		Visualizer v = gen.getVisualizer();
		VisualExplorer exp = new VisualExplorer(domain, env, v);
		
		
		exp.addKeyAction("q",ACTIONU);
		exp.addKeyAction("w",ACTIONL);
		exp.addKeyAction("e",ACTIONF);
		exp.addKeyAction("r",ACTIONR);
		exp.addKeyAction("t",ACTIONB);
		exp.addKeyAction("y",ACTIOND);
		exp.addKeyAction("a",ACTIONUPRIME);
		exp.addKeyAction("s",ACTIONLPRIME);
		exp.addKeyAction("d",ACTIONFPRIME);
		exp.addKeyAction("f",ACTIONRPRIME);
		exp.addKeyAction("g",ACTIONBPRIME);
		exp.addKeyAction("h",ACTIONDPRIME);
		exp.addKeyAction("z",ACTIONU2);
		exp.addKeyAction("x",ACTIONL2);
		exp.addKeyAction("c",ACTIONF2);
		exp.addKeyAction("v",ACTIONR2);
		exp.addKeyAction("b",ACTIONB2);
		exp.addKeyAction("n",ACTIOND2);
		
		exp.initGUI();
		
	

	}
}