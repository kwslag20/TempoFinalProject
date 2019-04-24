	# Authors: Kevin Ahn, Kyle Slager, Danqing Zhou
	# Date: Apr 2019
	# Compiled from A.btm
	.data
	.globl	gc_flag
	.globl	class_name_table
gc_flag:
	.word	0
class_name_0:
	.word	1
	.word	8
	.word	String_dispatch_table
	.word	6
	.ascii	"Object"
	.byte	0
	.align	2
class_name_1:
	.word	1
	.word	8
	.word	String_dispatch_table
	.word	6
	.ascii	"String"
	.byte	0
	.align	2
class_name_2:
	.word	1
	.word	4
	.word	String_dispatch_table
	.word	3
	.ascii	"Sys"
	.byte	0
	.align	2
class_name_3:
	.word	1
	.word	4
	.word	String_dispatch_table
	.word	4
	.ascii	"Main"
	.byte	0
	.align	2
class_name_4:
	.word	1
	.word	8
	.word	String_dispatch_table
	.word	6
	.ascii	"TextIO"
	.byte	0
	.align	2
label0:
	.word	1
	.word	76
	.word	String_dispatch_table
	.word	76
	.ascii	"/Users/kwslager/Desktop/project16AhnSlagerZhao/src/proj16AhnSlagerZhao/A.asm"
	.byte	0
	.align	2


class_name_table:
	.word	class_name_0
	.word	class_name_1
	.word	class_name_2
	.word	class_name_3
	.word	class_name_4


	.globl	Main_template
	.globl	Object_template
	.globl	String_template
	.globl	Sys_template
	.globl	Main_template
	.globl	TextIO_template
String_template:
	.word	1
	.word	16
	.word	String_dispatch_table
	.word	0
Object_template:
	.word	2
	.word	16
	.word	Object_dispatch_table
	.word	0
Sys_template:
	.word	3
	.word	16
	.word	Sys_dispatch_table
	.word	0
Main_template:
	.word	4
	.word	24
	.word	Main_dispatch_table
	.word	0
	.word	0
	.word	0
TextIO_template:
	.word	5
	.word	32
	.word	TextIO_dispatch_table
	.word	0
	.word	0
	.word	0
	.word	0
	.word	0


	.globl	Main_dispatch_table
	.globl	Object_dispatch_table
	.globl	String_dispatch_table
	.globl	Sys_dispatch_table
	.globl	Main_dispatch_table
	.globl	TextIO_dispatch_table

String_dispatch_table:
	.word	Object.clone
	.word	String.equals
	.word	String.toString
	.word	String.length
	.word	String.substring
	.word	String.concat

Object_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString

Sys_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Sys.exit
	.word	Sys.time
	.word	Sys.random

Main_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	Main.main
	.word	Main.tool

TextIO_dispatch_table:
	.word	Object.clone
	.word	Object.equals
	.word	Object.toString
	.word	TextIO.readStdin
	.word	TextIO.readFile
	.word	TextIO.writeStdout
	.word	TextIO.writeStderr
	.word	TextIO.writeFile
	.word	TextIO.getString
	.word	TextIO.getInt
	.word	TextIO.putString
	.word	TextIO.putInt
	.text
	.globl	main
	.globl	Main_init
	.globl	Main.main
main:
	jal __start
	# Generating a Constant Int Expression
	li $v0 3
	# storing field y at 0
	sw $v0 0($a0)
	# Generating a Constant Int Expression
	li $v0 4
	# storing field x at 4
	sw $v0 4($a0)
main:
Main.main:
	# Generating LEFT side of expression
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	add $sp $sp -4
	sw $v0 0($sp)
	move $v1 $v0
	# Generating RIGHT side of expression
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 0($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	add $sp $sp -4
	sw $v0 0($sp)
	sgt $v0 $v1 $v0
	# Jumping to else statement if 'if' is false
	beq $v0 $zero label1
	# Generating the THEN label instructions
label0:
	add $sp $sp -4
	sw $a0 0($sp)
	# Generating a Constant Int Expression
	li $v0 5
	# GENERATING AN AssignExpr with NULL
	lw $v0 4($a0)
	# GENERATING AN AssignExpr with 
	lw $v0 4($a0)
	sw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	b label2
	# Generating the ELSE label instructions
label1:
label2:
	add $sp $sp -4
	sw $ra 0($sp)
	la $ra label4
	add $sp $sp -4
	sw $a0 0($sp)
	# Generating a Constant Int Expression
	li $v0 0
	# GENERATING AN AssignExpr with NULL
	lw $v0 4($a0)
	# GENERATING AN AssignExpr with 
	lw $v0 4($a0)
	sw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
label3:
	# Generating LEFT side of expression
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	add $sp $sp -4
	sw $v0 0($sp)
	move $v1 $v0
	# Generating RIGHT side of expression
	# Generating a Constant Int Expression
	li $v0 5
	add $sp $sp -4
	sw $v0 0($sp)
	slt $v0 $v1 $v0
	beq $v0 $zero label4
label4:
	add $sp $sp -4
	sw $ra 0($sp)
	la $ra label6
	# Generating the PREDICATE label
label5:
	# Generating LEFT side of expression
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	add $sp $sp -4
	sw $v0 0($sp)
	move $v1 $v0
	# Generating RIGHT side of expression
	# Generating a Constant Int Expression
	li $v0 5
	add $sp $sp -4
	sw $v0 0($sp)
	slt $v0 $v1 $v0
	# Generating a CONDITIONAL BREAK to the postWhile label if predicate is false
	beq $v0 $zero label6
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 0($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# Generating UNARY INCREMENT instruction
	add $v0 $v0 1
	sw $v0 0($a0)
	# Generating an UNCONDITIONAL BREAK back to the predicate label
	b label5
label6:
	lw $ra 0($sp)
	add $sp $sp 4
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
	# Generating UNARY INCREMENT instruction
	add $v0 $v0 1
	sw $v0 4($a0)
label4:
	lw $ra 0($sp)
	add $sp $sp 4
tool:
Main.tool:
	add $sp $sp -4
	sw $a0 0($sp)
	# Generating LEFT side of expression
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 8($fp)
	lw $a0 0($sp)
	add $sp $sp 4
	add $sp $sp -4
	sw $v0 0($sp)
	# Generating RIGHT side of expression
	# Generating VAREXPR
	add $sp $sp -4
	sw $a0 0($sp)
	# VarExpr with NULL
	lw $v0 12($fp)
	lw $a0 0($sp)
	add $sp $sp 4
	lw $v1 0($sp)
	add $sp $sp 4
	# Generating ADD instruction
	add $v0 $v0 $v1
	# GENERATING AN AssignExpr with NULL
	lw $v0 4($a0)
	# GENERATING AN AssignExpr with 
	lw $v0 4($a0)
	sw $v0 4($a0)
	lw $a0 0($sp)
	add $sp $sp 4
