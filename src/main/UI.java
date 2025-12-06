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
    Font maruMonica, pursiaBold;
    BufferedImage keyImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");
    public String currentDialogue = "";
    
    // INVENTORY
    private final int slotCol = 5;
    private final int slotRow = 4;
    private int slotSize;
    private int slotX;
    private int slotY;
    
    public UI(gamePanel gp) {
        this.gp = gp;
        
        // Calculate inventory slot positions
        slotSize = gp.tileSize + 3;
        
        try {
            InputStream is = getClass().getResourceAsStream("/font/x12y16pxMaruMonica.ttf");
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
            is = getClass().getResourceAsStream("/font/Purisa Bold.ttf");
            pursiaBold = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        
        OBJ_Key key = new OBJ_Key(gp);
        keyImage = key.image;
    }
    
    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }
    
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        g2.setFont(maruMonica);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);
        
        // PLAY STATE
        if (gp.gameState == gp.playState) {
            drawPlayScreen();
        }
        // PAUSE STATE
        if (gp.gameState == gp.pauseState) {
            drawPauseScreen();
        }
        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState) {
            drawDialogueScreen();
        }
        // INVENTORY STATE
        if (gp.gameState == gp.inventoryState) {
            drawInventory();
        }
    }
    
    public void drawPlayScreen() {
        if (gameFinished == true) {
            g2.setFont(maruMonica);
            g2.setColor(Color.white);
            String text;
            int textLength;
            int x;
            int y;
            text = "You found the treasure!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2 + (gp.tileSize * 4);
            g2.drawString(text, x, y);
            
            text = "Your Time is :" + dFormat.format(playTime) + "!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2 + (gp.tileSize * 3);
            g2.drawString(text, x, y);
            
            g2.setFont(maruMonica);
            g2.setColor(Color.yellow);
            text = "Congratulations!";
            textLength = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth / 2 - textLength / 2;
            y = gp.screenHeight / 2 - (gp.tileSize * 2);
            g2.drawString(text, x, y);
            gp.gameThread = null;
        } else {
            g2.setFont(maruMonica);
            g2.setColor(Color.white);
            
            // Display key count in top-left corner
            g2.drawImage(keyImage, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize, gp.tileSize, null);
            g2.drawString("x" + gp.player.hasKey, 74, 65);
            
            // TIME
            playTime += (double) 1 / 60;
            
            // Show "Press I for Inventory" hint
            g2.setFont(g2.getFont().deriveFont(20F));
            g2.drawString("Press I for Inventory", gp.tileSize / 2, gp.screenHeight - 20);
            
            if (messageOn == true) {
                g2.setFont(g2.getFont().deriveFont(30F));
                g2.drawString(message, gp.tileSize / 2, gp.tileSize * 5);
                messageCounter++;
                // if message is displayed for more than 120 frames delete it (2 seconds)
                if (messageCounter > 120) {
                    messageCounter = 0;
                    messageOn = false;
                }
            }
        }
    }
    
    public void drawInventory() {
        // Draw background frame
        int frameX = gp.tileSize;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize * 14;
        int frameHeight = gp.tileSize * 10;
        drawSubWindow(frameX, frameY, frameWidth, frameHeight);
        
        // Draw title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
        g2.setColor(Color.WHITE);
        String text = "INVENTORY";
        int x = getxForCenterText(text);
        int y = frameY + gp.tileSize;
        g2.drawString(text, x, y);
        
        // Draw inventory slots - bigger slots
        int biggerSlotSize = gp.tileSize + 20; // Increased from +3 to +20
        slotX = frameX + 20;
        slotY = frameY + gp.tileSize * 2;
        
        // Draw slot grid
        for (int i = 0; i < slotRow; i++) {
            for (int j = 0; j < slotCol; j++) {
                int currentSlotX = slotX + (biggerSlotSize * j);
                int currentSlotY = slotY + (biggerSlotSize * i);
                
                // Draw slot background
                g2.setColor(new Color(50, 50, 50));
                g2.fillRoundRect(currentSlotX, currentSlotY, gp.tileSize + 15, gp.tileSize + 15, 10, 10);
                
                // Draw slot border
                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(currentSlotX, currentSlotY, gp.tileSize + 15, gp.tileSize + 15, 10, 10);
            }
        }
        
        // Draw items in inventory - bigger images
        int itemX = slotX + 7;
        int itemY = slotY + 7;
        int itemSize = gp.tileSize + 5; // Make items bigger
        
        for (int i = 0; i < gp.player.inventory.size(); i++) {
            // Draw item image with bigger size
            g2.drawImage(gp.player.inventory.get(i).image, itemX, itemY, itemSize, itemSize, null);
            
            itemX += biggerSlotSize;
            if ((i + 1) % slotCol == 0) {
                itemX = slotX + 7;
                itemY += biggerSlotSize;
            }
        }
        
        // Draw item count
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 24F));
        g2.setColor(Color.WHITE);
        text = "Items: " + gp.player.inventory.size() + "/" + gp.player.maxInventorySize;
        x = frameX + 20;
        y = frameY + frameHeight - 20;
        g2.drawString(text, x, y);
        
        // Draw instruction
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        text = "Press I or C to close";
        x = getxForCenterText(text);
        y = frameY + frameHeight - 50;
        g2.drawString(text, x, y);
    }

    public void drawDialogueScreen() {
        // WINDOW
        int x = gp.tileSize * 3;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 6);
        int height = gp.tileSize * 4;

        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.tileSize;
        y += gp.tileSize;
        for (String line : currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += 40;
        }
    }
    
    public void drawSubWindow(int x, int y, int width, int height) {
        Color c = new Color(0, 0, 0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);
        c = new Color(255, 255, 255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
    }
    
    public void drawPauseScreen() {
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
        String text = "PAUSED";
        
        int x = getxForCenterText(text);
        int y = gp.screenHeight / 2;
        
        g2.drawString(text, x, y);
    }
    
    public int getxForCenterText(String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth / 2 - length / 2;
        return x;
    }
}