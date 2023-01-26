l:
	./r -lexer tests/fibonacci.c
p:
	./r -parser tests/fibonacci.c
clean:
	find -name *~* -delete
	ant clean
