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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

class Globals{
    final static double APP_WIDTH = 400;
    final static double APP_HEIGHT = 800;
    final static double ONE_THIRD_APP_HEIGHT = APP_HEIGHT / 3;
}

class Helicopter extends GameObject{
    private final double chopperBodyWidth = 20;
    private final double chopperBodyHeight = 30;
    private final double chopperTailWidth = 6;
    private final double chopperTailHeight = 35;
    private double accelerationLevel = 0;
    private static double speed;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean ignition;
    private int chopperFuel;
    GameText heliText = new GameText(-11, -35, Color.LEMONCHIFFON);
    Cloud cloud = new Cloud();

    public Helicopter(){
        speed = 0;
        ignition = false;
        chopperFuel = 25000;
        heliText.setText("F: " + chopperFuel);

        Rectangle chopperBody = new Rectangle();
        chopperBody.setWidth(chopperBodyWidth);
        chopperBody.setHeight(chopperBodyHeight);
        chopperBody.setFill(Color.LEMONCHIFFON);

        Rectangle chopperTail = new Rectangle();
        chopperTail.setWidth(chopperTailWidth);
        chopperTail.setHeight(chopperTailHeight);
        chopperTail.setFill(Color.LEMONCHIFFON);
        chopperTail.setTranslateX(7);
        chopperTail.setTranslateY(-34);

        translate(190, 110);

        add(chopperBody);
        add(chopperTail);
        add(heliText);
    }
    public void accelerate(){
        if(ignition){
            if(speed < 10){
                accelerationLevel++;
                speed = 0.1 * accelerationLevel;
            }
        }
    }
    public void decelerate(){
        if(ignition){
            if(speed > -2){
                accelerationLevel--;
                speed = 0.1 * accelerationLevel;
            }
        }
    }
    public void fuelDepletion(){
        if(ignition){
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
        ignition = !ignition;
        System.out.println("Ignition Status: " + ignition);
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
            this.rotate(this.getMyRotation() - 15);
        }
    }
    public void counterClockwiseTurn(){
        if(speed > 0){
            this.rotate(this.getMyRotation() + 15);
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
    public void update(){
        this.translate(this.myTranslation.getX() + getVelocityX(),
                        this.myTranslation.getY() + getVelocityY());
        this.setPivot();
        fuelDepletion();
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
        helipadRect.setTranslateX(150);
        helipadRect.setTranslateY(50);

        Ellipse helipadCircle = new Ellipse(helipadRadius, helipadRadius);
        helipadCircle.setFill(Color.SADDLEBROWN);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setStrokeWidth(2);
        helipadCircle.setTranslateX(200);
        helipadCircle.setTranslateY(100);

        this.getChildren().addAll(helipadRect, helipadCircle);
    }
}

class Cloud extends Pane{
    Random random = new Random();
    Helicopter choppah = new Helicopter();
    private static final double cloudRadius = 50;
    private int cloudSeed = 0;
    public Cloud() {
        Circle cloud = new Circle();
        cloud.setFill(Color.WHITE);
        cloud.setRadius(cloudRadius);
        cloud.setTranslateX(
                randomNumberGenerator(0 + cloudRadius,
                                        Globals.APP_WIDTH - cloudRadius));
        cloud.setTranslateY(
                randomNumberGenerator(Globals.ONE_THIRD_APP_HEIGHT + cloudRadius
                                     ,Globals.APP_HEIGHT - cloudRadius));
        GameText cloudText = new GameText(
                cloud.getTranslateX() - 14, cloud.getTranslateY() + 8,
                Color.BLACK
        );
        cloudText.setText(cloudSeed + "%");
        getChildren().addAll(cloud, cloudText);
    }
    public void seedCloud(){

    }
    public double randomNumberGenerator(double min, double max){
        return min + ((max - min) + 1) * random.nextDouble();
    }
}

class Pond extends Pane{
    Random random = new Random();
    private static final double pondRadius = 30;
    private double pondSeed = 0;
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
        GameText cloudText = new GameText(
                pond.getTranslateX() - 12, pond.getTranslateY() + 7,
                Color.BLACK
        );
        cloudText.setText(pondSeed + "%");
        getChildren().addAll(pond, cloudText);
    }
    public double randomNumberGenerator(double min, double max){
        return min + ((max - min) + 1) * random.nextDouble();
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

            choppah.update();
        }
    };
    public Game(){
        super.setScaleY(-1);
    }
    public void init(){
        this.getChildren().clear();
        this.getChildren().addAll(new Helipad(), new Pond(), new Cloud(),
                choppah = new Helicopter());
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
