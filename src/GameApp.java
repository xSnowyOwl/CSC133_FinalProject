import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

import java.net.http.HttpResponse;
import java.security.Key;
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

class Globals{
    final static double APP_WIDTH = 800;
    final static double APP_HEIGHT = 800;
    final static double ONE_THIRD_APP_HEIGHT = APP_HEIGHT / 3;
}

class Helicopter extends GameObject{
    private final double chopperBodyWidth = 17;
    private final double chopperBodyHeight = 30;
    private final double chopperTailWidth = 5;
    private final double chopperTailHeight = 28;
    private double accelerationLevel = 0;
    private static double speed;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean isReady;
    private int chopperFuel;
    Bounds chopperBounds;
    GameText heliText = new GameText(-11, -35, Color.LEMONCHIFFON);
    HelicopterBlade blade = new HelicopterBlade();

    public Helicopter(){
        speed = 0;
        isReady = false;
        chopperFuel = 25000;
        heliText.setText("F: " + chopperFuel);
        chopperBounds = new BoundingBox(
                myTranslation.getX(), myTranslation.getY(), chopperBodyWidth,
                chopperBodyHeight
        );

        Circle chopperBody = new Circle();
        chopperBody.setRadius(chopperBodyWidth);
        chopperBody.setFill(Color.LEMONCHIFFON);
        chopperBody.setTranslateX(10);
        chopperBody.setTranslateY(15);

        Rectangle chopperButt = new Rectangle();
        chopperButt.setHeight(8);
        chopperButt.setWidth(28);
        chopperButt.setFill(Color.LEMONCHIFFON);
        chopperButt.setTranslateX(-4);
        chopperButt.setTranslateY(-2);

        Rectangle chopperTail = new Rectangle();
        chopperTail.setWidth(chopperTailWidth);
        chopperTail.setHeight(chopperTailHeight);
        chopperTail.setFill(Color.LEMONCHIFFON);
        chopperTail.setTranslateX(8);
        chopperTail.setTranslateY(-29);

        Rectangle frontConnector = new Rectangle();
        frontConnector.setWidth(45);
        frontConnector.setHeight(2);
        frontConnector.setFill(Color.DARKGREY);
        frontConnector.setTranslateX(-12);
        frontConnector.setTranslateY(20);

        Rectangle backConnector = new Rectangle();
        backConnector.setWidth(45);
        backConnector.setHeight(2);
        backConnector.setFill(Color.DARKGREY);
        backConnector.setTranslateX(-12);

        Rectangle chopperLeftLeg = new Rectangle();
        chopperLeftLeg.setHeight(40);
        chopperLeftLeg.setWidth(4);
        chopperLeftLeg.setFill(Color.DARKSLATEGREY);
        chopperLeftLeg.setTranslateX(-16);
        chopperLeftLeg.setTranslateY(-10);

        Rectangle chopperRightLeg = new Rectangle();
        chopperRightLeg.setHeight(40);
        chopperRightLeg.setWidth(4);
        chopperRightLeg.setFill(Color.DARKSLATEGREY);
        chopperRightLeg.setTranslateX(32);
        chopperRightLeg.setTranslateY(-10);

        Rectangle tailRotor = new Rectangle();
        tailRotor.setWidth(10);
        tailRotor.setHeight(2);
        tailRotor.setFill(Color.WHITE);
        tailRotor.setTranslateX(6);
        tailRotor.setTranslateY(-28);

        Rectangle tailBlade = new Rectangle();
        tailBlade.setWidth(2);
        tailBlade.setHeight(8);
        tailBlade.setFill(Color.BLACK);
        tailBlade.setTranslateX(16);
        tailBlade.setTranslateY(-31);

        Circle bodyRotor = new Circle();
        bodyRotor.setRadius(2);
        bodyRotor.setFill(Color.WHITE);
        bodyRotor.setTranslateX(10);
        bodyRotor.setTranslateY(7);

        translate(Globals.APP_WIDTH / 2 - chopperBodyWidth + 7,
                108);

        blade.translate(chopperBodyWidth / 2 - 2,
                chopperBodyHeight - 72);
        add(frontConnector);
        add(backConnector);
        add(chopperBody);
        add(chopperButt);
        add(tailRotor);
        add(tailBlade);
        add(chopperTail);
        add(chopperLeftLeg);
        add(chopperRightLeg);
        add(blade);
        add(bodyRotor);
        add(heliText);
    }
    public double getChopperBodyWidth(){
        return chopperBodyWidth;
    }
    public double getChopperBodyHeight(){
        return chopperBodyHeight;
    }
    public void accelerate(){
        if(isReady){
            if(speed < 10){
                accelerationLevel++;
                speed = 0.1 * accelerationLevel;
            }
        }
    }
    public void decelerate(){
        if(isReady){
            if(speed > -2){
                accelerationLevel--;
                speed = 0.1 * accelerationLevel;
            }
        }
    }
    public void fuelDepletion(){
        if(isReady){
            if(chopperFuel >= 0){
                chopperFuel -= 5 * Math.abs((.05 * accelerationLevel + 1));
            }
            else{
                chopperFuel *= 0;
            }
        }
        heliText.setText("F: " + chopperFuel);
    }
    public void ignition(){
        isReady = !isReady;
        System.out.println("Ignition Status: " + isReady);
    }
    public double getSpeed(){
        return speed;
    }
    public void setPivot(){
        this.myRotation.setPivotX(chopperBodyWidth / 2);
        this.myRotation.setPivotY(chopperBodyHeight - 5);
    }
    public void clockwiseTurn(){
        if(speed > 0){
            rotate(getMyRotation() - 15);
        }
    }
    public void counterClockwiseTurn(){
        if(speed > 0){
            rotate(getMyRotation() + 15);
        }
    }
    public double getVelocityX(){
        velocityX = speed * -sin(Math.toRadians(getMyRotation()));
        return velocityX;
    }
    public double getVelocityY(){
        velocityY = speed * cos(Math.toRadians(getMyRotation()));
        return velocityY;
    }
    public void moveHeli(){
        translate(myTranslation.getX() + getVelocityX(),
                myTranslation.getY() + getVelocityY());
    }
    public void update(){
        moveHeli();
        setPivot();
        if(isReady){
            blade.rotateBlade();
        }
        blade.setPivot();
        fuelDepletion();
    }
}
class HelicopterBlade extends GameObject{
    public HelicopterBlade(){
        Rectangle heliBlade = new Rectangle();
        heliBlade.setWidth(5);
        heliBlade.setHeight(100);
        heliBlade.setFill(Color.BLACK);
        heliBlade.setStroke(Color.WHITE);
        heliBlade.setRotate(45);
        add(heliBlade);
    }
    public void rotateBlade(){
        rotate(getMyRotation() + 20);
    }
    public void setPivot(){
        this.myRotation.setPivotX(3);
        this.myRotation.setPivotY(49);
    }
}

class Helipad extends Pane{
    private static final int helipadWidth = 100;
    private static final int helipadHeight = 100;
    private static final int helipadRadius = 45;
    public Helipad(){
        Rectangle helipadRect = new Rectangle(helipadWidth, helipadHeight);
        helipadRect.setFill(Color.SANDYBROWN);
        helipadRect.setStroke(Color.BLACK);
        helipadRect.setStrokeWidth(2);
        helipadRect.setTranslateX(Globals.APP_WIDTH / 2 - helipadWidth / 2);
        helipadRect.setTranslateY(50);

        Ellipse helipadCircle = new Ellipse(helipadRadius, helipadRadius);
        helipadCircle.setFill(Color.SADDLEBROWN);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setStrokeWidth(2);
        helipadCircle.setTranslateX(Globals.APP_WIDTH / 2);
        helipadCircle.setTranslateY(100);

        this.getChildren().addAll(helipadRect, helipadCircle);
    }
}

class Cloud extends GameObject{
    Random random = new Random();
    Circle cloud;
    Bounds cloudBounds;
    GameText cloudText = new GameText(-14, 8, Color.BLACK);
    private static final int cloudRadius = 75;
    private double cloudVelocity;
    private int cloudSeed = 0;
    public Cloud() {
        cloudVelocity = randomNumberGenerator(0.1, 1);
        cloud = new Circle();
        cloud.setFill(Color.WHITE);
        cloud.setRadius(cloudRadius);
        translate(randomNumberGenerator(0 + cloudRadius,
                Globals.APP_WIDTH - cloudRadius), randomNumberGenerator(
                        Globals.ONE_THIRD_APP_HEIGHT + cloudRadius
                ,Globals.APP_HEIGHT - cloudRadius));
        cloudText.setTranslateX(cloud.getTranslateX());
        cloudText.setTranslateY(cloud.getTranslateY());
        cloudText.setText(cloudSeed + "%");
        cloudBounds = new BoundingBox(this.getTranslateX(),
                this.getTranslateY(), cloudRadius * 2, cloudRadius * 2);
        getChildren().addAll(cloud, cloudText);
    }
    public void moveCloud(){
        translate(this.myTranslation.getX() + cloudVelocity,
                this.myTranslation.getY());
    }
    public int getRadius(){
        return cloudRadius;
    }
    public int getCloudSeed(){
        return cloudSeed;
    }
    public void seedingCloud(){
        if(this.cloudSeed < 100){
            this.cloudSeed++;
            this.cloudText.setText(cloudSeed + "%");
            System.out.println("Cloud is being seeded.");
        }
    }
    public void saturateCloud(){
        cloud.setFill(Color.color(1 - (cloudSeed * .0045),
                1 - (cloudSeed * .0045), 1 - (cloudSeed * .0045)));
    }
    public double randomNumberGenerator(double min, double max){
        return min + ((max - min) + 1) * random.nextDouble();
    }
    public void update(){
        //this.moveCloud();
        this.saturateCloud();
    }
}

class Pond extends Pane{
    Random random = new Random();
    GameText pondText;
    Cloud cloud;
    private static double seedTime = 0;
    private static final double pondRadius = 30;
    private int pondSeed = 0;
    public Pond() {
        Circle pond = new Circle();
        pond.setFill(Color.DEEPSKYBLUE);
        pond.setRadius(pondRadius);
        pond.setTranslateX(
                randomNumberGenerator(0 + pondRadius,
                                        Globals.APP_WIDTH - pondRadius));
        pond.setTranslateY(
                randomNumberGenerator(Globals.ONE_THIRD_APP_HEIGHT - pondRadius,
                                      Globals.APP_HEIGHT - pondRadius));
        pondText = new GameText(
                pond.getTranslateX() - 12, pond.getTranslateY() + 7,
                Color.BLACK
        );
        pondText.setText(pondSeed + "%");
        getChildren().addAll(pond, pondText);
    }
    public void seedPond(){
        seedTime++;
        if(seedTime % 60 == 0 ){
            pondSeed++;
        }
        pondText.setText(pondSeed + "%");
    }
    public double getSeedTime(){
        return seedTime;
    }
    public int getPondSeed(){
        return pondSeed;
    }
    public double randomNumberGenerator(double min, double max){
        return min + ((max - min) + 1) * random.nextDouble();
    }
    public void update(){
        seedPond();
    }
}

class GameText extends GameObject{
    Text text = new Text();
    public GameText(double tx, double ty, Color color){
        text.setFont(Font.font(12));
        text.setFill(color);
        text.setScaleY(-1);
        translate(tx, ty);
        add(text);
    }
    public void setText(String setString){
        text.setText(setString);
    }
}

class Game extends Pane{
    Helicopter choppah = new Helicopter();
    Cloud cloud = new Cloud();
    Pond pond = new Pond();

    AnimationTimer game = new AnimationTimer() {
        double oldFrame = -1;
        double elapsedTime = 0;
        @Override
        public void handle(long currentFrame) {
            if(oldFrame < 0) oldFrame = currentFrame;
            double frameTime = (currentFrame - oldFrame) / 1e9;
            oldFrame = currentFrame;
            elapsedTime += frameTime;

            choppah.update();
            cloud.update();
            if(cloud.getCloudSeed() >= 30){
                pond.update();
            }
        }
    };
    public Game(){
        super.setScaleY(-1);
    }
    public void init(){
        this.getChildren().clear();
        this.getChildren().addAll(new Helipad(),
                pond = new Pond(),
                cloud = new Cloud(),
                choppah = new Helicopter());
    }
    public boolean isHelicopterColliding(Helicopter helicopter, Cloud cloud){
        if(this.cloud.myTranslation.getX() - cloud.getRadius() <
                helicopter.myTranslation.getX() &&
                this.cloud.myTranslation.getY() - cloud.getRadius() <
                        helicopter.myTranslation.getY() &&
                this.cloud.myTranslation.getX() + cloud.getRadius() >
                        helicopter.myTranslation.getX() +
                                helicopter.getChopperBodyWidth() &&
                this.cloud.myTranslation.getY() + cloud.getRadius() >
                        helicopter.myTranslation.getY() +
                                helicopter.getChopperBodyHeight()
        ){
            return true;
        }
        else {
            return false;
        }
    }
    public void seedCloud(){
        if(isHelicopterColliding(choppah, cloud)){
            cloud.seedingCloud();
            System.out.println("Helicopter is in cloud.");
        }
    }
}

public class GameApp extends Application {
    Game rainmaker = new Game();
    @Override
    public void start(Stage primaryStage) throws Exception {
        rainmaker.init();
        Scene scene = new Scene(rainmaker, Globals.APP_WIDTH,
                Globals.APP_HEIGHT, Color.SANDYBROWN);
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
                if(event.getCode() == KeyCode.R){
                    rainmaker.init();
                }
                if(event.getCode() == KeyCode.SPACE){
                    rainmaker.seedCloud();
                }
                if(event.getCode() == KeyCode.M){
                    System.out.println("Pond Seed Time: " + rainmaker.pond.getSeedTime());
                    System.out.println("Current Pond Seed: " + rainmaker.pond.getPondSeed());
                }
            }
        });
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
