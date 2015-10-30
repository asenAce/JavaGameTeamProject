package game;

import graphics.Assets;
import graphics.ImgLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Scoreboard {

    public int x;
    public int y;;

    public Scoreboard(int x, int y) {
        //this.x = 0;
        //this.y = 0;
    }

    public void tick() {

    }

    public void render(Graphics g) {
        g.drawImage(Assets.scoreboardImage,475,0,null);
        for (int i = 0; i < 5 ; i++) {
            g.drawImage(Assets.nineScore,480 + (45 * i),250,null);
        }
        //g.drawImage(Assets.hearthImage,480,117,null);
        g.drawImage(Assets.zeroLives,545,70,null);
        g.drawImage(Assets.oneLives,635,70,null);
        //g.drawImage(Assets.score, 490, 220, null);

    }
}
