/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Runnable;

import unalcol.agents.Agent;
import unalcol.agents.AgentProgram;
import unalcol.termites.Program.HLwSandCPheromoneProgramPfImpl;
import unalcol.termites.Program.HybridLwSandCProgramImpl;
import unalcol.termites.Program.HybridLwSandLwCProgramImpl;
import unalcol.termites.Program.HybridPheromoneLevyWalkProgramImpl;
import unalcol.termites.Program.HybridPheromoneLevyWalkProgramImpl2;
import unalcol.termites.Program.LevyWalkAndCarriersProgramPfImpl;
import unalcol.termites.Program.LevyWalkAndCarriersProgramPfImpl2;
import unalcol.termites.Program.LevyWalkProgramImpl;
import unalcol.termites.Program.OnlySeekersLevyWalkProgramImpl;
import unalcol.termites.Program.OnlySeekersLevyWalkProgramImpl2;
import unalcol.termites.Program.RandomProgramImpl;
import unalcol.termites.Program.SequentialProgramImpl;
import unalcol.termites.Program.SeekersAndCarriersProgramImpl;
import unalcol.termites.Program.SeekersandCarriersLevyWalkProgramPfImpl;
import unalcol.termites.Program.TurnOnContactProgramImpl;
import unalcol.termites.TermitesLanguage.TermitesLanguage;
import unalcol.termites.World.World;
import unalcol.termites.World.WorldHybridLWSandCImpl;
import unalcol.termites.World.WorldHybridLwSandLwCImpl;
import unalcol.termites.World.WorldLwphCLwEvapImpl;
import unalcol.termites.World.WorldTemperaturesLWHOneStepPheromoneImpl;
import unalcol.termites.World.WorldTemperaturesOneStepHLwSandcPheromoneImpl;
import unalcol.termites.World.WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl;
import unalcol.termites.World.WorldLevyWalkImpl;
import unalcol.termites.World.WorldSequentialImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneEvaporationImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneEvaporationMapImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2;
import unalcol.termites.World.WorldTemperaturesOneStepOnePheromoneImpl;
import unalcol.termites.World.WorldTemperaturesOneStepPheromoneImpl;
import unalcol.termites.World.WorldTemperaturesOneStepRandomImpl;
import unalcol.termites.World.WorldTemperaturesTocOneStepLWOnePheromoneEvaporationImpl;
import unalcol.types.collection.vector.Vector;

/**
 * Factory Class to create a determined kind of World and Program given some
 * arguments
 *
 * @author Arles Rodriguez
 */
public class ProgramWorldFactory {

    /**
     * Creates and assign a program to each Agent
     *
     * @param popSize
     * @param probFailure
     * @param failuresByTermite
     * @return an AgentProgram
     */
    public static AgentProgram createProgram(int popSize, float probFailure, int failuresByTermite) {
        AgentProgram program;
        switch (AppMain.getMode()) {
            case "sequential":
                program = new SequentialProgramImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "random":
                program = new RandomProgramImpl(new TermitesLanguage(), probFailure);
                break;
            case "levywalk":
                program = new LevyWalkProgramImpl(new TermitesLanguage(), probFailure);
                break;
            case "lwphevap":
                program = new OnlySeekersLevyWalkProgramImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "lwphevapMap":
                program = new OnlySeekersLevyWalkProgramImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "lwphevap2":
                program = new OnlySeekersLevyWalkProgramImpl2(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "lwsandc":
            case "lwphclwevap":
                program = new LevyWalkAndCarriersProgramPfImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "lwsandc2":
                program = new LevyWalkAndCarriersProgramPfImpl2(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "hybrid":
                program = new HybridPheromoneLevyWalkProgramImpl(new TermitesLanguage(), popSize, probFailure, failuresByTermite);
                break;
            case "hybrid2":
                program = new HybridPheromoneLevyWalkProgramImpl2(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "sandclw":
                program = new SeekersandCarriersLevyWalkProgramPfImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "hlwsandc":
                program = new HLwSandCPheromoneProgramPfImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
            case "turnoncontact":
                //program = new LevyWalkProgramImpl(new TermitesLanguage(), probFailure);
                program = new TurnOnContactProgramImpl(new TermitesLanguage(), probFailure);
                break;
            case "hybrid3":
                program = new HybridLwSandCProgramImpl(new TermitesLanguage(), probFailure, failuresByTermite);
                break;
            case "hybrid4":
                program = new HybridLwSandLwCProgramImpl(new TermitesLanguage(), probFailure, failuresByTermite);
                break;
            default:
                program = new SeekersAndCarriersProgramImpl(new TermitesLanguage(), null, popSize, probFailure, failuresByTermite);
                break;
        }
        return program;
    }

    /**
     * Create a world with a vector of agents and a world size given
     *
     * @param termites
     * @param size_w
     * @param size_h
     * @return a World given the parameters of size and agents
     */
    public static World createWorld(Vector<Agent> termites, int size_w, int size_h) {
        World world;
        switch (AppMain.mode) {
            case "sequential":
                world = new WorldSequentialImpl(termites, size_w, size_h);
                break;
            case "random":
                world = new WorldTemperaturesOneStepRandomImpl(termites, size_w, size_h);
                break;
            case "sandc":
                world = new WorldTemperaturesOneStepPheromoneImpl(termites, size_w, size_h);
                break;
            case "oneph":
                world = new WorldTemperaturesOneStepOnePheromoneImpl(termites, size_w, size_h);
                break;
            case "onephevap":
                world = new WorldTemperaturesOneStepOnePheromoneEvaporationImpl(termites, size_w, size_h);
                break;
            case "levywalk":
                world = new WorldLevyWalkImpl(termites, size_w, size_h);
                break;
            case "lwphevapMap":
                world = new WorldTemperaturesOneStepOnePheromoneEvaporationMapImpl(termites, size_w, size_h);
                break;
            case "lwphevap":
            case "lwphevap2":
                world = new WorldTemperaturesOneStepLWOnePheromoneEvaporationImpl(termites, size_w, size_h);
                break;
            case "lwsandc":
            case "lwsandc2":
                world = new WorldTemperaturesLWHOneStepPheromoneImpl(termites, size_w, size_h);
                break;
            case "hybrid":
                world = new WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl(termites, size_w, size_h);
                break;
            case "hybrid2":
                world = new WorldTemperaturesOneStepOnePheromoneHybridLWEvaporationImpl2(termites, size_w, size_h);
                break;
            case "hybrid3":
                world = new WorldHybridLWSandCImpl(termites, size_w, size_h);
                break;
            case "hybrid4":
                world = new WorldHybridLwSandLwCImpl(termites, size_w, size_h);
                break;
            case "sandclw":
                world = new WorldTemperaturesOneStepPheromoneImpl(termites, size_w, size_h);
                break;
            case "hlwsandc":
                world = new WorldTemperaturesOneStepHLwSandcPheromoneImpl(termites, size_w, size_h);
                break;
            case "lwphclwevap":
                world = new WorldLwphCLwEvapImpl(termites, size_w, size_h);
                break;
            case "turnoncontact":
                world = new WorldTemperaturesTocOneStepLWOnePheromoneEvaporationImpl(termites, size_w, size_h);
                break;
            default:
                world = new WorldTemperaturesOneStepRandomImpl(termites, size_w, size_h);
                break;
        }
        return world;
    }

}
