package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.Random;

class MyAgentState
{
  public int[][] world = new int[30][30];
  public int initialized = 0;
  final int UNKNOWN     = 0;
  final int WALL         = 1;
  final int CLEAR     = 2;
  final int DIRT        = 3;
  final int HOME        = 4;
  final int ACTION_NONE             = 0;
  final int ACTION_MOVE_FORWARD     = 1;
  final int ACTION_TURN_RIGHT     = 2;
  final int ACTION_TURN_LEFT         = 3;
  final int ACTION_SUCK             = 4;

  public int agent_x_position = 1;
  public int agent_y_position = 1;
  public int agent_last_action = ACTION_NONE;

  public static final int NORTH = 0;
  public static final int EAST = 1;
  public static final int SOUTH = 2;
  public static final int WEST = 3;
  public int agent_direction = EAST;

  MyAgentState()
  {
    for (int i=0; i < world.length; i++)
      for (int j=0; j < world[i].length ; j++)
    world[i][j] = UNKNOWN;
    world[1][1] = HOME;
    agent_last_action = ACTION_NONE;
  }
  public void updatePosition(DynamicPercept p)
  {
    Boolean bump = (Boolean)p.getAttribute("bump");

    if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
    {
      switch (agent_direction) {
      case MyAgentState.NORTH:
    agent_y_position--;
    break;
      case MyAgentState.EAST:
    agent_x_position++;
    break;
      case MyAgentState.SOUTH:
    agent_y_position++;
    break;
      case MyAgentState.WEST:
    agent_x_position--;
    break;
      }
    }

  }

  public void updateWorld(int x_position, int y_position, int info)
  {
    world[x_position][y_position] = info;
  }

  public void printWorldDebug()
  {
    for (int i=0; i < world.length; i++)
    {
      for (int j=0; j < world[i].length ; j++)
      {
    if (world[j][i]==UNKNOWN)
      System.out.print(" ? ");
    if (world[j][i]==WALL)
      System.out.print(" # ");
    if (world[j][i]==CLEAR)
      System.out.print(" . ");
    if (world[j][i]==DIRT)
      System.out.print(" D ");
    if (world[j][i]==HOME)
      System.out.print(" H ");
      }
      System.out.println("");
    }
  }
}

class MyAgentProgram implements AgentProgram {

  private int initnialRandomActions =10;
  private Random random_generator = new Random();

  // Here you can define your variables!
  public int iterationCounter = 50;
  public MyAgentState state = new MyAgentState();



  public int uTurn = 0;
  public int turnDir = 0;

  public Boolean turning = false;



  // moves the Agent to a random start position
  // uses percepts to update the Agent position - only the position, other percepts are ignored
  // returns a random action
  private Action moveToRandomStartPosition(DynamicPercept percept) {

    int action = random_generator.nextInt(6);
    initnialRandomActions--;
    System.out.println(initnialRandomActions);
    state.updatePosition(percept);
    if(action==0) {
      state.agent_direction = ((state.agent_direction-1) % 4);
      if (state.agent_direction<0)
    state.agent_direction +=4;
      state.agent_last_action = state.ACTION_TURN_LEFT;
      return LIUVacuumEnvironment.ACTION_TURN_LEFT;
    } else if (action==1) {
      state.agent_direction = ((state.agent_direction+1) % 4);
      state.agent_last_action = state.ACTION_TURN_RIGHT;
      return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
    }
    forwardCounter = 0;
    state.agent_last_action=state.ACTION_MOVE_FORWARD;
    return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
  }


// The forwardcounter calls the random-action function when it hits 5 in the main loop
//it is used for the times when the agent is stuck between blocks and can only go forward.

  int forwardCounter = 0;


  private Action moveToHomePosition(DynamicPercept percept) {

//The forwardcounter in the goning home action stops the program when the ACTION_MOVE_FORWARD is called over 15 times.
 if (forwardCounter > 15)
    {
      //return moveToRandomStartPosition((DynamicPercept) percept);
      return NoOpAction.NO_OP;
    }
    System.out.println(forwardCounter);
    System.out.println(iterationCounter);


    state.updatePosition(percept);
    System.out.println(state.agent_direction);
//Runs this loop while the agent is not in the home position (x = 1, y = 1)  
    while (state.agent_x_position != 1 || state.agent_y_position != 1){

//Checks if the agent_y_position and if its not 1 and the agent isn't going up and turns the agent to the up and moves the agent forward
      if (state.agent_y_position != 1){
        while(state.agent_direction != 0)
        {
            state.agent_direction = ((state.agent_direction+1) % 4);
            state.agent_last_action = state.ACTION_TURN_RIGHT;
            forwardCounter = 0;
            return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
        }

        forwardCounter = forwardCounter + 1;
      	state.agent_last_action = state.ACTION_MOVE_FORWARD;
      	return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
      }
       else
        if (state.agent_x_position != 1)
        {

          while(state.agent_direction != 3)
          {
            state.agent_direction = ((state.agent_direction+1) % 4);
            state.agent_last_action = state.ACTION_TURN_RIGHT;
            forwardCounter = 0;
            return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
          }
      
        forwardCounter = forwardCounter + 1;
        state.agent_last_action = state.ACTION_MOVE_FORWARD;
        System.out.println(state.agent_direction);
        return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;

        }

      }

      return NoOpAction.NO_OP;

  }

  private Action turnRight(DynamicPercept percept) {
    state.agent_direction = ((state.agent_direction+1) % 4);
    if (state.agent_direction<0)
      state.agent_direction +=4;

    state.agent_last_action = state.ACTION_TURN_RIGHT;
    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
  }


  private Action turnLeft(DynamicPercept percept)
  {
    state.agent_direction = ((state.agent_direction-1) % 4);
    if (state.agent_direction<0)
      state.agent_direction +=4;

    state.agent_last_action = state.ACTION_TURN_LEFT;
    return LIUVacuumEnvironment.ACTION_TURN_LEFT;
  }


@Override
public Action execute(Percept percept) {

  // DO NOT REMOVE this if condition!!!
  if (initnialRandomActions>0)
  {
    return moveToRandomStartPosition((DynamicPercept) percept);
  }
  else if (initnialRandomActions==0)
  {
      // process percept for the last step of the initial random actions
      initnialRandomActions--;
      state.updatePosition((DynamicPercept) percept);
      System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
      state.agent_last_action=state.ACTION_SUCK;
      return LIUVacuumEnvironment.ACTION_SUCK;
  }



    // This example agent program will update the internal agent state while only moving forward.
    // START HERE - code below should be modified!


    System.out.println("x=" + state.agent_x_position);
    System.out.println("y=" + state.agent_y_position);
    System.out.println("dir=" + state.agent_direction);


    iterationCounter--;

//goes in the homeloop if the itterationcounter is less than 0
    while(iterationCounter < 0)
    {
      System.out.println("in home loop");
      return moveToHomePosition((DynamicPercept) percept);
    }

//calls the moveToRandomStartPosition when the agent gets stuck between blocks
    if (forwardCounter > 5)
    {
      System.out.println("random stuff");

      return moveToRandomStartPosition((DynamicPercept) percept);
    }


    DynamicPercept p = (DynamicPercept) percept;
    Boolean bump = (Boolean)p.getAttribute("bump");
    Boolean dirt = (Boolean)p.getAttribute("dirt");
    Boolean home = (Boolean)p.getAttribute("home");
    System.out.println("percept: " + p);

    // State update based on the percept value and the last action
    state.updatePosition((DynamicPercept)percept);
    if (bump)
    {
      switch (state.agent_direction)
      {
      case MyAgentState.NORTH:
    state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
    break;
      case MyAgentState.EAST:
    state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
    break;
      case MyAgentState.SOUTH:
    state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
    break;
      case MyAgentState.WEST:
    state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
    break;
      }
    }


    if (dirt)
    {
      state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
    }
    else
    {
      state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
    }
      state.printWorldDebug();


    if (turning == true)
    {
      System.out.println("I'm Turning!");


//moves the agent down one step the agent bumps with a wall and turns
      if (uTurn == 1)
      {
      uTurn++;
      state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
      state.agent_last_action = state.ACTION_MOVE_FORWARD;
      return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
      }
      else

        {
          uTurn = 0;
          turning = false;

          if (turnDir == 0)
          {
            return turnRight((DynamicPercept) percept);
          }

          else
          {
            return turnLeft((DynamicPercept) percept);
          }

        }
      }



    if (dirt)
    {
      forwardCounter = 0;
      System.out.println("DIRT -> choosing SUCK action!");
      state.agent_last_action=state.ACTION_SUCK;
      return LIUVacuumEnvironment.ACTION_SUCK;

    }

    else
    {
//if the agent has direction 1,2 or 3 and bumps with a wall, the agent goes down a step and turns to the opposite direction. 
      if (bump && state.agent_direction == 1)
      {
        turning = true;
        uTurn++;
        turnDir = 0;
          forwardCounter = 0;
        return turnRight((DynamicPercept)percept);
      }

      else if (bump && state.agent_direction == 2)
      {
        turning = true;
        uTurn++;
        turnDir = 0;
          forwardCounter = 0;
        return turnRight((DynamicPercept)percept);
      }

      else if (bump && state.agent_direction == 3)
      {
        turning = true;
        uTurn++;
        turnDir = 1;
          forwardCounter = 0;
        return turnLeft((DynamicPercept)percept);
      }

 forwardCounter = forwardCounter + 1;
      state.agent_last_action = state.ACTION_MOVE_FORWARD;
      return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;

    }
  }
}



public class MyVacuumAgent extends AbstractAgent
{
  public MyVacuumAgent()
  {
    super(new MyAgentProgram());
  }
}
