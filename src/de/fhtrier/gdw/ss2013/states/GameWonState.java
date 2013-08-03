package de.fhtrier.gdw.ss2013.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import de.fhtrier.gdw.ss2013.MainGame;
import de.fhtrier.gdw.ss2013.assetloader.AssetLoader;
import de.fhtrier.gdw.ss2013.game.world.World;
import de.fhtrier.gdw.ss2013.gui.utils.CenteredText;
import de.fhtrier.gdw.ss2013.gui.MechanicalCounter;
import de.fhtrier.gdw.ss2013.gui.RollerCounter;
import de.fhtrier.gdw.ss2013.gui.ScoreCounter;

public class GameWonState extends BasicGameState {

    private boolean RunUpdateAtLeastOneTime = false;

    //?
    int stellen = 6;
	int counter = 0;
	int zehnerpotenz = 10;
	int score = 123;
	int timer = 0;
	boolean ready = false;
	//?
	
	//MechanicalCounter
	MechanicalCounter mechanicalCounter;
	

	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
	    
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
	    
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
	    if(RunUpdateAtLeastOneTime)
	    {
	        
	    }
	    else
	    {
	        RunUpdateAtLeastOneTime = true;
	    }
	    
	       /* DO NOT DELETE! FOR FUTURE USE
         * 
         * 
        timer += delta;
        
        if (timer >= 100 && counter < score){
           
            System.out.println("zaehler[0].up");
            counter++;
            timer = 0;
            
            for (int j = 1; j <= stellen; j++){
                
                if (counter % zehnerpotenz == 0){
                            System.out.println("zaehler["+ j +"].up");
                }
               
                zehnerpotenz *= 10;
            }
            
            
        }
        zehnerpotenz = 10;
        initTime += delta;
        */
	}

	@Override
	public int getID() {
		return MainGame.WINSTATE;
	}

}
