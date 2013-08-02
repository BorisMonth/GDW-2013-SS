
package de.fhtrier.gdw.ss2013.game.player;

import java.util.HashSet;
import java.util.Set;

import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import de.fhtrier.gdw.ss2013.assetloader.AssetLoader;
import de.fhtrier.gdw.ss2013.assetloader.infos.GameDataInfo;
import de.fhtrier.gdw.ss2013.constants.PlayerConstants;
import de.fhtrier.gdw.ss2013.game.Entity;
import de.fhtrier.gdw.ss2013.game.EntityCollidable;
import de.fhtrier.gdw.ss2013.game.cheats.Cheats;
import de.fhtrier.gdw.ss2013.game.world.World;
import de.fhtrier.gdw.ss2013.game.world.enemies.AbstractEnemy;
import de.fhtrier.gdw.ss2013.game.world.objects.Switch;
import de.fhtrier.gdw.ss2013.input.AlienController;
import de.fhtrier.gdw.ss2013.input.AstronautController;
import de.fhtrier.gdw.ss2013.physix.InteractionManager;
import de.fhtrier.gdw.ss2013.physix.PhysixShape;

public final class Astronaut extends EntityCollidable implements AstronautController, AlienController {

	private float oxygen;
	private float maxOxygen;
	private boolean carryAlien;
	private Animation bewegungs_ani;
	private float speed;
	private float jumpSpeed;
	private int jumpDelay = 0;
	private int jumpDelayTotal;
	// set of entities, which can currently be activated with the action button
	private InteractionManager interactionManager;

	private Set<Switch> switches = new HashSet<>();

	private PlayerState state;
	private boolean invertAnimation;
	private boolean walking;
	private Alien alien;
	private float pickupDistance;
	private GameDataInfo gameData;
	private Animation animation;
	private int groundContacts;
	private int superjumpDelay = 0;

	public Astronaut () {
        AssetLoader al = AssetLoader.getInstance();
        for(PlayerState s: PlayerState.values()) {
            s.setAnimation(al.getAnimation("player_couple_" + s.toString()));
        }
        
		gameData = AssetLoader.getInstance().getGameData();
	}

	public void setAlien (Alien alien) {
		this.alien = alien;
		carryAlien = true;
		alien.setOnPlayer(true);
	}

	/** {@inheritDoc} */
	@Override
	protected void initialize () {
		super.initialize();
		renderLayer = Integer.MAX_VALUE;
		speed = gameData.combined.speed;
		jumpSpeed = gameData.combined.jumpSpeed;
		jumpDelayTotal = gameData.combined.jumpDelay;
		pickupDistance = gameData.astronaut.pickupDistance;
		maxOxygen = gameData.astronaut.oxygen;
		oxygen = maxOxygen;
		carryAlien = true;
		invertAnimation = false;
		setState(PlayerState.standing);
	}

	public float getOxygen () {
		return oxygen;
	}

	public void setOxygen (float oxygen) {
		this.oxygen = oxygen;
	}

	public float getMaxOxygen () {
		return maxOxygen;
	}

	public void setMaxOxygen (float maxOxygen) {
		this.maxOxygen = maxOxygen;
	}

	public void setJumpSpeed (float newJumpSpeed) {
		jumpSpeed = newJumpSpeed;
	}

	@Override
	public void update (GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if (jumpDelay > 0) jumpDelay -= delta;
		if (oxygen > 0)
			this.oxygen -= (this.maxOxygen * PlayerConstants.OXYGEN_PERCENTAGE_LOST_PER_SECOND) * (delta / 1000f);
		else
			die();
        
		switch (state) {
		case jumping:
			if (getVelocity().y > 0) {
                setState(PlayerState.falling);
            }
			break;
		case superjump:
			if (isGrounded()) {
				setState(PlayerState.superjump_end);
			}
			break;
		case superjump_start:
			if (animation.isStopped()) {
				setState(PlayerState.superjump);
			}
			break;
		case superjump_end:
			if (animation.isStopped()) {
				setState(PlayerState.standing);
			}
			break;
		case falling:
			if (isGrounded()) {
				setState(PlayerState.standing);
			}
			break;
		default:
            if (getVelocity().x == 0 && getVelocity().y == 0 || !(walking)) {
                setState(PlayerState.standing);
            }
			break;

		}
// if (!oldState.equals(state)) Log.debug(state.toString());
// oldState = state;
	}

	public void preInput () {
		walking = false;
	}

	@Override
	public void moveRight () {
		setVelocityX(speed);
		if (!(state.equals(PlayerState.superjump) || state.equals(PlayerState.superjump_start) || state
			.equals(PlayerState.superjump_end)|| state.equals(PlayerState.jumping) || state.equals(PlayerState.falling))) {
			setState(PlayerState.walking);
		}
		invertAnimation = false;
		walking = true;
	}

	@Override
	public void moveLeft () {
		setVelocityX(-speed);
		if (!(state.equals(PlayerState.superjump) || state.equals(PlayerState.superjump_start)
			|| state.equals(PlayerState.superjump_end) || state.equals(PlayerState.jumping) || state.equals(PlayerState.falling))) {
			setState(PlayerState.walking);
		}
		invertAnimation = true;
		walking = true;
	}

	@Override
	public void jump () {
		if (jumpDelay <= 0 && isGrounded()) {
			jumpDelay = 0;
			setVelocityY(-jumpSpeed);
			physicsObject.applyImpulse(new Vector2f(0, -jumpSpeed));
			setState(PlayerState.jumping);
			jumpDelay = jumpDelayTotal;
		}
	}

	public void superjump () {
		if (superjumpDelay <= 0 && isGrounded() && isCarryAlien()) {
			jumpDelay = 0;
			setVelocityY(-jumpSpeed * 2);
			physicsObject.applyImpulse(new Vector2f(0, -jumpSpeed * 2));
			setState(PlayerState.superjump_start);
			jumpDelay = jumpDelayTotal;
		}
	}

	@Override
	public void action () {
		for (Switch s : switches) {
			s.turnSwitch();
		}
	}

	public boolean isCarryAlien () {
		return carryAlien;
	}

	public float getJumpSpeed () {
		return jumpSpeed;
	}

	public PlayerState getState () {
		return state;
	}

	public float getSpeed () {
		return speed;
	}

	public void setSpeed (float newSpeed) {
		speed = newSpeed;
	}

	@Override
	public void render (GameContainer container, Graphics g) throws SlickException {
		Vector2f position = getPosition();

		if (invertAnimation) {
			animation.draw(position.x + animation.getWidth() / 2, position.y - animation.getHeight() / 2, -animation.getWidth(),
				animation.getHeight());
		} else {
			animation.draw(position.x - animation.getWidth() / 2, position.y - animation.getHeight() / 2);
		}

// Log.debug(animation.getFrame()+1 +"/" + animation.getFrameCount());
// if (!oldState.equals(state)) Log.debug(String.valueOf(state));
// oldState = state;
	}

	public void setState (PlayerState state) {
		if (this.state == null || !this.state.equals(state)) {
			this.state = state;
			updateStateAnimation();
		}
	}

	private void updateStateAnimation () {
		if (isCarryAlien()) {
			setAnimation(state.getAnimation());
		} else {
			setAnimation(state.getAnimation());
		}
	}

	// overriding because the InteractionManager has to be added to the
	// PhysixObject
	// and the Astronaut needs to know its InteractionManager, so it is done
	// here
	@Override
	public void setPhysicsObject (PhysixShape physicsObject) {
		interactionManager = new InteractionManager();
		physicsObject.addCollisionListener(interactionManager);
		super.setPhysicsObject(physicsObject);
	}

	public Animation getAnimation () {
		return animation;
	}

	public void die () {
		if (!Cheats.isGodmode) World.getInstance().shallBeReseted(true);
	}

	public void setAnimation (Animation animation) {
        if(this.animation != animation) {
            this.animation = animation;
            animation.restart();
        }
	}

	public boolean isGrounded () {
		return groundContacts > 0;
	}

	@Override
	public void beginContact (Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		PhysixShape objectA = (PhysixShape)a.getBody().getUserData();
		PhysixShape objectB = (PhysixShape)b.getBody().getUserData();

		if (physicsObject == objectA && a.m_shape.getType() == ShapeType.CIRCLE && !b.m_isSensor) {
			groundContacts++;
		} else if (physicsObject == objectB && b.m_shape.getType() == ShapeType.CIRCLE && !a.m_isSensor) {
			groundContacts++;
		}

        Entity other = getOtherEntity(contact);
		if (other instanceof AbstractEnemy) {
			AbstractEnemy damageDealer = (AbstractEnemy)objectA.getOwner();
            
			Vector2f damageTakerPos = getPosition();
			Vector2f damageTakerDim = getPhysicsObject().getDimension();

			Vector2f damageDealerPos = damageDealer.getPosition();
			Vector2f damageDealerDim = damageDealer.getPhysicsObject().getDimension();

			if ((damageTakerPos.x + damageTakerDim.x > damageDealerPos.x - damageDealerDim.x) //
				&& ((damageTakerPos.x - damageTakerDim.x < damageDealerPos.x + damageDealerDim.x))
				&& (damageTakerPos.y + damageTakerDim.y < damageDealerPos.y)) { // player deals damage
				World.getScoreCounter().addScore(5);
				World.getInstance().getEntityManager().removeEntity(damageDealer);
				setVelocityY(-.50f * getJumpSpeed());
			} else {
				// Wird in Bullet-Klassen geregelt
// if (damageTaker instanceof Astronaut && !(damageDealer instanceof PlayerBullet))
// ((Astronaut) damageTaker).setOxygen(0);

			}
		}

		if (other instanceof Switch) {
			switches.add((Switch)other);
		}
	}

	@Override
	public void endContact (Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		PhysixShape objectA = (PhysixShape)a.getBody().getUserData();
		PhysixShape objectB = (PhysixShape)b.getBody().getUserData();
		if (physicsObject == objectA && a.m_shape.getType() == ShapeType.CIRCLE && !b.m_isSensor) {
			groundContacts--;
		} else if (physicsObject == objectB && b.m_shape.getType() == ShapeType.CIRCLE && !a.m_isSensor) {
			groundContacts--;
		}
		assert (groundContacts >= 0);

        Entity other = getOtherEntity(contact);
		if (other instanceof Switch) {
			switches.remove((Switch)other);
		}
	}

	@Override
	public void shoot () {
		assert (alien != null);
		if (carryAlien) {
			alien.shoot();
			// FIXME: if carry do other shoot
		} else {
			alien.shoot();
		}
	}

	@Override
	public void nextAbility () {
		assert (alien != null);
		alien.nextAbility();
	}

	@Override
	public void previousAbility () {
		assert (alien != null);
		alien.previousAbility();
	}

	@Override
	public void useAbility () {
		assert (alien != null);
		if (carryAlien) {
			alien.useAbility();
		} else {
			alien.useAbility();
		}
	}

	@Override
	public void setCursor (int x, int y) {
		assert (alien != null);
		alien.setCursor(x, y);
	}

	@Override
	public void cursorRight (float scale) {
		assert (alien != null);
		alien.cursorRight(scale);
	}

	@Override
	public void cursorLeft (float scale) {
		assert (alien != null);
		alien.cursorLeft(scale);
	}

	@Override
	public void cursorUp (float scale) {
		assert (alien != null);
		alien.cursorUp(scale);
	}

	@Override
	public void cursorDown (float scale) {
		assert (alien != null);
		alien.cursorDown(scale);
	}

	@Override
	public void toggleAlien () {
		if (carryAlien) {
			carryAlien = false;
			alien.setOnPlayer(false);

			speed = gameData.astronaut.speed;
			jumpSpeed = gameData.astronaut.jumpSpeed;
			jumpDelayTotal = gameData.astronaut.jumpDelay;
			updateStateAnimation();
		} else if (getPosition().distance(alien.getPosition()) <= pickupDistance) {
			carryAlien = true;
			alien.setOnPlayer(true);
			updateStateAnimation();

			speed = gameData.combined.speed;
			jumpSpeed = gameData.combined.jumpSpeed;
			jumpDelayTotal = gameData.combined.jumpDelay;
		}
	}



    @Override
    public void initPhysics() {
        GameDataInfo info = AssetLoader.getInstance().getGameData();
        createPhysics(BodyType.DYNAMIC, origin.x, origin.y)
                .density(info.combined.density).friction(info.combined.friction)
                .asPlayer(info.combined.width, info.combined.height);
    }
}
