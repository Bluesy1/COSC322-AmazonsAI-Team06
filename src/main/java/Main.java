import State.*;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

import java.util.*;


public class Main extends GamePlayer{

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
//		Main player = new Main("Team-06", "Team-06");
		Main player = new Main("", "");

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

	@SuppressWarnings("unchecked")
    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
    	//This method will be called by the GameClient when it receives a game-related message
    	//from the server.
	
    	//For a detailed description of the message types and format, 
    	//see the method GamePlayer.handleGameMessage() in the game-client-api document. 

		switch (messageType) {
			case GameMessage.GAME_STATE_BOARD -> {
				System.out.println(msgDetails.keySet());
				ArrayList<Integer> board = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
				getGameGUI().setGameState(board);
				gameState = new State(board);
//				System.out.printf("Board initialized by %s:%n%s%n", messageType, gameState.boardToString());
//				System.out.println(board);
			}
			case GameMessage.GAME_ACTION_START -> {
//				System.out.println(msgDetails.keySet());
//				ArrayList<Integer> board = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.GAME_STATE);
//				getGameGUI().setGameState(board);
//				gameState = new State(board);
//				System.out.printf("Board initialized by %s:%n%s%n", messageType, gameState.boardToString());
				isBlack = msgDetails.get(AmazonsGameMessage.PLAYER_BLACK).equals(getGameClient().getUserName());
				if (isBlack) {
					// Make a random move
					ArrayList<Action> moves = Generator.availableMoves(gameState, State.BLACK);
					Action randomAction = moves.get(new Random().nextInt(moves.size()));
					System.out.printf("'Chosen' random move: %s\n", randomAction);
					Map<String, Object> response = randomAction.toServerResponse();
					getGameClient().sendMoveMessage(response);
					getGameGUI().updateGameState(response);
					gameState = new State(gameState, randomAction);
				}
			}
			case GameMessage.GAME_ACTION_MOVE -> {
//				ArrayList<Integer> curPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_CURR);
//				ArrayList<Integer> newPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.QUEEN_POS_NEXT);
//				ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get(AmazonsGameMessage.ARROW_POS);
				getGameGUI().updateGameState(msgDetails);
				Action action = new Action(msgDetails);
				System.out.printf("Received opponent move: %s\n", action);
				gameState = new State(gameState, action);
				// Make a random move
				ArrayList<Action> moves = Generator.availableMoves(gameState, isBlack ? State.BLACK : State.WHITE);
				Action randomAction = moves.get(new Random().nextInt(moves.size()));
				System.out.printf("'Chosen' random move: %s\n", randomAction);
				Map<String, Object> response = randomAction.toServerResponse();
				getGameClient().sendMoveMessage(response);
				getGameGUI().updateGameState(response);
				gameState = new State(gameState, randomAction);
			}
			default -> {
				System.out.printf("Unknown Message Type: %s%n\t%s%n", messageType, msgDetails);
			}
		}
		System.out.println(gameState.boardToString());
    	return true;   	
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
