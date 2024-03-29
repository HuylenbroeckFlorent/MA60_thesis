# Solving safety game played on graphs.

Thesis wrote by HUYLENBROECK Florent, in order to achieve the Master grade in computer Sciences.

**Director** : Véronique BRUYERE. 

**Reviewers** : Gaëtan STAQUET, Clément TAMINES. 

This thesis studies the solving of safety games played on finite and infinite graphs. For the finite case, an algorithm computing attractors was implemented. For the inifinite games, a learning algorithm unsing finite state automatas and SAT solvers was implemented, inspired from D. Neider's work in '[An Automaton Learning Approach to Solving Safety Games over Infinite Graphs](https://link.springer.com/chapter/10.1007/978-3-662-49674-9_12)'.
The report can be found at 'report/memoire.pdf' and the code is contained in the 'code/' directory.

# Requirements
To run the code for the infinite case, python3, ant, java 11+ and C++ 17+ runtime are required. Only python3 is required to run the finite case.

# Building Z3
To build Z3 for the infinite case, navigate to 'code/infinite_case/lib/z3-master' and run
```sh
python3 scripts/mk_make.py --java
cd build
make
sudo make install
```
Then, in 'code/infinite_case', run
```sh
ant build
```
To uninstall Z3, navigate to 'code/infinite_case/lib/z3-master' and run
```sh
cd build
sudo make uninstall
```
To clean the java classes, in 'code/infinite_case', run
```sh
ant clean
```
See 'code/inifinite_case/lib/z3-master/README.md' for more informations on Z3.
# Using the solver
To run the project for the finite case :
- Go to code/finite_case
- Open finite_arena.py and edit the main function to create a finite arena.
- Call the instance method 'attractor([vertices], player) to compute the attractor for player number 'player' towards the set of vertices '[vertices]'
- Then simply execute
```sh
python3 finite_arena.py
```
To run the project for infinite case :
- Go to  code/infinite_case
- Open src/Main.java and create a SafetyGame object. (SafetyGame class has built-in function to generate safety games).
- Then,to run the solver, simply execute
```sh
ant
```

