package academy.mindswap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here

        Game game = new Game();

        System.out.println(game.pickRandomQuestion().getQuestion());
        System.out.println(game.pickRandomQuestion().getQuestion());
        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());
//        System.out.println(game.pickRandomQuestion().getQuestion());

//        System.out.println(game.pickRandomQuestion().getQuestion()); //erro -> null.getQuestion(). Como resolver isto?
//        System.out.println(game.pickRandomQuestion());
//        System.out.println(game.pickRandomQuestion());


        System.out.println(game.getTotalNumberOfQuestions());
        System.out.println(game.getQuestionsAsked());


        System.out.println();
        System.out.println(game.getQuestions().get(0).getQuestion());
        System.out.println(game.getQuestions().get(1).getQuestion());
        System.out.println(game.getQuestions().get(0).getQuestionLine());
        System.out.println(game.getQuestions().get(0).getHint());
        System.out.println(game.getQuestionsAsked());

        game.showQuestion();
        game.showQuestion();
        game.showQuestion();
    }
}
