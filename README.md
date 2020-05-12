# tch_client
Scan and server client component of TCruch - TrueCrypt detection and distributed attack system.

# TCrunch
This was devised as a method to more accurately detect TrueCrypt containers on a host than the tools already available as well as provide an efficient distributed attack system in order to maximize available CPU power.

The detection system works on a combination of the Chi-Square and Monte Carlo Pi tests, the results being far more accurate than other detection systems.

The attack system is a dynamic heterogeneous distributed attack structure allowing for the addition and removal of attacker machines at will without loss or degradation of the attack process.

# Component purpose
This is the user client for the system and is responsible for the TrueCrypt file detection and upload to the attack server. This component can also upload an attack dictionary to the attack server to use against the file if preferred. 

The detection system works on a cascading method of chi-square to monte carlo pi, due to the inherant increased resources required for the latter test it is only used when the former less precisde test results in a value above a specific threshold. That way we keep resources to a minimal while ramping up the accuracy of the system. This system outperformed both TChunt and FIT in comparative testing. 

If a file is identified as a TrueCrypt container, the client will extract a file fragment containing the containers encrypted header and upload it to the server. Using this file fragment we will be able to crack the password for the container (regardless of size) with a file fragment of 300KB which makes distribution much easier. 

This is one of the more strict access control components and there are several settings and credentials that must be provided in order for the client to operate as intended. 

# FULL RESEARCH PAPER
TBA
