# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


# valueIterationAgents.py
# -----------------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


import mdp, util
from collections import defaultdict

from learningAgents import ValueEstimationAgent
import collections

class ValueIterationAgent(ValueEstimationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A ValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100):
        """
          Your value iteration agent should take an mdp on
          construction, run the indicated number of iterations
          and then act according to the resulting policy.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state, action, nextState)
              mdp.isTerminal(state)
        """
        self.mdp = mdp
        self.discount = discount
        self.iterations = iterations
        self.values = util.Counter() # A Counter is a dict with default 0
        self.runValueIteration()

    def runValueIteration(self):
        # Write value iteration code here
        "*** YOUR CODE HERE ***"
        times=1
        allstates=self.mdp.getStates()
        while times<=self.iterations:
            for state in allstates:
                currentvals=[self.values[state] for state in allstates]
            for state in allstates:
                if not self.mdp.isTerminal(state):
                   allactions=self.mdp.getPossibleActions(state)
                   kvalues=[]
                   for action in allactions:
                       alldestinations=self.mdp.getTransitionStatesAndProbs(state,action)
                       value=0
                       for nextState,prob in alldestinations:
                           reward=self.mdp.getReward(state,action,nextState)
                           index=allstates.index(nextState)
                           value+=prob*(reward+self.discount*currentvals[index])
                       kvalues.append(value)
                   self.values[state]=max(kvalues)
            times+=1


    def getValue(self, state):
        """
          Return the value of the state (computed in __init__).
        """
        return self.values[state]


    def computeQValueFromValues(self, state, action):
        """
          Compute the Q-value of action in state from the
          value function stored in self.values.
        """
        "*** YOUR CODE HERE ***"
        alldestinations=self.mdp.getTransitionStatesAndProbs(state,action)
        value=0
        for nextState,prob in alldestinations:
            reward=self.mdp.getReward(state,action,nextState)
            value+=prob*(reward+self.discount*self.values[nextState])
        return value

    def computeActionFromValues(self, state):
        """
          The policy is the best action in the given state
          according to the values currently stored in self.values.

          You may break ties any way you see fit.  Note that if
          there are no legal actions, which is the case at the
          terminal state, you should return None.
        """
        "*** YOUR CODE HERE ***"
        if self.mdp.isTerminal(state):
           return None
        allactions=self.mdp.getPossibleActions(state)
        allvals=[self.computeQValueFromValues(state,action) for action in allactions]
        value=max(allvals)
        index=allvals.index(value)
        return allactions[index]

    def getPolicy(self, state):
        return self.computeActionFromValues(state)

    def getAction(self, state):
        "Returns the policy at the state (no exploration)."
        return self.computeActionFromValues(state)

    def getQValue(self, state, action):
        return self.computeQValueFromValues(state, action)

class AsynchronousValueIterationAgent(ValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        An AsynchronousValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs cyclic value iteration
        for a given number of iterations using the supplied
        discount factor.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 1000):
        """
          Your cyclic value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy. Each iteration
          updates the value of only one state, which cycles through
          the states list. If the chosen state is terminal, nothing
          happens in that iteration.

          Some useful mdp methods you will use:
              mdp.getStates()
              mdp.getPossibleActions(state)
              mdp.getTransitionStatesAndProbs(state, action)
              mdp.getReward(state)
              mdp.isTerminal(state)
        """
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        "*** YOUR CODE HERE ***"
        times=1
        allstates=self.mdp.getStates()
        chosen=0
        while times<=self.iterations:
            chosenstate=allstates[chosen]
            if not self.mdp.isTerminal(chosenstate):
               allactions=self.mdp.getPossibleActions(chosenstate)
               qvalues=[]
               for action in allactions:
                  q_value=self.computeQValueFromValues(chosenstate,action)
                  qvalues.append(q_value)
               self.values[chosenstate]=max(qvalues)
            chosen=(chosen+1)%len(allstates)
            times+=1

class PrioritizedSweepingValueIterationAgent(AsynchronousValueIterationAgent):
    """
        * Please read learningAgents.py before reading this.*

        A PrioritizedSweepingValueIterationAgent takes a Markov decision process
        (see mdp.py) on initialization and runs prioritized sweeping value iteration
        for a given number of iterations using the supplied parameters.
    """
    def __init__(self, mdp, discount = 0.9, iterations = 100, theta = 1e-5):
        """
          Your prioritized sweeping value iteration agent should take an mdp on
          construction, run the indicated number of iterations,
          and then act according to the resulting policy.
        """
        self.theta = theta
        ValueIterationAgent.__init__(self, mdp, discount, iterations)

    def runValueIteration(self):
        "*** YOUR CODE HERE ***"
        fringe=util.PriorityQueue()
        predecessors={}
        """Updating predecessors for all states"""
        allstates=self.mdp.getStates()
        for state in allstates:
            if not self.mdp.isTerminal(state):
               allactions=self.mdp.getPossibleActions(state)
               for action in allactions:    
                 alldestinations=self.mdp.getTransitionStatesAndProbs(state,action)
                 for nextState,prob in alldestinations:
                     if nextState in predecessors:
                        predecessors[nextState].add(state)
                     else:
                        predecessors[nextState]={state}
        """Second part of algorithm"""
        for state in allstates:
            if not self.mdp.isTerminal(state):
               allactions=self.mdp.getPossibleActions(state)
               qvalues=[]
               for action in allactions:
                 q_value=self.computeQValueFromValues(state,action)
                 qvalues.append(q_value)
               diff=abs(max(qvalues)-self.values[state])
               fringe.update(state,-diff)
        """Third part of algorithm"""
        for i in range(self.iterations):
            if not fringe.isEmpty():
               stateS=fringe.pop()
               if not self.mdp.isTerminal(stateS):
                  allactions=self.mdp.getPossibleActions(stateS)
                  qvalues=[]
                  for action in allactions:
                    q_value=self.computeQValueFromValues(stateS,action)
                    qvalues.append(q_value)
                  self.values[stateS]=max(qvalues)
               for p in predecessors[stateS]:
                   if not self.mdp.isTerminal(p):
                      allactions=self.mdp.getPossibleActions(p)
                      qvalues=[]
                      for action in allactions:
                        q_value=self.computeQValueFromValues(p,action)
                        qvalues.append(q_value)
                      diff=abs(max(qvalues)-self.values[p])
                      if diff>self.theta:
                         fringe.update(p,-diff)



