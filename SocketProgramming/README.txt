README file 

The project is developed in Java using JDK 1.7 and Eclipse IDE

Design Decisions:

1)Used Byte array to send and recieve data so that, while dealing with -ve integers we get the correct total
2)For #2 and #3 converted the input into string and then encoded it into UTF-8 so that we can send strings such as reset and it would reset the sum
3) For #3 and #5 used HashTable to handle multiple clients, HashTable was used because it is Thread safe and synchronus

Testing the project
for all programs first start the server and then the client

To execute program 1 
Input Client : java SockClient1 10			Output From Server: result is 10
			   java SockClient1 20								result is 30
			   java SockClient1 - 40							result is -10
			   
To execute program 2
Input Client : java SockClient2 10			Output From Server: result is 10
			   java SockClient2 reset							result is 0
			   java SockClient2 - 40							result is -40
			   
			   
To execute program 3
Input Client : java SockClient3 1 20			Output From Server: result is 20
			   java SockClient3 2 40 								result is 40
			   java SockClient3 1 -30								result is -10
			   java SockClient3 2  50								result is 90
			   
To execute program 4
Input Client : java SockClient4 10			Output From Server: result is 10
			   java SockClient4 reset							result is 0
			   java SockClient4 - 40							result is -40
			   
			   
			   
To execute program 5

start two clients at the same time and the input as follows

Input Client : java SockClient3 1 20 1000			Output From Server: result is 20
			   java SockClient3 1 40 1000								result is 60
			   java SockClient3 2 -30									result is -30
			   java SockClient3 2  50									result is 20