# Solving safety game played on graphs.

Thesis wrote by HUYLENBROECK Florent, in order to achieve the Master grade in computer Sciences.

**Director** : Véronique BRUYERE. 

**Reviewers** : Gaëtan STACQUET, Clément TAMINES. 

This thesis studies the solving of safety games played on finite and infinite graphs. 
The report can be found at report/memoire.pdf and the code is contained in the code directory.

# Requirements
To run the code for the infinite case, python3, ant, java 11+ and C++17+ are required. 

# Building Z3
To build the project for the infinite case, navigate to 'code/infinite_case/lib/z3-master' and run
```sh
python3 scripts/mk_make.py --java --prefix=../..
cd build
make
sudo make install
```
To uninstall, navigate to 'code/infinite_case/lib/z3-master' and run
```sh
cd build
sudo make uninstall
```

# Run
To run the project for the finite case :
- Go to code/finite_case
- Open finite_arena.py and edit the main function to create a finite arena.
- Call the instance method 'attractor([vertices], player) to compute the attractor for player number 'player' towards the set of vertices '[vertices]'

To run the project for infinite case :
- Go to  code/infinite_case
- Open src/Main.java and create a SafetyGame object. (SafetyGame class has built-in function to generate safety games).
- run 'ant' to build and run the project.
