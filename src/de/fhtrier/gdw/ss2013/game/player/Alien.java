package de.fhtrier.gdw.ss2013.game.player;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.Log;

import de.fhtrier.gdw.ss2013.assetloader.AssetLoader;
import de.fhtrier.gdw.ss2013.game.world.World;
import de.fhtrier.gdw.ss2013.input.AlienController;

public class Alien extends Player implements AlienController {

    private int selectedAbility;
    private float mana;
    private float maxMana;
    private final Vector2f cursor = new Vector2f();
    private GameContainer container;
    
    public Alien() {
    	setAnimation(AssetLoader.getInstance().getAnimation("alien_standing")); // Alien does NOT have different movestates! byRobin
        selectedAbility = 1;
        maxMana = 0.0f;
        mana = maxMana;     
    }

    public void setContainer(GameContainer container) {
        this.container = container;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public float getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(float maxMana) {
        this.maxMana = maxMana;
    }

    @Override
    public void shoot() {
        Log.debug("shooting");
     

    }

    @Override
    public void nextAbility() {
        selectedAbility = (selectedAbility % 3) + 1;

        Log.debug("rotate ability");
    }

    @Override
    public void previousAbility() {

        /*if (selectedAbility > 0){
         selectedAbility--;
         }else{
         selectedAbility = 3;
         }*/

        selectedAbility = ((selectedAbility + 1) % 3) + 1;

    }

    public int getselectedAbility() {
        return selectedAbility;
    }

    @Override
    public void useAbility() {
        Log.debug("using ability");
      
    }

    @Override
    public void setCursor(int x, int y) {
        cursor.set(Math.min(container.getWidth(), Math.max(0, x)), 
                Math.min(container.getHeight(), Math.max(0, y)));
        // TODO Auto-generated method stub
        //   Log.debug("target direction");
        //  setZustand("animtest");
    }

    @Override
    public void update(GameContainer container, int delta) throws SlickException {
        // TODO Auto-generated method stub
        super.update(container, delta);

     //  if (World.getInstance().getAstronaut().isCarryAlien() == true) {
           //this.setPosition(World.getInstance().getAstronaut().getPosition().x, (World.getInstance().getAstronaut().getPosition().y));
     //  }
    }
    
    @Override
    public void render(GameContainer container, Graphics g) throws SlickException {
    	Astronaut astronaut = World.getInstance().getAstronaut();
    	// Just render alien if astronaut does not carry the alien
    	if (astronaut != null && !astronaut.isCarryAlien()) {
    		super.render(container, g);
    	}
    }

    @Override
    public void cursorLeft(float scale) {
        cursor.x = Math.min(container.getWidth(), Math.max(0, cursor.x-10.0f*scale));
    }

    @Override
    public void cursorRight(float scale) {
        cursor.x = Math.min(container.getWidth(), Math.max(0, cursor.x+10.0f*scale));
    }

    @Override
    public void cursorUp(float scale) {
        cursor.y = Math.min(container.getHeight(), Math.max(0, cursor.y-10.0f*scale));
    }

    @Override
    public void cursorDown(float scale) {
        cursor.y = Math.min(container.getHeight(), Math.max(0, cursor.y+10.0f*scale));
    }

    public Vector2f getCursor() {
        return cursor;
    }
    
    
    
}
