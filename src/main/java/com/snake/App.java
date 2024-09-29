package com.snake;

import javax.swing.JFrame;

public final class App {
    
    public static void main(String[] args) {

        int boardWidth = 600;
        int boardHeight = boardWidth;

        var jFrame = new JFrame("Snake");
        jFrame.setSize(boardWidth, boardHeight);
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        var snakeGame = new SnakeGame(boardWidth, boardHeight);
        jFrame.add(snakeGame);
        jFrame.pack();
        snakeGame.requestFocus();

        jFrame.setVisible(true);

    }
}
