package de.fhtrier.gdw.ss2013.physics.test;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import de.fhtrier.gdw.commons.tiled.TiledMap;
import de.fhtrier.gdw.ss2013.assetloader.AssetLoader;
import de.fhtrier.gdw.ss2013.game.EntityManager;
import de.fhtrier.gdw.ss2013.game.camera.Camera;
import de.fhtrier.gdw.ss2013.game.player.Alien;
import de.fhtrier.gdw.ss2013.game.player.Astronaut;
import de.fhtrier.gdw.ss2013.game.world.LevelLoader;
import de.fhtrier.gdw.ss2013.game.world.enemies.FlyingEnemy;
import de.fhtrier.gdw.ss2013.game.world.enemies.Meteroid;
import de.fhtrier.gdw.ss2013.game.world.objects.OxygenFlower;
import de.fhtrier.gdw.ss2013.input.InputManager;
import de.fhtrier.gdw.ss2013.physics.DebugDrawer;
import de.fhtrier.gdw.ss2013.physics.PhysicsManager;
import de.fhtrier.gdw.ss2013.physics.PhysicsTools;
import de.fhtrier.gdw.ss2013.physics.RectanglePhysicsObject;
import de.fhtrier.gdw.ss2013.renderer.MapRenderer;
import de.fhtrier.gdw.ss2013.sound.SoundLocator;
import de.fhtrier.gdw.ss2013.sound.services.DefaultSoundPlayer;

public class TestWorld {

    private TiledMap map;
    private MapRenderer mapRender;
    private Camera camera;
    private Astronaut astronaut;
    private Input input;

    // physics debug
    private DebugDrawer physicDebug;
    public boolean debugDraw = true;

    private EntityManager entityManager;

    public TestWorld(GameContainer container, StateBasedGame game) {
        input = container.getInput();
        map = null;
        entityManager = new EntityManager();

        try {
            map = AssetLoader.getInstance().loadMap("demo");
            LevelLoader.load(map, entityManager);

            mapRender = new MapRenderer(map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        camera = new Camera(map);

        entityManager = new EntityManager();

        // physic debug stuff
        if (debugDraw) {
            physicDebug = new DebugDrawer(container, camera);
            PhysicsManager.getInstance().getPhysicsWorld()
                    .setDebugDraw(physicDebug);
        }

        astronaut = entityManager.createEntityAt(Astronaut.class, new Vector2f(
                200, 200));
        InputManager.getInstance().getKeyboard()
                .setAstronautController(astronaut);

        SoundLocator.provide(new DefaultSoundPlayer(astronaut));

    }

    public void render(GameContainer container, Graphics g)
            throws SlickException {
        Vector2f astronautPos = astronaut.getPosition();
        camera.update(container.getWidth(), container.getHeight(),
                astronautPos.x, astronautPos.y);

        // Background image TODO: translate
        g.drawImage(AssetLoader.getInstance().getImage("world_background"), 0,
                0);

        mapRender
                .renderTileLayers(g, -camera.getTileOverlapX(),
                        -camera.getTileOverlapY(), camera.getTileX(),
                        camera.getTileY(), camera.getNumTilesX(),
                        camera.getNumTilesY());

        g.pushTransform();
        g.translate(-camera.getOffsetX(), -camera.getOffsetY());

        // draw entities
        entityManager.render(container, g);

        if (debugDraw)
            PhysicsManager.getInstance().getPhysicsWorld().drawDebugData();

        g.popTransform();
    }

    public void update(GameContainer container, int delta)
            throws SlickException {
        // update entities
        entityManager.update(container, delta);
        PhysicsManager.getInstance().update(container, delta);
        // This is just a placeholder, not for actual use.
        Vector2f astronautPos = astronaut.getPosition();
        float speed = 6;
        if (input.isKeyDown(Input.KEY_UP)) {
            astronautPos.y -= speed;
        }
        if (input.isKeyDown(Input.KEY_DOWN)) {
            astronautPos.y += speed;
        }
        if (input.isKeyDown(Input.KEY_LEFT)) {
            astronautPos.x -= speed;
        }
        if (input.isKeyDown(Input.KEY_RIGHT)) {
            astronautPos.x += speed;
        }

        if (input.isKeyPressed(Input.KEY_SPACE)) {

            RectanglePhysicsObject rpo = new RectanglePhysicsObject(
                    BodyType.DYNAMIC, new Vec2(30, 30), new Vec2(500, 300));
            rpo.setMassData(100);
            rpo.setLinearVelocity(new Vec2(
                    (float) (100 + Math.random() * 1000 - 500),
                    (float) (100 + Math.random() * 500)));
            System.out.println("spawn rec");
        }
        
    }

    public Vector2f screenToWorldPosition(Vector2f screenPosition) {
        /**
         * Top-left (0,0) / Bottom-right (width,height)
         */
        Vector2f worldPos = new Vector2f(camera.getOffsetX(),
                camera.getOffsetY());

        return worldPos.add(screenPosition);

    }

    public Vector2f worldToScreenPosition(Vector2f worldPosition) {
        Vector2f screenPos = new Vector2f(-camera.getOffsetX(),
                -camera.getOffsetY());

        return screenPos.add(worldPosition);
    }

    public Astronaut getAstronaut() {
        return astronaut;
    }

    public Camera getCamera() {
        return camera;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

}
