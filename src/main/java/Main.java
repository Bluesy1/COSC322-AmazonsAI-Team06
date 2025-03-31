import State.*;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;


public class Main extends GamePlayer {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    private static final Action initialMove = new Action(new Pair(3, 9), new Pair(3, 3), new Pair(6, 3));
    private final BaseGameGUI gameGui;
    private final String passwd;
    private final ActionFactory actionFactory;
    private final boolean useMinimax;
    private final int DEPTH = 5;
    //    private int depth = INIT_DEPTH;
    private final int topN;
    private GameClient gameClient = null;
    private State gameState = null;
    private String userName;
    private boolean isBlack;
    private String colorName;
    private int moveCounter = 0;
    private FileWriter logFile = null;


    /**
     * Any name and passwd
     *
     * @param userName any string (used as display username in gui)
     * @param passwd   any string (can be empty)
     */
    public Main(String userName, String passwd, ActionFactory actionFactory, boolean useMinimax, int topN) {
        this.userName = userName + "-TopN=" + topN + "-" + (new Random()).nextInt(1000);
        this.passwd = passwd;
        this.actionFactory = actionFactory;
        this.useMinimax = useMinimax;
        //To make a GUI-based player, create an instance of BaseGameGUI
        //and implement the method getGameGUI() accordingly
        this.gameGui = new BaseGameGUI(this);
        this.topN = topN;
    }

    /**
     * The main method
     *
     * @param args for name and passwd (current, any string would work)
     */
    public static void main(String[] args) {
        Main player = new Main("Team-06", "", new MinDistanceActionFactory(), true, 2);

        switch (args.length > 0 ? args[0] : "") {
            case "2" -> {
                Main player2 = new Main("Team-06-reference", "", new MinDistanceActionFactory(), true, 3);
                player2.Go();
            }
            case "human" -> {
                HumanPlayer human = new HumanPlayer();
                human.Go();
            }
            default -> {
            }
        }


        if (player.getGameGUI() == null) {
            player.Go();
        } else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(player::Go);
        }
    }

    @Override
    public void onLogin() {
        userName = gameClient.getUserName();
        if (gameGui != null) {
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
                colorName = isBlack ? "Black" : "White";
                if (logFile != null) {
                    try {
                        logFile.flush();
                        logFile.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
                String fileName = userName + "_" + colorName + "_" + sdf.format(date) + ".log";
                File file = new File(fileName);
                int counter = 0;
                while (file.exists()) {
                    file = new File(fileName + "." + ++counter);
                }
                try {
                    // noinspection ResultOfMethodCallIgnored
                    file.createNewFile();
                    logFile = new FileWriter(file);
                } catch (IOException ignored) {
                    logFile = null;
                }
                logMessage(String.format("We are playing as %s.", colorName));
                if (isBlack) {
                    moveCounter++;
                    sendMove(initialMove);
                    logMessage(String.format("Made initial move: %s", initialMove));
                }
            }
            case GameMessage.GAME_ACTION_MOVE -> {
                updateGameState(msgDetails);
                Action action = new Action(msgDetails);
                logMessage(String.format("We are playing as %s.", colorName));
                logMessage(String.format("Received opponent move: %s", action));
                boolean valid = Utils.validateMove(gameState, action, isBlack ? State.WHITE : State.BLACK, true);
                moveCounter++;
//                depth = INIT_DEPTH + moveCounter / 8;
                if (!valid) {
                    logMessage("%sReceived an invalid Move!!!!!%s", ANSI_RED);
                }
                gameState = new State(gameState, action);
                if (makeGameMove()) {
                    if (Generator.availableMoves(gameState, isBlack ? State.WHITE : State.BLACK).isEmpty()) {
                        logMessage("No moves available for opponent!! We won!\uD83C\uDF89", ANSI_GREEN);
                        if (logFile != null) {
                            try {
                                logFile.close();
                            } catch (IOException ignored) {
                            }
                            logFile = null;
                        }
                    }
                }
            }
            default -> System.out.printf("Unknown Message Type: %s%n\t%s%n", messageType, msgDetails);
        }
        logMessage(gameState.boardToString());
        return true;
    }

    private boolean makeGameMove() {
        Action move = null;
        long startTime = System.currentTimeMillis();
        ActionControlPair[] moves = actionFactory.getAction(gameState, isBlack, topN);
        if (useMinimax) {
            move = AlphaBetaMinimax.getBestMove(moves, DEPTH, isBlack, topN, actionFactory, gameState, moveCounter);
        } else {
            if (moves != null) {
                move = moves[0].getAction();
            }
        }
        if (move != null) {
            moveCounter++;
            sendMove(move);
            long endTime = System.currentTimeMillis();
            logMessage(String.format("Chosen move: %s (Time Taken: %.3f s)", move, (endTime - startTime) / 1000D));
            return true;
        } else {
            logMessage("No moves available!! We lost.☹️", ANSI_RED);
            if (logFile != null) {
                try {
                    logFile.close();
                } catch (IOException ignored) {
                }
                logFile = null;
            }
            return false;
        }
    }

    private void logMessage(String msg) {
        if (logFile != null) {
            try {
                logFile.write(msg);
                logFile.write('\n');
                logFile.flush();
            } catch (IOException ignored) {
            }
        }
        System.out.printf("%s%n", msg);
    }

    private void logMessage(String msg, String formatChar) {
        if (logFile != null) {
            try {
                logFile.write(msg);
                logFile.write('\n');
                logFile.flush();
            } catch (IOException ignored) {
            }
        }
        System.out.printf("%s%s%s%n", formatChar, msg, ANSI_RESET);
    }

    private void sendMove(Action move) {
        Map<String, Object> payload = move.toServerResponse();
        getGameClient().sendMoveMessage(payload);
        updateGameState(payload);
        gameState = new State(gameState, move);
    }

    @SuppressWarnings("unchecked")
    private void updateGameState(Map<String, Object> move) {
        getGameGUI().updateGameState((ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_CURR), (ArrayList<Integer>) move.get(AmazonsGameMessage.QUEEN_POS_NEXT), (ArrayList<Integer>) move.get(AmazonsGameMessage.ARROW_POS));
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
        return this.gameGui;
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, this);
    }


}//end of class
