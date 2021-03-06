package game;

import display.Display;
import graphics.Assets;
import graphics.ImgLoader;
import graphics.SpriteSheet;
import objects.*;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {
    public static final int WIDTH = 720;
    public static final int HEIGHT = 658;
    public static final int START_POSITION = 450;
    public final int LEFT_BORDER = 55;
    public final int RIGHT_BORDER = 332;


    private String title;
    private Thread thread;
    private boolean isRunning;
    private Display display;
    private BufferStrategy bs;
    private Graphics g;
    private InputHandler ih;
    private SpriteSheet spriteSheet;

    private Player player;
    private OtherCars otherCar;
    private OtherCars otherCar3;
    private Car car;

    private Track track;
    private Scoreboard scoreboard;

    private Coins coins;
    private Skull skull;
    public static boolean isDie = false;

    public final int positionX1 = 70;
    public final int positionX2 = 320;

    public Game(String title) {
        this.title = title;
        this.isRunning = false;
    }

    public void init(){
        this.display =  new Display(this.title, WIDTH, HEIGHT);
        this.ih = new InputHandler(this.display);
        this.spriteSheet = new SpriteSheet(ImgLoader.loadImage("/img/spriteCars.png"));
        Assets.init();
        this.track = new Track(START_POSITION, HEIGHT);
        this.otherCar = new OtherCars(positionX1, 0, 0);
        this.otherCar3 = new OtherCars(positionX2, 530, 1);
        this.car = new Car();
        this.player = new Player(200, HEIGHT - 190);
        this.scoreboard = new Scoreboard(0,0, this.player);
        this.coins = new Coins(this.player, 150, 0);
        this.skull = new Skull(this.player, 200, 0);
    }

    private void tick(){
        this.otherCar.tick();
        this.otherCar3.tick();
        this.car.tick();
        this.track.tick();
        this.player.tick();

        if (this.player.getX() <= this.LEFT_BORDER) {
            this.player.setX(this.LEFT_BORDER);
        } else if (this.player.getX() >= this.RIGHT_BORDER) {
            this.player.setX(this.RIGHT_BORDER);
        }

        if(this.player.intersects(this.otherCar)
        || this.player.intersects(this.otherCar3)
        || this.player.intersects(this.car)){
            int takeBlood = this.player.getBlood() - 5;
            this.player.setBlood(takeBlood);
        }

        this.player.intersectsObject(this.coins);
        this.player.intersectsObject(this.skull);

        this.scoreboard.tick();
        this.coins.tick();
        this.skull.tick();

        if(this.isDie){
            stop();
        }
    }

    private void render(){
        this.bs = this.display.getCanvas().getBufferStrategy();

        if(this.bs == null){
            this.display.getCanvas().createBufferStrategy(2);
            this.bs = display.getCanvas().getBufferStrategy();
        }
        this.g = this.bs.getDrawGraphics();

        //START DRAWING
        this.track.render(g);
        this.player.render(g);


        if(this.player.getBlood() < 0){
            g.drawImage(Assets.red, -90, 0, Game.WIDTH - 50, Game.HEIGHT - 50, null);
            isDie = true;
        }
        this.scoreboard.render(g);
        this.coins.render(g);
        this.otherCar.render(g);
        this.otherCar3.render(g);
        this.car.render(g);
        this.skull.render(g);

        this.bs.show();
        this.g.dispose();
    }

    @Override
    public void run() {
        this.init();
        int fps = 30;
        double ticksPF = 1_000_000_000 / fps;
        double delta = 0;
        long now;
        long lastTimeTicked = System.nanoTime();

        while(isRunning){
            now = System.nanoTime();
            delta += (now - lastTimeTicked) / ticksPF;
            lastTimeTicked = now;
            if (delta >= 1){
                tick();
                render();
                delta--;
            }
        }
        this.stop();
    }

    public synchronized void start(){
        if(this.isRunning){
            return;
        }
        this.isRunning = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public synchronized void stop(){
        if(!this.isRunning){
            return;
        }
        this.isRunning = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
