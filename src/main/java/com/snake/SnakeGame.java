package com.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    private record SnakePoint(int x, int y) {
    }

    // Dimensions
    private int boardWidth;
    private int boardHeight;
    private final int cellSize;
    private final int FRAME_RATE = 60;
    private final int numCells;

    // Snake
    private List<SnakePoint> snake;

    // Food
    private SnakePoint food;

    private Random random;

    // Game Logic
    private Timer gameLoop;
    private int velocityX;
    private int velocityY;

    // Game Over
    private boolean gameOver;

    SnakeGame(int boardWidth, int boardHeight) {

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        setLayout(null);
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        random = new Random();

        cellSize = (boardWidth / FRAME_RATE) * 2;
        numCells = boardWidth / cellSize;

        snake = new ArrayList<>();

        resetGame();

        gameLoop = new Timer(90, this);
        gameLoop.start();
    }

    private void resetGame() {
        gameOver = false;
        snake.clear();
        spawnFood();
        spawnHead();
        randomVelocity();
    }

    private void spawnHead() {
        int x, y;
        boolean onFood;

        do {
            x = random.nextInt(numCells);
            y = random.nextInt(numCells);

            onFood = food.equals(new SnakePoint(x, y));

        } while (onFood);

        var newHead = new SnakePoint(x, y);
        snake.addFirst(newHead);
    }

    private void spawnFood() {
        int x, y;
        boolean onSnake;

        do {
            x = random.nextInt(numCells);
            y = random.nextInt(numCells);

            onSnake = snake.contains(new SnakePoint(x, y));

        } while (onSnake);

        food = new SnakePoint(x, y);
    }

    private void randomVelocity() {
        int x = 0;
        int y = 0;

        do {
            x = random.nextInt(3) - 1;
            y = x == 0 ? random.nextInt(3) - 1 : 0;
        } while (y == 0 && x == 0);

        velocityX = x;
        velocityY = y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {

        // Food
        g.setColor(Color.RED);
        g.fill3DRect(food.x * cellSize, food.y * cellSize, cellSize, cellSize, true);

        // Snake
        Color startColor = Color.GREEN;
        int snakeLength = snake.size();

        for (int i = 0; i < snakeLength; i++) {

            float factor = (snakeLength > 1) ? (float) i / (snakeLength - 1) : 0.0f;

            int red = startColor.getRed();
            int green = (int) (startColor.getGreen() * (1 - 0.3 * factor));
            int blue = startColor.getBlue();

            g.setColor(new Color(red, green, blue));

            g.fill3DRect(snake.get(i).x * cellSize, snake.get(i).y * cellSize, cellSize, cellSize, true);
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        if (gameOver) {
            g.setColor(Color.RED);

            gameLoop.stop();

            FontMetrics fm = g.getFontMetrics();

            String[] lines = {
                    String.format("Game Over: %s", snake.size() - 1),
                    "Press Space Key For Reset"
            };

            int centerX = boardWidth / 2;
            int centerY = boardHeight / 2;

            int lineHeight = fm.getHeight();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                int textWidth = fm.stringWidth(line);
                int x = centerX - (textWidth / 2);
                int y = centerY + (i * lineHeight);
                g.drawString(line, x, y);
            }

        } else {
            g.drawString("Score: " + (snake.size() - 1), cellSize / 2, cellSize);
        }
    }

    private void move() {

        final var currentHead = snake.getFirst();
        final var newHead = new SnakePoint(currentHead.x + velocityX, currentHead.y + velocityY);

        snake.addFirst(newHead);

        // Snake
        if (collisionIntern()) {
            gameOver = true;
        } else if (newHead.equals(food)) {
            spawnFood();
        } else if (collision()) {
            gameOver = true;
            snake.removeFirst();
        } else {
            snake.removeLast();
        }

    }

    private boolean collisionIntern() {
        final var unique = new HashSet<>(snake);
        return unique.size() != snake.size();
    }

    private boolean collision() {
        var currentHead = snake.getFirst();

        return currentHead.x < 0 || currentHead.x * cellSize >= boardWidth
                || currentHead.y < 0 || currentHead.y * cellSize >= boardHeight;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
        move();

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (velocityY != 1) {
                    velocityX = 0;
                    velocityY = -1;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (velocityY != -1) {
                    velocityX = 0;
                    velocityY = 1;
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (velocityX != -1) {
                    velocityX = 1;
                    velocityY = 0;
                }
                break;
            case KeyEvent.VK_LEFT:
                if (velocityX != 1) {
                    velocityX = -1;
                    velocityY = 0;
                }
                break;
            case KeyEvent.VK_SPACE:
                if (gameOver) {
                    resetGame();
                    gameLoop.start();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
