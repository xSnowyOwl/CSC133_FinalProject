import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

interface Updatable{
    void update();
}

abstract class GameObject extends Group implements Updatable{
    protected Translate myTranslation;
    protected Rotate myRotation;
    protected Scale myScale;

    public GameObject(){
        myTranslation = new Translate();
        myRotation = new Rotate();
        myScale = new Scale();
        this.getTransforms().addAll(myTranslation, myRotation, myScale);
    }

    public void rotate(double degrees) {
        myRotation.setAngle(degrees);
        myRotation.setPivotX(0);
        myRotation.setPivotY(0);
    }

    public void scale(double scaleX, double scaleY) {
        myScale.setX(scaleX);
        myScale.setY(scaleY);
    }

    public void translate(double translateX, double translateY) {
        myTranslation.setX(translateX);
        myTranslation.setY(translateY);
    }

    public double getMyRotation(){
        return myRotation.getAngle();
    }

    public void update(){
        for(Node n : getChildren()) {
            if (n instanceof Updatable)
                ((Updatable) n).update();
        }
    }

    void add(Node node) {
        this.getChildren().add(node);
    }
}

class HelicopterBody extends GameObject{
    private static final double chopperBodyWidth = 30;
    private static final double chopperBodyHeight = 50;
    public HelicopterBody(){
        super();
        Rectangle chopperBody = new Rectangle();
        chopperBody.setWidth(chopperBodyWidth);
        chopperBody.setHeight(chopperBodyHeight);
        chopperBody.setFill(Color.LEMONCHIFFON);
        add(chopperBody);
    }
}

class HelicopterTail extends GameObject{
    private static final double chopperTailWidth = 10;
    private static final double chopperTailHeight = 60;
    public HelicopterTail(){
        Rectangle chopperTail = new Rectangle();
        chopperTail.setWidth(chopperTailWidth);
        chopperTail.setHeight(chopperTailHeight);
        chopperTail.setFill(Color.LEMONCHIFFON);
        add(chopperTail);
    }
}

class HelicopterBlade extends GameObject{
    private static final double chopperBladeWidth = 2;
    private static final double chopperBladeLength = 60;
    public HelicopterBlade(){
        Rectangle chopperBlade = new Rectangle(chopperBladeWidth,
                                                chopperBladeLength);
        chopperBlade.setFill(Color.BLACK);
        add(chopperBlade);
    }
}

class HelicopterPropeller extends HelicopterBlade{
    public HelicopterBlade makeBlade(double tx, double ty, double sy,
                                     int degrees){
        HelicopterBlade blade = new HelicopterBlade();
        blade.rotate(degrees);
        blade.scale(1, sy);
        blade.translate(tx, ty);
        return blade;
    }
}

class BigPropeller extends HelicopterPropeller{
    public BigPropeller(){
        Ellipse spin = new Ellipse(60, 60);
        spin.setFill(Color.BLACK);
        spin.setOpacity(.25);
        spin.setTranslateX(15);
        spin.setTranslateY(25);
        add(spin);
        add(makeBlade(14, 25, 1, 45));
        add(makeBlade(14, 25, 1, 135));
        add(makeBlade(14, 25, 1, 225));
        add(makeBlade(14, 25, 1, 315));
    }
    public void update(){
        this.rotate(this.getMyRotation() + 3);
    }
    public void setPivot(){
        this.myRotation.setPivotX(this.myTranslation.getX() + 15);
        this.myRotation.setPivotY(this.myTranslation.getY() + 25);
    }
}

class SmallPropeller extends HelicopterPropeller{
    public SmallPropeller(){
        Ellipse smallSpin = new Ellipse(15, 15);
        smallSpin.setFill(Color.BLACK);
        smallSpin.setOpacity(.25);
        smallSpin.setTranslateX(15);
        smallSpin.setTranslateY(-60);
        add(smallSpin);
        add(makeBlade(14, -60, .25, 0));
        add(makeBlade(14, -60, .25, 120));
        add(makeBlade(14, -60, .25, 240));
    }
    public void update(){
        this.rotate(this.getMyRotation() - 5);
    }
    public void setPivot(){
        this.myRotation.setPivotX(this.myTranslation.getX() + 15);
        this.myRotation.setPivotY(this.myTranslation.getY() - 60);
    }
}

class Helicopter extends GameObject{
    //BigPropeller bigPropeller = new BigPropeller();
    //SmallPropeller smallPropeller = new SmallPropeller();
    private double accelerationLevel = 0;
    private double speed = 0;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean ignition = false;


    public Helicopter(){
        HelicopterBody body = new HelicopterBody();
        add(makeTail(10, -59, 0));
        add(body);
        //add(bigPropeller);
        //add(smallPropeller);
    }
    private HelicopterTail makeTail(double tx, double ty, int degrees){
        HelicopterTail tail = new HelicopterTail();
        tail.rotate(degrees);
        tail.translate(tx, ty);
        return tail;
    }

    public void accelerate(){
        if(ignition){
            if(speed < 2){
                accelerationLevel++;
                speed = 0.2 * accelerationLevel;
            }
        }
    }
    public void decelerate(){
        if(ignition){
            if(speed > -2){
                accelerationLevel--;
                speed = 0.2 * accelerationLevel;
            }
        }
    }
    public void ignition(){
        ignition = !ignition;
        System.out.println("Ignition Status: " + ignition);
    }
    public double getSpeed(){
        return speed;
    }
    public void setPivot(){
        this.myRotation.setPivotX(this.myTranslation.getX() + 15);
        this.myRotation.setPivotY(this.myTranslation.getY() + 25);
    }
    public void clockwiseTurn(){
        if(speed > 0){
            this.rotate(this.getMyRotation() - 3);
        }

    }
    public void counterClockwiseTurn(){
        if(speed > 0){
            this.rotate(this.getMyRotation() + 3);
        }
    }
    public double getVelocityX(){
        velocityX = speed * cos(Math.toRadians(90 + getMyRotation()));
        return velocityX;
    }
    public double getVelocityY(){
        velocityY = speed * sin(Math.toRadians(90 - getMyRotation()));
        return velocityY;
    }

    public void update(double velocityX, double velocityY){
        this.setTranslateX(this.getTranslateX() + velocityX);
        this.setTranslateY(this.getTranslateY() + velocityY);
        this.setPivot();
/*        bigPropeller.update();
        bigPropeller.setPivot();
        smallPropeller.update();
        smallPropeller.setPivot();*/
    }
}

class Helipad extends Pane{
    private static final int helipadWidth = 200;
    private static final int helipadHeight = 200;
    private static final int helipadRadius = 80;
    private static final int helipadStartX = -85;
    private static final int helipadStartY = -90;
    public Helipad(){
        Rectangle helipadRect = new Rectangle(helipadWidth, helipadHeight);
        helipadRect.setFill(Color.SANDYBROWN);
        helipadRect.setStroke(Color.BLACK);
        helipadRect.setStrokeWidth(2);
        helipadRect.setTranslateX(helipadStartX);
        helipadRect.setTranslateY(helipadStartY);

        Ellipse helipadCircle = new Ellipse(helipadRadius, helipadRadius);
        helipadCircle.setFill(Color.SADDLEBROWN);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setStrokeWidth(2);
        helipadCircle.setTranslateX(helipadRect.getTranslateX() + 100);
        helipadCircle.setTranslateY(helipadRect.getTranslateY()
                                    + helipadRadius + 20);

        this.getChildren().addAll(helipadRect, helipadCircle);
    }
}

class Cloud extends Pane{
    Random random = new Random();
    double cloudRadius = 50;
    public Cloud(double upperMap, double lowerMap, double leftMap,
                 double rightMap){
        Circle cloud = new Circle();
        cloud.setFill(Color.WHITE);
        cloud.setRadius(cloudRadius);
        cloud.setTranslateY(
                random.nextDouble((upperMap - lowerMap - 2 * cloudRadius))
                                                + (lowerMap + cloudRadius));
        cloud.setTranslateX(
                random.nextDouble((rightMap - cloudRadius))
                                                + (leftMap + cloudRadius));
        getChildren().add(cloud);
    }
}

class Pond extends Pane{
    Random random = new Random();
    double pondRadius = 30;
    public Pond(double upperMap, double lowerMap, double leftMap,
                double rightMap){
        Circle pond = new Circle();
        pond.setFill(Color.BLUE);
        pond.setRadius(pondRadius);
        pond.setTranslateY(
                random.nextDouble((upperMap - lowerMap - 2 * pondRadius))
                                            + (lowerMap + pondRadius));
        pond.setTranslateX(
                random.nextDouble((rightMap - pondRadius))
                                            + (leftMap + pondRadius));
        getChildren().add(pond);
    }
}


class Game extends Pane{
    private final static double APP_WIDTH = 900;
    private final static double APP_HEIGHT = 1000;
    Helicopter choppah = new Helicopter();
    Helipad helipad = new Helipad();
    Cloud cloud = new Cloud((APP_HEIGHT - 100),
            (helipad.getTranslateY() + 200),
            -438,
            APP_WIDTH - 438);
    Pond pond = new Pond((APP_HEIGHT - 100),
            (helipad.getTranslateY() + 200),
            -438,
            APP_WIDTH - 438);

    AnimationTimer game = new AnimationTimer() {
        double oldFrame = -1;
        double elapsedTime = 0;
        int frameCount = 0;
        @Override
        public void handle(long currentFrame) {
            if(oldFrame < 0) oldFrame = currentFrame;
            double frameTime = (currentFrame - oldFrame) / 1e9;
            oldFrame = currentFrame;
            elapsedTime += frameTime;

            choppah.update(choppah.getVelocityX(), choppah.getVelocityY());
        }
    };

    public Game(){
        this.getChildren().addAll(helipad, cloud, pond, choppah);
    }
}


public class GameApp extends Application {
    Pane root = new Pane();
    Point2D rainmakerApp = new Point2D(900, 1000);
    Game rainmaker = new Game();
    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, rainmakerApp.getX(),
                                rainmakerApp.getY(), Color.SANDYBROWN);
        root.getChildren().add(rainmaker);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.UP) {
                    rainmaker.choppah.accelerate();
                    System.out.println("Speed: " + rainmaker.choppah.getSpeed());
                }
                if(event.getCode() == KeyCode.DOWN){
                    rainmaker.choppah.decelerate();
                    System.out.println("Speed: " + rainmaker.choppah.getSpeed());
                }
                if(event.getCode() == KeyCode.LEFT){
                    rainmaker.choppah.counterClockwiseTurn();
                }
                if(event.getCode() == KeyCode.RIGHT){
                    rainmaker.choppah.clockwiseTurn();
                }
                if(event.getCode() == KeyCode.I){
                    if(rainmaker.choppah.getSpeed() == 0){
                        rainmaker.choppah.ignition();
                    }
                }
            }

        });

        root.setScaleY(-1);
        root.setTranslateX(438);
        root.setTranslateY(-100);
        rainmaker.game.start();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Rainmaker");
        primaryStage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }

}
