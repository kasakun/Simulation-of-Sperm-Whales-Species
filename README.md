# Simulation-of-Sperm-Whales-Species

## Initial goal
The initial goal is to realize one season. If the process is advance, we can try ten years.

## Class
Main Process, Chengxi Yao

Killer Whales, Zeyu CHen

Sperm Whales, Taoyouwei Gao

Marine Mammals, Weihua Zhu

The mechanism is:
Every thread schedule its own events. Because we are using threads, we DO NOT using communication. Each thread can 
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

1. Remain sperm whales and marine mammals are negeative. @Matthew @weihua
2. No food resource info output. @Yao
3. Need output file @Yao

## Have a nice day!

