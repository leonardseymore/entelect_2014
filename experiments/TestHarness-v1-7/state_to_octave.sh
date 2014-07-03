#!/usr/bin/env groovy

print "["
System.in.eachLine() {line->
	line.eachWithIndex() {c, i ->
		switch (c) {
			case '.':
				print 1
			break
			case '*':
				print 10
			break
			default:
				print 0
			break
		}

        if (i < line.length() - 1) {
			print ","
		}
	}
	println ";"
}
print "]"
