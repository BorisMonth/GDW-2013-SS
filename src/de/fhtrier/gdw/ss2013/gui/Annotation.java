/**
 * Boris, David (UI-Team)
 */
package de.fhtrier.gdw.ss2013.gui;

import java.util.ArrayList;

import org.newdawn.slick.Font;
import org.newdawn.slick.Image;

import de.fhtrier.gdw.ss2013.game.Entity;
import de.fhtrier.gdw.ss2013.game.EntityManager;
import de.fhtrier.gdw.ss2013.game.filter.EntityFilter;
import de.fhtrier.gdw.ss2013.game.world.World;
import de.fhtrier.gdw.ss2013.game.world.objects.Switch;
import de.fhtrier.gdw.ss2013.gui.utils.CenteredText;



public class Annotation {
    
    private World worldinstance;
    private EntityManager entityManager;
    //private Image toolTipSwitch;
    private Font font;
    
	public Annotation() {

	}

	public void init(World worldinstance, Font font)
	{
	    this.font=font;
	    this.worldinstance = worldinstance;
	    entityManager = worldinstance.getEntityManager();
	  
	    //this.toolTipSwitch = toolTipSwitch;
	}
	
	public void update() {

	    
	}

	public void render() {
	    drawTooltip(Switch.class,"Setz Alien hier drauf zum Umlegen");
	    drawTooltip(Switch.class,"Drücke \"Aktivieren\" zum aktivieren.");	    
	}

    private void drawTooltip(Class<? extends EntityFilter> filter, String string) {
        ArrayList<Entity> entities = entityManager.getClosestEntitiesByFilter(worldinstance.getAstronaut().getPosition(), 100, filter);
	    //Hebel: rendern
	    for (int i = 0; i < entities.size(); i++){
	        CenteredText.draw(entities.get(i).getPosition().x + 40, entities.get(i).getPosition().x - 40, string, font);
	        //tooltip.draw(entities.get(i).getPosition().x + 40, entities.get(i).getPosition().x - 40);
	    }
    }

}
