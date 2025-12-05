package entity;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;

import main.UtiltiyTool;
import main.gamePanel;

import java.awt.Rectangle;
//ABSTRACT CLASS
public class entity {
public int worldX,worldY;
public int speed;
public BufferedImage up1,up2,down1,down2,left1,left2,right1,right2;
public String direction;
gamePanel gp;
public int spriteCounter =0;
public int spriteNum =1;

public Rectangle solidArea = new Rectangle(0,0,48,48);
public int solidAreadDefaultX,solidAreadDefaultY;
public boolean collisionOn =false;
public int actionLockCounter =0;
public entity(gamePanel gp) {
	this.gp =gp;	
}
public void setAction() {}
public void update() {
	setAction();
	collisionOn =false;
	gp.cChecker.chekTile(this);
	gp.cChecker.checkObject(this, false);
	gp.cChecker.checkPlayer(this);
	// IF COLLISION IS FALSE, PLAYER CAN MOVE 
    if (collisionOn == false) {
        switch (direction) {
            case "up":
                worldY -= speed;
                break;
            case "down":
                worldY += speed;
                break;
            case "left":
                worldX -= speed;
                break;
            case "right":
                worldX += speed;
                break;
        }
    }
    
    spriteCounter++;
    if (spriteCounter > 12) {
        if (spriteNum == 1) {
            spriteNum = 2;
        } else if (spriteNum == 2) {
            spriteNum = 1;
        }
        spriteCounter = 0;
    }
}
public BufferedImage setup(String imagePath) {
UtiltiyTool uTool =  new UtiltiyTool();
BufferedImage  image = null;
try {
image = ImageIO.read(getClass().getResourceAsStream(imagePath+".png"));
image = uTool.scaleImage(image, 33, 43);
}catch(IOException e) {
e.printStackTrace();
}
return image;
}
public void draw(Graphics2D g2) {
    BufferedImage image = null;

	int screenX = worldX -gp.player.worldX +gp.player.screenX;
	int screenY = worldY -gp.player.worldY +gp.player.screenY;
if(worldX +gp.tileSize>gp.player.worldX -gp.player.screenX && worldX -gp.tileSize <gp.player.worldX +gp.player.screenX
		&& worldY + gp.tileSize >gp.player.worldY -gp.player.screenY && worldY -gp.tileSize<gp.player.worldY +gp.player.screenY) {
	switch (direction) {
    case "up":
        if (spriteNum == 1) {
            image = up1;
        }
        if (spriteNum == 2) {
            image = up2;
        }
        break;
    case "down":
        if (spriteNum == 1) {
            image = down1;
        }
        if (spriteNum == 2) {
            image = down2;
        }
        break;
    case "left":
        if (spriteNum == 1) {
            image = left1;
        }
        if (spriteNum == 2) {
            image = left2;
        }
        break;
    case "right":
        if (spriteNum == 1) {
            image = right1;
        }
        if (spriteNum == 2) {
            image = right2;
        }
        break;
}
	g2.drawImage(image, screenX,  screenY,   gp.tileSize, gp.tileSize, null);

}
}
}
