import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.*;

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
    private double accelerationLevel = 0;
    private static double speed;
    private boolean isOff;
    private boolean isStarting;
    private boolean isStopping;
    private boolean isReady;
    private int chopperFuel;
    Bounds chopperBounds;
    GameText heliText = new GameText(-11, -35, Color.LEMONCHIFFON);
    HelicopterBlade blade = new HelicopterBlade();

    public Helicopter(){
        speed = 0;
        isOff = true;
        isStarting = false;
        isStopping = true;
        isReady = false;
        chopperFuel = 100;
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
        double chopperTailWidth = 5;
        chopperTail.setWidth(chopperTailWidth);
        double chopperTailHeight = 28;
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
    public int getFuel(){
        return chopperFuel;
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
        if(isStarting){
            if(chopperFuel >= 0){
                chopperFuel -= 5 * Math.abs((.05 * accelerationLevel + 1));
            }
            else{
                chopperFuel = 0;
            }
        }
        heliText.setText("F: " + chopperFuel);
    }
    public void ignition(){
        isStarting = !isStarting;
        isStopping = !isStopping;
        System.out.println("Starting Status: " + isStarting);
        System.out.println("Stopping Status: " + isStopping);
    }
    public void bladeStatus(){
        if(blade.getRotationSpeed() >= 20){
            isReady = !isReady;
        } else{
            isReady = !isReady;
        }
        if(blade.getRotationSpeed() > 0){
            isOff = !isOff;
        } else{
            isOff = !isOff;
        }
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
        return speed * -sin(Math.toRadians(getMyRotation()));
    }
    public double getVelocityY(){
        return speed * cos(Math.toRadians(getMyRotation()));
    }
    public void moveHeli(){
        translate(myTranslation.getX() + getVelocityX(),
                myTranslation.getY() + getVelocityY());
    }
    public void update(){
        moveHeli();
        setPivot();
        if(isStarting){
            blade.rotateBlade();
        }
        if(isStopping){
            blade.slowDownBlade();
        }
        blade.setPivot();
        fuelDepletion();
        bladeStatus();
    }
}
class HelicopterBlade extends GameObject{
    private static int windUpTime = 0;
    private static int rotationalSpeed;
    public HelicopterBlade(){
        rotationalSpeed = 0;
        Rectangle heliBlade = new Rectangle();
        heliBlade.setWidth(5);
        heliBlade.setHeight(100);
        heliBlade.setFill(Color.BLACK);
        heliBlade.setStroke(Color.WHITE);
        heliBlade.setRotate(45);
        add(heliBlade);
    }
    public int getRotationSpeed(){
        return rotationalSpeed;
    }
    public void rotateBlade(){
        if(rotationalSpeed < 20){
            windUpTime++;
            if(windUpTime % 15 == 0){
                rotationalSpeed += 1;
            }
        }
        rotate(getMyRotation() + 4 + rotationalSpeed);
    }
    public void slowDownBlade(){
        if(rotationalSpeed > 0){
            windUpTime++;
            if(windUpTime % 15 == 0){
                rotationalSpeed -= 1;            }
        }
        rotate(getMyRotation() + rotationalSpeed);
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

        Ellipse helipadCircle = new Ellipse(helipadRadius, helipadRadius);
        helipadCircle.setFill(Color.SADDLEBROWN);
        helipadCircle.setStroke(Color.WHITE);
        helipadCircle.setStrokeWidth(2);
        helipadCircle.setTranslateX(helipadRect.getTranslateX() +
                helipadWidth / 2.0);
        helipadCircle.setTranslateY(helipadRect.getTranslateY() +
                helipadHeight / 2.0);

        setTranslateX(Globals.APP_WIDTH / 2 - helipadWidth / 2.0);
        setTranslateY(50);
        this.getChildren().addAll(helipadRect, helipadCircle);
    }

    public int getHelipadWidth() {
        return helipadWidth;
    }

    public int getHelipadHeight() {
        return helipadHeight;
    }
}

class Cloud extends GameObject{
    Random random = new Random();
    Circle cloud;
    Bounds cloudBounds;
    GameText cloudText = new GameText(-14, 8, Color.BLACK);
    private static final int cloudRadius = 75;
    private final double windSpeed;
    private int cloudSeed = 0;
    private int decayTime = 0;
    public Cloud() {
        windSpeed = randomNumberGenerator(0.3, 0.6);
        cloud = new Circle();
        cloud.setFill(Color.WHITE);
        cloud.setRadius(cloudRadius);
        cloud.setOpacity(.75);
        translate(-cloudRadius, randomNumberGenerator(
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
        translate(this.myTranslation.getX() + windSpeed,
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
        }
    }
    public void decayingCloud(){
        if(this.cloudSeed > 0){
            this.decayTime++;
            if(this.decayTime % 60 == 0){
                this.cloudSeed--;
            }
        }
        this.cloudText.setText(cloudSeed + "%");
    }
    public void saturateCloud(){
        cloud.setFill(Color.color(1 - (cloudSeed * .0045),
                1 - (cloudSeed * .0045), 1 - (cloudSeed * .0045)));
    }
    public double randomNumberGenerator(double min, double max){
        return min + ((max - min) + 1) * random.nextDouble();
    }
    public void update(){
        this.moveCloud();
        this.saturateCloud();
        this.decayingCloud();
    }
}

class Pond extends GameObject{
    Random random = new Random();
    GameText pondText;
    private double seedTime = 0;
    private static final double pondRadius = 30;
    private int pondSeed = random.nextInt(30);
    public Pond() {
        double pondX = randomCoordinateX();
        double pondY = randomCoordinateY();
        Circle pond = new Circle(pondRadius, Color.DEEPSKYBLUE);
        setTranslateX(pondX);
        setTranslateY(pondY);
        pondText = new GameText(
                pond.getTranslateX() - 12, pond.getTranslateY() + 7,
                Color.BLACK
        );
        pondText.setText(pondSeed + "%");
        getChildren().addAll(pond, pondText);
    }
    public void seedPond(){
        if(this.pondSeed < 100){
            this.seedTime++;
            if(this.seedTime % 60 == 0 ){
                System.out.println("Pond is being seeded!");
                this.pondSeed++;
            }
        }
        this.pondText.setText(this.pondSeed + "%");
    }
    public double randomNumberGenerator(double min, double max){
        return min + ((max - min) + 1) * random.nextDouble();
    }
    public double randomCoordinateX(){
        return randomNumberGenerator(0 + pondRadius,
                Globals.APP_WIDTH - pondRadius);
    }
    public double randomCoordinateY(){
        return randomNumberGenerator(Globals.ONE_THIRD_APP_HEIGHT - pondRadius,
                Globals.APP_HEIGHT - pondRadius);
    }
    public void fillPond(){
        this.scale(1 + pondSeed * 0.01, 1 + pondSeed * 0.01);
    }
    public void update(){
        this.seedPond();
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

class DistanceLines extends Pane{
    Cloud cloud;
    ArrayList<Pond> ponds;
    Line distanceLine0;
    Line distanceLine1;
    Line distanceLine2;
    static boolean isVisible = false;
    public DistanceLines(Cloud cloud, ArrayList<Pond> ponds) {
        this.cloud = cloud;
        this.ponds = ponds;
        distanceLine0 = new Line(cloud.myTranslation.getX(),
                cloud.myTranslation.getY(), ponds.get(0).getTranslateX(),
                ponds.get(0).getTranslateY());
        distanceLine1 = new Line(cloud.myTranslation.getX(),
                cloud.myTranslation.getY(), ponds.get(1).getTranslateX(),
                ponds.get(1).getTranslateY());
        distanceLine2 = new Line(cloud.myTranslation.getX(),
                cloud.myTranslation.getY(), ponds.get(2).getTranslateX(),
                ponds.get(2).getTranslateY());
        distanceLine0.setStroke(Color.BLACK);
        distanceLine1.setStroke(Color.BLACK);
        distanceLine2.setStroke(Color.BLACK);
        distanceLine0.setVisible(isVisible);
        distanceLine1.setVisible(isVisible);
        distanceLine2.setVisible(isVisible);
        getChildren().addAll(distanceLine0, distanceLine1, distanceLine2);
    }
    public void visibility(){
        System.out.println("Visibility boolean modified!");
        System.out.println(isVisible);
        isVisible = !isVisible;
    }
    public void checkVisibility(){
        if(lineLength(this.distanceLine0) > 240){
            this.distanceLine0.setVisible(false);
        }else{
            this.distanceLine0.setVisible(isVisible);
        }
        if(lineLength(this.distanceLine1) > 240){
            this.distanceLine1.setVisible(false);
        }else{
            this.distanceLine1.setVisible(isVisible);
        }
        if(lineLength(this.distanceLine2) > 240){
            this.distanceLine2.setVisible(false);
        }else{
            this.distanceLine2.setVisible(isVisible);
        }
    }
    public void moveLines(Cloud cloud){
        distanceLine0.setStartX(cloud.myTranslation.getX());
        distanceLine0.setStartY(cloud.myTranslation.getY());
        distanceLine1.setStartX(cloud.myTranslation.getX());
        distanceLine1.setStartY(cloud.myTranslation.getY());
        distanceLine2.setStartX(cloud.myTranslation.getX());
        distanceLine2.setStartY(cloud.myTranslation.getY());
    }

    public double lineLength(Line line){
        return Math.sqrt(Math.pow((line.getStartY() - line.getEndY()), 2)
                + Math.pow((line.getStartX() - line.getEndX()), 2));
    }
    public void update(Cloud cloud){
        moveLines(cloud);
        checkVisibility();
    }
}

class GameOver {
    GameApp gameApp;
    Alert gameOver = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Hello World",
            ButtonType.YES,
            ButtonType.NO);
    public GameOver(GameApp x){
        gameApp = x;
    }
    public void popUp(){
        gameOver.setOnHidden(event -> {
            if(gameOver.getResult() == ButtonType.YES){
                gameApp.rainmaker.init();
                gameOver.close();
            }else{
                System.exit(0);
            }
        });
    }
    public void winLoseConditions(){
        if(gameApp.rainmaker.choppah.getFuel() <= 0){
            System.out.println("HERO");
            gameOver.show();
            popUp();
        }
    }
}

class Game extends Pane{
    Helicopter choppah = new Helicopter();
    Helipad helipad = new Helipad();
    Pane cloudySky = new Pane();
    ArrayList<Pond> ponds = new ArrayList<>();
    ArrayList<DistanceLines> distanceLines = new ArrayList<>();
    int frameCount = 0;
    GameOver pop;

    AnimationTimer game = new AnimationTimer() {
        double oldFrame = -1;
        double elapsedTime = 0;

        @Override
        public void handle(long currentFrame) {
            if(oldFrame < 0) oldFrame = currentFrame;
            double frameTime = (currentFrame - oldFrame) / 1e9;
            oldFrame = currentFrame;
            elapsedTime += frameTime;
            spawnClouds();
            respawnLines();

            seedPonds();
            fillPonds();
            choppah.update();
            updateClouds();
            updateLines();
            pop.winLoseConditions();
        }
    };
    public Game(GameOver pop){
        super.setScaleY(-1);
        this.pop = pop;
    }
    public void init(){
        ponds.clear();
        distanceLines.clear();
        spawnPonds();
        this.getChildren().clear();
        this.getChildren().addAll(new Helipad(),
                ponds.get(0),
                ponds.get(1),
                ponds.get(2),
                cloudySky = new Pane(new Cloud()),
                choppah = new Helicopter()
                );
    }
    public boolean isChopperInCloud(Helicopter helicopter,
                                             Cloud cloud){
        return cloud.myTranslation.getX() - cloud.getRadius() <
                helicopter.myTranslation.getX() &&
                cloud.myTranslation.getY() - cloud.getRadius() <
                        helicopter.myTranslation.getY() &&
                cloud.myTranslation.getX() + cloud.getRadius() >
                        helicopter.myTranslation.getX() +
                                helicopter.getChopperBodyWidth() &&
                cloud.myTranslation.getY() + cloud.getRadius() >
                        helicopter.myTranslation.getY() +
                                helicopter.getChopperBodyHeight();
    }
    public boolean isChopperOnHelipad(Helicopter choppah, Helipad helipad){
        return helipad.getTranslateX() <
                choppah.myTranslation.getX() - choppah.getChopperBodyWidth() &&
                helipad.getTranslateX() + helipad.getHelipadWidth() >
                        choppah.myTranslation.getX() +
                                choppah.getChopperBodyWidth() &&
                helipad.getTranslateY() < choppah.myTranslation.getY() -
                        choppah.getChopperBodyHeight() &&
                helipad.getTranslateY() + helipad.getHelipadHeight() >
                        choppah.myTranslation.getY() +
                                choppah.getChopperBodyWidth();
    }
    public void spawnPonds(){
        while(ponds.size() < 3){
            ponds.add(new Pond());
        }
    }
    public void seedPonds(){
        for(int i = 0; i < cloudySky.getChildren().size(); i++){
            for(int j = 0; j < distanceLines.size(); j++){
                if(distanceLines.get(j).lineLength(
                        distanceLines.get(j).distanceLine0) < 240 &&
                        ((Cloud)cloudySky.getChildren().get(j)).getCloudSeed() > 30){
                    ponds.get(0).update();
                }
                if(distanceLines.get(j).lineLength(
                        distanceLines.get(j).distanceLine1) < 240 &&
                        ((Cloud)cloudySky.getChildren().get(j)).getCloudSeed() > 30){
                    ponds.get(1).update();
                }
                if(distanceLines.get(j).lineLength(
                        distanceLines.get(j).distanceLine2) < 240 &&
                        ((Cloud)cloudySky.getChildren().get(j)).getCloudSeed() > 30){
                    ponds.get(2).update();
                }
            }
        }
    }
    public void fillPonds(){
        for (Pond pond : ponds) {
            pond.fillPond();
        }
    }
    public void seedCloud(){
        for(int i = 0; i < cloudySky.getChildren().size(); i++){
            if(isChopperInCloud(choppah,
                    (Cloud)cloudySky.getChildren().get(i)))
            {
                ((Cloud)cloudySky.getChildren().get(i)).seedingCloud();
            }
        }
    }
    public void spawnClouds(){
        if(cloudySky.getChildren().size() < 3){
            cloudySky.getChildren().add(new Cloud());
        }
        else if(cloudySky.getChildren().size() < 5){
            frameCount++;
            if(frameCount % 180 == 0){
                if(Math.random() < 0.5){
                    cloudySky.getChildren().add(new Cloud());
                }
            }
        }
    }
    public void updateClouds(){
        for(int i = 0; i < cloudySky.getChildren().size(); i++){
            if(((Cloud)cloudySky.getChildren().get(i)).myTranslation.getX() -
                    ((Cloud)cloudySky.getChildren().get(i)).getRadius() >
                    Globals.APP_WIDTH){
                distanceLines.get(i).getChildren().clear();
                distanceLines.remove(distanceLines.get(i));
                cloudySky.getChildren().remove(cloudySky.getChildren().get(i));
            }else{
                ((Cloud)cloudySky.getChildren().get(i)).update();
            }
        }
    }
    public void updateLines(){
        for(int i = 0; i < distanceLines.size(); i++){
            distanceLines.get(i).update(((Cloud)cloudySky.getChildren().get(i)));
        }
    }
    public void respawnLines(){
        if(distanceLines.size() < cloudySky.getChildren().size()){
            distanceLines.add(new DistanceLines((
                    (Cloud)cloudySky.getChildren().get(
                            distanceLines.size())), ponds));
            getChildren().add(distanceLines.get(distanceLines.size() - 1));
        }
    }
    public void toggleLines(){
        for (DistanceLines distanceLine : distanceLines) {
            distanceLine.visibility();
        }
    }

}

public class GameApp extends Application {
    GameOver pop = new GameOver(this);
    Game rainmaker = new Game(pop);
    @Override
    public void start(Stage primaryStage){
        rainmaker.init();
        Scene scene = new Scene(rainmaker, Globals.APP_WIDTH,
                Globals.APP_HEIGHT, Color.SANDYBROWN);
        scene.setOnKeyPressed(event -> {
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
                if(rainmaker.choppah.getSpeed() == 0 &&
                        rainmaker.isChopperOnHelipad(rainmaker.choppah,
                                rainmaker.helipad)){
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
                //Reserved for testing purposes.
                System.out.println("Hello World.");
            }
            if(event.getCode() == KeyCode.D){
                //System.out.println("Distance lines pressed!");
                rainmaker.toggleLines();
            }
        });

        rainmaker.game.start();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Rainmaker");
        primaryStage.show();
    }
/*    public void init(Game game){
        game.ponds.clear();
        game.distanceLines.clear();
        game.spawnPonds();
        game.getChildren().clear();
        game.getChildren().addAll(new Helipad(),
                game.ponds.get(0),
                game.ponds.get(1),
                game.ponds.get(2),
                game.cloudySky = new Pane(new Cloud()),
                game.choppah = new Helicopter()
        );
    }*/
    public static void main(String[] args){
        Application.launch(args);
    }
}
/*
* TODO LIST:
*   -Win/Lose condition and screen
*   -Image Background and Helipad
*   -State design via classes?
*/
