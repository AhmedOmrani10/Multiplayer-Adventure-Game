package entity;

import java.util.Random;

import main.gamePanel;


public class NPC_OldMan extends entity {
    
    public NPC_OldMan(gamePanel gp) {
        super(gp);
        direction = "down";
        speed = 1;
        getImage();
        setDialogue();
    }
    
    public void getImage() {
        up1 = setup("/npc/oldman_up_1");
        up2 = setup("/npc/oldman_up_2");
        down1 = setup("/npc/oldman_down_1");
        down2 = setup("/npc/oldman_down_2");
        left1 = setup("/npc/oldman_left_1");
        left2 = setup("/npc/oldman_left_2");
        right1 = setup("/npc/oldman_right_1");
        right2 = setup("/npc/oldman_right_2");
    }
    
    public void setDialogue() {
        dialogues[0] = "Hello, lad.";
        dialogues[1] = "So you've come to\n this island to find the treasure?";
        dialogues[2] = "I used to be a great wizard but now... \nI'm a bit too old for taking an adventure.";
        dialogues[3] = "Well, good luck on you.";
        dialogues[4] = "You can talk with me again\nwhen you're stuck.";
    }
    
    public void setAction() {
        actionLockCounter++;
        if (actionLockCounter == 120) {
            // Only the master player (player 1) should decide NPC movement
            if (gp.isNPCMaster()) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                String newDirection = direction;
                
                if (i <= 25) {
                    newDirection = "up";
                } else if (i <= 50) {
                    newDirection = "down";
                } else if (i <= 75) {
                    newDirection = "left";
                } else {
                    newDirection = "right";
                }
                
                direction = newDirection;
                
                // Broadcast NPC state change
                gp.broadcastNPCState(0, worldX, worldY, direction, spriteNum);
            }
            actionLockCounter = 0;
        }
    }
    
    public void speak() {
        super.speak();
    }
    
    // Method to update NPC state from network
    public void updateFromNetwork(int worldX, int worldY, String direction, int spriteNum) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.spriteNum = spriteNum;
    }
}