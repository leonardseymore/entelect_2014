package za.co.entelect.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.agents.Epa;
import za.co.entelect.challenge.agents.Greedie;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.filters.PillCluster;
import za.co.entelect.challenge.ai.search.*;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.groovy.GameFactory;

import javax.sound.sampled.Line;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Collection;

public class MainFields {

    private static final Logger logger = LoggerFactory.getLogger(MainFields.class);

    public static void main(String[] args) throws FileNotFoundException {
        GameState gameState = GameFactory.fromClasspathFile("/initial.state");

        String directory = "C:\\Users\\leonard\\Dropbox\\Software Developer\\java\\entelect\\pacman\\pacman\\experiments";
        LinearFalloff linearFalloff = new LinearFalloff();
        StepFalloff stepFalloff = new StepFalloff(5);
        Falloff falloff = stepFalloff;

        float[][] ainf = Influence.getInfluence(gameState, gameState.getPlayerAPos(), linearFalloff);
        logger.debug("A Influence:\n{}\n", Util.toSv(ainf));
        Util.writeToFile(directory, "y_inf.txt", Util.toSv(ainf));

        float[][] binf = Influence.getInfluence(gameState, gameState.getPlayerBPos(), linearFalloff);
        logger.debug("B Influence:\n{}\n", Util.toSv(binf));
        Util.writeToFile(directory, "o_inf.txt", Util.toSv(binf));

        float[][] clusteredPills = PillCluster.getCellPotential(gameState);
        logger.debug("Clusters:\n{}\n", Util.toSv(clusteredPills));
        Util.writeToFile(directory, "pill_cluster.txt", Util.toSv(clusteredPills));

        int bpNum = 1;
        float[][] bpSumInf = new float[Constants.WIDTH][Constants.HEIGHT];
        for (XY bonusPill : gameState.getBonusPills()) {
            float[][] bpInf = Influence.getInfluence(gameState, bonusPill, new ExponentialFalloff());
            logger.debug("Bonus Pill {} Influence:\n{}\n", bonusPill, Util.toSv(bpInf));
            Util.writeToFile(directory, "bp" + bpNum + "_inf.txt", Util.toSv(bpInf));
            for (int x = 0; x < Constants.WIDTH; x++) {
                for (int y = 0; y < Constants.HEIGHT; y++) {
                    bpSumInf[x][y] += bpInf[x][y];
                }
            }
            bpNum++;
        }
        logger.debug("Total Bonus Pill Influence:\n{}\n", Util.toSv(Util.normalize(bpSumInf)));
        Util.writeToFile(directory, "bp_sum_inf.txt", Util.toSv(bpSumInf));

        float[][] pillSumInf = new float[Constants.WIDTH][Constants.HEIGHT];
        for (XY pill : gameState.getPills()) {
            float[][] pillInf = Influence.getInfluence(gameState, pill, new ExponentialFalloff());
            for (int x = 0; x < Constants.WIDTH; x++) {
                for (int y = 0; y < Constants.HEIGHT; y++) {
                    pillSumInf[x][y] += pillInf[x][y];
                }
            }
        }
        logger.debug("Total Pill Influence:\n{}\n", Util.toSv(Util.normalize(pillSumInf)));
        Util.writeToFile(directory, "p_sum_inf.txt", Util.toSv(pillSumInf));

        int[][] clusterSizes = PillCluster.getClusterSize(gameState);
        logger.debug("Cluster sizes:\n{}\n", Util.toSv(clusterSizes));
    }
}
