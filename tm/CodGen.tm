*Compilación TM
*Preludio estándar:
  0:    LD  6,0(0)	load maxaddress from location 0
  1:    ST  0,0(0)	clear location 0
*Fin del preludio estándar
  2:    IN  0,0,0	read value
  3:    ST  0,1(5)	read: store value
  4:    IN  0,0,0	read value
  5:    ST  0,0(5)	read: store value
  6:    LD  0,1(5)	load id value
  7:    ST  0,0(6)	op: push left
  8:    LD  0,0(5)	load id value
  9:    LD  1,0(6)	op: load left
 10:    ADD  0,1,0	op +
 11:    ST  0,2(5)	assign: store value
 12:    LD  0,2(5)	load id value
 13:    ST  0,0(6)	op: push left
 14:    LDC  0,1(0)	load const int
 15:    LD  1,0(6)	op: load left
 16:    ADD  0,1,0	op +
 17:    OUT  0,0,0	write ac
*Fin de la ejecución.
 18:    HALT  0,0,0	
