%report of clasical information collected (best agents in successful experiments

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.InformationCollected1 ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.InformationCollected1 ..\results mazeoff levywalk lwphevap hybrid sequential random 

%report of round number of successful experiments

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.RoundNumber1 ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.RoundNumber1 ..\results mazeoff levywalk lwphevap hybrid sequential random 

REM report of information collected of best agents with special output for hybrid algorithms.

REM java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.HybridInformationCollected ..\results beal levywalk lwphevap hybrid sequential random 

REM java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.HybridInformationCollected ..\results mazeoff levywalk lwphevap hybrid sequential random 

%report of success rates 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.SucessfulRates ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.SucessfulRates ..\results mazeoff levywalk lwphevap hybrid sequential random 

%Report of global information collected by each experiment.

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.HybridGlobalInfoReport ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.HybridGlobalInfoReport ..\results mazeoff levywalk lwphevap hybrid sequential random 

%Report of average messages sent by experiment.

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.MessagesSent1 ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.MessagesSent1 ..\results mazeoff levywalk lwphevap hybrid sequential random 

%Report of round of best agents by experiment (including not succesful experiments.

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.BestAgentsRoundInfoCollected ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.BestAgentsRoundInfoCollected ..\results mazeoff levywalk lwphevap hybrid sequential random 


%Report of round of best agents by experiment (including not succesful experiments.

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.BestAgentsPercentageInfoCollected ..\results beal levywalk lwphevap hybrid sequential random 

java  -Xmx4200m -classpath TermitesSimulator.jar unalcol.termites.boxplots.BestAgentsPercentageInfoCollected ..\results mazeoff levywalk lwphevap hybrid sequential random 

pause