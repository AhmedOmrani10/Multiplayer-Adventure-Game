package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.gamePanel;

public class RemotePlayer extends entity {
    public int playerId;
    private long lastUpdateTime;
    private static final long TIMEOUT = 5000; // 5 seconds timeout
    
    public RemotePlayer(gamePanel gp, int playerId) {
        super(gp); // Call parent constructor with gamePanel
        this.playerId = playerId;
        this.lastUpdateTime = System.currentTimeMillis();
        
        setDefaultValues();
        getPlayerImage();
    }
    
    public void setDefaultValues() {
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }
    
    public void getPlayerImage() {
        // Use the setup method from parent entity class
        up1 = setup("/player/walkUp1-02");
        up2 = setup("/player/walkUp2-02");
        right1 = setup("/player/idleRight-02");
        right2 = setup("/player/walkRight-02");
        left1 = setup("/player/idleLeft");
        left2 = setup("/player/walkLeft-02");
        down1 = setup("/player/walk_without_wepons_down_1-02");
        down2 = setup("/player/walk_without_wepons_down_2-02");
    }
    
    public void updatePosition(int worldX, int worldY, String direction, int spriteNum) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.spriteNum = spriteNum;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public boolean isActive() {
        return (System.currentTimeMillis() - lastUpdateTime) < TIMEOUT;
    }
    
    public void draw(Graphics2D g2, int screenX, int screenY) {
        BufferedImage image = null;
        
        switch (direction) {
            case "up":
                image = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                image = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                image = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                image = (spriteNum == 1) ? right1 : right2;
                break;
        }
        
        // Calculate screen position relative to local player
        int screenPosX = screenX - gp.player.worldX + worldX;
        int screenPosY = screenY - gp.player.worldY + worldY;
        
        // Only draw if on screen
        if (screenPosX + gp.tileSize > 0 && screenPosX < gp.screenWidth &&
            screenPosY + gp.tileSize > 0 && screenPosY < gp.screenHeight) {
            
            g2.drawImage(image, screenPosX, screenPosY, 33, 43, null);
            
            // Draw player ID above the remote player
            g2.setColor(Color.WHITE);
            g2.drawString("P" + playerId, screenPosX + 8, screenPosY - 5);
        }
    }
}