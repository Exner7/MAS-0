// Agent md in project app

/* Initial beliefs and rules */

free_cell_directions([]). blocked_cell_directions([]).
num_of_free_moves(1). num_of_collisions(1).
routes([]). routes_head([]).
plan([]).

/* Initial goals */

!wait.

/* Plans */

+!wait : (energy(E) & E == 0) | (routes(RTs) & .length(RTs, 0)) <-
    wait; !wait.
+!wait <- !plan; !work; !wait.

@plan[atomic]
+!plan : free_cell_directions(FCDs) & blocked_cell_directions(BCDs) &
         nodes(NDs) & routes(RTs) & carry(C) & energy(E) &
         num_of_free_moves(NoFMs) & num_of_collisions(NoCs) <-
    planner.plan(FCDs, BCDs, NDs, RTs, C, E, NoFMs, NoCs, NewPL);
    -+plan(NewPL).

+!work : (energy(E) & E == 0) | (routes(RTs) & .length(RTs, 0)) <- !wait.
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
@unload[atomic]
+!execute(A) : A == 4 & routes(RTs) & src(Src) & dst(Dst) <-
    unload;
    !remove_route([Src, Dst], RTs);
    !plan.
@load[atomic]
+!execute(A) : 5 <= A & A <= 8 <-
    load(A - 4);
    !src;
    -+dst(A - 4).
@wait
+!execute(A) : num_of_free_moves(NoFMs) <-
    .print("Searching for a viable plan...");
    .print("Decreasing new-move risk...");
    -+num_of_free_moves(NoFMs + 1);
    wait.

// Subplans

@route[atomic]
+route(R) : routes(RTs) <-
    .concat(RTs, [R], NewRTs);
    -+routes(NewRTs);
    .print("Routes: ", NewRTs);
    !plan.

@check_collision[atomic]
+!check :   robot(Cell, premove) & robot(Cell) & direction(Dir) &
            blocked_cell_directions(BCDs) & not .member([Cell, Dir], BCDs) &
            num_of_collisions(NoCs) <-
    .concat(BCDs, [[Cell, Dir]], NewBCDs);
    -+blocked_cell_directions(NewBCDs);
    -+num_of_collisions(NoCs + 1);
    !plan.
@check_free[atomic]
+!check :   robot(Cell, premove) & direction(Dir) &
            free_cell_directions(FCDs) & not .member([Cell, Dir], FCDs) &
            num_of_free_moves(NoFMs) <-
    .concat(FCDs, [[Cell, Dir]], NewFCDs);
    -+free_cell_directions(NewFCDs);
    -+num_of_free_moves(NoFMs + 1).
+!check.

@src[atomic]
+!src : robot(Cell) & nodes(NDs) & .nth(1, NDs, Cell) <- -+src(1).
+!src : robot(Cell) & nodes(NDs) & .nth(2, NDs, Cell) <- -+src(2).
+!src : robot(Cell) & nodes(NDs) & .nth(3, NDs, Cell) <- -+src(3).
+!src : robot(Cell) & nodes(NDs) & .nth(4, NDs, Cell) <- -+src(4).

@remove_route[atomic]
+!remove_route(R, Routes) : not .member(R, Routes).
+!remove_route([Src, Dst], [[Src, Dst] | T]) : routes_head(Head) <-
    .concat(Head, T, NewRoutes);
    -+routes(NewRoutes);
    -+routes_head([]);
    .print("Routes: ", NewRoutes).
+!remove_route(R, [H | T]) : routes_head(Head) <-
    .concat(Head, [H], NewHead);
    -+routes_head(NewHead);
    !remove_route(R, T).
