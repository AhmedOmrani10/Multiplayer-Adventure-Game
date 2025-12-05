package main;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.awt.BasicStroke;
import object.OBJ_Key;
import java.awt.RenderingHints;

import java.awt.Font;
import java.awt.FontFormatException;
public class UI {
gamePanel gp;
Graphics2D g2;
Font maruMonica,pursiaBold;
BufferedImage keyImage;
public boolean messageOn = false;
public String message ="";
int messageCounter = 0;
public boolean gameFinished =false;
double playTime;
DecimalFormat dFormat = new DecimalFormat("#0.00");
public String currentDialogue ="";
public UI(gamePanel gp) {
	this.gp = gp;
	
try {
	InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
	maruMonica = Font.createFont(Font.TRUETYPE_FONT,is);
	is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
	pursiaBold = Font.createFont(Font.TRUETYPE_FONT,is);
} catch (FontFormatException | IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
	OBJ_Key key = new OBJ_Key(gp);
	keyImage = key.image;
}
public void showMessage(String text) {
	message =  text;
	messageOn =true;
}
public void draw(Graphics2D g2) {
	this.g2 =g2;
	g2.setFont(maruMonica);
	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	g2.setColor(Color.white);
	//PLAY STATE
	if(gp.gameState == gp.playState) {
		
	}
	//PAUSE STATE
	if(gp.gameState == gp.pauseState) {
		 drawPauseScreen();
	}
	//DIALOGUE STATE
	if(gp.gameState == gp.dialogueState) {
		drawDialogueScreen();
	}
	if(gameFinished ==true) {
		g2.setFont(maruMonica);
	    g2.setColor(Color.white);
		String text;
		int textLength;
		int x;
		int y;
		text =  "You found the treasure!";
		textLength = (int) g2.getFontMetrics().getStringBounds(text,g2).getWidth();
		 x = gp.screenWidth/2-textLength/2;
		 y = gp.screenHeight/2 + (gp.tileSize*4);
		 g2.drawString(text,x,y);
		 
		 
		 
		 	text =  "Your Time is :"+dFormat.format(playTime)+"!";
			textLength = (int) g2.getFontMetrics().getStringBounds(text,g2).getWidth();
			 x = gp.screenWidth/2-textLength/2;
			 y = gp.screenHeight/2 + (gp.tileSize*3);
			 g2.drawString(text,x,y);
		 
		 
		 g2.setFont(maruMonica);
		 g2.setColor(Color.yellow);
		 text = "Congratulations!";
		 textLength = (int) g2.getFontMetrics().getStringBounds(text,g2).getWidth();
		 x = gp.screenWidth/2-textLength/2;
		 y = gp.screenHeight/2 - (gp.tileSize*2);
		 g2.drawString(text,x,y);
		 gp.gameThread=null;
		 
		 
		 
	}else {
		g2.setFont(maruMonica);
	    g2.setColor(Color.white);
	    g2.drawImage(keyImage,gp.tileSize/2,gp.tileSize/2,gp.tileSize,gp.tileSize,null);
	    g2.drawString("x"+gp.player.hasKey,74,65);
       // TIME
	    playTime +=(double)1/60;
	    //g2.drawString("Time:"+dFormat.format(playTime),gp.tileSize*11,65);
	    if(messageOn ==true) {
	    	g2.setFont(g2.getFont().deriveFont(30F));
	        g2.drawString(message,gp.tileSize/2,gp.tileSize*5);
	messageCounter++;
	//if message is displayed for more thatn  120 frames delete it (2 seconds)
	if(messageCounter > 120 ) {
		messageCounter = 0;
		messageOn =false;
	}
	    }
	}
}

public void drawDialogueScreen() {
	//Window
	// WINDOW
    int x = gp.tileSize * 3;
    int y = gp.tileSize / 2;
    int width = gp.screenWidth - (gp.tileSize * 6);
    int height = gp.tileSize * 4;

    drawSubWindow(x,y,width,height);

    g2.setFont(g2.getFont().deriveFont(Font.PLAIN,28F));
    x += gp.tileSize;
    y += gp.tileSize;
    for(String line : currentDialogue.split("\n"))   // splits dialogue until "\n" as a line
    {
        g2.drawString(line,x,y);
        y += 40;
    }
    
}
public void drawSubWindow(int x, int y, int width, int height)
{
    Color c = new Color(0,0,0,210);  // R,G,B, alfa(opacity)
    g2.setColor(c);
    g2.fillRoundRect(x,y,width,height,35,35);
    c = new Color(255,255,255);
    g2.setColor(c);
    g2.setStroke(new BasicStroke(5));    // 5 = width of outlines of graphics
    g2.drawRoundRect(x+5,y+5,width-10,height-10,25,25);
   

}
public void drawPauseScreen() {
	g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
	String text = "PAUSED";
	
	int x = getxForCenterText(text);
	int y = gp.screenHeight/2;
	
	g2.drawString(text,x,y);
}
public int getxForCenterText(String text) {
	int length = (int)g2.getFontMetrics().getStringBounds(text,g2).getWidth();
	int x  = gp.screenWidth/2 -length/2;
	return x;
}
}
