package com.lld;
import com.lld.model.Game;
import com.lld.model.Player;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MainApplication {
    public static void main(String[] args) {
        System.out.println("Hello World!!!!");
        System.out.println("Welcome To Tic-Tac-Toe Game!!!");
        Game game=new Game();
        game.startGame(
                new Player("Pratham",'X'),
                new Player("Swami",'O'),
                true
        );
        game.makeMove(0,0);
        game.makeMove(0,1);
        game.makeMove(0,2);
        System.out.println(game.getGameState());
    }
}