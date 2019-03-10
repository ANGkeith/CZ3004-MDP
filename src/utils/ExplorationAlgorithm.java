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
    public static int timesNotCalibrated = 0;


    public ExplorationAlgorithm(MyRobot myRobot, SimulatorController sim, ExplorationType explorationType) {
        this.myRobot = myRobot;
        this.sim = sim;
        this.explorationType = explorationType;
    }

    public void explorationLogic(boolean isBruteForcing) throws Exception {
        boolean explorationCompletedFlag = false;
        while (!explorationCompletedFlag && explorationStoppingConditions()) {
            if (myRobot.hasObstacleToItsImmediateRight() || myRobot.rightBlindSpotHasObstacle()) {
                if (!myRobot.hasObstacleRightInFront()) {
                    sim.forward();
                } else if (!myRobot.hasObstacleToItsImmediateLeft()) {
                    sim.left();
                } else if (myRobot.hasObstacleToItsImmediateLeft()) {
                    sim.right();
                    sim.right();
                }
            } else {
                sim.right();
                sim.forward();
            }
            if (myRobot.isAtGoalZone()) {
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
