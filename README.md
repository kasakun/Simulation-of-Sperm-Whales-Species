# Simulation-of-Sperm-Whales-Species

## Initial goal
The initial goal is to realize one season. If the process is advance, we can try ten years.

## Class
Main Process, Chengxi Yao

Killer Whales, Zeyu CHen

Sperm Whales, Taoyouwei Gao

Marine Mammals, Weihua Zhu

The mechanism is:
Every thread schedule its own events. Becgause we are using threads, we DO NOT using communication. Each thread can 
directly call the specific object function to modify the value. The shared value must be protected by the lock.

The examples are marine mammals(MM) and sperm whales(SW) consume food. SW and MM has its own funciton eat(). Consuming
food resource of Main Process.

The first step is to construct the functions in each class.

Then construct the thread function in the main after engine class is constructed. You can use main function to test if 
functions in your process works.

The event engine class is complete.

## Simulaition

The whole Simulation Logic is changed.

10 years long simulation. 

You can change the time by `timeLimit`

## Problem So far

1. How to determine the demand when calculate a species death for hunger @all 
2. The competition between sperm whales and marine mammals are not presented. Main proc should distribute the total food
resource for these two speices. @Yao
3. The fishery only influence the exact number of the food resource, not actually influence the number of species. @Yao
 
4. Hunt rate should be related to the number of species. @ Zeyu Chen

## Have a nice day!

