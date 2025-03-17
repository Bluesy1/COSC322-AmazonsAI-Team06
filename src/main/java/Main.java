import State.*;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

import java.util.*;


public class Main extends GamePlayer{

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";

    private GameClient gameClient = null;
    private BaseGameGUI gamegui = null;
	private State gameState = null;

    private String userName = null;
    private String passwd = null;
	private boolean isBlack;


    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
		Main player = new Main("Team-06", "");

		switch (args[0]) {
			case "2"  -> {
				Main player2 = new Main("Team-06-2", "");
				player2.Go();
			}
			case "human" -> {
				HumanPlayer human = new HumanPlayer();
				human.Go();
			}
			default -> {}
		}


    	if(player.getGameGUI() == null) {
    		player.Go();
    	}
    	else {
    		BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(player::Go);
    	}
    }

    /**
     * Any name and passwd
     * @param userName
      * @param passwd
     */
    public Main(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		switch (messageType) {
			case GameMessage.GAME_STATE_BOARD -> {
				// noinspection unchecked
				ArrayList<Integer> board = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
				getGameGUI().setGameState(board);
				gameState = new State(board);
			}
			case GameMessage.GAME_ACTION_START -> {
				isBlack = msgDetails.get(AmazonsGameMessage.PLAYER_BLACK).equals(getGameClient().getUserName());
				System.out.printf("%sWe are playing as %s.%s%n", ANSI_GREEN, isBlack ? "Black" : "White", ANSI_RESET);
				if (!isBlack) {
					// Make a BFS move
					Action bfsAction = getBFSAction();
					assert bfsAction != null;
					System.out.printf("'Chosen' random move: %s%n", bfsAction);
					sendMove(bfsAction);
				}
			}
			case GameMessage.GAME_ACTION_MOVE -> {
				updateGameState(msgDetails);
				Action action = new Action(msgDetails);
				System.out.printf("%sWe are playing as %s.%s%n", ANSI_GREEN, isBlack ? "Black" : "White", ANSI_RESET);
				System.out.printf("Received opponent move: %s%n", action);
				boolean valid = Utils.validateMove(gameState, action, isBlack? State.WHITE : State.BLACK);
				if (!valid) {
					System.out.printf("%sReceived an invalid Move!!!!!%s%n", ANSI_RED, ANSI_RESET);
				}
				gameState = new State(gameState, action);
				// Make a BFS move
				Action bfsAction = getBFSAction();
				if (bfsAction == null) {
					System.out.printf("%sNo moves available!! We lost.%s☹️%n", ANSI_RED, ANSI_RESET);
				} else {
					System.out.printf("'Chosen' random move: %s%n", bfsAction);
					System.out.printf("%sMoving a %s Queen.%s%n", ANSI_RED,
							gameState.getPos(bfsAction.getOrigin()) == State.BLACK ? "Black": "White",
							ANSI_RESET);
					sendMove(bfsAction);
					if (Generator.availableMoves(gameState, isBlack ? State.WHITE : State.BLACK).isEmpty()) {
						System.out.printf("%sNo moves available for opponent!! We won!%s\uD83C\uDF89%n", ANSI_GREEN, ANSI_RESET);
					}
				}
			}
			default -> System.out.printf("Unknown Message Type: %s%n\t%s%n", messageType, msgDetails);
		}
		System.out.println(gameState.boardToString());
    	return true;   	
    }

	private Action getRandomAction() {
		ArrayList<Action> moves = Generator.availableMoves(gameState, isBlack ? State.BLACK : State.WHITE);
		if (moves.isEmpty()) {
			return null;
		}
		return moves.get(new Random().nextInt(moves.size()));
	}

	private Action getBFSAction() {
		//long endTime = System.currentTimeMillis() + 28000;

		int ourColor = isBlack ? State.BLACK : State.WHITE;
		ArrayList<Action> ourMoves = Generator.availableMoves(gameState, ourColor);
		Collections.shuffle(ourMoves);
		if (ourMoves.isEmpty()) {
			return null;
		}

		int currentControl = 0;
		Action bfsAction = null;
		for (Action action : ourMoves) {
			if (!Utils.validateMove(gameState, action, ourColor)) {continue;}
			int tempControl = 0;
			State actionOutcome = new State(gameState, action);
			Pair[] ourQueens = actionOutcome.getQueens(ourColor);
			Pair[] theirQueens = actionOutcome.getQueens(ourColor == State.BLACK ? ourColor+1 : ourColor-1);
			int[][] board = actionOutcome.getBoard();

			tempControl = BFSMinDistance.minDistanceEvaluation(board, ourQueens, theirQueens);
			if (tempControl > currentControl) {
				bfsAction = action;
				currentControl = tempControl;
			}
			//if (System.currentTimeMillis() > endTime) break;
		}

		return bfsAction;
	}

	private void sendMove(Action move) {
		Map<String, Object> payload = move.toServerResponse();
		getGameClient().sendMoveMessage(payload);
		updateGameState(payload);
		gameState = new State(gameState, move);
	}

	@SuppressWarnings("unchecked")
	private void updateGameState(Map<String, Object> move) {
		getGameGUI().updateGameState(
				(ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_CURR),
				(ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_NEXT),
				(ArrayList<Integer>) move.get(AmazonsGameMessage.ARROW_POS)
		);
	}

    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return  this.gamegui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class
