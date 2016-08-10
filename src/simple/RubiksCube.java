package simple;

public class RubiksCube{
//	private static final int WHITE = 0;
//	private static final int BLUE = 1;
//	private static final int RED = 2;
//	private static final int GREEN = 3;
//	private static final int ORANGE = 4;
//	private static final int YELLOW = 5;
	private static final int TOP = 0;
	private static final int LEFT = 1;
	private static final int FRONT = 2;
	private static final int RIGHT = 3;
	private static final int BACK = 4;
	private static final int DOWN = 5;
	private int[][] layout; //goes in order of Top Left Front Right Back Down
	public RubiksCube(){
	/*Position 4 on each face will never change
		  0 1 2
		  3 4 5 
		  6 7 8
	0 1 2 0 1 2 0 1 2 0 1 2
	3 4 5 3 4 5 3 4 5 3 4 5 
	6 7 8 6 7 8 6 7 8 6 7 8	
		  0 1 2
		  3 4 5 
		  6 7 8
	*/
		layout = new int[][]{ {0,0,0,0,0,0,0,0,0}
							,{1,1,1,1,1,1,1,1,1}
							,{2,2,2,2,2,2,2,2,2}
							,{3,3,3,3,3,3,3,3,3}
							,{4,4,4,4,4,4,4,4,4}
							,{5,5,5,5,5,5,5,5,5}};
//		layout = new int[][]{ {0,1,2,3,4,5,6,7,8}
//		,{0,1,2,3,4,5,6,7,8}
//		,{0,1,2,3,4,5,6,7,8}
//		,{0,1,2,3,4,5,6,7,8}
//		,{0,1,2,3,4,5,6,7,8}
//		,{0,1,2,3,4,5,6,7,8}};
    }
	public void turn(int[][] cube, int direction, boolean isPrime, boolean isTwice){
		layout = cube;
		if(isTwice){
			if(direction == 0){
				this.U2();
			}
			else if(direction == 1){
				this.L2();
			}
			else if(direction == 2){
				this.F2();
			}
			else if(direction == 3){
				this.R2();
			}
			else if(direction == 4){
				this.B2();
			}
			else if (direction == 5){
				this.D2();
			}
		}
		else{
			if(!isPrime){
				if(direction == 0){
					this.U();
				}
				else if(direction == 1){
					this.L();
				}
				else if(direction == 2){
					this.F();
				}
				else if(direction == 3){
					this.R();
				}
				else if(direction == 4){
					this.B();
				}
				else if (direction == 5){
					this.D();
				}
			}
			else{
				if(direction == 0){
					this.UPrime();
				}
				else if(direction == 1){
					this.LPrime();
				}
				else if(direction == 2){
					this.FPrime();
				}
				else if(direction == 3){
					this.RPrime();
				}
				else if(direction == 4){
					this.BPrime();
				}
				else if (direction == 5){
					this.DPrime();
				}
			}
		}
	}
	
	public void U2(){
		this.U();
		this.U();
	}
	
	public void L2(){
		this.L();
		this.L();
	}
	
	public void F2(){
		this.F();
		this.F();
	}

	public void R2(){
		this.R();
		this.R();
	}

	public void B2(){
		this.B();
		this.B();
	}

	public void D2(){
		this.D();
		this.D();
	}
	
	public void U(){
		int[] lftdata = {layout[LEFT][0],layout[LEFT][1], layout[LEFT][2]}; 
		for(int i = LEFT; i <= RIGHT; i++){
			for(int j =0; j<=2; j++){
				layout[i][j] = layout[i+1][j];
			}
		}
		layout[BACK][0] = lftdata[0];
		layout[BACK][1] = lftdata[1];
		layout[BACK][2] = lftdata[2];
		this.rotateFace(TOP);
		
	}
	
	public void UPrime(){
		//CHANGE THIS LATER
		this.U();
		this.U();
		this.U();
	}
	
	public void D(){
		int[] bckdata = {layout[BACK][6],layout[BACK][7], layout[BACK][8]}; 
		for(int i = BACK; i >= FRONT; i--){
			for(int j =6; j<=8; j++){
				layout[i][j] = layout[i-1][j];
			}
		}
		layout[LEFT][6] = bckdata[0];
		layout[LEFT][7] = bckdata[1];
		layout[LEFT][8] = bckdata[2];
		this.rotateFace(DOWN);
	}
	
	public void DPrime(){
		this.D();
		this.D();
		this.D();
	}
	
	public void B(){
		this.BPrime();
		this.BPrime();
		this.BPrime();
	}
	
	public void BPrime(){
		int[] rgtdata = {layout[RIGHT][2],layout[RIGHT][5], layout[RIGHT][8]}; 
		
		layout[RIGHT][2] = layout[TOP][0];
		layout[RIGHT][5] = layout[TOP][1];
		layout[RIGHT][8] = layout[TOP][2];
		layout[TOP][0] = layout[LEFT][6];
		layout[TOP][1] = layout[LEFT][3];
		layout[TOP][2] = layout[LEFT][0];
		layout[LEFT][6] = layout[DOWN][8];
		layout[LEFT][3] = layout[DOWN][7];
		layout[LEFT][0] = layout[DOWN][6];
		layout[DOWN][8] = rgtdata[0];
		layout[DOWN][7] = rgtdata[1];
		layout[DOWN][6] = rgtdata[2];
		this.rotateFacePrime(BACK);
	}
	
	public void F(){
		int[] lftdata = {layout[LEFT][2],layout[LEFT][5], layout[LEFT][8]};
		layout[LEFT][2] = layout[DOWN][0];
		layout[LEFT][5] = layout[DOWN][1];
		layout[LEFT][8] = layout[DOWN][2];
		layout[DOWN][0] = layout[RIGHT][6];
		layout[DOWN][1] = layout[RIGHT][3];
		layout[DOWN][2] = layout[RIGHT][0];
		layout[RIGHT][6] = layout[TOP][6];
		layout[RIGHT][3] = layout[TOP][7];
		layout[RIGHT][0] = layout[TOP][8];
		layout[TOP][6] = lftdata[0];
		layout[TOP][7] = lftdata[1];
		layout[TOP][8] = lftdata[2];
		this.rotateFace(FRONT);
	}
	
	public void FPrime(){
		this.F();
		this.F();
		this.F();
	}
	
	public void L(){
		int[] frtdata = {layout[FRONT][0],layout[FRONT][3], layout[FRONT][6]}; 
		layout[FRONT][0] = layout[TOP][0];
		layout[FRONT][3] = layout[TOP][3];
		layout[FRONT][6] = layout[TOP][6];
		layout[TOP][0] = layout[BACK][2];
		layout[TOP][3] = layout[BACK][5];
		layout[TOP][6] = layout[BACK][8];
		layout[BACK][2] = layout[DOWN][0];
		layout[BACK][5] = layout[DOWN][3];
		layout[BACK][8] = layout[DOWN][6];
		layout[DOWN][0] = frtdata[0];
		layout[DOWN][3] = frtdata[1];
		layout[DOWN][6] = frtdata[2];
		this.rotateFace(LEFT);
	}
	
	public void LPrime(){
		this.L();
		this.L();
		this.L();
	}
	
	public void R(){
		int[] frtdata = {layout[FRONT][2],layout[FRONT][5], layout[FRONT][8]}; 
		layout[FRONT][2] = layout[DOWN][2];
		layout[FRONT][5] = layout[DOWN][5];
		layout[FRONT][8] = layout[DOWN][8];
		layout[DOWN][2] = layout[BACK][6];
		layout[DOWN][5] = layout[BACK][3];
		layout[DOWN][8] = layout[BACK][0];
		layout[BACK][6] = layout[TOP][2];
		layout[BACK][3] = layout[TOP][5];
		layout[BACK][0] = layout[TOP][8];
		layout[TOP][2] = frtdata[0];
		layout[TOP][5] = frtdata[1];
		layout[TOP][8] = frtdata[2];
		this.rotateFace(RIGHT);
	}
	
	public void RPrime(){
		int[] frtdata = {layout[FRONT][2],layout[FRONT][5], layout[FRONT][8]}; 
		layout[FRONT][2] = layout[TOP][2];
		layout[FRONT][5] = layout[TOP][5];
		layout[FRONT][8] = layout[TOP][8];
		layout[TOP][2] = layout[BACK][6];
		layout[TOP][5] = layout[BACK][3];
		layout[TOP][8] = layout[BACK][0];
		layout[BACK][6] = layout[DOWN][2];
		layout[BACK][3] = layout[DOWN][5];
		layout[BACK][0] = layout[DOWN][8];
		layout[DOWN][2] = frtdata[0];
		layout[DOWN][5] = frtdata[1];
		layout[DOWN][8] = frtdata[2];
		this.rotateFacePrime(RIGHT);
	}
	
	public void printCube(){
		System.out.println("_ _ _ " + layout[0][0] + " " + layout[0][1] + " " + layout[0][2] + " _ _ _ _ _ _");
		System.out.println("_ _ _ " + layout[0][3] + " " + layout[0][4] + " " + layout[0][5] + " _ _ _ _ _ _");
		System.out.println("_ _ _ " + layout[0][6] + " " + layout[0][7] + " " + layout[0][8] + " _ _ _ _ _ _");
		System.out.println(layout[1][0] + " " + layout[1][1] + " " + layout[1][2] + " " + layout[2][0] + " " + layout[2][1] + " " + layout[2][2] + " " + layout[3][0] + " " + layout[3][1] + " " + layout[3][2] + " " + layout[4][0] + " " + layout[4][1] + " " + layout[4][2]);
		System.out.println(layout[1][3] + " " + layout[1][4] + " " + layout[1][5] + " " + layout[2][3] + " " + layout[2][4] + " " + layout[2][5] + " " + layout[3][3] + " " + layout[3][4] + " " + layout[3][5] + " " + layout[4][3] + " " + layout[4][4] + " " + layout[4][5]);
		System.out.println(layout[1][6] + " " + layout[1][7] + " " + layout[1][8] + " " + layout[2][6] + " " + layout[2][7] + " " + layout[2][8] + " " + layout[3][6] + " " + layout[3][7] + " " + layout[3][8] + " " + layout[4][6] + " " + layout[4][7] + " " + layout[4][8]);
		System.out.println("_ _ _ " + layout[5][0] + " " + layout[5][1] + " " + layout[5][2] + " _ _ _ _ _ _");
		System.out.println("_ _ _ " + layout[5][3] + " " + layout[5][4] + " " + layout[5][5] + " _ _ _ _ _ _");
		System.out.println("_ _ _ " + layout[5][6] + " " + layout[5][7] + " " + layout[5][8] + " _ _ _ _ _ _");
		System.out.println();
	}
	
	public void rotateFace(int face){
		int tmpStore0 = layout[face][0];
		int tmpStore1 = layout[face][1];
		int tmpStore2 = layout[face][2];
		int tmpStore5 = layout[face][5];
		layout[face][0] = layout[face][6]; 
		layout[face][1] = layout[face][3]; 
		layout[face][2] = tmpStore0; 
		layout[face][3] = layout[face][7];
		layout[face][5] = tmpStore1;
		layout[face][6] = layout[face][8];
		layout[face][7] = tmpStore5;
		layout[face][8] = tmpStore2;
	}
	
	public void rotateFacePrime(int face){
		int tmpStore0 = layout[face][0];
		int tmpStore1 = layout[face][1];
		int tmpStore3 = layout[face][3];
		int tmpStore6 = layout[face][6];
		layout[face][0] = layout[face][2]; 
		layout[face][1] = layout[face][5]; 
		layout[face][2] = layout[face][8]; 
		layout[face][3] = tmpStore1; 
		layout[face][5] = layout[face][7]; 
		layout[face][6] = tmpStore0;
		layout[face][7] = tmpStore3;
		layout[face][8] = tmpStore6; 
		
	}
	
	public int[][] returnCube(){
		return layout;
	}
	

}


/*
 * White->T->z=2
 * Blue->L->y=0
 * Red->F->x=0
 * Green->R->y=2
 * Orange->B->x=2
 * Yellow->D->z=0
 */
/* (0,0,0) is the corner of FRD 
 * x goes F->B, y goes  R->L, z goes D->T
 * Orientation Priority for edges: Orientation for Red/Orange, then Blue/Green 
 * Orientation 0 if color is aligned on correct axis, 1 if not
 * (1,0,0) {[3][7],[5][5]} 012,212
 * (1,2,0) {[1][7],[5][3]} 012,212
 * (1,0,2) {[3][1],[0][5]} 010,210
 * (1,2,2) {[1][1],[0][3]} 010,210
 * (0,1,0) {[2][7],[5][1]} 102,122
 * (2,1,0) {[4][7],[5][7]} 102,122
 * (0,1,2) {[2][1],[0][7]} 100,120
 * (2,1,2) {[4][1],[0][1]} 100,120
 * (0,0,1) {[2][5],[3][3]} 120,122
 * (2,0,1) {[4][3],[3][5]} 120,122
 * (0,2,1) {[2][3],[1][5]} 100,102
 * (2,2,1) {[4][5],[1][3]} 100,102
 * Priority for corners: Red/Orange ([2][X] or [4][X])
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
//[xCurr,yCurr,zCurr,x,y,z,orientation]






























