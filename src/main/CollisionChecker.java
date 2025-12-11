package main;

import javax.swing.text.html.parser.Entity;

import entity.entity;

public class CollisionChecker {
	gamePanel gp;
	public  CollisionChecker(gamePanel gp) {
		this.gp =gp;
	}
	// ===========================================================
    // Check collision between an entity and tiles in the world
    // Determines if the entity will collide with a solid tile in the next move
    // ===========================================================
	
	public void chekTile(entity entity) {
		//calculate the world coordinates of the four sidesof the entity’s solid area.
		int entityLeftWorldX = entity.worldX +entity.solidArea.x;
		int entityRightWorldX = entity.worldX +entity.solidArea.x+entity.solidArea.width;
		int entityTopWorldY =entity.worldY +entity.solidArea.y;
		int entityBottomWorldY = entity.worldY +entity.solidArea.y +entity.solidArea.height;
		
		
		int entityLeftCol = entityLeftWorldX/gp.tileSize;
		int entityRightCol = entityRightWorldX/gp.tileSize;
		int entityTopRow = entityTopWorldY/gp.tileSize;
		int entityBottomRow = entityBottomWorldY/gp.tileSize;
		
		
		int tileNum1,tileNum2;
		
		switch(entity.direction) {
		case "up":
			entityTopRow = (entityTopWorldY -entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2]
					.collision == true) {
				entity.collisionOn =true;
			}
			
			break;
		case "down":
		    entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
		    tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
		    tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
		    if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2].collision == true) {
		        entity.collisionOn = true;
		    }
		    break;
		case "left":
			entityLeftCol = (entityLeftWorldX -entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2]
					.collision == true) {
				entity.collisionOn =true;
			}
			break;
		case "right":
			entityRightCol = (entityRightWorldX +entity.speed)/gp.tileSize;
			tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
			tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
			if(gp.tileM.tile[tileNum1].collision == true || gp.tileM.tile[tileNum2]
					.collision == true) {
				entity.collisionOn =true;
			}
			break;
		}

	}
	// ===========================================================
    // Check collision between an entity and objects in the world
    // Returns index of object if player collides, else 999
    // ===========================================================
public int checkObject(entity entity,boolean player) {
	
	int index = 999;
	for(int i =0;i<gp.obj.length;i++) {
		if(gp.obj[i]!=null) {
			// Get entity's solid area position
			entity.solidArea.x = entity.worldX+entity.solidArea.x;
			entity.solidArea.y = entity.worldY +entity.solidArea.y;
		// Get the object's solid area position  
		gp.obj[i].solidArea.x = gp.obj[i].worldX +gp.obj[i].solidArea.x;
		gp.obj[i].solidArea.y = gp.obj[i].worldY +gp.obj[i].solidArea.y;
switch(entity.direction) {
case "up" :
	entity.solidArea.y -= entity.speed;
	// WE DIDN'T USE INTERSECTR METHOD ON TILES BECAUSE WILL WIL HAVE TO CHECK ON ALL THE TILES IN CONTRAST THE OBKECT ARE ONLY 10 AT MAX 17:00
	if(entity.solidArea.intersects(gp.obj[i].solidArea)) {
if(gp.obj[i].collision == true) {
	entity.collisionOn =true;
}
// to handle interction like pick up an item
if(player ==true) {
	index = i;
}
	}
	break;
case "down" :
	entity.solidArea.y += entity.speed;
	if(entity.solidArea.intersects(gp.obj[i].solidArea)) {
		if(gp.obj[i].collision == true) {
			entity.collisionOn =true;
		}
		if(player ==true) {
			index = i;
		}	}
	break;
case "left" :
	entity.solidArea.x -= entity.speed;
	if(entity.solidArea.intersects(gp.obj[i].solidArea)) {
		if(gp.obj[i].collision == true) {
			entity.collisionOn =true;
		}
		if(player ==true) {
			index = i;
		}	}
	break;
case "right" :
	entity.solidArea.x += entity.speed;
	if(entity.solidArea.intersects(gp.obj[i].solidArea)) {
		if(gp.obj[i].collision == true) {
			entity.collisionOn =true;
		}
		if(player ==true) {
			index = i;
		}	}
	break;
}
entity.solidArea.x = entity.solidAreadDefaultX;
entity.solidArea.y = entity.solidAreadDefaultY;
gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;

	
		}
		
	}
	return index;
}
// ===========================================================
// Check collision between an entity and other entities (NPCs/monsters)
// Returns index of target collided with, else 999
// ===========================================================
//npc or monster
public int checkEntity(entity entity,entity[] target ) {

	int index = 999;
	for(int i =0;i<target.length;i++) {
		if(target[i]!=null) {
			// Get entity's solid area position
			entity.solidArea.x = entity.worldX+entity.solidArea.x;
			entity.solidArea.y = entity.worldY +entity.solidArea.y;
		// Get the object's solid area position  
			target[i].solidArea.x = target[i].worldX +target[i].solidArea.x;
			target[i].solidArea.y = target[i].worldY +target[i].solidArea.y;
switch(entity.direction) {
case "up" :
	entity.solidArea.y -= entity.speed;
	if(entity.solidArea.intersects(target[i].solidArea)) {

	entity.collisionOn =true;


	index = i;

	}
	break;
case "down" :
	entity.solidArea.y += entity.speed;
	if(entity.solidArea.intersects(target[i].solidArea)) {
		
			entity.collisionOn =true;
	
			index = i;
		}	
	break;
case "left" :
	entity.solidArea.x -= entity.speed;
	if(entity.solidArea.intersects(target[i].solidArea)) {
		
			entity.collisionOn =true;
		
			index = i;
			}
	break;
case "right" :
	entity.solidArea.x += entity.speed;
	if(entity.solidArea.intersects(target[i].solidArea)) {
		
			entity.collisionOn =true;
		
		
			index = i;
			break;
			}
	
}
entity.solidArea.x = entity.solidAreadDefaultX;
entity.solidArea.y = entity.solidAreadDefaultY;
target[i].solidArea.x = target[i].solidAreadDefaultX;
target[i].solidArea.y = target[i].solidAreadDefaultY;

	
		}
		
	}
	return index;	
}
//===========================================================
// Check collision between an entity and the player
// Sets collisionOn = true if entity would hit the player
// ===========================================================
public void checkPlayer(entity entity) {
	// Get entity's solid area position
	entity.solidArea.x = entity.worldX+entity.solidArea.x;
	entity.solidArea.y = entity.worldY +entity.solidArea.y;
// Get the object's solid area position  
	gp.player.solidArea.x = gp.player.worldX +gp.player.solidArea.x;
	gp.player.solidArea.y = gp.player.worldY +gp.player.solidArea.y;
switch(entity.direction) {
case "up" :
entity.solidArea.y -= entity.speed;
if(entity.solidArea.intersects(gp.player.solidArea)) {

entity.collisionOn =true;




}
break;
case "down" :
entity.solidArea.y += entity.speed;
if(entity.solidArea.intersects(gp.player.solidArea)) {

	entity.collisionOn =true;

	
}	
break;
case "left" :
entity.solidArea.x -= entity.speed;
if(entity.solidArea.intersects(gp.player.solidArea)) {

	entity.collisionOn =true;

	
	}
break;
case "right" :
entity.solidArea.x += entity.speed;
if(entity.solidArea.intersects(gp.player.solidArea)) {

	entity.collisionOn =true;



	break;
	}

}
entity.solidArea.x = entity.solidAreadDefaultX;
entity.solidArea.y = entity.solidAreadDefaultY;
gp.player.solidArea.x = gp.player.solidAreadDefaultX;
gp.player.solidArea.y = gp.player.solidAreadDefaultY;


}

}
