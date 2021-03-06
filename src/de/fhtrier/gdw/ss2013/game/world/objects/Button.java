package de.fhtrier.gdw.ss2013.game.world.objects;

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import de.fhtrier.gdw.ss2013.assetloader.AssetLoader;
import de.fhtrier.gdw.ss2013.game.Entity;
import de.fhtrier.gdw.ss2013.game.player.Alien;
import de.fhtrier.gdw.ss2013.game.player.Astronaut;
import de.fhtrier.gdw.ss2013.physix.ICollisionListener;
import de.fhtrier.gdw.ss2013.physix.PhysixShape;
import de.fhtrier.gdw.ss2013.sound.SoundLocator;
import de.fhtrier.gdw.ss2013.sound.SoundPlayer;

/**
 * Button Entity<br>
 * <br>
 * Behavior:<br>
 * - gets activated when collision with first {@link Player} or {@link Box}
 * starts<br>
 * - gets deactivated when all collisions with Players and Boxes stop<br>
 * - de/activates all connected Entities when getting de/activated
 * 
 * @author Kevin, Georg<br>
 *         Editor: BreakingTheHobbit
 * 
 * @see ObjectController
 * @see ICollisionListener
 */
public class Button extends ObjectController implements ICollisionListener {

    protected int pressContacts;
    private Image unpressedImg;
    private Image pressedImg;
    private SoundPlayer soundPlayer;
    private Sound buttonSound;

    public Button() {
        unpressedImg = AssetLoader.getInstance().getImage("button_unpressed");
        pressedImg = AssetLoader.getInstance().getImage("button_pressed");
    }

    @Override
    public boolean isBottomPositioned() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        super.initialize();
        pressContacts = 0;
        setImage(unpressedImg);
        setInitialSize(unpressedImg.getWidth(), unpressedImg.getHeight());
        soundPlayer = SoundLocator.getPlayer();
        buttonSound = SoundLocator.loadSound("knopfschalter");
    }

    @Override
    public void initPhysics() {
        createPhysics(BodyType.STATIC, origin.x, origin.y).sensor(true).asBox(
                initialSize.x, initialSize.y);
    }

    public void setActivated(boolean active) {
        // FIXME DAFUQ is that???
        if (isActivated != active) {
            if (active) {
                activate();
            } else {
                deactivate();
            }
            isActivated = active;
        }
    }

    @Override
    public void activate() {
        buttonSound.stop();
        soundPlayer.playSoundAt(buttonSound, this);
        super.activate();
    }

    @Override
    public void deactivate() {
        buttonSound.stop();
        soundPlayer.playSoundAt(buttonSound, this);
        super.deactivate();
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
        super.update(container, delta);
        if (isActivated() && getImage().equals(unpressedImg)) {
            setImage(pressedImg);
        } else if (!isActivated() && getImage().equals(pressedImg)) {
            setImage(unpressedImg);
        }
    }

    public boolean isPressed() {
        return pressContacts > 0;
    }

    @Override
    public void setPhysicsObject(PhysixShape physicsObject) {
        super.setPhysicsObject(physicsObject);
        this.physicsObject.addCollisionListener(this);
    }

    @Override
    public void beginContact(Contact contact) {
        Entity other = getOtherEntity(contact);

        // activate only if nothing relevant touched the button before
        boolean wasInactive = pressContacts == 0;
        if (other instanceof Astronaut || other instanceof Alien
                || other instanceof Box) {
            pressContacts++;
        }

        // activate the button, but only if nothing touched the button before,
        // but does now
        if (wasInactive && pressContacts > 0) {
            activate();
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity other = getOtherEntity(contact);

        // activate only if nothing relevant touched the button before
        boolean wasActive = pressContacts > 0;
        if (other instanceof Astronaut || other instanceof Alien
                || other instanceof Box) {
            pressContacts--;
        }

        // activate the button, but only if nothing touched the button before,
        // but does now
        if (wasActive && pressContacts == 0) {
            deactivate();
        }
    }
}
