package academy.mindswap.server;

import academy.mindswap.server.commands.Command;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.player.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Game implements Runnable{

    private List<Question> questions = new LinkedList<>();
    private PlayerConnectionHandler player;
    private int totalNumberOfQuestions;



    public Game() {
//        this.player = null;
        this.players = new CopyOnWriteArrayList<>();
        // players = Collections.synchronizedList(new ArrayList<>());
        //   players = new ArrayList<>();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getQuestionsAsked() {
        return questions.size();
    }

    private boolean repeatedQuestion(String checkQuestion) {
        for (Question question : questions) {
            if (question.getQuestion().equals(checkQuestion)) {
                return true;
            }
        }
        return false;
    }

    public int getTotalNumberOfQuestions() {
        try {
            this.totalNumberOfQuestions = Files.readAllLines(Paths.get("./src/q&a.txt")).stream()
                    .filter(q -> q.endsWith("?"))
                    .toList()
                    .size();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalNumberOfQuestions;
    }

    public Question pickRandomQuestion() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("./src/q&a.txt"));

        int lines = 0;

        while (reader.readLine() != null) {
            lines++;
        }

        List<String> file = Files.readAllLines(Paths.get("./src/q&a.txt"));
        int randomNumber = (int) ((Math.random() * (lines - 0)) + 0);
        String randomQuestion = file.get(randomNumber);

        if (randomQuestion.endsWith("?") && !repeatedQuestion(randomQuestion)) {

            Question question = new Question();

            question.setQuestion(randomQuestion);
            question.setQuestionID(file.get(randomNumber - 2));
            question.setDifficulty(file.get(randomNumber - 1));
            question.setAnswerA(file.get(randomNumber + 1));
            question.setAnswerB(file.get(randomNumber + 2));
            question.setAnswerC(file.get(randomNumber + 3));
            question.setAnswerD(file.get(randomNumber + 4));
            question.setCorrectAnswer(file.get(randomNumber + 5));
            question.setHint(file.get(randomNumber + 6));
            question.setQuestionLine(randomNumber + 1);

            questions.add(question);

            reader.close();

            return question;
        }

        return pickRandomQuestion();
    }

    public void showRandomQuestion() throws IOException {

        if (getQuestionsAsked() >= getTotalNumberOfQuestions()) {
            System.out.printf(GameMessages.NO_MORE_QUESTIONS);
            return;
        }

        Question question = pickRandomQuestion();
        formatQuestions(question);
    }

    public void showQuestion(Question question) throws IOException {

        if (getQuestionsAsked() >= getTotalNumberOfQuestions()) {
            System.out.printf(GameMessages.NO_MORE_QUESTIONS);
            return;
        }

        formatQuestions(question);
    }

    public void showQuestion(String QuestionID) throws IOException {

        if (getQuestionsAsked() >= getTotalNumberOfQuestions()) {
            System.out.printf(GameMessages.NO_MORE_QUESTIONS);
            return;
        }

        String questionID = "132";

        questions.stream().map(q -> q.getQuestionID()).filter(q -> q.equals(questionID)).toList();

//            formatQuestions(questionID);
    }

    private Question findQuestionByID(String questionID) throws IOException {

        List<String> file = Files.readAllLines(Paths.get("./src/q&a.txt"));

        String IDNumber = file.stream().filter(s -> s.equals(questionID)).toString();

        for (Question question : questions) {
            if (question.getQuestionID().equals(questionID)) {
                return question;
            }
        }

        return null;
    }


    public Question pickQuestionByID(String idQuestion) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("./src/q&a.txt"));

        int lines = 0;

        while (reader.readLine() != null) {
            lines++;
        }

        List<String> file = Files.readAllLines(Paths.get("./src/q&a.txt"));
//        int number = Integer.parseInt(file.stream().filter(s -> s.equals(idQuestion)).toString());
        String IDNumber = file.stream().filter(s -> s.equals(idQuestion)).toString();


//        if (questionString.endsWith("?") && !repeatedQuestion(questionString)) {
//
//
//            Question question = new Question();
//
//            question.setQuestion(questionString);
//            question.setQuestionID(file.get(IDNumber-2));
//            question.setDifficulty(file.get(IDNumber-1));
//            question.setAnswerA(file.get(IDNumber+1));
//            question.setAnswerB(file.get(IDNumber+2));
//            question.setAnswerC(file.get(IDNumber+3));
//            question.setAnswerD(file.get(IDNumber+4));
//            question.setCorrectAnswer(file.get(IDNumber+5));
//            question.setHint(file.get(IDNumber+6));
//            question.setQuestionLine(IDNumber+1);
//
//            questions.add(question);
//
//            reader.close();
//
//            return question;
//        }

        System.out.printf(GameMessages.REPEATED_QUESTIONS);

        return null;
    }

    private void formatQuestions(Question question) {

        int space = question.getAnswerA().length() > question.getAnswerC().length() ?
                question.getAnswerA().length() - question.getAnswerC().length() :
                question.getAnswerC().length() - question.getAnswerA().length();

        String spaces = " ".repeat(space);

        System.out.println(question.getQuestion());

        System.out.println();

        if (question.getAnswerA().length() > question.getAnswerC().length()) {
            System.out.println(question.getAnswerA() + "              " +
                    "                  " + question.getAnswerB());

            System.out.println(question.getAnswerC() + spaces + "              " +
                    "                  " + question.getAnswerD());
        } else {
            System.out.println(question.getAnswerA() + spaces + "              " +
                    "                  " + question.getAnswerB());

            System.out.println(question.getAnswerC() + "              " +
                    "                  " + question.getAnswerD());
        }

        System.out.println();
    }

    public void showHint() {

        if (this.player.getHintsRemaining() == 0) {
            System.out.println(GameMessages.NO_MORE_HINTS);
            return;
        }

        Question question = questions.get(questions.size() - 1);

        if (question.isShowHint()) {
            System.out.printf(GameMessages.HELP_ALREADY_USED);
            return;
        }

        System.out.println(question.getHint());
        System.out.println();
        this.player.setHintsRemaining(this.player.getHintsRemaining() - 1);
        question.setShowHint(true);
    }

    public void show5050() throws IOException {

        //Question should have a List<Answers> answers (answers.get(0)-> a) etc...)

        if (this.player.get_5050Remaining() == 0) {
            System.out.println(GameMessages.NO_MORE_5050);
            return;
        }

        Question question = questions.get(questions.size() - 1);

        if (question.isShow5050()) {
            System.out.printf(GameMessages.HELP_ALREADY_USED);
            return;
        }

        String answer = question.getCorrectAnswer() + ")";
        int randomNumber = (int) ((Math.random() * (3 - 0)) + 0);

        if (question.getAnswerA().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        } else if (question.getAnswerB().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        } else if (question.getAnswerC().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        } else if (question.getAnswerD().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        }

        question.setShow5050(true);
        this.player.set_5050Remaining(this.player.get_5050Remaining() - 1);
        showQuestion(question);

    }

    public boolean checkValidInput(String input) {
        if (input.equalsIgnoreCase("a") || input.equalsIgnoreCase("b") ||
                input.equalsIgnoreCase("c") || input.equalsIgnoreCase("d") ||
                input.equalsIgnoreCase("f") || input.equalsIgnoreCase("h")) {
            return true;
        }
        return false;
    }

    public boolean checkCorrectAnswer() {
        Question question = questions.get(questions.size() - 1);
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        System.out.println();
        if (!checkValidInput(answer)) {
            System.out.printf(GameMessages.INVALID_INPUT);
            return checkCorrectAnswer();
        }
        if (answer.equalsIgnoreCase(question.getCorrectAnswer())) {
            System.out.printf(GameMessages.CORRECT_ANSWER, player.getName());
            System.out.println();
            return true;
        }
        System.out.printf(GameMessages.WRONG_ANSWER, player.getName(), question.getCorrectAnswer());
        System.out.println();
        player.setLivesRemaining(player.getLivesRemaining() - 1);
        return false;
    }

    public void loading() throws InterruptedException {

        System.out.println();

        new Thread(()-> {
            int i = 0;
            while(i++ < 100) {
                System.out.print("Loading Questions: [");
                int j=0;
                while(j++<i){
                    System.out.print(ConsoleColors.GREEN+ "€" + ConsoleColors.RESET) ;
                }
                while(j++<100){
                    System.out.print(" ");
                }
                System.out.print("] : "+ i+"%");
                try {
                    Thread.sleep(50l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("\r");
            }
        }).start();

        Thread.sleep(8000);
    }

    public /*synchronized*/ void play_() throws IOException, InterruptedException {

        broadcast(GameMessages.GAME_STARTED);

        System.out.println(players.size());

        loading();

        Scanner scanner = new Scanner(System.in);

//        while (player.getLivesRemaining() > 0 && totalNumberOfQuestions >= getQuestionsAsked()) {

        Thread.sleep(2000);

        boolean roundIsOver = false;

        broadcast(pickRandomQuestion().getQuestion());

        showRandomQuestion();

        System.out.printf(GameMessages.SELECT_ANSWER);

        while(!roundIsOver){
            String choice = scanner.nextLine();
            switch (choice) {
                case "a", "b", "c", "d":
                    System.out.printf(GameMessages.LOCK_ANSWER);
                    checkCorrectAnswer();
                    roundIsOver = true;
                    break;
                case "f":
                    show5050();
                    break;
                case "h":
                    showHint();
                    break;
                default:
                    System.out.printf(GameMessages.INVALID_INPUT);
            }
        }
//        }
    }

//    public void play(Player player) throws IOException, InterruptedException {
//
//        this.player = player;
//
//        System.out.printf(GameMessages.GAME_STARTED);
//
//        loading();
//
//        Scanner scanner = new Scanner(System.in);
//
//        while (player.getLivesRemaining() > 0 && totalNumberOfQuestions >= getQuestionsAsked()) {
//
//            Thread.sleep(2000);
//
//            boolean roundIsOver = false;
//
//            System.out.printf(GameMessages.ROUND_NUMBER, (getQuestionsAsked() + 1));
//
//            showRandomQuestion();
//
//            System.out.printf(GameMessages.SELECT_ANSWER);
//
//            while(!roundIsOver){
//                String choice = scanner.nextLine();
//                switch (choice) {
//                    case "a", "b", "c", "d":
//                        System.out.printf(GameMessages.LOCK_ANSWER);
//                        checkCorrectAnswer();
//                        roundIsOver = true;
//                        break;
//                    case "f":
//                        show5050();
//                        break;
//                    case "h":
//                        showHint();
//                        break;
//                    default:
//                        System.out.printf(GameMessages.INVALID_INPUT);
//                }
//            }
//        }
//    }

    @Override
    public /*synchronized*/ void run() {

        broadcast(GameMessages.GAME_STARTED);

        System.out.println(players.size());

        try {
            loading();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);

        while (player.getLivesRemaining() > 0 && totalNumberOfQuestions >= getQuestionsAsked()) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            boolean roundIsOver = false;

            try {
                broadcast(pickRandomQuestion().getQuestion());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                showRandomQuestion();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.printf(GameMessages.SELECT_ANSWER);

            while(!roundIsOver){
                String choice = scanner.nextLine();
                switch (choice) {
                    case "a", "b", "c", "d":
                        System.out.printf(GameMessages.LOCK_ANSWER);
                        checkCorrectAnswer();
                        roundIsOver = true;
                        break;
                    case "f":
                        try {
                            show5050();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "h":
                        showHint();
                        break;
                    default:
                        System.out.printf(GameMessages.INVALID_INPUT);
                }
            }
        }
    }


    public class Question {

        private String questionID;
        private String difficulty;
        private String question;
        private String answerA;
        private String answerB;
        private String answerC;
        private String answerD;
        private String correctAnswer;
        private String hint;
        private int questionLine;
        private boolean showHint;
        private boolean show5050;


        public Question() {

        }

        public boolean isShowHint() {
            return showHint;
        }

        public void setShowHint(boolean showHint) {
            this.showHint = showHint;
        }

        public boolean isShow5050() {
            return show5050;
        }

        public void setShow5050(boolean show5050) {
            this.show5050 = show5050;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswerA() {
            return answerA;
        }

        public void setAnswerA(String answerA) {
            this.answerA = answerA;
        }

        public String getAnswerB() {
            return answerB;
        }

        public void setAnswerB(String answerB) {
            this.answerB = answerB;
        }

        public String getAnswerC() {
            return answerC;
        }

        public void setAnswerC(String answerC) {
            this.answerC = answerC;
        }

        public String getAnswerD() {
            return answerD;
        }

        public void setAnswerD(String answerD) {
            this.answerD = answerD;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public int getQuestionLine() {
            return questionLine;
        }

        public void setQuestionLine(int questionLine) {
            this.questionLine = questionLine;
        }

        public String getQuestionID() {
            return questionID;
        }

        public void setQuestionID(String questionID) {
            this.questionID = questionID;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }


    }

//    public void myAnswers() throws IOException {
//
//        BufferedReader reader = new BufferedReader(new FileReader("./src/answers.txt"));
//
//        String[] divide = Files.readAllLines(Paths.get("./src/answers.txt")).toString().split("____");
//
//        String[][] answers = new String[divide.length][6];
//
//        for (int i = 0; i < divide.length; i++) {
//            for (int j = 0; j < 6; j++) {
//                answers[i][j] = reader.readLine();
//            }
//        }
//
//        reader.close();
//        System.out.println(answers[3][4]);
//
//    }




    private ServerSocket serverSocket;
    private ExecutorService service;
    private final List<PlayerConnectionHandler> players;


    public void start(int port) throws IOException, InterruptedException {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serverSocket = new ServerSocket(port);
        service = Executors.newCachedThreadPool();
        int numberOfConnections = 0;

        while (true) {
            acceptConnection(numberOfConnections);
            ++numberOfConnections;

//            for (int i = 0; i < players.size(); i++) {
//                try {
//                    play_(players.get(i));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            service.submit(this);      // = newThread.start()

        }


    }

    public void acceptConnection(int numberOfConnections) throws IOException {
        Socket playerSocket = serverSocket.accept();
        PlayerConnectionHandler playerConnectionHandler =
                new PlayerConnectionHandler(playerSocket,
                        GameMessages.DEFAULT_NAME + numberOfConnections);
        service.submit(playerConnectionHandler);
        this.player = playerConnectionHandler;
        //addClient(clientConnectionHandler);
    }

    private void addPlayer(PlayerConnectionHandler playerConnectionHandler) {
        /*synchronized (players) {
            players.add(clientConnectionHandler);
        }*/

        players.add(playerConnectionHandler);
        playerConnectionHandler.send(GameMessages.WELCOME);
        broadcast(playerConnectionHandler.getName(), GameMessages.PLAYER_ENTERED_GAME);
    }


    public void broadcast(String name, String message) {
        players.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public void broadcast(String message) {
        players.stream()
                .forEach(handler -> handler.send(message));
    }

    public String listPlayers() {
        StringBuffer buffer = new StringBuffer();
        players.forEach(player -> buffer.append(player.getName()).append("\n"));
        return buffer.toString();
    }

    public void removePlayer(PlayerConnectionHandler playerConnectionHandler) {
        players.remove(playerConnectionHandler);

    }

    public Optional<PlayerConnectionHandler> getPlayerByName(String name) {
        return players.stream()
                .filter(playerConnectionHandler -> playerConnectionHandler.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public class PlayerConnectionHandler implements Runnable {



        private int hintsRemaining;
        private int _5050Remaining;
        private int livesRemaining;
        private boolean isPlaying;
        private int moneyAmount;

        private String name;
        private Socket playerSocket;
        private BufferedWriter out;
        private String message;


        public PlayerConnectionHandler(Socket clientSocket, String name) throws IOException {
            this.playerSocket = clientSocket;
            this.name = name;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            this.name = name;
            this.hintsRemaining = 3;
            this._5050Remaining = 3;
            this.livesRemaining = 3;
        }

        public String getName() {
            return name;
        }

        public int getMoneyAmount() {
            return moneyAmount;
        }

        public void setMoneyAmount(int moneyAmount) {
            if(getMoneyAmount() < 0){
                this.moneyAmount = 0;
                return;
            }
            this.moneyAmount = moneyAmount;
        }

        public int getHintsRemaining() {
            return hintsRemaining;
        }

        public int get_5050Remaining() {
            return _5050Remaining;
        }

        public int getLivesRemaining() {
            return livesRemaining;
        }

        public void setHintsRemaining(int hintsRemaining) {
            this.hintsRemaining = hintsRemaining;
        }

        public void set_5050Remaining(int _5050Remaining) {
            this._5050Remaining = _5050Remaining;
        }

        public void setLivesRemaining(int livesRemaining) {
            this.livesRemaining = livesRemaining;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        @Override
        public void run() {
            addPlayer(this);
            try {
                // BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Scanner in = new Scanner(playerSocket.getInputStream());
                while (in.hasNext()) {
                    message = in.nextLine();
                    if (isCommand(message)) {
                        dealWithCommand(message);
                        continue;
                    }
                    if (message.equals("")) {
                        continue;
                    }

                    broadcast(name, message);
                }
            } catch (IOException e) {
                System.err.println(GameMessages.CLIENT_ERROR + e.getMessage());
            } finally {
                removePlayer(this);
            }
        }

        private boolean isCommand(String message) {
            return message.startsWith("/");
        }

        private void dealWithCommand(String message) throws IOException {
            String description = message.split(" ")[0];
            Command command = Command.getCommandFromDescription(description);

            if (command == null) {
                out.write(GameMessages.NO_SUCH_COMMAND);
                out.newLine();
                out.flush();
                return;
            }

            command.getHandler().execute(Game.this, this);
        }

        public void send(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                removePlayer(this);
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                playerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getMessage() {
            return message;
        }
    }




}
