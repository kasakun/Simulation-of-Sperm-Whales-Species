# Simulation-of-Sperm-Whales-Species

## How to run
Code zip file includes two directories. In the bin is the compiled software.
In the src is the source code of software. Since java compiler tools are
not deployed on deepthought. The given software is compiled locally.

In the given bin dir, user can simply run Sim.class to see the result.


Two modes are available.

### Default

```
$ java Sim
```
If user does not give any arguments, the simulation will run 3600 days by default.
The code is tested on the deepthought and is working correctly.

### Year mode

```
$ java Sim [data file] [fishery level]
```

User can input a `csv` file with `(year, whaling numbers)` pairs and a fishery level(0, 1, 2, 3, 4, 5. 0 is the weakest,
5 is the strongest)


## Result
Output files are logs of each thread.

The log of three species are mainly hunting event and accidental death event.

The log of main process is mainly natual factors changes, such as ocean temperature.  


In addition, the python code and the historical data are also located in this directory.

