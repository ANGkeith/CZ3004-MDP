package utils;

import controllers.SimulatorController;
import models.Grid;
import models.MyRobot;

import java.util.ArrayList;

import static models.Constants.*;

public class ExplorationAlgorithm {

    private MyRobot myRobot;
    private ExplorationType explorationType;
    private SimulatorController sim;
    public static int timesNotCalibratedHorizontal = 0;
    public static int timesNotCalibratedVertical = 0;
    public static String[] positionToTakePicArr;
    public static int picTaken = 0;
    private boolean visitedTopLeft;
    private boolean visitedBottomRight;
    private boolean visitedGoalZone;


    public ExplorationAlgorithm(MyRobot myRobot, SimulatorController sim, ExplorationType explorationType) {
        this.myRobot = myRobot;
        this.sim = sim;
        this.explorationType = explorationType;
    }

    public void explorationLogic() throws Exception {
        boolean explorationCompletedFlag = false;
        visitedBottomRight = false;
        visitedGoalZone = false;
        visitedTopLeft = false;
        int antiLoopCounter = 0;

        timesNotCalibratedHorizontal = 0;
        timesNotCalibratedVertical = 0;

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


            if (myRobot.hasObstacleToImmediateRight() || myRobot.rightBlindSpotHasObstacle()) {
                if (!myRobot.hasObstacleRightInFront()) {
                    if (myRobot.frontSensorReadingGives(0, 0, 0) && myRobot.nextFwdLeftSideAlreadyExplored(0) && myRobot.rightSensorReadingGives(1,1)) {
                        // move two forward step
                        sim.ultraInstinct();
                    } else {
                        sim.forward();
                        antiLoopCounter = 0;
                    }
                } else if (myRobot.isInDeadEnd()) {
                    myRobot.calibrateFront();
                    myRobot.calibrateRight();
                    while(myRobot.immediateLeftSideHasObstacle()) {
                        sim.reverse();
                    }
                    myRobot.calibrateRight();
                    sim.left();
                    sim.forward();
                } else if (!myRobot.hasObstacleToImmediateLeft()) {
                    myRobot.calibrateRight();
                    myRobot.calibrateFront();
                    sim.left();
                    antiLoopCounter = 0;
                } else if (myRobot.hasObstacleToImmediateLeft()) {
                    sim.right();
                    sim.right();
                    antiLoopCounter = 0;
                }
            } else {
                if (myRobot.hasObstacleOneGridFromTheRight()) {
                    antiLoopCounter = 0;
                    if (!myRobot.hasObstacleRightInFront() && !myRobot.inStartZone()) {
                        // delay turning by one step
                        sim.forward();
                        if (myRobot.rightSensorReadingGives(0, 0) && !myRobot.rightBlindSpotHasObstacle2() && !myRobot.rightBlindSpotHasObstacle()) {
                            sim.right();
                            sim.forward();
                            sim.forward();
                        } else if (myRobot.rightBlindSpotHasObstacle() || myRobot.rightSensorReadingGives(1, 2)){
                            sim.forward();
                        } else if (myRobot.rightSensorReadingGives(2, 2)){
                            sim.right();
                            sim.forward();
                            sim.left();
                        } else {
                            sim.right();
                            sim.forward();
                            sim.left();
                        }
                    } else {

                        if (myRobot.rightSideFrontSensorThirdGridNeedsToBeExplored() || myRobot.inStartZone()) {
                            sim.right();
                            sim.forward();
                        } else if (!myRobot.frontFacingArenaWall()) {
                            sim.right();
                            sim.forward();
                        } else {
                            if (myRobot.hasObstacleDiagonalLeftBehindRobot()) {
                                sim.right();
                                sim.right();
                                if ((myRobot.rightSensorReadingGives(2, 2)
                                        || myRobot.rightSensorReadingGives(2, 0)
                                        || myRobot.rightSensorReadingGives(0, 2))) {
                                    sim.forward();
                                }
                            } else {
                                sim.left();
                            }
                        }
                    }
                } else {
                    sim.right();
                    if (myRobot.leftSensorDetectedObstacle()) {
                        takePicFlag = true;
                    }
                    sim.forward();
                    antiLoopCounter++;
                    if (antiLoopCounter == 5) {
                        antiLoopCounter = 0;
                        sim.left();
                    }
                }

            }

            if (myRobot.isAtBtmRight() && !visitedBottomRight) {
                visitedBottomRight = true;
            }

            if (myRobot.isAtTopLeft() && !visitedTopLeft) {
                visitedTopLeft = true;
            }

            if (myRobot.inGoalZone() && !visitedGoalZone) {
                visitedGoalZone = true;
                myRobot.setHasFoundGoalZoneFlag(true);
            }

            if (myRobot.getHasFoundGoalZoneFlag() && myRobot.isAtStartZone()) {
                explorationCompletedFlag = true;
            }
        }
    }

    public void explorationLogic2() throws Exception {
        boolean takePicFlag = false;
        boolean explorationCompletedFlag = false;
        int count = 0;
        myRobot.takePicture();
        while (!explorationCompletedFlag && explorationStoppingConditions()) {
            if (takePicFlag == true) {
                myRobot.takePicture();
                takePicFlag = false;
            }
            if (myRobot.leftSensorDetectedObstacle()) {
                takePicFlag = true;
            }

            if (myRobot.hasObstacleToImmediateRight() || myRobot.rightBlindSpotHasObstacle()) {
                if (!myRobot.hasObstacleRightInFront()) {
                    sim.forward();
                    count = 0;
                } else if (!myRobot.hasObstacleToImmediateLeft()) {
                    myRobot.calibrateRight();
                    myRobot.calibrateFront();
                    sim.left();
                    count = 0;
                } else if (myRobot.hasObstacleToImmediateLeft()) {
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
            if (myRobot.isAtCenterOfGoalZone()) {
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
        boolean takePicFlag = true;

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
            if (myRobot.hasObstacleToImmediateRight() || myRobot.rightBlindSpotHasObstacle()) {
                if (!myRobot.hasObstacleRightInFront()) {
                    if (myRobot.ifNeedToTakePictureOfBlindSpotGrid1()) {
                        sim.left();
                        sim.left();
                        myRobot.takePicture();
                        sim.right();
                        sim.right();
                    }
                    if (myRobot.obstacleFaceBehindHasNotBeenCaptured()) {
                        sim.left();
                        myRobot.takePicture();
                        sim.right();
                    }
                    sim.forward();
                    count = 0;
                } else if (!myRobot.hasObstacleToImmediateLeft()) {
                    if (myRobot.ifNeedToTakePictureOfBlindSpotGrid1()) {
                        sim.left();
                        sim.left();
                        myRobot.takePicture();
                        sim.right();
                    } else {
                        sim.left();
                    }
                    count = 0;
                } else if (myRobot.hasObstacleToImmediateLeft()) {
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
            if (myRobot.isAtCenterOfGoalZone()) {
                myRobot.setHasFoundGoalZoneFlag(true);
            }

            if (myRobot.getHasFoundGoalZoneFlag() && myRobot.isAtStartZone()) {
                explorationCompletedFlag = true;
                // TODO NOT TESTED
                ArrayList<Grid> arrayList = myRobot.getArena().getUncapturedObstacle();
                Grid curGrid;
                ArrayList<Grid> needsToBeExplored = new ArrayList();
                for (int i = 0; i < arrayList.size(); i++) {
                    curGrid = arrayList.get(i);
                    if (!curGrid.isU()) {
                        if (myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() - 2, curGrid.getCol())
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() - 4, curGrid.getCol() + 1)
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() - 4, curGrid.getCol() - 1)) {
                            needsToBeExplored.add(curGrid);

                        }
                    }
                    if (!curGrid.isD()) {
                        if (myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() + 2, curGrid.getCol())
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() + 4, curGrid.getCol() + 1)
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() + 4, curGrid.getCol() - 1)) {
                            needsToBeExplored.add(curGrid);

                        }
                    }
                    if (!curGrid.isL()) {
                        if (myRobot.getArena().robotCanBePlaceAt(curGrid.getRow(), curGrid.getCol() - 2)
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() + 1, curGrid.getCol() - 4)
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() - 1, curGrid.getCol() - 4)) {
                            needsToBeExplored.add(curGrid);

                        }
                    }
                    if (!curGrid.isR()) {
                        if (myRobot.getArena().robotCanBePlaceAt(curGrid.getRow(), curGrid.getCol() + 2)
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() + 1, curGrid.getCol() + 4)
                                || myRobot.getArena().robotCanBePlaceAt(curGrid.getRow() - 1, curGrid.getCol() + 4)) {
                            needsToBeExplored.add(curGrid);
                        }
                    }
                }
                String s = "";
                for (Grid g: needsToBeExplored) {
                    if (!g.isU()) {
                        if (myRobot.getArena().robotCanBePlaceAt(g.getRow() - 2, g.getCol())) {
                            s += (g.getRow() - 2) + "," + g.getCol() + ",W|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() - 4, g.getCol() + 1)) {
                            s += (g.getRow() - 4) + "," + (g.getCol() + 1) + ",W|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() - 4, g.getCol() - 1)) {
                            s += (g.getRow() - 4) + "," + (g.getCol() -1) + ",W|";
                        }
                    }
                    if (!g.isD()) {
                        if (myRobot.getArena().robotCanBePlaceAt(g.getRow() + 2, g.getCol())) {
                            s += (g.getRow() + 2) + "," + g.getCol() + ",E|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() + 4, g.getCol() + 1)) {
                            s += (g.getRow() + 4) + "," + (g.getCol() + 1) + ",E|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() + 4, g.getCol() - 1)) {
                            s += (g.getRow() + 4) + "," + (g.getCol() -1) + ",E|";
                        }
                    }
                    if (!g.isL()) {
                        if (myRobot.getArena().robotCanBePlaceAt(g.getRow(), g.getCol() - 2)) {
                            s += (g.getRow()) + "," + (g.getCol() - 2) + ",S|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() + 1, g.getCol() - 4)) {
                            s += (g.getRow() + 1) + "," + (g.getCol() - 4) + ",S|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() - 1, g.getCol() - 4)) {
                            s += (g.getRow() - 1) + "," + (g.getCol() -4) + ",S|";
                        }
                    }
                    if (!g.isR()) {
                        if (myRobot.getArena().robotCanBePlaceAt(g.getRow(), g.getCol() + 2)) {
                            s += (g.getRow()) + "," + (g.getCol() + 2)+ ",N|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() + 1, g.getCol() + 4)) {
                            s += (g.getRow() + 1) + "," + (g.getCol() + 4) + ",N|";
                        } else if (myRobot.getArena().robotCanBePlaceAt(g.getRow() - 1, g.getCol() + 4)) {
                            s += (g.getRow() - 1) + "," + (g.getCol() +4) + ",N|";
                        }
                    }
                }

                positionToTakePicArr = s.split("\\|");
                // TODO NOT TESTED

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
