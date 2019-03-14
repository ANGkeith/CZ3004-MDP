package utils;

import controllers.SimulatorController;
import models.MyRobot;

import static controllers.SimulatorController.numFwd;
import static controllers.SimulatorController.numTurn;
import static models.Constants.*;

public class ExplorationAlgorithm {

    private MyRobot myRobot;
    private ExplorationType explorationType;
    private SimulatorController sim;
    public static int timesNotCalibratedR = 0;
    public static int timesNotCalibratedF = 0;


    public ExplorationAlgorithm(MyRobot myRobot, SimulatorController sim, ExplorationType explorationType) {
        this.myRobot = myRobot;
        this.sim = sim;
        this.explorationType = explorationType;
    }

    public void explorationLogic(boolean isBruteForcing) throws Exception {
        boolean explorationCompletedFlag = false;
        int count = 0;
        while (!explorationCompletedFlag && explorationStoppingConditions()) {
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

            if (isBruteForcing) {
                // shortcircuit if estimated to be in an infinite loop
                if (((numFwd + numTurn * 2)> 50 && myRobot.getArena().getCoveragePercentage() < 30.0) || numFwd > 200) {
                    explorationCompletedFlag = true;
                }
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
