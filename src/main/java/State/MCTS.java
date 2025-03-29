//package State;
//
//import java.util.*;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.*;
//
//public class MCTS {
//    private final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
//    private final double EXPLORATION_CONSTANT = Math.sqrt(2);
//    private final int SIMULATION_DEPTH = 12;
//
//    private final MinDistanceActionFactory actionFactory;
//    private Node root;
//    private boolean isBlack;
//    private int topActions;
//    private int moveCounter;
//
//    public MCTS(State initialState, boolean isBlack, int topActions, int moveCounter) {
//        this.actionFactory = new MinDistanceActionFactory();
//        this.root = new Node(initialState, null, isBlack);
//        this.isBlack = isBlack;
//        this.topActions = topActions;
//        this.moveCounter = moveCounter;
//    }
//
//    public Action findBestMove(int iterations) {
//        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
//        List<Future<?>> futures = new ArrayList<>();
//
//        for (int i = 0; i < NUM_THREADS; i++) {
//            futures.add(executor.submit(() -> {
//                for (int j = 0; j < iterations/NUM_THREADS; j++) {
//                    search(root);
//                }
//            }));
//        }
//
//        try {
//            for (Future<?> future : futures) future.get();
//            executor.shutdown();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return root.getBestChild().action;
//    }
//
//    private void search(Node node) {
//        Node selected = select(node);
//        Node expanded = expand(selected);
//        double result = simulate(expanded.state, isBlack);
//        backpropagate(expanded, result);
//    }
//
//    // Core MCTS components with heuristic integration
//    private Node select(Node node) {
//        while (!node.isLeaf()) {
//            if (!node.isFullyExpanded()) {
//                return expand(node);
//            }
//            node = node.selectChild(EXPLORATION_CONSTANT);
//        }
//        return node;
//    }
//
//    private Node expand(Node node) {
//        Action[] actions = actionFactory.getAction(node.state, isBlack);
//        for (Action action : actions) {
//            if (!node.hasChild(action)) {
//                State newState = new State(node.state, action);
//                Node child = new Node(newState, action);
//                node.addChild(child);
//                return child;
//            }
//        }
//        return node;
//    }
//
//    private double simulate(State state, boolean isBlack) {
//        State simState = state;
//        int depth = 0;
//        while (!isTerminal(simState) && depth < SIMULATION_DEPTH) {
//            Action[] actions = new Action[topActions];
//            actions = actionFactory.getAction(simState, isBlack, moveCounter, topActions);
//            if (actions.length == 0) break;
//            Action action = actions[ThreadLocalRandom.current().nextInt(actions.length)];
//            simState = new State(simState, action);
//            depth++;
//        }
//        return evaluateState(simState);
//    }
//
//    private void backpropagate(Node node, double result) {
//        while (node != null) {
//            node.updateStats(result);
//            node = node.parent;
//        }
//    }
//
//    // Helper methods
//    private double evaluateState(State state) {
//        // Implement your min-distance heuristic evaluation here
//        return 0.0; // Range [-1, 1]
//    }
//
//    private boolean isTerminal(State state) {
//        // Implement terminal state check
//        return false;
//    }
//
//    // Node class with thread-safe statistics
//    private class Node {
//        public final State state;
//        public final Action action;
//        public final Node parent;
//        public boolean isBlack;
//        public final List<Node> children = new CopyOnWriteArrayList<>();
//        public final AtomicInteger visits = new AtomicInteger(0);
//        public final AtomicDouble totalScore = new AtomicDouble(0);
//        public int topN;
//
//        public Node(State state, Action action, boolean isBlack, int topN) {
//            this.state = state;
//            this.action = action;
//            this.parent = this;
//            this.isBlack = isBlack;
//            this.topN = topN;
//        }
//
//        public boolean isLeaf() {
//            return children.isEmpty();
//        }
//
//        public boolean isFullyExpanded() {
//            return children.size() >= actionFactory.getAction(state, isBlack, 0, topN).length;
//        }
//
//        public Node selectChild(double explorationWeight) {
//            return children.parallelStream()
//                    .max(Comparator.comparingDouble(c ->
//                            c.totalScore.get() / c.visits.get() +
//                                    explorationWeight * Math.sqrt(Math.log(visits.get()) / c.visits.get())
//                    ))
//                    .orElseThrow();
//        }
//
//        public void addChild(Node child) {
//            children.add(child);
//        }
//
//        public boolean hasChild(Action action) {
//            return children.parallelStream().anyMatch(c -> c.action.equals(action));
//        }
//
//        public void updateStats(double result) {
//            visits.incrementAndGet();
//            totalScore.addAndGet(result);
//        }
//
//        public Node getBestChild() {
//            return children.stream()
//                    .max(Comparator.comparingDouble(c -> c.visits.get()))
//                    .orElseThrow();
//        }
//
//        public boolean isBlackTurn() {
//            return state.getQueens(1).length > 0; // Adjust based on turn tracking
//        }
//    }
//}