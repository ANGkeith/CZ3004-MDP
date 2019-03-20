package utils;

import controllers.SimulatorController;
import models.MyRobot;

import static models.Constants.*;

public class ExplorationAlgorithm {

    private MyRobot myRobot;
    private ExplorationType explorationType;
    private SimulatorController sim;
    public static int timesNotCalibratedR = 0;
    public static int timesNotCalibratedF = 0;
    public static int picTaken = 0;


    public ExplorationAlgorithm(MyRobot myRobot, SimulatorController sim, ExplorationType explorationType) {
        this.myRobot = myRobot;
        this.sim = sim;
        this.explorationType = explorationType;
    }

    public void explorationLogic() throws Exception {
        boolean explorationCompletedFlag = false;
        int count = 0;
        boolean takePicFlag = false;
        myRobot.takePicture();
        while (!explorationCompletedFlag && explorationStoppingConditions()) {
            if (takePicFlag == true) {
                myRobot.takePicture();
                takePicFlag = false;
            }
            if (myRobot.leftSensorDetectedObstacle()) {
                takePicFlag = true;
            }


            if (myRobot.hasObstacleToItsImmediateRight() || myRobot.rightBlindSpotHasObstacle()) {
                if (!myRobot.hasObstacleRightInFront()) {
                    sim.forward();
                    count = 0;
                } else if (!myRobot.hasObstacleToItsImmediateLeft()) {
                    sim.left();
                    count = 0;
                } else if (myRobot.hasObstacleToItsImmediateLeft()) {
                    sim.right();
                    sim.right();
                    count = 0;
                }
            } else {
                sim.right();
                if (myRobot.leftSensorDetectedObstacle()) {
                    takePicFlag = true;
                }
                sim.forward();
                count++;
                if (count == 5) {
                    count = 0;
                    sim.left();
                }

            }
            if (myRobot.isAtGoalZone()) {
                timesNotCalibratedF = 200;
                timesNotCalibratedR = 200;
                myRobot.setHasFoundGoalZoneFlag(true);
            }

            if (myRobot.getHasFoundGoalZoneFlag() && myRobot.isAtStartZone()) {
                explorationCompletedFlag = true;
            }

        }
    }

    public void imageExploration() throws Exception {
        boolean explorationCompletedFlag = false;
        int count = 0;
        boolean takePicFlag = false;

        while (!explorationCompletedFlag && explorationStoppingConditions()) {
            if (takePicFlag == true) {
                myRobot.takePicture();
                takePicFlag = false;
            }
            if (myRobot.leftSensorDetectedObstacle()) {
                takePicFlag = true;
            }
            if (myRobot.frontSensorDetectedObstacle2GridAway()) {
                if (myRobot.canReverseByOne()) {
                    sim.reverse();
                    sim.right();
                    myRobot.takePicture();
                    sim.left();
                    sim.forward();
                }
            }
            if (myRobot.hasObstacleToItsImmediateRight() || myRobot.rightBlindSpotHasObstacle()) {
                if (!myRobot.hasObstacleRightInFront()) {
                    if (myRobot.ifNeedToTakePictureOfBlindSpotGrid1()) {
                        sim.left();
                        sim.left();
                        myRobot.takePicture();
                        sim.right();
                        sim.right();
                        //System.out.println((19 - myRobot.getCurRow() + " , " + myRobot.getCurCol()));
                    }
                    sim.forward();
                    count = 0;
                } else if (!myRobot.hasObstacleToItsImmediateLeft()) {
                    if (myRobot.ifNeedToTakePictureOfBlindSpotGrid1()) {
                        sim.left();
                        sim.left();
                        myRobot.takePicture();
                        sim.right();
                        //System.out.println((19 - myRobot.getCurRow() + " , " + myRobot.getCurCol()));
                    } else {
                        sim.left();
                    }
                    count = 0;
                } else if (myRobot.hasObstacleToItsImmediateLeft()) {
                    if (myRobot.ifNeedToTakePictureOfBlindSpotGrid1()) {
                        sim.right();
                        sim.right();
                        myRobot.takePicture();
                    } else {
                        sim.right();
                        sim.right();
                    }
                    count = 0;
                }
            } else {
                sim.right();
                if (myRobot.leftSensorDetectedObstacle()) {
                    takePicFlag = true;
                }
                sim.forward();
                // handles stair case
                if (myRobot.ifNeedToTakePictureOfBlindSpotGrid2()) {
                    sim.left();
                    sim.left();
                    myRobot.takePicture();
                    sim.right();
                    sim.right();
                }
                count++;
                if (count == 5) {
                    count = 0;
                    sim.left();
                }
            }
            if (myRobot.isAtGoalZone()) {
                timesNotCalibratedF = 200;
                timesNotCalibratedR = 200;
                myRobot.setHasFoundGoalZoneFlag(true);
            }

            if (myRobot.getHasFoundGoalZoneFlag() && myRobot.isAtStartZone()) {
                explorationCompletedFlag = true;
            }

        }
    }

    private boolean explorationStoppingConditions() {
        if (explorationType == ExplorationType.NORMAL) {
            return true;
        } else if (explorationType == ExplorationType.COVERAGE_LIMITED) {
            if (myRobot.getExplorationCoverageLimit() > 0) {
                return (myRobot.getExplorationCoverageLimit() > myRobot.getArena().getCoveragePercentage());
            }

        } else if (explorationType == ExplorationType.TIME_LIMITED) {
            if (myRobot.getExplorationTimeLimitInSeconds() > 0) {
                return (myRobot.getExplorationTimeLimitInSeconds() > sim.timeElapsed[0]);
            }
        }
        return true;
    }
}
