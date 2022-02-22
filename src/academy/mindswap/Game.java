package academy.mindswap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Game {

    private List<Question> questions = new LinkedList<>();
    private List<Player> players = new LinkedList<>();
    private int questionsAsked;
    private int totalNumberOfQuestions;


    public List<Question> getQuestions() {
        return questions;
    }

    public int getQuestionsAsked() {
        return questionsAsked;
    }

    private boolean repeatedQuestion(String checkQuestion){
        for (Question question: questions) {
            if(question.getQuestion().equals(checkQuestion)){
                return true;
            }
        }
        return false;
    }

    public int getTotalNumberOfQuestions() {
        try {
            this.totalNumberOfQuestions = Files.readAllLines(Paths.get("./src/q&a.txt")).stream().filter(q->q.endsWith("?")).toList().size();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalNumberOfQuestions;
    }

    public Question pickRandomQuestion() throws IOException {

        if(getQuestionsAsked() >= getTotalNumberOfQuestions()){
            System.out.printf(GameMessages.NO_MORE_QUESTIONS);
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader("./src/q&a.txt"));

        int lines = 0;

        while (reader.readLine() != null) {
            lines++;
        }

        List<String> file = Files.readAllLines(Paths.get("./src/q&a.txt"));
        int randomNumber = (int) ((Math.random() * (lines - 0)) + 0);
        String randomQuestion = file.get(randomNumber);

        if (randomQuestion.endsWith("?") && !repeatedQuestion(randomQuestion)) {

            this.questionsAsked++;

            Question question = new Question();

            question.setQuestion(randomQuestion);
            question.setAnswerA(file.get(randomNumber+1));
            question.setAnswerB(file.get(randomNumber+2));
            question.setAnswerC(file.get(randomNumber+3));
            question.setAnswerD(file.get(randomNumber+4));
            question.setCorrectAnswer(file.get(randomNumber+5));
            question.setHint(file.get(randomNumber+6));
            question.setQuestionLine(randomNumber+1);

            questions.add(question);

            reader.close();

            return question;
        }

        return pickRandomQuestion();
    }

    public void showQuestion() throws IOException {

        if(getQuestionsAsked() >= getTotalNumberOfQuestions()){
            System.out.printf(GameMessages.NO_MORE_QUESTIONS);
            return;
        }

        Question question = pickRandomQuestion();

        System.out.println(question.getQuestion());
        System.out.println(question.getAnswerA() + "              " +
                "                  "  + question.getAnswerC());
        System.out.println(question.getAnswerB() + "              " +
                "                  "  + question.getAnswerD());

        System.out.println();
    }

    public void play(){

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

    public class Question {

        private String question;
        private String answerA;
        private String answerB;
        private String answerC;
        private String answerD;
        private String correctAnswer;
        private String hint;
        private int questionLine;


        public Question() {
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

    }

}
