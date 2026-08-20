// Agent "hedge" in project app

/* Initial beliefs and rules */

free_cell_directions([]). blocked_cell_directions([]).
num_of_free_moves(1). num_of_collisions(1).
routes([]). routes_head([]).
risk(0). alt_risk(0). // MAS2
plan([]).

/* Initial goals */

!meet. // MAS2
!wait.

/* Plans */

/* MAS2 : Contract Net Protocol - Start... */

@meet
+!meet : .my_name(Me) <-
    .print("Hi, I'm ", Me);
    .broadcast(tell, agent(Me)).

@agent
+agent(Ag) <-
    .print("Hello, ", Ag).

@route
+route(R) : agent(Ag) & risk(Risk) &
            free_cell_directions(FCDs) & blocked_cell_directions(BCDs) &
            nodes(NDs) & routes(RTs) & carry(C) & energy(E) &
            num_of_free_moves(NoFMs) & num_of_collisions(NoCs) <-

    -+managed_route(R);

    .concat(RTs, [R], AltRTs);
    agt.planner.plan(
            FCDs, BCDs, NDs, AltRTs, C, E, NoFMs, NoCs, AltRisk, AltPL);

    .send(Ag, askOne, cfp(R, AgAltRisk, AgRisk), cfp(R, AgAltRisk, AgRisk));
    !assing(Ag, AgAltRisk, AgRisk, AltRisk, Risk).

@cfp
+?cfp(R, AltRisk, Risk) : risk(Risk) &
        free_cell_directions(FCDs) & blocked_cell_directions(BCDs) &
        nodes(NDs) & routes(RTs) & carry(C) & energy(E) &
        num_of_free_moves(NoFMs) & num_of_collisions(NoCs) <-

    .concat(RTs, [R], AltRTs);
    agt.planner.plan(
            FCDs, BCDs, NDs, AltRTs, C, E, NoFMs, NoCs, AltRisk, AltPL).

@assing_ag
+!assing(Ag, AgAltRisk, AgRisk, AltRisk, Risk) : 
        AgAltRisk - AgRisk < AltRisk - Risk & managed_route(R) <-
    .send(Ag, tell, assigned(R));
    .print("MyDiff (", AltRisk - Risk, ") VS AgDiff (", AgAltRisk - AgRisk, ")" );
    .print("~~~> ASSIGNED ", R, " TO ", Ag).
@assing_me[atomic]
+!assing(Ag, AgAltRisk, AgRisk, AltRisk, Risk) : 
        managed_route(R) & routes(RTs) <-
    .concat(RTs, [R], NewRTs);
    -+routes(NewRTs);
    .print("MyDiff (", AltRisk - Risk, ") VS AgDiff (", AgAltRisk - AgRisk, ")" );
    .print("---> Assigned ", R, " to Me");
    .print("+route(", R, "), NewRTs: ", NewRTs);
    !plan.

@assigned[atomic]
+assigned(R) : routes(RTs) <-
    .concat(RTs, [R], NewRTs);
    -+routes(NewRTs);
    .print("+assigned(", R, "), NewRTs: ", NewRTs);
    !plan.

/* ... MAS2 : Contract Net Protocol - End */

+!wait : (routes(RTs) & .length(RTs, 0)) <-
    wait; !wait.
@wait_trans[atomic] // MAS2 : Routes Transfer
+!wait : risk(Risk) & Risk == 0 & agent(Ag) & routes(RTs) <-
    .send(Ag, tell, transfer(RTs)); 
    -+routes([]);
    .print(">>>>>> Transfered routes.");
    !wait.
+!wait <- !plan; !work; !wait.

@transfer[atomic]
+transfer(TransRTs) : routes(RTs) <-
    .print("<<<<<< Received routes.");
    .concat(RTs, TransRTs, NewRTs);
    -+routes(NewRTs);
    .print("NewRTs: ", NewRTs);
    !plan.

@plan
+!plan : free_cell_directions(FCDs) & blocked_cell_directions(BCDs) &
         nodes(NDs) & routes(RTs) & carry(C) & energy(E) &
         num_of_free_moves(NoFMs) & num_of_collisions(NoCs) <-
    agt.planner.plan(FCDs, BCDs, NDs, RTs, C, E, NoFMs, NoCs, Risk, PL);
    -+risk(Risk);
    -+plan(PL).

+!work : (routes(RTs) & .length(RTs, 0)) <- !wait.
+!work : plan(PL) & .length(PL, 0).
+!work : plan([A | PLTail]) <-
    -+plan(PLTail);
    !execute(A);
    !work.

@move
+!execute(A) : 0 <= A & A <= 3 & robot(Cell) <-
    -+robot(Cell, premove);
    -+direction(A);
    move(A);
    !check.
@unload
+!execute(A) : A == 4 & routes(RTs) & src(Src) & dst(Dst) <-
    unload;
    !remove_route([Src, Dst], RTs);
    !plan.
@load
+!execute(A) : 5 <= A & A <= 8 <-
    load(A - 4);
    !src;
    -+dst(A - 4).
@wait
+!execute(A) : num_of_free_moves(NoFMs) <-
    -+num_of_free_moves(NoFMs + 3);
    wait.

// Subplans

@check_collision[atomic]
+!check : robot(Cell, premove) & robot(Cell) & direction(Dir) &
          blocked_cell_directions(BCDs) & not .member([Cell, Dir], BCDs) &
          num_of_collisions(NoCs) <-
    .concat(BCDs, [[Cell, Dir]], NewBCDs);
    -+blocked_cell_directions(NewBCDs);
    -+num_of_collisions(NoCs + 1);
    .broadcast(tell, blocked_cell_direction(Cell, Dir)); // MAS2 Communication
    !plan.
@check_free[atomic]
+!check : robot(Cell, premove) & direction(Dir) &
          free_cell_directions(FCDs) & not .member([Cell, Dir], FCDs) &
          num_of_free_moves(NoFMs) <-
    .concat(FCDs, [[Cell, Dir]], NewFCDs);
    -+free_cell_directions(NewFCDs);
    -+num_of_free_moves(NoFMs + 1);  
    .broadcast(tell, free_cell_direction(Cell, Dir)). // MAS2 Communication
+!check.

/* MAS2 : Movement Communication - Start... */

@blocked_cell_direction[atomic]
+blocked_cell_direction(Cell, Dir) : 
        num_of_collisions(NoCs) & 
        blocked_cell_directions(BCDs) & not .member([Cell, Dir], BCDs) <-
    .concat(BCDs, [[Cell, Dir]], NewBCDs);
    -+blocked_cell_directions(NewBCDs);
    -+num_of_collisions(NoCs + 1);
    !plan.

@free_cell_direction[atomic]
+free_cell_direction(Cell, Dir) : 
        num_of_free_moves(NoFMs) &
        free_cell_directions(FCDs) & not .member([Cell, Dir], FCDs) <-
    .concat(FCDs, [[Cell, Dir]], NewFCDs);
    -+free_cell_directions(NewFCDs);
    -+num_of_free_moves(NoFMs + 1).

/* ... MAS2 : Movement Communication - End */

// Utility subplans

@src
+!src : robot(Cell) & nodes(NDs) & .nth(1, NDs, Cell) <- -+src(1).
+!src : robot(Cell) & nodes(NDs) & .nth(2, NDs, Cell) <- -+src(2).
+!src : robot(Cell) & nodes(NDs) & .nth(3, NDs, Cell) <- -+src(3).
+!src : robot(Cell) & nodes(NDs) & .nth(4, NDs, Cell) <- -+src(4).

@remove_route[atomic]
+!remove_route(R, RTs) : not .member(R, RTs).
+!remove_route([Src, Dst], [[Src, Dst] | T]) : routes_head(Head) <-
    .concat(Head, T, NewRTs);
    .print("+!remove_route(", [Src, Dst], "), NewRTs: ", NewRTs);
    -+routes(NewRTs);
    -+routes_head([]).
+!remove_route(R, [H | T]) : routes_head(Head) <-
    .concat(Head, [H], NewHead);
    -+routes_head(NewHead);
    !remove_route(R, T).
