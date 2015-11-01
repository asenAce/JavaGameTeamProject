package game;

import display.Display;
import graphics.Assets;
import graphics.ImgLoader;
import graphics.SpriteSheet;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {
    public static final int WIDTH = 720;
    public static final int HEIGHT = 660;
    public static final int START_POSITION = 480 - 210;
    private String title;
    private Thread thread;
    private boolean isRunning;
    private Display display;
    private BufferStrategy bs;
    private Graphics g;
    private InputHandler ih;

    private SpriteSheet spriteSheet;

    private static Player player;
    private OtherCars otherCar;
    private Track track;
    private Scoreboard scoreboard;

    public Game(String title)
    {
        this.title = title;
        this.isRunning = false;
    }

    public void init(){
        this.display =  new Display(this.title, this.WIDTH, this.HEIGHT);
        this.ih = new InputHandler(this.display);
        this.spriteSheet = new SpriteSheet(ImgLoader.loadImage("/img/car.png"));
        Assets.init();
        this.track = new Track(START_POSITION,this.HEIGHT);
        this.otherCar = new OtherCars(START_POSITION - 150, 10);
        this.player = new Player(START_POSITION + 10, this.HEIGHT - 250, 1);

        this.scoreboard = new Scoreboard(0,0);

    }

    private void tick(){
        this.otherCar.tick();
        this.track.tick();
        this.player.tick();
        this.scoreboard.tick();

        if (this.player.x <= 25) {

            this.player.x = 25;
        } else if (this.player.x >= 303) {

            this.player.x = 303;
        }
        if(this.player.intersects(otherCar)){
           this.player.lives--;
        }
        if(this.player.lives <= 0) {
            //System.out.println("Dead");
            //stop();
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
        this.otherCar.render(g);
        this.scoreboard.render(g);

        //END DRAWING

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
