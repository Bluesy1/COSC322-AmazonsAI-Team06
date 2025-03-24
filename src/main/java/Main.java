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
    private final BaseGameGUI gameGui;
	private State gameState = null;

    private String userName;
    private final String passwd;
	private boolean isBlack;
	private final ActionFactory actionFactory;
	private int moveCounter = 0;


    /**
     * The main method
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
		Main player = new Main("Team-06", "", new TerritoryActionFactory());

		switch (args.length > 0 ? args[0] : "") {
			case "2"  -> {
				Main player2 = new Main("Team-06-reference", "", new MinDistanceActionFactory());
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
     * @param userName any string (used as display username in gui)
      * @param passwd any string (can be empty)
     */
    public Main(String userName, String passwd, ActionFactory actionFactory) {
    	this.userName = userName;
    	this.passwd = passwd;
		this.actionFactory = actionFactory;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gameGui = new BaseGameGUI(this);
    }
 


    @Override
    public void onLogin() {
		userName = gameClient.getUserName();
		if(gameGui != null) {
			gameGui.setRoomInformation(gameClient.getRoomList());
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
				if (isBlack) {
					// Make a move
					Action move = actionFactory.getAction(gameState, true, moveCounter);
					moveCounter++;
					assert move != null;
					System.out.printf("Chosen move: %s%n", move);
					sendMove(move);
				}
			}
			case GameMessage.GAME_ACTION_MOVE -> {
				updateGameState(msgDetails);
				Action action = new Action(msgDetails);
				System.out.printf("%sWe are playing as %s.%s%n", ANSI_GREEN, isBlack ? "Black" : "White", ANSI_RESET);
				System.out.printf("Received opponent move: %s%n", action);
				boolean valid = Utils.validateMove(gameState, action, isBlack? State.WHITE : State.BLACK, true);
				moveCounter++;
				if (!valid) {
					System.out.printf("%sReceived an invalid Move!!!!!%s%n", ANSI_RED, ANSI_RESET);
				}
				gameState = new State(gameState, action);
				// Make a move
				Action move = actionFactory.getAction(gameState, isBlack, moveCounter);
				moveCounter++;
				if (move == null) {
					System.out.printf("%sNo moves available!! We lost.%s☹️%n", ANSI_RED, ANSI_RESET);
				} else {
					System.out.printf("Chosen move: %s%n", move);
					System.out.printf("%sMoving a %s Queen.%s%n", ANSI_RED, isBlack ? "Black": "White", ANSI_RESET);
					sendMove(move);
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
		return  this.gameGui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(userName, passwd, this);			
	}

 
}//end of class
