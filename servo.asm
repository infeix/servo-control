.INCLUDE "m8def.inc"
.EQU takt = 1000000
.DEF sollzeit = r23 
.DEF istZeit = r20 
.DEF akku = r16
.DEF bkku = r17
.DEF state = r24
.DEF iaaa = r18
.DEF zzzz = r25
.DEF ffff = r26
.DEF ibbb = r19
.DEF tindex = r21
.DEF pindex = r22
.CSEG
rjmp start
.ORG $13

start:	ldi akku,LOW(RAMEND)
		out SPL,akku
		ldi akku,HIGH(RAMEND)
		out SPH,akku
		ldi akku,$ff

		ldi sollzeit,$00
		ldi istzeit,$00
		ldi iaaa,$00
		ldi ibbb,$00
		ldi tindex,$00
		ldi pindex,$00
		ldi zzzz,$00
		ldi ffff,$ff
		ldi akku, (1<<CS00)       ; CS00 setzen: Teiler 1
        out TCCR0, akku
        ldi akku, (1<<TOIE0)      ; TOIE0: Interrupt 
		out TIMSK, akku

		ldi akku,$ff
		out DDRB,akku
		out DDRC,akku
		ldi akku,45
		ldi bkku,2
		ldi state,0				;			24
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;mod 20 Takte
longidle: nop					;1   Takte  25; 43->45; 65; 85; 105; 5125; 5145...
		nop						;1   Takte 	26
		nop						;1   Takte 	27
		nop						;1   Takte  28
		nop						;1   Takte 	29
		nop						;1   Takte  30; 5170;
		nop						;1   Takte 	31
		nop						;1   Takte  32
		nop						;1   Takte  33
		nop						;1   Takte  34
		inc 	iaaa			;1   Takte  35
		cpi     iaaa, 0			;1   Takte  36
		breq	overflow		;1+4 Takte  37 : Wenn gleich dann springen!
		nop
		cp     ibbb, bkku		;1   Takte  42
		brne    blongidle		;1+1 Takte  43 : Wenn ungleich dann springen!					
		cp     iaaa, akku       ;1   Takte
        breq    over
		rjmp	longidle		;2   Takte
overflow: inc 	ibbb			;1   Takte  5139
	 	rjmp	blongidle		;2   Takte  5140
blongidle: 	nop
		rjmp	longidle		;2   Takte  5140

over:	nop
		nop
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;mod 20 Takte over

		cpi     state, 1		;1   Takte
		breq	ausschalt		;1+1 Takte: Wenn gleich dann springen!
		nop
anschalt: nop
		nop
		nop
		nop						;1   Takte
		ldi state,1				;1   Takte
		ldi bkku,0				;1   Takte
		ldi akku,49				;1   Takte
		ldi iaaa,$ff			;1   Takte
		ldi ibbb,$00			;1   Takte
		out PORTC,ffff			;1   Takte
		rjmp	longidle		;2   Takte
ausschalt: inc 	iaaa			;1   Takte
		nop						;1   Takte
		nop						;1   Takte
		nop						;1   Takte
		nop						;1   Takte
		ldi state,0				;1   Takte
		ldi bkku,3				;1   Takte
		ldi akku,31				;1   Takte
		ldi iaaa,$ff			;1   Takte
		ldi ibbb,$00			;1   Takte
		cp sollzeit, iaaa		;1   Takte
		brne ausschalt			;1+1 Takte
		out PORTC,zzzz			;1   Takte
		rjmp longidle			;2   Takte
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;mod 20 Takte

.EXIT
