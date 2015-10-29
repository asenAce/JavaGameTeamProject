package game;

import display.Display;
import graphics.Assets;
import graphics.ImgLoader;
import graphics.SpriteSheet;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable{
    private String title;
    private int width;
    private int height;
    private Thread thread;
    private boolean isRunning;
    private Display display;
    private BufferStrategy bs;
    private Graphics g;
    private InputHandler ih;

    private SpriteSheet spriteSheet;
    private int x;
    private int y;

    private Player player;
    private OtherCars otherCar;



    public Game(String title, int width, int height)
    {
        this.title = title;
        this.width = width;
        this.height = height;
        this.isRunning = false;
        this.x = 270;
        this.y = 410;
    }

    public void init(){
        this.display =  new Display(this.title, this.width, this.height);
        this.ih = new InputHandler(this.display);
        this.spriteSheet = new SpriteSheet(ImgLoader.loadImage("/img/car.png"));
        Assets.init();
        this.player = new Player(this.x + 10, this.y, 1);
        this.otherCar = new OtherCars(this.x, 10);

    }

    private void tick(){
       // this.y -= 5;//check move
        this.player.tick();
        this.otherCar.tick();

        if(this.player.intersects(otherCar)){
            this.player.lives--;
        }
        if(this.player.lives <= 0){
            System.out.println("Dead");
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
        //this.g.clearRect(0,0,this.width,this.height);
        this.g.drawImage(ImgLoader.loadImage("/img/bkg.jpg"),0,0,null);
        //this.g.drawImage(this.player, 100, 100, null);
        //this.g.drawImage(Assets.playerCar1,x,y,null);
        //this.g.drawImage(Assets.playerCar2,x + 45,y,null);
        //this.g.drawImage(Assets.ambulance,x + 30,y,null);
        //this.g.drawImage(Assets.policeCar,x + 15,y,null);
        //this.g.drawImage(Assets.taxi,x + 30,y,null);
        //this.g.drawImage(Assets.playerCar4,x + 25,y,null);
        //this.g.drawImage(Assets.playerCar5,x,y,null);
        //this.g.drawImage(Assets.playerCar6,x + 10,y,null);
        //this.g.drawImage(Assets.playerCar7,x + 25,y,null);

        this.player.render(g);
        this.otherCar.render(g);
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
